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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import net.sourceforge.jwap.util.Logger;
import net.sourceforge.jwap.wtp.pdu.CWTPAbort;
import net.sourceforge.jwap.wtp.pdu.CWTPAck;
import net.sourceforge.jwap.wtp.pdu.CWTPInvoke;
import net.sourceforge.jwap.wtp.pdu.CWTPPDU;
import net.sourceforge.jwap.wtp.pdu.CWTPResult;
import net.sourceforge.jwap.wtp.pdu.CWTPSegmResult;
import net.sourceforge.jwap.wtp.pdu.EWTPCorruptPDUException;



/**
 * This class implements a WTP state machine of one transaction according
 * to the WTP specification by the WAP Forum. It uses CWTPManagement to send
 * the PDUs. CWTPManagement receives PDUs from the remote, decodes them and
 * calls #process(CWTPPDU pdu) of the corresponding tranaction (this class).
 * <p>
 * To be informed of thrown service primitives by this layer, you should
 * implement the interface IWTPUpperLayer and give it with the constructor.
 * </p><p>
 * To construct a service primitive to be processed by this WTP-socket,
 * use objects of the class CWTPEvent, that implement service primitives.
 * </p><p>
 * To Use a whole WAP Stack besides you need the WSP layer, implemented
 * in package mmsjua.wap.wsp.
 * </p><p>
 * <b>Notice</b>, that development is actually in progress.
 * Most features are implemented but only for Initiator, not for a responder!
 * (for a definition of these terms refer to the spec, section 3.2.)
 * </p><p>
 * <b>Section descriptions</b> used in this class refer to
 * WTP Specification WAP-224-WTP-20010710-a
 * by the <a href="http://www.wapforum.org">WAP Forum</a>
 * </p>
 *
 */
public class CWTPInitiator implements IWTPTransaction {
    private static final byte STATE_NULL = 0x00;
    private static final byte STATE_RESULT_WAIT = 0x01;
    private static final byte STATE_RESULT_RESP_WAIT = 0x02;
    private static final byte STATE_WAIT_TIMEOUT = 0x03;
    private static final byte STATE_WAIT_ACK = 0x04;
    private static final String[] states = {
        "NULL", "RESULT WAIT", "RESULT RESP WAIT", "WAIT TIMEOUT", "WAIT ACK"
    };
    protected static Logger logger = Logger.getLogger(CWTPInitiator.class);
    public static final int RCR_MAX = 3;
    public static final int AEC_MAX = 3;

    /**
     * 9.4.3
     * counter to generate unique TIDs
     * uint16
     */
    private static int genTID = -1;
    private byte state = 0x00;

    // which session does this transaction belong to?
    private IWTPUpperLayer upperLayer;

    // used to send and receive
    private CWTPSocket wtpSocket;

    // is this transaction aborted?
    private boolean aborted = false;
    private short abortCode;

    /**
     * 5.3.1.7
     * Class Type 1, 2 or 3
     */
    private byte classType;

    /**
     * 9.4.1
     * ack interval
     * this sets a bound for the amount of time to wait before sending an acknowledgement.
     */
    private javax.swing.Timer a_timer;
    private int a = 5000;

    /**
     * 9.4.1
     * retry interval
     * This sets a bound for the amount of time to wait before re-transmitting a PDU.
     */
    private javax.swing.Timer r_timer;
    private int r = 10000;

    /**
     * 9.4.1
     * wait timeout interval (only class 2 initiator and class 1 responder)
     * This sets a bound for the amount of time to wait before state information
     * about a transaction is released.
     */
    private javax.swing.Timer w_timer;
    private int w = 5000;

    /**
     * 7.14.3
     * While segmentation each segment has to be acknowledged. This timer
     * waits before resending a segment.
     */
    private javax.swing.Timer s_timer;
    private int s = 5000;
    private CWTPPDU segment;
    private int segment_sended = 1;
    private final int segment_sended_max = 4;

    /**
     * 9.4.2
     * re-transmission counter
     * acknowledgement expireation counter
     */
    private int rcr = 0;
    private int aec = 0;

    /**
     * uint16
     */
    private int sendTID;
    private int rcvTID;
    private int lastTID;

    /**
     * recently sent PDU - hold it for retransmission
     */
    private CWTPPDU sentPDU;
    private boolean holdOn = false;
    private boolean uack = false;

