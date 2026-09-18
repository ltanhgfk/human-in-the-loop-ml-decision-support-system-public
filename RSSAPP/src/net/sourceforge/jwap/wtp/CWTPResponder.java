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

import net.sourceforge.jwap.util.Logger;
import net.sourceforge.jwap.wtp.pdu.CWTPAbort;
import net.sourceforge.jwap.wtp.pdu.CWTPAck;
import net.sourceforge.jwap.wtp.pdu.CWTPInvoke;
import net.sourceforge.jwap.wtp.pdu.CWTPPDU;
import net.sourceforge.jwap.wtp.pdu.CWTPResult;
import net.sourceforge.jwap.wtp.pdu.EWTPCorruptPDUException;


public class CWTPResponder implements IWTPTransaction{

  private static final byte STATE_LISTEN = 0x00;
  private static final byte STATE_TIDOK_WAIT = 0x01;
  private static final byte STATE_INVOKE_RESP_WAIT = 0x02;
  private static final byte STATE_RESULT_WAIT = 0x03;
  private static final byte STATE_RESULT_RESP_WAIT = 0x04;
  private static final byte STATE_WAIT_TIMEOUT = 0x05;

  private static final String[] states = {"LISTEN",
                                          "TIDOK WAIT",
                                          "INVOKE RESP WAIT",
                                          "RESULT WAIT",
                                          "RESLUT RESP WAIT",
                                          "WAIT TIMEOUT"};

  static Logger logger = Logger.getLogger(CWTPResponder.class);

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
   * 9.4.2
   * re-transmission counter
   * acknowledgement expireation counter
   */
  private int rcr = 0;
  private int aec = 0;

  public static final int RCR_MAX = 3;
  public static final int AEC_MAX = 3;

  /**
   * 9.4.3
   * counter to generate unique TIDs
   * uint16
   */
  private static int genTID = 0;

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
  public CWTPResponder(CWTPSocket wtp_Socket,
                        IWTPUpperLayer upper_Layer,
                        CWTPInvoke initPDU,
                        boolean ackType,
                        byte classtype
                        ){
    init(wtp_Socket, upperLayer, ackType, classtype);

    rcvTID = initPDU.getTID();
    sendTID = rcvTID - 32768; // sendTID = rcvTID XOR 0x8000

    // process the invoke pdu
    try{
      process(initPDU);
    } catch (EWTPAbortedException e){
      logger.error("PDU processing aborted", e);
    }
  }

