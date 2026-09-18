/**
 * JWAP - A Java Implementation of the WAP Protocols
 * Copyright (C) 2001-2004 Niko Bender
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package net.sourceforge.jwap.wtp;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Hashtable;

import net.sourceforge.jwap.util.Logger;
import net.sourceforge.jwap.util.Utils;
import net.sourceforge.jwap.wtp.pdu.CWTPAbort;
import net.sourceforge.jwap.wtp.pdu.CWTPAck;
import net.sourceforge.jwap.wtp.pdu.CWTPInvoke;
import net.sourceforge.jwap.wtp.pdu.CWTPPDU;
import net.sourceforge.jwap.wtp.pdu.EWTPCorruptPDUException;


/**
 * This class interfaces to the lower layer, the UDP Layer.
 * We use a DatagramSocket here.<br>
 * Per WSP session (defined by local port + address and remote port + address)
 * we need one object of type CWTPSocket. We may need more than one transaction
 * (CWTPTransaction) per session. CWTPSocket listens in a endless thread
 * for new datagrams on the DatagramSocket. If it gets one, it uses
 * CWTPFactory to decode the PDU. Then it associates the PDU with a transaction
 * by comparing the Transaction Identifier.<br>
 * The constructor is private. Use #getInstance(CWTPTransaction t) to
 * get an object for a specific transaction. This is because we want to check,
 * if there already exists a CWTPManagement for the session the transaction
 * belongs to.<br>
 */
public class CWTPSocket extends Thread 
{
    static Logger logger = Logger.getLogger(CWTPSocket.class);
    
    /** WAP Default client port (49200)  */
    public static final int DEFAULT_PORT = 9201;//49200;//62000;//49200;//the anh da sua lai cho nay

    /**
     * the underliing Layer, a DatagramSocket (UDP)
     */
    private DatagramSocket socket;

    /**
     * the upper Layer, modelling the session, WSP
     */
    private IWTPUpperLayer upperLayer;

    // remote port and address
    private int toPort;
    private InetAddress toAddress;
    
    // Used to synchronize reader-thread with close() method
    private byte[] lock = new byte[0];
    private boolean isRunning;
    
    /**
     * Holds all transactions belonging to this management entity and
     * their corresponding Transaction IDs wrapped in an Integer
     */
    private Hashtable transactions = new Hashtable();

    public CWTPSocket(InetAddress toAddress, int toPort,
        IWTPUpperLayer upperLayer) throws SocketException {
        this(toAddress, toPort, null, DEFAULT_PORT, upperLayer);
    }
    
    public CWTPSocket(InetAddress toAddress, int toPort, InetAddress localAddress, 
        int localPort, IWTPUpperLayer upperLayer) throws SocketException {
        if( localAddress == null ) {
        	//socket.setReuseAddress(true);
            socket = new DatagramSocket(localPort);
        } else {
        	//socket.setReuseAddress(true);//The anh bo sung o day   
            socket = new DatagramSocket(localPort, localAddress);     
            
        }
        
        socket.setSoTimeout(1000);
        this.upperLayer = upperLayer;
        this.toAddress = toAddress;
        this.toPort = toPort;
        this.setName("CWTPSocket-"+toAddress.getHostAddress()+":"+toPort);
        isRunning=true;
        this.start();
    }

    public CWTPInitiator tr_invoke(IWTPUpperLayer upper_Layer,
        CWTPEvent initPacket, boolean ackType, byte classType) {
        return new CWTPInitiator(this, upper_Layer, initPacket, ackType,
            classType);
    }

    public void send(CWTPPDU pdu) {
        byte[] sendBytes = pdu.toByteArray();

        try {
            if (logger.isDebugEnabled()) {
                logger.debug("sending " + CWTPPDU.types[pdu.getPDUType()] +
                    ", TID: " + pdu.getTID());
                logger.debug("\n"+Utils.hexDump("data to send: ", sendBytes));
            }

            socket.send(new DatagramPacket(sendBytes, sendBytes.length,
                    toAddress, toPort));
        } catch (IOException e) {
            logger.error("IOException while sending.", e);
        }
    }

