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
package net.sourceforge.jwap.wsp;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.Vector;

import net.sourceforge.jwap.util.Logger;
import net.sourceforge.jwap.wsp.pdu.CWSPConnect;
import net.sourceforge.jwap.wsp.pdu.CWSPConnectReply;
import net.sourceforge.jwap.wsp.pdu.CWSPDisconnect;
import net.sourceforge.jwap.wsp.pdu.CWSPGet;
import net.sourceforge.jwap.wsp.pdu.CWSPHeaders;
import net.sourceforge.jwap.wsp.pdu.CWSPPDU;
import net.sourceforge.jwap.wsp.pdu.CWSPPost;
import net.sourceforge.jwap.wsp.pdu.CWSPRedirect;
import net.sourceforge.jwap.wsp.pdu.CWSPReply;
import net.sourceforge.jwap.wsp.pdu.CWSPResume;
import net.sourceforge.jwap.wsp.pdu.CWSPSuspend;
import net.sourceforge.jwap.wsp.pdu.EWSPCorruptPDUException;
import net.sourceforge.jwap.wtp.CWTPEvent;
import net.sourceforge.jwap.wtp.CWTPSocket;
import net.sourceforge.jwap.wtp.EWTPAbortedException;
import net.sourceforge.jwap.wtp.IWTPTransaction;
import net.sourceforge.jwap.wtp.IWTPUpperLayer;


/**
 * This class implements the WSP state machine named "management entity"
 * in the Wireless Session Protocol Specification by the <a href="http://www.wapforum.org">WAP-forum</a>.
 * <br>
 * Using this class the programmer can use all methods of the WSP layer.
 * To use the WAP-Stack with a class corresponding to HttpURLConnection
 * please use WapURLConnection by calling<br>
 * <code><br>
 * URL example = new URL("wap://wap.nokia.com");<br>
 * URLConnection con = example.getConnection();<br>
 * </code>
 * @author Niko Bender
 */
public class CWSPSession implements IWTPUpperLayer {
    // SESSION states
    public static final short STATE_NULL = 0;
    public static final short STATE_CONNECTING = 1;
    public static final short STATE_CONNECTED = 2;
    public static final short STATE_SUSPENDED = 3;
    public static final short STATE_RESUMING = 4;

    // Abort reason code assignments
    // Table 35 in spec.
    public static final short ABORT_PROTOERR = 0xE0;
    public static final short ABORT_DISCONNECT = 0xE1;
    public static final short ABORT_SUSPEND = 0xE2;
    public static final short ABORT_RESUME = 0xE3;
    public static final short ABORT_CONGESTION = 0xE4;
    public static final short ABORT_CONNECTERR = 0xE5;
    public static final short ABORT_MRUEXCEEDED = 0xE6;
    public static final short ABORT_MOREXCEEDED = 0xE7;
    public static final short ABORT_PEERREQ = 0xE8;
    public static final short ABORT_NETERR = 0xE9;
    public static final short ABORT_USERREQ = 0xEA;
    public static final short ABORT_USERRFS = 0xEB;
    public static final short ABORT_USERPND = 0xEC;
    public static final short ABORT_USERDCR = 0xED;
    public static final short ABORT_USERDCU = 0xEE;
    static Logger logger = Logger.getLogger(CWSPSession.class);
    public String[] states = {
        "STATE_NULL", "STATE_CONNECTING", "STATE_CONNECTED", "STATE_SUSPENDED",
        "STATE_RESUMING"
    };
    private boolean isSuspended = false;
    private short suspendCode;
    private boolean isDisconnected = false;
    private short disconnectCode;

    /**
     * the actual session state
     */
    private short state = STATE_NULL;

    /**
     * the acual transaction concering the session management
     */
    private IWTPTransaction wtp;

    /**
     * the Layer below
     */
    private CWTPSocket socket;

    /**
     * Holds all pending CWSPMethodManagers of this session
     */
    private Vector methods = new Vector();

    /**
     * Holds all pending CWSPPushManagers of this session
     */
    private Vector pushes = new Vector();
    private IWSPUpperLayer upperlayer;

    //////////////////////////////////////////////////////////////////////////////
    ////////////////////////////// Protocol parameters and variables - sect. 7.1.3

    /**
     * Maximum Receive Unit (sect. 7.1.3.1)
     */
    private int MRU = 2048;//1400