    /**
     * next segment to send while in STATE_WAIT_ACK
     */
    private int segmentIndex = 0;
    private ByteArrayOutputStream segdata;
    private int segend;
    private short seglast;
    private int segTID;

    //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    //XXXXXXXXXXXXXXXXXXXXXXX CONSTRUCTOR XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

    /**
     * Constructs a CWTPSocket using a DatagramSocket (UDP).
     * Even implementing all parameters belonging to according to
     * TR-Invoke service primitive (section 5.3.1),
     * which are not temporary for one transaction.
     * This socket can be used several times by service primitives.
     * After all it has to be closed by calling <code>close()</code>
     *
     * @see #close(short)
     * @param to destination address (section 5.3.1.3)
     * @param port destination port (section 5.3.1.4)
     * @param ackType Ack Type (section 5.3.1.5)
     * @param classType Class Type (0, 1 or 2) (section 5.3.1.8)
     * @throws IllegalArgumentException
     * @throws SocketException
     */
    public CWTPInitiator(CWTPSocket wtp_Socket, IWTPUpperLayer upper_Layer,
        CWTPEvent initPacket, boolean ackType, byte classType) {
        init(wtp_Socket, upper_Layer, ackType, classType);

        sendTID = CWTPInitiator.generateNewTID();
        rcvTID = sendTID - 32768; // rcvTID = sendTID XOR 0x8000

        // process the init service primitive
        try {
            process(initPacket);
        } catch (EWTPAbortedException e) {
            logger.warn("Event processing aborted", e);
        }
    }