    /**
     * This method in an (endless) loop receives DatagramPackets (UDP)
     * by the DatagramSocket layer below.
     * <br>
     * Stopping: The loop can be interrupted by calling interrupt().
     * If you would like to stop the whole socket you better
     * should call close(). It also closes the DatagramSocket below.
     */
    public void run() {
        while (isRunning) {
            logger.debug("WTP-Layer listening...");

            /** @todo MRU capability negotiation
             * The folowing number (1400) should not be hard coded!
             * It is the MRU that is negotiated via WSP.
             * This is part of the WSP CAPABILITY NEGOTIATION Task!
             */
            DatagramPacket in = new DatagramPacket(new byte[2048], 2048);//1400

            try {
                while (isRunning) {
                    try {
                        socket.receive(in);

                        break;
                    } catch (InterruptedIOException ie) {
                        // timeout from read...
                    }
                }
            } catch (IOException e) {
                if (isRunning) {
                    logger.error("IOException from socket.receive()", e);
                }
            }

            // Have we been stopped? 
            if (!isRunning) {
                break;
            }

            // is the remote host allowed to communicate with this session?
            // if not, ignore it!
            //if (toAddress.equals(in.getAddress()) && toPort == in.getPort()){
            // decode the bytes
            CWTPPDU pdu = null;
            IWTPTransaction transact = null;

            try {
                // uses now the actual data length from the DatagramPacket
                // instead of the length of the  byte[] buffer
                pdu = CWTPPDU.decode(in.getData(), in.getLength());

                if (logger.isDebugEnabled()) {
                    logger.debug("received WTP PDU: " +
                        CWTPPDU.types[pdu.getPDUType()] + " TID: " +
                        pdu.getTID() + " | " + (pdu.getTID() + 32768));//32768
                }
            } catch (EWTPCorruptPDUException e) {
                // if the TID is available process in state machine
                // else: ignore
                if (e.isTidAvailable()) {
                    transact = getTransaction(e.getTid() + 32768);//32768

                    if (transact != null) {
                        // process in state machine
                        transact.process(e);
                    }
                }
            }

            // associate the PDU with the corresponding transaction
            transact = getTransaction(pdu.getTID() + 32768);//32768

            if (transact == null) {
                // there is no transaction with this TID
                //logger.debug("new Transaction");
                // if rcvInvoke start new transaction:
                if (pdu.getPDUType() == CWTPPDU.PDU_TYPE_INVOKE) {
                    CWTPInvoke pdu2 = (CWTPInvoke) pdu;

                    if ((pdu2.getTCL() == CWTPInitiator.CLASS_TYPE_1) ||
                            (pdu2.getTCL() == CWTPInitiator.CLASS_TYPE_2)) {
                        CWTPResponder resp = new CWTPResponder(this,
                                upperLayer, pdu2, pdu2.getU_P(), pdu2.getTCL());
                    }
                }

                // if Ack PDU with TIDve flag set, send abort PDU
                if (pdu.getPDUType() == CWTPPDU.PDU_TYPE_ACK) {
                    if (((CWTPAck) pdu).getTve_tok()) {
                        send(new CWTPAbort(CWTPAbort.ABORT_REASON_INVALIDTID));
                    }
                }

                // else: ignore
            } else {
                // pdu is associated with transaction
                try {
                    //logger.debug("PDU associated");
                    // process in state machine of transaction
                    transact.process(pdu);
                } catch (EWTPAbortedException e3) {
                    logger.warn("Transaction aborted", e3);
                    transact = getTransaction(pdu.getTID());

                    if (transact != null) {
                        removeTransaction(transact);
                    }
                }
            }
        }
        // Notify close()...
        synchronized(lock) {
            lock.notifyAll(); 
        }
    }

    /**
     * Close the socket. Closes the underliing DatagramSocket (UDP)
     */
    public void close() {
        logger.debug("close(): Closing socket");
        isRunning=false;
        socket.close();
        if( Thread.currentThread() != this ) {
            synchronized(lock) {
                try {
                    logger.debug("close(): waiting for thread to finish");
                    lock.wait(5000); // Wait max 5 secs for reader thread to terminate...
                    logger.debug("close(): done");
                } catch (InterruptedException e) {}
            }
        }
    }

    //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    //XXXXXXXXXXXXXXX get/add/remove transactions XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    public boolean addTransaction(IWTPTransaction t) {
        Integer tid = new Integer(t.getTID());

        synchronized (transactions) {
            if (transactions.containsKey(tid)) {
                return false;
            }

            transactions.put(tid, t);
        }

        return true;
    }

    public IWTPTransaction getTransaction(int TID) {
        Integer tid = new Integer(TID);

        return (IWTPTransaction) transactions.get(tid);
    }

    public boolean removeTransaction(IWTPTransaction t) {
        Integer tid = new Integer(t.getTID());

        return transactions.remove(tid) != null;
    }

    //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    //XXXXXXXXXXXXXXX getter/setter XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    public InetAddress getLocalAddress() {
        return socket.getLocalAddress();
    }

    public int getLocalPort() {
        return socket.getLocalPort();
    }

    public InetAddress getRemoteAddress() {
        return toAddress;
    }

    public int getRemotePort() {
        return toPort;
    }
}