    /**
     * Maximum Outstanding Method Requests (sect. 7.1.3.2)
     */
    private int MOM = 1;

    /**
     * Maximum Outstanding Push Requests (sect. 7.1.3.3)
     */
    private int MOP = 1;

    /**
     * keeps track of the number of push transactions in process in the client
     * (sect. 7.1.4.2)
     */

    // pushs.size();

    /**
     * saves the session identifier (sect. 7.1.4.3)
     * We will get this from the ConnectReply by the Server
     */
    private long session_id = 0;
    
    /**
     * do we use IWSPUpperLayer2 or 
     */
    private byte version = 0;

    /**
     * Construct a new WSP Session.
     * @param toAddress address of the WAP gateway
     * @param toPort WAP gateway port
     * @param upperLayer WSP Upper Layer
     * @param verbose verbose logging
     * @throws SocketException if the underlying WTP socket cannot be created
     * 
     */
    public CWSPSession(InetAddress toAddress, int toPort,
        IWSPUpperLayer upperLayer, boolean verbose) throws SocketException 
    {
        this(toAddress, toPort, null, CWTPSocket.DEFAULT_PORT, upperLayer, verbose);
    }
    
    /**
     * Construct a new WSP Session.
     * @param toAddress address of the WAP gateway
     * @param toPort WAP gateway port
     * @param localAddress local address to bind to (null to let the OS decide)
     * @param localPort local port to bind to (use 0 to let the OS pick a free port)
     * @param upperLayer WSP Upper Layer
     * @param verbose verbose logging
     * @throws SocketException if the underlying WTP socket cannot be created
     */
    public CWSPSession(InetAddress toAddress, int toPort, 
        InetAddress localAddress, int localPort,
        IWSPUpperLayer upperLayer, boolean verbose) throws SocketException 
    {
        if ((upperLayer != null) && upperLayer instanceof IWSPUpperLayer2) {
            this.version = 2;
        } else {
            this.version = 1;
        }
        
        // Make sure that the logging system is initialized
        Logger.initLogSystem(verbose);

        this.upperlayer = upperLayer;
        socket = new CWTPSocket(toAddress, toPort, localAddress, localPort, this);

        // there can not be any session with the same peer address quadruplet!
    }

    /**
     * Construct a new WSP session.
     * @param toAddress the address of the WAP gateway
     * @param toPort WAP gateway port
     * @param upperLayer WSP Upper Layer
     * @throws SocketException if the underlying WTP socket cannot be created
     */
    public CWSPSession(InetAddress toAddress, int toPort,
        IWSPUpperLayer upperLayer) throws SocketException {
        this(toAddress, toPort, upperLayer, false);
    }

    
    /**
     * Construct a new WSP session.
     * @param toAddress the address and port of the WAP gateway
     * @param upperLayer WSP Upper Layer
     * @param verbose verbose logging
     * @throws SocketException if the underlying WTP socket cannot be created
     */
    public CWSPSession(CWSPSocketAddress address, IWSPUpperLayer upperLayer, 
        boolean verbose) throws SocketException {
        this(address, null, upperLayer, verbose);
    }

    
    /**
     * Construct a new WSP session. 
     * @param address the address and port of the WAP gateway
     * @param localAddress the local address and port or null
     * @param upperLayer WSP Upper Layer
     * @param verbose verbose logging
     * @throws SocketException if the underlying WTP socket cannot be created
     */
    public CWSPSession(CWSPSocketAddress address, CWSPSocketAddress localAddress,
        IWSPUpperLayer upperLayer, boolean verbose) throws SocketException {
        this(address.getAddress(), address.getPort(), 
            localAddress==null?null:localAddress.getAddress(),
            localAddress==null?CWTPSocket.DEFAULT_PORT:localAddress.getPort(),
            upperLayer, verbose);
    }
    //////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////// WSP service primitives - S-*.*

    /**
     * Establish a WSP connection.
     */
    public synchronized void s_connect() {
      s_connect(null);
    }
    