  private void init(CWTPSocket wtp_Socket,
                    IWTPUpperLayer upper_Layer,
                    boolean ackType,
                    byte classtype
                  ) throws IllegalArgumentException {
    this.upperLayer = upper_Layer;
    this.wtpSocket = wtp_Socket;
    wtpSocket.addTransaction(this);
    uack = ackType;
    setClassType(classtype);

    // initialize timer and add actionListener
    // see declaration of a_timer above
    a_timer = new javax.swing.Timer(a,
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          a_timer.stop();
          if (state == STATE_INVOKE_RESP_WAIT){
            // check acknowledgement counter
            if (aec < AEC_MAX){
              logger.debug("acknowledgement timer abgelaufen: aec < AEC_MAX");

              aec++;
              a_timer.restart();
              setState(STATE_RESULT_RESP_WAIT);
            } else if (aec == AEC_MAX){
              //abort if acknowledgement counter exceeds the maximum
              logger.debug("acknowledgement timer abgelaufen: aec == AEC_MAX");


              CWTPAbort send = new CWTPAbort(sendTID);
              send.setAbortReason(CWTPAbort.ABORT_REASON_NORESPONSE);
              wtpSocket.send(send);
              close(CWTPAbort.ABORT_REASON_NORESPONSE);
              aec = 0;
              upperLayer.tr_abort(CWTPAbort.ABORT_REASON_NORESPONSE);
              setState(STATE_LISTEN);
            } else if (!uack && classType == CLASS_TYPE_1){
              logger.debug("acknowledgement timer abgelaufen: uack == false classtype 1");

              CWTPAck send = new CWTPAck(sendTID);
              wtpSocket.send(send);
              w_timer.restart();
              setState(STATE_WAIT_TIMEOUT);
            } else if (!uack && classType == CLASS_TYPE_2){
              logger.debug("acknowledgement timer abgelaufen: uack == false classtype 2");

              CWTPAck ack = new CWTPAck(sendTID);
              wtpSocket.send(ack);
              setState(STATE_RESULT_WAIT);
            }
          }
          else if (state == STATE_RESULT_WAIT){
            logger.debug("acknowledgement timer abgelaufen.");

            CWTPAck ack = new CWTPAck(sendTID);
            wtpSocket.send(ack);
            setState(STATE_RESULT_WAIT);
          }
        }
      }
    );

    // see declaration of r_timer above
    r_timer = new javax.swing.Timer(r,
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          r_timer.stop();
          if (state == STATE_RESULT_RESP_WAIT){
            // check retransmission counter
            if (rcr < RCR_MAX){
              logger.debug("retransmission timer "+ rcr
                            + " mal abgelaufen. Re-sending Result.");

              rcr++;
              r_timer.restart();
              // re-send recent Result
              wtpSocket.send(sentPDU);
              setState(STATE_RESULT_RESP_WAIT);
            } else if (rcr == RCR_MAX){
              logger.debug("retransmission timer " + rcr
                            + " mal abgelaufen. Abbruch!");

              // abort
              close(CWTPAbort.ABORT_REASON_UNKNOWN);
              upperLayer.tr_abort(CWTPAbort.ABORT_REASON_UNKNOWN);
              rcr = 0;
              setState(STATE_LISTEN);
            }
          }
        }
      }
    );

    // see declaration of w_timer above
    w_timer = new javax.swing.Timer(w,
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          w_timer.stop();
          if (state == STATE_WAIT_TIMEOUT){
            logger.debug("wait timeout");

            setState(STATE_LISTEN);
            close((short)0x00);
          }
        }
      }
    );
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
  public synchronized void process(CWTPPDU pdu) throws EWTPAbortedException{
    if (aborted){
      throw new EWTPAbortedException(abortCode);
    }
    switch (state){
      ///////////////////// STATE LISTEN ///////////////////////////////////////
      case 0x00:
        // invoke pdu in state listen
        if(pdu.getPDUType() == CWTPPDU.PDU_TYPE_INVOKE){
          if (this.classType == IWTPTransaction.CLASS_TYPE_1 ||
              this.classType == IWTPTransaction.CLASS_TYPE_2){
            if (true/** @todo is tid ok? */){
              // TID OK
              a_timer.restart();
              CWTPEvent initPacket = new CWTPEvent(pdu.getPayload(),
                                                     CWTPEvent.TR_INVOKE_IND);
              initPacket.setTransaction(this);
              upperLayer.tr_process(initPacket);
              setState(STATE_INVOKE_RESP_WAIT);
            }
            else {
              // TID not OK
              CWTPAck ack = new CWTPAck(sendTID);
              ack.setTve_tok(true);
              wtpSocket.send(ack);
              setState(STATE_TIDOK_WAIT);
            }
          }
          else if (classType == CLASS_TYPE_0){
            CWTPEvent initPacket = new CWTPEvent(pdu.getPayload(),
                                                   CWTPEvent.TR_INVOKE_IND);
            initPacket.setTransaction(this);
            upperLayer.tr_process(initPacket);
            setState(STATE_LISTEN);
          }
        }// end invoke PDU in listen
        break;

      //////////////////// STATE TIDOK WAIT ////////////////////////////////////
      case 0x01:
        if (pdu.getPDUType() == CWTPPDU.PDU_TYPE_ACK &&
            (classType == CLASS_TYPE_1 || classType == CLASS_TYPE_2) &&
            (true) /** @todo TID ok? */){
          CWTPEvent initPacket = new CWTPEvent(pdu.getPayload(),
                                                     CWTPEvent.TR_INVOKE_IND);
          initPacket.setTransaction(this);
          upperLayer.tr_process(initPacket);
          a_timer.restart();
          setState(STATE_INVOKE_RESP_WAIT);
        }
        else if(pdu.getPDUType() == CWTPPDU.PDU_TYPE_ABORT){
          short abortReason = ((CWTPAbort)pdu).getAbortReason();
          close(abortReason);
          upperLayer.tr_abort(abortReason);
          setState(STATE_LISTEN);
        }
        else if(pdu.getPDUType() == CWTPPDU.PDU_TYPE_INVOKE){
          if(pdu.getRID()){
            CWTPAck ack = new CWTPAck(sendTID);
            ack.setTve_tok(true);
            wtpSocket.send(ack);
            setState(STATE_TIDOK_WAIT);
          }
          else {
            // ignore
            setState(STATE_TIDOK_WAIT);
          }
        }
        break;

      ///////////////////// STATE INVOKE RESP WAIT /////////////////////////////
      case 0x02:
        if (pdu.getPDUType() == CWTPPDU.PDU_TYPE_ABORT){
          short abortReason = ((CWTPAbort)pdu).getAbortReason();
          close(abortReason);
          upperLayer.tr_abort(abortReason);
          setState(STATE_LISTEN);
        }
        else if (pdu.getPDUType() == CWTPPDU.PDU_TYPE_INVOKE){
          // ignore
          setState(STATE_INVOKE_RESP_WAIT);
        }
        break;

      ///////////////////// STATE RESULT WAIT //////////////////////////////////
      case 0x03:
        if(pdu.getPDUType() == CWTPPDU.PDU_TYPE_INVOKE){
          if(pdu.getRID()){
            if(true/** @todo ack pdu already sent? */){
              // resend Ack PDU
              setState(STATE_RESULT_WAIT);
            }
            else{
              // ignore
              setState(STATE_RESULT_WAIT);
            }
          }
          else{
            // ignore
            setState(STATE_RESULT_WAIT);
          }
        }
        else if(pdu.getPDUType() == CWTPPDU.PDU_TYPE_ABORT){
          short abortReason = ((CWTPAbort)pdu).getAbortReason();
          close(abortReason);
          upperLayer.tr_abort(abortReason);
          setState(STATE_LISTEN);
        }
        break;
      ///////////////////// STATE RESULT RESP WAIT /////////////////////////////
      case 0x04:
        if(pdu.getPDUType() == CWTPPDU.PDU_TYPE_ABORT){
          short abortReason = ((CWTPAbort)pdu).getAbortReason();
          close(abortReason);
          upperLayer.tr_abort(abortReason);
          setState(STATE_LISTEN);
        }
        else if(pdu.getPDUType() == CWTPPDU.PDU_TYPE_ACK){
          CWTPAck pduAck = (CWTPAck)pdu;
          if(pduAck.getTve_tok()){
            // ignore
            setState(STATE_RESULT_RESP_WAIT);
          } else{
            CWTPEvent p = new CWTPEvent(pdu.getPayload(), CWTPEvent.TR_RESULT_CNF);
            upperLayer.tr_process(p);
            setState(STATE_LISTEN);
          }
        }
        break;
      ///////////////////// STATE WAIT TIMEOUT /////////////////////////////////
      case 0x05:
        if(pdu.getPDUType() == CWTPPDU.PDU_TYPE_INVOKE){
          CWTPInvoke invokepdu = (CWTPInvoke)pdu;
          if(invokepdu.getRID()){
            CWTPAck ackpdu = new CWTPAck(sendTID);
            /** @todo input exitInfo TPI if available seite 56 */
            wtpSocket.send(ackpdu);
            setState(STATE_WAIT_TIMEOUT);
          }
          else {
            // ignore
            setState(STATE_WAIT_TIMEOUT);
          }
        }
        else if(pdu.getPDUType() == CWTPPDU.PDU_TYPE_ACK){
          CWTPAck acki = (CWTPAck)pdu;
          if(acki.getTve_tok() &&
             acki.getRID()){
            CWTPAck ackpdu = new CWTPAck(sendTID);
            /** @todo input exitInfo TPI if available seite 56 */
            wtpSocket.send(ackpdu);
            setState(STATE_WAIT_TIMEOUT);
          }
        }
        else if(pdu.getPDUType() == CWTPPDU.PDU_TYPE_ABORT){
          short abortReason = ((CWTPAbort)pdu).getAbortReason();
          close(abortReason);
          upperLayer.tr_abort(abortReason);
          setState(STATE_LISTEN);
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
  public synchronized void process(CWTPEvent p) throws EWTPAbortedException{
    if (aborted){
      throw new EWTPAbortedException(abortCode);
    }
    switch (state){
      ///////////////////// STATE LISTEN ///////////////////////////////////////
      //case 0x00:
      //not possible
      //////////////////// STATE TIDOK WAIT ////////////////////////////////////
      //case 0x01:
      //not possible
      ///////////////////// STATE INVOKE RESP WAIT /////////////////////////////
      case 0x02:
        if(p.getType() == CWTPEvent.TR_INVOKE_RES){
          if (classType == CLASS_TYPE_1){
            /** @todo input exitinfo tpi if availabe */
            CWTPAck ack = new CWTPAck(sendTID);
            wtpSocket.send(ack);
            w_timer.restart();
            setState(STATE_WAIT_TIMEOUT);
          }
          else if(classType == CLASS_TYPE_2){
            a_timer.restart();
            setState(STATE_RESULT_WAIT);
          }
        }
        else if(p.getType() == CWTPEvent.TR_RESULT_REQ){
          rcr = 0;
          sentPDU = new CWTPResult(p.getUserData(), sendTID);
          wtpSocket.send(sentPDU);
          r_timer.restart();
          setState(STATE_RESULT_RESP_WAIT);
        }
        break;
      ///////////////////// STATE RESULT WAIT //////////////////////////////////
      case 0x03:
        if(p.getType() == CWTPEvent.TR_RESULT_REQ){
          rcr = 0;
          sentPDU = new CWTPResult(p.getUserData(), sendTID);
          wtpSocket.send(sentPDU);
          r_timer.restart();
          setState(STATE_RESULT_RESP_WAIT);
        }
        break;
      ///////////////////// STATE RESULT RESP WAIT /////////////////////////////
      //case 0x04:
      //not possible
      ///////////////////// STATE WAIT TIMEOUT /////////////////////////////////
      //case 0x05:
      //not possible
    }
  }

  /**
   * RcvErrorPDU
   * @param e exception thrown by CWTPFactory
   */
  public void process(EWTPCorruptPDUException e){
    CWTPAbort abort = new CWTPAbort(sendTID);
    abort.setAbortReason(CWTPAbort.ABORT_REASON_PROTOERR);
    wtpSocket.send(abort);
    if (state != STATE_LISTEN){
      if (state != STATE_TIDOK_WAIT){
        upperLayer.tr_abort(CWTPAbort.ABORT_REASON_PROTOERR);
      }
      close(CWTPAbort.ABORT_REASON_PROTOERR);
      setState(STATE_LISTEN);
    }
  }

  /**
   * use this method to invoke a TR-ABORT.REQ by the upper Layer
   */
  public void abort(){
    abort(CWTPAbort.ABORT_REASON_UNKNOWN);
  }

  /**
   * use this method to invoke a TR-ABORT.REQ by the upper Layer
   */
  public void abort(short abortReason){
    if (state == STATE_INVOKE_RESP_WAIT ||
        state == STATE_RESULT_WAIT ||
        state == STATE_RESULT_RESP_WAIT ||
        state == STATE_WAIT_TIMEOUT){
      close(abortReason);
      CWTPAbort abort = new CWTPAbort(sendTID);
      abort.setAbortReason(abortReason);
      setState(STATE_LISTEN);
    }
  }

  public void close(short reasonCode){
    abortCode = reasonCode;
    aborted = true;
    r_timer.stop();
    w_timer.stop();
    a_timer.stop();
    setState(STATE_LISTEN);
    wtpSocket.removeTransaction(this);
  }

  //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
  //XXXXXXXXXXXXXXXXXXXXXX SET/GET XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

  public int getTID(){
    return sendTID;
  }

  public void setClassType(byte classType) throws IllegalArgumentException{
    if (classType == 1 |classType == 2 | classType == 0){
      this.classType = classType;
      return;
    } else{
      throw new IllegalArgumentException("Class Type has to be 1, 2 or 3");
    }
  }

  public byte getClassType(){
    return classType;
  }

  private void setState(byte state){
    logger.debug(">>> WTP Responder: " + states[state] + "<<<");
  }

  public boolean isAborted(){
    return aborted;
  }

  public short getAbortCode(){
    return abortCode;
  }
}