    private void init(CWTPSocket wtp_Socket, IWTPUpperLayer upper_Layer,
        boolean ackType, byte classType) throws IllegalArgumentException {
        this.upperLayer = upper_Layer;
        this.wtpSocket = wtp_Socket;
        uack = ackType;
        setClassType(classType);

        // initialize timer and add actionListener
        // see declaration of a_timer above
        a_timer = new javax.swing.Timer(a,
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        a_timer.stop();

                        if (state == STATE_RESULT_RESP_WAIT) {
                            // check acknowledgement counter
                            if (!uack) { // if user acknowledgement is turned off

                                // unfortunately I do not remember why I have coded this branch like this.
                                logger.error("acknowledgement timer: exceeded " +
                                    (aec + 1) + " time(s) --> cancel");

                                CWTPAck send = new CWTPAck(sendTID);
                                wtpSocket.send(send);
                                w_timer.restart();
                                setState(STATE_WAIT_TIMEOUT);
                            } else if (aec < AEC_MAX) {
                                if(logger.isDebugEnabled()) {
                                    logger.debug("acknowledgement timer: exceeded " +
                                        (aec + 1) + " time(s).");
                                }
                                aec++;
                                a_timer.restart();
                                setState(STATE_RESULT_RESP_WAIT);
                            } else if (aec == AEC_MAX) {
                                //abort if acknowledgement counter exceeds the maximum
                                logger.error("acknowledgement timer: exceeded " +
                                    (aec + 1) + " time(s) --> cancel");

                                CWTPAbort send = new CWTPAbort(sendTID);
                                send.setAbortReason(CWTPAbort.ABORT_REASON_NORESPONSE);
                                wtpSocket.send(send);
                                close(CWTPAbort.ABORT_REASON_NORESPONSE);
                                aec = 0;
                                setState(STATE_NULL);
                            }
                        }
                    }
                });

        // see declaration of r_timer above
        r_timer = new javax.swing.Timer(r,
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        r_timer.stop();

                        if (state == STATE_RESULT_WAIT) {
                            // check retransmission counter
                            if (rcr < RCR_MAX) {
                                /** @todo Ack(TIDok) already sent? Page 53 */
                                if(logger.isDebugEnabled()) {
                                    logger.debug("retransmission timer: exceeded " +
                                    (rcr + 1) + " time(s) --> re-send");
                                }
                                rcr++;
                                r_timer.restart();

                                // re-send recent Invoke
                                sentPDU.setRID(true);
                                wtpSocket.send(sentPDU);
                                setState(STATE_RESULT_WAIT);
                            } else if (rcr == RCR_MAX) {
                                logger.error("retransmission timer: exceeded " +
                                    (rcr + 1) + " time(s) --> cancel");

                                // abort
                                close(CWTPAbort.ABORT_REASON_UNKNOWN);
                                upperLayer.tr_abort(CWTPAbort.ABORT_REASON_UNKNOWN);
                                rcr = 0;
                                setState(STATE_NULL);
                            }
                        }
                    }
                });

        // see declaration of w_timer above
        w_timer = new javax.swing.Timer(w,
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        w_timer.stop();

                        if (state == STATE_WAIT_TIMEOUT) {
                            logger.debug("wait timeout");
                            setState(STATE_NULL);
                            close((short) 0x00);
                        }
                    }
                });

        // see declaration of w_timer above
        s_timer = new javax.swing.Timer(s,
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        s_timer.stop();

                        if (segment_sended > segment_sended_max) {
                            if(logger.isDebugEnabled()) {
                                logger.debug("wait_ack timeout (" + segment_sended +
                                    "), abort.");
                            }
                            segment_sended = 1;
                            setState(STATE_NULL);
                            close((short) 0x00);
                        } else {
                            if (state == STATE_WAIT_ACK) {
                                if(logger.isDebugEnabled()) {
                                    logger.debug("wait_ack timeout (" +
                                        segment_sended + "), re-sending segment.");
                                }
                                segment.setRID(true);
                                wtpSocket.send(segment);
                                segment_sended++;
                                s_timer.restart();
                            }
                        }
                    }
                });
    }

    //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

    /**
     * Invoked by the run()-Method of the Management-Entity CWTPManagement.
     * Processes given protocol data units
     * according to state machine described in section 9.5
     * <b>Notice:</b> Only WTP Initiator is implemented!
     *
     * @param pdu the pdu to be processed in the state machine
     */
    public synchronized void process(CWTPPDU pdu) throws EWTPAbortedException {
        if (aborted) {
            throw new EWTPAbortedException(abortCode);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("" + sendTID + ": " + CWTPPDU.types[pdu.getPDUType()] +
                " in " + states[state] + " class " + classType + " holdOn " +
                holdOn);
        }

        switch (state) {
        ///////////////////// NULL //////////////////////////////////////////////
        case 0x00:

            // because only initiator is implemented,
            // we do not need to react on pdu-events in NULL state
            break;

        ///////////////////// RESULT WAIT ///////////////////////////////////////
        case 0x01:

            // RcvAck in RESULT WAIT
            if (pdu.getPDUType() == CWTPPDU.PDU_TYPE_ACK) {
                if (((CWTPAck) pdu).getTve_tok() &&
                        ((classType == 1) || (classType == 2)) &&
                        (rcr < RCR_MAX)) {
                    CWTPAck send = new CWTPAck(((CWTPAck) pdu).getTID() &
                            0x7fff);
                    send.setTve_tok(true);
                    wtpSocket.send(send);
                    rcr++;
                    r_timer.restart();
                    setState(STATE_RESULT_WAIT);
                } else if (((CWTPAck) pdu).getTve_tok() &&
                        ((classType == 1) || (classType == 2))) {
                    setState(STATE_RESULT_WAIT);
                } else if ((classType == 2) && !holdOn) {
                    r_timer.stop();
                    upperLayer.tr_process(new CWTPEvent(pdu.getPayload(),
                            CWTPEvent.TR_INVOKE_CNF));
                    this.holdOn = true;
                    setState(CWTPInitiator.STATE_RESULT_WAIT);
                } else if ((classType == 2) && holdOn) {
                    setState(STATE_RESULT_WAIT);
                } else if (classType == 1) {
                    r_timer.stop();
                    upperLayer.tr_process(new CWTPEvent(pdu.getPayload(),
                            CWTPEvent.TR_INVOKE_CNF));
                    setState(STATE_NULL);
                }
            }
            // RcvAbort in RESULT WAIT
            else if (pdu.getPDUType() == CWTPPDU.PDU_TYPE_ABORT) {
                short abortReason = ((CWTPAbort) pdu).getAbortReason();
                close(abortReason);
                upperLayer.tr_abort(abortReason);
                setState(STATE_NULL);
            }
            // RcvResult in RESULT WAIT
            else if (pdu.getPDUType() == CWTPPDU.PDU_TYPE_RESULT && classType == 2) {
              CWTPResult res = (CWTPResult) pdu;
              byte nextState=STATE_RESULT_RESP_WAIT;
          
              // Stop timer
              r_timer.stop();
          
              // Check for segmentation indication
              if( !res.getTTR() )
              {
                logger.debug("Not last packet");
                segend = 0; seglast = 0;
                segdata = new  ByteArrayOutputStream(64*1024); // 64kbyte segs
                segTID = pdu.getTID();
                try {
                  segdata.write(res.getPayload());
                } catch (IOException e) {
                  logger.warn("Exception writing to ByteArrayOutputStream",e);
                }
                nextState=STATE_RESULT_WAIT; // We will wait for more to come
              }
              setState(nextState);

              // if !holdOn generate TR_Invoke.cnf
              if(!holdOn) {
                upperLayer.tr_process(
                  new CWTPEvent(pdu.getPayload(), CWTPEvent.TR_INVOKE_CNF));
              }
          
              if( !res.getTTR() && res.getGTR() ) {
                byte payload[] = new byte[2];
                CWTPAck ack = new CWTPAck(sendTID);
                payload[0] = (0x03 << 3) | 0x01; // psn, one byte length
                payload[1] = 0; // Implicit sequence number 0
                ack.setCON(true); // headers follow ;)
                ack.setPayload(payload);
                if(logger.isDebugEnabled()) {
                    logger.debug("sending ack:" + ack.toString());
                }
                wtpSocket.send(ack);
              }
              
              if( nextState != STATE_RESULT_WAIT ) { 
                // generate tr_result.ind
                upperLayer.tr_process(
                  new CWTPEvent(pdu.getPayload(), CWTPEvent.TR_RESULT_IND));
              }
              // restart ack timer
              a_timer.restart();
            } 
            else if (pdu.getPDUType() == CWTPPDU.PDU_TYPE_SEGM_RESULT) {
              logger.debug("Segmented result");
              boolean sendAck = false;
              boolean sendToUpper = false;
              CWTPSegmResult sres = (CWTPSegmResult) pdu;

              w_timer.restart();
              a_timer.restart();

              if (sres.getTID() != segTID) {
                logger.warn("Dumping out-of sync segment from outer space!");
                return;
              }
              if (seglast + 1 == sres.getPSN()) {
                // we are in line with stuff
                seglast = sres.getPSN();
                try {
                  segdata.write(sres.getPayload());
                } catch (IOException e) {
                  logger.warn("Exception writing to ByteArrayOutputStream",e);
                }
                if (sres.getGTR()) {
                  logger.debug("Last packet of packet group");
                  // ==> ttr must be 0  -> end of group
                  sendAck = true;
                } else if (sres.getTTR()) {
                  logger.debug("Last packet of message");
                  // EOF :)
                  sendAck = true;
                  sendToUpper = true;
                  setState(STATE_RESULT_RESP_WAIT);
                } //else not last packet...
              } else {
                logger.warn(
                  "LOST PACKET - last "
                    + seglast
                    + " current psn "
                    + sres.getPSN());
                // @todo: send NACK
              }
              if (sendAck) {
                byte payload[] = new byte[2];
                CWTPAck send = new CWTPAck(sendTID);
                payload[0] = (0x03 << 3) | 0x01; // psn, one byte length
                payload[1] = (byte) (0x7f & sres.getPSN());
                send.setCON(true); // headers follow ;)
                send.setPayload(payload);

                if(logger.isDebugEnabled()) {
                    logger.debug("sending ack:" + send.toString());
                }
                wtpSocket.send(send);
              }
              if (sendToUpper) {
                byte[] payload = segdata.toByteArray();
                upperLayer.tr_process(new CWTPEvent(payload, CWTPEvent.TR_RESULT_IND));
                segdata = null;
                segend = 0;
              }
            }
            break;

        ///////////////////// RESULT RESP WAIT //////////////////////////////////
        case 0x02:

            // RcvAbort in RESULT RESP WAIT
            if (pdu.getPDUType() == CWTPPDU.PDU_TYPE_ABORT) {
                short abortReason = ((CWTPAbort) pdu).getAbortReason();
                close(abortReason);
                upperLayer.tr_abort(abortReason);
                setState(STATE_NULL);
            }
            // RcvResult in RESULT RESP WAIT
            else if (pdu.getPDUType() == CWTPPDU.PDU_TYPE_RESULT) {
                logger.debug("blp");
                setState(STATE_RESULT_RESP_WAIT);

                // RcvSegmResult in RESULT RESP WAIT
            } else if (pdu.getPDUType() == CWTPPDU.PDU_TYPE_SEGM_RESULT) {
                logger.debug("XXXXXX blpseg");

                setState(STATE_RESULT_RESP_WAIT);
            }

            break;

        ///////////////////// WAIT TIMEOUT //////////////////////////////////////
        case 0x03:

            // RcvAbort in WAIT TIMEOUT
            if (pdu.getPDUType() == CWTPPDU.PDU_TYPE_ABORT) {
                short abortReason = ((CWTPAbort) pdu).getAbortReason();
                close(abortReason);
                upperLayer.tr_abort(abortReason);
                setState(STATE_NULL);
            }
            // RcvResult in WAIT TIMEOUT
            else if (pdu.getPDUType() == CWTPPDU.PDU_TYPE_RESULT) {
                if (pdu.getRID()) {
                    /** @todo input TPI exitinfo if available - page 54 **/
                    CWTPAck send = new CWTPAck(sendTID);
                    wtpSocket.send(send);
                    setState(STATE_WAIT_TIMEOUT);
                } else {
                    setState(STATE_WAIT_TIMEOUT);
                }
            }

            break;

        ///////////////////// WAIT ACK WHILE SEGMENTATION SENDING ////////////////
        case 0x04:

            // rcvAck in STATE_WAIT_ACK
            if (pdu.getPDUType() == CWTPPDU.PDU_TYPE_ACK) {
                //re-initialize re-sending counter
                s_timer.stop();
                segment_sended = 1;
                segment = null;

                // send next segment
                pdu = (CWTPPDU) (sentPDU.getSegments().elementAt(segmentIndex));
                wtpSocket.send(pdu);

                segmentIndex++;

                // last segment sent
                if (sentPDU.getSegments().size() == segmentIndex) {
                    segmentIndex = 0;

                    if ((classType == 1) | (classType == 2)) {
                        // start timer to re-send whole message
                        r_timer.restart();
                        setState(STATE_RESULT_WAIT);
                    } else if (classType == 0) {
                        setState(STATE_NULL);
                        close((short) 0x00);
                    }
                }
                // not last segment
                else {
                    // save segment for re-sending
                    segment = pdu;

                    // start timer to re-send segment
                    s_timer.restart();
                }
            }

            break;
        }
    }

    /**
     * Invoked by higher layers to process given service primitives
     * according to state machine described in section 9.5.<br>
     * <b>Notice:</b> Only WTP Initiator is implemented!
     *
     * @param p the Service Primitive to be processed
     */
    public synchronized void process(CWTPEvent p) throws EWTPAbortedException {
        if (aborted) {
            throw new EWTPAbortedException(abortCode);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("" + sendTID + ": " + CWTPEvent.types[p.getType()] +
                " in " + states[state]);
        }

        switch (state) {
        ///////////////////// STATE NULL /////////////////////////////////////////
        case 0x00:

            // TR-INVOKE.REQ
            // some things do do when receiving a TR-Invoke
            // were already done in the constructor - so please see there!
            if (p.getType() == CWTPEvent.TR_INVOKE_REQ) {
                if (((classType == 1) | (classType == 2)) && uack) {
                    CWTPInvoke send = new CWTPInvoke(p.getUserData(), sendTID,
                            classType);
                    wtpSocket.addTransaction(this);
                    wtpSocket.send(send);
                    sentPDU = send;
                    this.rcr = 0;
                    r_timer.restart();
                    setState(STATE_RESULT_WAIT);
                } else if (((classType == 1) | (classType == 2)) && !uack) {
                    CWTPInvoke send = new CWTPInvoke(p.getUserData(), sendTID,
                            classType);
                    wtpSocket.addTransaction(this);
                    wtpSocket.send(send);
                    sentPDU = send;
                    this.rcr = 0;

                    // do we have to segmentate?
                    if (!send.getTTR()) {
                        setState(STATE_WAIT_ACK);
                    } else {
                        r_timer.restart();
                        setState(STATE_RESULT_WAIT);
                    }
                } else if (classType == 0) {
                    CWTPInvoke send = new CWTPInvoke(p.getUserData(), sendTID,
                            classType);
                    wtpSocket.addTransaction(this);
                    wtpSocket.send(send);

                    // do we have to segmentate?
                    if (!send.getTTR()) {
                        sentPDU = send;
                        setState(STATE_WAIT_ACK);
                    } else {
                        setState(STATE_NULL);
                        close((short) 0x00);
                    }
                }
            }
             //end TR-INVOKE.REQ

            break;

        //////////////////// STATE RESULT WAIT ///////////////////////////////////
        case 0x01:
            break;

        ///////////////////// STATE RESULT RESP WAIT /////////////////////////////
        case 0x02:

            // TR-Result.res
            if (p.getType() == CWTPEvent.TR_RESULT_RES) {
                if (p.getExitInfo().length != 0) {
                    CWTPAck send = new CWTPAck(sendTID);

                    /** @todo input TPI exitinfo into "send" - page 54 top **/
                    wtpSocket.send(send);

                    w_timer.restart();
                    setState(STATE_WAIT_TIMEOUT);
                } else {
                    CWTPAck send = new CWTPAck(sendTID);
                    wtpSocket.send(send);

                    w_timer.restart();
                    setState(STATE_WAIT_TIMEOUT);
                }
            }
             // end TR-Result.res

            break;

        ///////////////////// STATE WAIT TIMEOUT /////////////////////////////////
        case 0x03:
            break;
        }
    }

    public void process(EWTPCorruptPDUException e) {
        if (state != STATE_NULL) {
            CWTPAbort send = new CWTPAbort(e.getTid());
            send.setAbortReason(CWTPAbort.ABORT_REASON_PROTOERR);
            wtpSocket.send(send);
            close(CWTPAbort.ABORT_REASON_PROTOERR);
            upperLayer.tr_abort(CWTPAbort.ABORT_REASON_PROTOERR);
            setState(STATE_NULL);
        }
    }

    /**
     * use this method to invoke a TR-ABORT.REQ by the upper Layer
     */
    public void abort() {
        abort(CWTPAbort.ABORT_REASON_UNKNOWN);
    }

    /**
     * use this method to invoke a TR-ABORT.REQ by the upper Layer
     */
    public void abort(short abortReason) {
        // TR-ABORT.REQ
        if ((state == 0x01) || (state == 0x02) || (state == 0x03)) {
            if(logger.isDebugEnabled()) {
                logger.debug("" + sendTID + ": TR-ABORT.REQ Reason: " +
                    abortReason);
            }

            CWTPAbort send = new CWTPAbort(sendTID);
            send.setAbortReason(abortReason);
            wtpSocket.send(send);
            close(abortReason);
        }
    }

    public void close(short reasonCode) {
        abortCode = reasonCode;
        aborted = true;
        r_timer.stop();
        w_timer.stop();
        a_timer.stop();
        setState(STATE_NULL);
        wtpSocket.removeTransaction(this);
    }

    //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    //XXXXXXXXXXXXXXXXXXXXXX SET/GET XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

    /*
      public boolean getAckType(){
        return uack;
      }
      public void setAckType(boolean ackType){
        uack = ackType;
      }


      public IWTPUpperLayer getUpperLayer(){
        return upperLayer;
      }
    */
    public int getTID() {
        return sendTID;
    }

    public void setClassType(byte classType) throws IllegalArgumentException {
        if ((classType == 1) | (classType == 2) | (classType == 0)) {
            this.classType = classType;

            return;
        } else {
            throw new IllegalArgumentException("Class Type has to be 1, 2 or 3");
        }
    }

    public byte getClassType() {
        return classType;
    }

    private void setState(byte state) {
        //if (debug){
        //  log.log(0, this, "" + sendTID + ": " + states[this.state] + " >>> " + states[state]);
        //}
        this.state = state;
    }

    public boolean isAborted() {
        return aborted;
    }

    public short getAbortCode() {
        return abortCode;
    }

    //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

    /**
     * Static method, that generates a new (unique) Transaction ID
     * by incrementing genTID
     *
     * @return A new unique Transaction ID
     */
    private static synchronized int generateNewTID() {
        if (genTID == -1) {
            Random r = new Random();
            genTID = Math.abs(r.nextInt() % 255);
        }

        int result = genTID;

        if (genTID == 255) {
            genTID = 0;
        } else {
            ++genTID;
        }

        return result;
    }
}