    /** 
     * Establish a WSP connection using WSP headers
     * @param headers the WSP headers to set or null
     */
    public synchronized void s_connect(CWSPHeaders headers) {
        if (state == STATE_NULL) {
            abortAllMethods(ABORT_DISCONNECT);
            abortAllPushes(ABORT_DISCONNECT);

            // prepare WSP Connect PDU
            CWSPConnect pdu = new CWSPConnect();
            pdu.setHeaders(headers);

            // prepare WTP Service Primitive
            CWTPEvent initPacket = new CWTPEvent(pdu.toByteArray(),
                    CWTPEvent.TR_INVOKE_REQ);

            // construct transaction with initPacket
            wtp = socket.tr_invoke(this, initPacket, false,
                    IWTPTransaction.CLASS_TYPE_2);
            setState(STATE_CONNECTING);
        }
    }

    /**
     * S-Disconnect.req
     * @return S-Disconnect.ind
     */
    public synchronized void s_disconnect() {
        if (state == STATE_CONNECTING) {
            wtp.abort(ABORT_DISCONNECT);
            abortAllMethods(ABORT_DISCONNECT);
            s_disconnect_ind(ABORT_USERREQ);
            setState(STATE_NULL);
        } else if (state == STATE_CONNECTED) {
            abortAllMethods(ABORT_DISCONNECT);
            abortAllPushes(ABORT_DISCONNECT);

            CWSPDisconnect pdu = new CWSPDisconnect(session_id);
            CWTPEvent initPacket = new CWTPEvent(pdu.toByteArray(),
                    CWTPEvent.TR_INVOKE_REQ);
            wtp = socket.tr_invoke(this, initPacket, false,
                    IWTPTransaction.CLASS_TYPE_0);
            s_disconnect_ind(ABORT_USERREQ);
            setState(STATE_NULL);
        } else if (state == STATE_SUSPENDED) {
            s_disconnect_ind(ABORT_USERREQ);
            setState(STATE_NULL);
        } else if (state == STATE_RESUMING) {
            wtp.abort();
            abortAllMethods(ABORT_DISCONNECT);
            s_disconnect_ind(ABORT_USERREQ);
            setState(STATE_NULL);
        }
    }

    /**
     * S-Suspend.req
     * @return S-Suspend.ind
     */
    public synchronized boolean s_suspend() {
        if (state == STATE_CONNECTED) {
            abortAllMethods(ABORT_SUSPEND);
            abortAllPushes(ABORT_SUSPEND);

            CWSPSuspend pdu = new CWSPSuspend(this.session_id);
            CWTPEvent initPacket = new CWTPEvent(pdu.toByteArray(),
                    CWTPEvent.TR_INVOKE_REQ);
            wtp = socket.tr_invoke(this, initPacket, false,
                    IWTPTransaction.CLASS_TYPE_0);
            s_disconnect_ind(ABORT_USERREQ);
            setState(STATE_SUSPENDED);

            return true;
        } else if (state == STATE_RESUMING) {
            wtp.abort(ABORT_SUSPEND);
            abortAllMethods(ABORT_SUSPEND);

            CWSPSuspend pdu = new CWSPSuspend(this.session_id);
            CWTPEvent initPacket = new CWTPEvent(pdu.toByteArray(),
                    CWTPEvent.TR_INVOKE_REQ);
            wtp = socket.tr_invoke(this, initPacket, false,
                    IWTPTransaction.CLASS_TYPE_0);
            s_disconnect_ind(ABORT_USERREQ);
            setState(STATE_NULL);

            return true;
        }

        return false;
    }

    /**
     * S-Resume.req
     */
    public synchronized void s_resume() throws SocketException {
        if (state == STATE_CONNECTED) {
            abortAllMethods(ABORT_USERREQ);
            abortAllPushes(ABORT_USERREQ);

            // bind session to the new peer address quadruplet
            socket.close();
            socket = new CWTPSocket(socket.getRemoteAddress(),
                    socket.getRemotePort(), this);

            CWSPResume pdu = new CWSPResume(session_id);
            CWTPEvent initPacket = new CWTPEvent(pdu.toByteArray(),
                    CWTPEvent.TR_INVOKE_REQ);
            wtp = socket.tr_invoke(this, initPacket, false,
                    IWTPTransaction.CLASS_TYPE_2);
            setState(STATE_RESUMING);
        } else if (state == STATE_SUSPENDED) {
            CWSPResume pdu = new CWSPResume(session_id);
            CWTPEvent initPacket = new CWTPEvent(pdu.toByteArray(),
                    CWTPEvent.TR_INVOKE_REQ);
            wtp = socket.tr_invoke(this, initPacket, false,
                    IWTPTransaction.CLASS_TYPE_2);
            setState(STATE_RESUMING);
        }
    }

    /**
     * Use this method to construct a POST-MethodInvoke.req.
     * This method uses <code>methodInvoke(CWSPPDU pdu)</code>
     * to send the constructed WSP-POST-PDU.
     *
     * @param data The data to be POSTed
     * @param contentType The MIME-ContentType of the data to be POSTed
     */
    public synchronized CWSPMethodManager s_post(byte[] data,
        String contentType, String uri) {
        /** @todo hier müssen die Nachrichten aufgeteilt werden !!!! */
        CWSPPost pdu = new CWSPPost(data, contentType, uri);

        return s_methodInvoke(pdu);
    }

    /**
     * Use this method to construct a POST-MethodInvoke.req.
     * This method uses <code>methodInvoke(CWSPPDU pdu)</code>
     * to send the constructed WSP-POST-PDU.
     *
     * @param headers The headers defined for the request
     * @param data The data to be POSTed
     * @param contentType The MIME-ContentType of the data to be POSTed
     * @param uri the target URI to post to
     */
    public synchronized CWSPMethodManager s_post(CWSPHeaders headers,
        byte[] data, String contentType, String uri) {
        /** @todo hier müssen die Nachrichten aufgeteilt werden !!!! */
        CWSPPost pdu = new CWSPPost(data, contentType, uri);
        pdu.setHeaders(headers);

        return s_methodInvoke(pdu);
    }

    /**
     * Use this method to construct a GET-MethodInvoke.req.
     * This method uses <code>methodInvoke(CWSPPDU pdu)</code>
     * to send the constructed WSP-GET-PDU.
     *
     * @param uri The Unfied Resource Identifier of the resource to GET
     */
    public synchronized CWSPMethodManager s_get(String uri) {
        CWSPGet pdu = new CWSPGet(uri);

        return s_methodInvoke(pdu);
    }

    /**
     * Use this method to construct a GET-MethodInvoke.req.
     * This method uses <code>methodInvoke(CWSPPDU pdu)</code>
     * to send the constructed WSP-GET-PDU.
     *
     * @param headers The headers that are defined for the request
     * @param uri The Unfied Resource Identifier of the resource to GET
     */
    public synchronized CWSPMethodManager s_get(CWSPHeaders headers, String uri) {
        CWSPGet pdu = new CWSPGet(uri);
        pdu.setHeaders(headers);

        return s_methodInvoke(pdu);
    }

    /**
     * S-MethodInvoke.req
     * To construct a POST- or GET-Request please use
     * <code>get(String uri)</code> or
     * <code>post(byte[] data, String contentType)</code>
     * instead of this method.
     *
     * @param pdu The GET- or POST-PDU to be sent.
     */
    public synchronized CWSPMethodManager s_methodInvoke(CWSPPDU pdu) {
        if ((state != STATE_NULL) && (state != STATE_SUSPENDED)) {
            CWSPMethodManager m = null;
            synchronized(methods) {
              m = new CWSPMethodManager(pdu, this, upperlayer);
              methods.add(m);
            }
            return m;
        } else {
            return null;
        }
    }

    public void removeMethod(CWSPMethodManager m) {
        synchronized(methods) {
            methods.remove(m);
        }
    }
    
    // s-methodInvokeData implemented in CWTPMethodManager
    // s-methodResult implemented in CWTPMethodManager
    // s-methodResultData implemented in CWTPMethodManager
    // s-methodAbort.req implemented in CWTPMethodManager
    // S-confirmedPush.res implemented in CWSPPushManager
    // s_pushAbort.req implemented in CWSPPushManager
    // s_push.req not implemented in client
    private synchronized void s_connect_cnf() {
        logger.debug("s-connect.ind");
        upperlayer.s_connect_cnf();
    }

    private synchronized void s_suspend_ind(short reason) {
        logger.debug("s-suspend.ind");
        isSuspended = true;
        suspendCode = reason;
        upperlayer.s_suspend_ind(reason);
    }

    private synchronized void s_resume_cnf() {
        logger.debug("s-resume.ind");
        isSuspended = false;
        suspendCode = 0;
        upperlayer.s_resume_cnf();
    }

    private synchronized void s_disconnect_ind(short reason) {
        logger.debug("s-disconnect.ind");
        isDisconnected = true;
        disconnectCode = reason;
        upperlayer.s_disconnect_ind(reason);
        socket.close();
    }

    private synchronized void s_disconnect_ind(InetAddress[] redirectInfo) {
        logger.debug("s-disconnect.ind - redirected");
        isDisconnected = true;
        upperlayer.s_disconnect_ind(redirectInfo);
        socket.close();
    }
    
    private synchronized void s_disconnect_ind(CWSPSocketAddress[] redirectInfo) {
        logger.debug("s-disconnect.ind - redirected");
        isDisconnected = true;
        if (version == 2){
            ((IWSPUpperLayer2)upperlayer).s_disconnect_ind(redirectInfo);
        } else {
            InetAddress[] redirectInfo2 = new InetAddress[redirectInfo.length];
            for (int i = 0; i<redirectInfo.length; i++){
                redirectInfo2[i] = redirectInfo[i].getAddress();
            }
            upperlayer.s_disconnect_ind(redirectInfo2);
        }
        socket.close();
    }

    //////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////// implementing IWTPUpperLayer  - TR-*.*

    /**
     * process all TR-*.ind and TR-*.cnf service primitives except TR-Abort
     * (call tr-abort(short abortReason) to indicate a Abort by TR).
     * @param p The WTP Service primitive
     */
    public synchronized void tr_process(CWTPEvent p) {
        try {
            CWSPPDU pdu = null;

            if ((p.getUserData() != null) && (p.getUserData().length != 0)) {
                pdu = CWSPPDU.getPDU(p.getUserData());
            }

            if (logger.isDebugEnabled()) {
                logger.debug(CWTPEvent.types[p.getType()] + " in " +
                    states[state]);
            }

            switch (p.getType()) {
            case 0x01: //--------------------------------------------- TR-INVOKE.IND

                switch (state) {
                case STATE_CONNECTING:

                    if ((p.getTransaction().getClassType() == IWTPTransaction.CLASS_TYPE_1) &&
                            (pdu.getType() == CWSPPDU.PDU_TYPE_CONFIRMEDPUSH)) {
                        p.getTransaction().abort(ABORT_PROTOERR);
                    }

                    break;

                case STATE_CONNECTED:

                    if ((p.getTransaction().getClassType() == IWTPTransaction.CLASS_TYPE_0) &&
                            (pdu.getType() == CWSPPDU.PDU_TYPE_DISCONNECT)) {
                        abortAllMethods(ABORT_DISCONNECT);
                        abortAllPushes(ABORT_DISCONNECT);
                        s_disconnect_ind(ABORT_DISCONNECT);
                        setState(STATE_NULL);
                    } else if ((p.getTransaction().getClassType() == IWTPTransaction.CLASS_TYPE_0) &&
                            (pdu.getType() == CWSPPDU.PDU_TYPE_PUSH)) {
                        /** @todo s_push_ind() */
                    } else if ((p.getTransaction().getClassType() == IWTPTransaction.CLASS_TYPE_1) &&
                            (pdu.getType() == CWSPPDU.PDU_TYPE_CONFIRMEDPUSH)) {
                        /** @todo start new push transaction with this event */
                    }

                    break;

                case STATE_SUSPENDED:

                    if ((p.getTransaction().getClassType() == IWTPTransaction.CLASS_TYPE_0) &&
                            (pdu.getType() == CWSPPDU.PDU_TYPE_DISCONNECT)) {
                        s_disconnect_ind(ABORT_DISCONNECT);
                        setState(STATE_NULL);
                    } else if ((p.getTransaction().getClassType() == IWTPTransaction.CLASS_TYPE_1) &&
                            (pdu.getType() == CWSPPDU.PDU_TYPE_CONFIRMEDPUSH)) {
                        p.getTransaction().abort(ABORT_SUSPEND);
                    }

                    break;

                case STATE_RESUMING:

                    if ((p.getTransaction().getClassType() == IWTPTransaction.CLASS_TYPE_0) &&
                            (pdu.getType() == CWSPPDU.PDU_TYPE_DISCONNECT)) {
                        wtp.abort(ABORT_DISCONNECT);
                        abortAllMethods(ABORT_DISCONNECT);
                        s_disconnect_ind(ABORT_DISCONNECT);
                        setState(STATE_NULL);
                    } else if ((p.getTransaction().getClassType() == IWTPTransaction.CLASS_TYPE_1) &&
                            (pdu.getType() == CWSPPDU.PDU_TYPE_CONFIRMEDPUSH)) {
                        p.getTransaction().abort(ABORT_PROTOERR);
                    }
                }

                break;

            case 0x03: //--------------------------------------------- TR-INVOKE.CNF

                switch (state) {
                case STATE_CONNECTING:

                    //ignore
                    break;

                case STATE_CONNECTED:
                    break;

                case STATE_SUSPENDED:

                    // ignore
                    break;

                case STATE_RESUMING:
                    // ignore
                }

                break;

            case 0x05: //--------------------------------------------- TR-RESULT.IND

                switch (state) {
                case STATE_CONNECTING:

                    if (p.getUserData().length > MRU) {
                        wtp.abort(ABORT_MRUEXCEEDED);
                        abortAllMethods(ABORT_CONNECTERR);
                        s_disconnect_ind(ABORT_MRUEXCEEDED);
                        setState(STATE_NULL);
                    } else if (pdu.getType() == CWSPPDU.PDU_TYPE_CONNECTREPLY) {
                        CWSPConnectReply pdu2 = (CWSPConnectReply) pdu;
                        setState(STATE_CONNECTED);

                        CWTPEvent initPacket = new CWTPEvent(new byte[0],
                                CWTPEvent.TR_RESULT_RES);
                        wtp.process(initPacket);
                        session_id = pdu2.getServerSessionID();
                        s_connect_cnf();
                    } else if (pdu.getType() == CWSPPDU.PDU_TYPE_REDIRECT) {
                        CWTPEvent initPacket = new CWTPEvent(new byte[0],
                                CWTPEvent.TR_RESULT_RES);
                        wtp.process(initPacket);
                        abortAllMethods(ABORT_CONNECTERR);

                        CWSPRedirect pdu2 = (CWSPRedirect) pdu;
                        if (version == 2){
                            s_disconnect_ind(pdu2.getSocketAddresses());
                        } else{
                            s_disconnect_ind(pdu2.getInetAddresses());
                        }
                        setState(STATE_NULL);
                    } else if (pdu.getType() == CWSPPDU.PDU_TYPE_REPLY) {
                        CWTPEvent initPacket = new CWTPEvent(new byte[0],
                                CWTPEvent.TR_RESULT_RES);
                        wtp.process(initPacket);
                        abortAllMethods(ABORT_CONNECTERR);

                        CWSPReply pdu2 = (CWSPReply) pdu;
                        s_disconnect_ind(pdu2.getStatus());
                        setState(STATE_NULL);
                    } else {
                        wtp.abort(ABORT_PROTOERR);
                        abortAllMethods(ABORT_CONNECTERR);
                        s_disconnect_ind(ABORT_PROTOERR);
                        setState(STATE_NULL);
                    }

                    break;

                case STATE_CONNECTED:
                    break;

                case STATE_SUSPENDED:
                    break;

                case STATE_RESUMING:

                    if (p.getUserData().length > MRU) {
                        wtp.abort(ABORT_MRUEXCEEDED);
                        abortAllMethods(ABORT_SUSPEND);
                        s_suspend_ind(ABORT_MRUEXCEEDED);
                        setState(STATE_SUSPENDED);
                    } else if (pdu.getType() == CWSPPDU.PDU_TYPE_REPLY) {
                        CWSPReply pdu2 = (CWSPReply) pdu;

                        if (pdu2.getStatus() == CWSPReply._200_OK_Success) {
                            CWTPEvent initPacket = new CWTPEvent(new byte[0],
                                    CWTPEvent.TR_RESULT_RES);
                            wtp.process(initPacket);
                            s_resume_cnf();
                            setState(STATE_CONNECTED);
                        } else {
                            CWTPEvent initPacket = new CWTPEvent(new byte[0],
                                    CWTPEvent.TR_RESULT_RES);
                            wtp.process(initPacket);
                            abortAllMethods(ABORT_DISCONNECT);

                            CWSPReply pdu3 = (CWSPReply) pdu;
                            s_disconnect_ind(pdu3.getStatus());
                            setState(STATE_NULL);
                        }
                    } else {
                        wtp.abort(ABORT_PROTOERR);
                        abortAllMethods(ABORT_SUSPEND);
                        s_suspend_ind(ABORT_PROTOERR);
                        setState(STATE_SUSPENDED);
                    }
                }

                break;

            case 0x07: //--------------------------------------------- TR-RESULT.CNF

                switch (state) {
                case STATE_CONNECTING:
                    break;

                case STATE_CONNECTED:
                    break;

                case STATE_SUSPENDED:
                    break;

                case STATE_RESUMING:}

                break;
            }
        } catch (EWSPCorruptPDUException e) {
            logger.error("Corrupt PDU", e);
        } catch (EWTPAbortedException e2) {
            logger.error("Aborted", e2);
        }
    }

    /**
     * TR-Abort.ind
     * @param abortReason The abort reason
     */
    public void tr_abort(short abortReason) {
        if (logger.isDebugEnabled()) {
            logger.debug("WSP Session: TR-ABORT.REQ in " + states[state]);
        }

        if (state == STATE_CONNECTING) {
            abortAllMethods(ABORT_CONNECTERR);
            s_disconnect_ind(abortReason);
            setState(STATE_NULL);
        } else if (state == STATE_SUSPENDED) {
            // ignore
        } else if (state == STATE_RESUMING) {
            if (abortReason == ABORT_DISCONNECT) {
                abortAllMethods(ABORT_DISCONNECT);
                s_disconnect_ind(ABORT_DISCONNECT);
                setState(STATE_NULL);
            } else {
                abortAllMethods(ABORT_SUSPEND);
                s_suspend_ind(abortReason);
                setState(STATE_SUSPENDED);
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////// WSP pseudo events
    public void disconnect() {
        socket.close();

        if (state == STATE_CONNECTING) {
            wtp.abort(ABORT_DISCONNECT);
            abortAllMethods(ABORT_DISCONNECT);
            s_disconnect_ind(ABORT_DISCONNECT);
            setState(STATE_NULL);
        } else if (state == STATE_CONNECTED) {
            abortAllMethods(ABORT_DISCONNECT);
            abortAllPushes(ABORT_DISCONNECT);
            s_disconnect_ind(ABORT_DISCONNECT);
            setState(STATE_NULL);
        } else if (state == STATE_SUSPENDED) {
            s_disconnect_ind(ABORT_DISCONNECT);
            setState(STATE_NULL);
        } else if (state == STATE_RESUMING) {
            wtp.abort(ABORT_DISCONNECT);
            abortAllMethods(ABORT_DISCONNECT);
            s_disconnect_ind(ABORT_DISCONNECT);
            setState(STATE_NULL);
        }
    }

    public void suspend() {
        if (state == STATE_CONNECTING) {
            wtp.abort(ABORT_DISCONNECT);
            abortAllMethods(ABORT_DISCONNECT);
            s_disconnect_ind(ABORT_SUSPEND);
            setState(STATE_NULL);
        } else if (state == STATE_CONNECTED) {
            // resume facility enabled!
            abortAllMethods(ABORT_SUSPEND);
            abortAllPushes(ABORT_SUSPEND);
            s_disconnect_ind(ABORT_SUSPEND);
            setState(STATE_SUSPENDED);
        } else if (state == STATE_RESUMING) {
            wtp.abort(ABORT_SUSPEND);
            abortAllMethods(ABORT_SUSPEND);
            s_suspend_ind(ABORT_SUSPEND);
            setState(STATE_SUSPENDED);
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////// HELPERS
    private void abortAllMethods(short reason) {
        while( methods.size() > 0 ) {
            ((CWSPMethodManager) methods.elementAt(0)).abort(reason);
            methods.remove(0);
        }
    }

    private void abortAllPushes(short reason) {
        while( pushes.size() > 0 ) {
            ((CWSPPushManager) pushes.elementAt(0)).abort(reason);
            pushes.remove(0);
        }
    }

    /**
     * sets the state of the state machine
     * @param state the state to set
     */
    private void setState(short state) {
        if (logger.isDebugEnabled()) {
            logger.debug(states[this.state] + " >>> " + states[state]);
        }

        this.state = state;
    }

    public short getState() {
        return state;
    }

    public int getMRU() {
        return MRU;
    }

    public boolean isSuspended() {
        return isSuspended;
    }

    public short getSuspendedCode() {
        return suspendCode;
    }

    public boolean isDisonnected() {
        return isDisconnected;
    }

    public short getDisconnectCode() {
        return disconnectCode;
    }

    public CWTPSocket getWTPSocket() {
        return socket;
    }
}
