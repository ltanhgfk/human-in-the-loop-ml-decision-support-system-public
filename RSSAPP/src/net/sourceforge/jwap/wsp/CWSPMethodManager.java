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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import net.sourceforge.jwap.util.Logger;
import net.sourceforge.jwap.util.Utils;
import net.sourceforge.jwap.wsp.pdu.CWSPDataFragment;
import net.sourceforge.jwap.wsp.pdu.CWSPHeaders;
import net.sourceforge.jwap.wsp.pdu.CWSPPDU;
import net.sourceforge.jwap.wsp.pdu.CWSPReply;
import net.sourceforge.jwap.wsp.pdu.EWSPCorruptPDUException;
import net.sourceforge.jwap.wtp.CWTPEvent;
import net.sourceforge.jwap.wtp.EWTPAbortedException;
import net.sourceforge.jwap.wtp.IWTPTransaction;
import net.sourceforge.jwap.wtp.IWTPUpperLayer;



public class CWSPMethodManager implements IWTPUpperLayer {
    // Method states
    private static final short STATE_NULL = 0;
    private static final short STATE_REQUESTING = 1;
    private static final short STATE_WAITING = 2;
    private static final short STATE_WAITING2 = 3;
    private static final short STATE_COMPLETING = 4;
    private static final String[] states = {
        "STATE_NULL", "STATE_REQUESTING", "STATE_WAITING", "STATE_WAITING2",
        "STATE_COMPLETING"
    };
    static Logger logger = Logger.getLogger(CWSPMethodManager.class);
    private byte version = 0;
    private short state = STATE_NULL;
    private CWSPSession session;
    private IWTPTransaction wtp;
    private IWSPUpperLayer upperlayer;

    /**
     *  In case we have segmented data we will buffer the data in this Stream
     */
    private ByteArrayOutputStream waitingResultContent = new ByteArrayOutputStream();

    /**
     *  In case we have segmented data we will prepare this result - waiting for the whole data
     */
    private CWSPResult waitingResult = new CWSPResult(this, null, null, null);

    ////////////////////////////////////////////////////////////////// CONSTRUCTOR
    
    /**
     * s-methodInvoke.req implemented in CWSPSession calls this constructor
     * @param initpdu
     * @param session
     */
    public CWSPMethodManager(CWSPPDU initpdu, CWSPSession session,
        IWSPUpperLayer upperLayer) {
        if ((upperLayer != null) && upperLayer instanceof IWSPUpperLayer2) {
            this.version = 2;
        } else {
            this.version = 1;
        }

        this.session = session;
        this.upperlayer = upperLayer;
        setState(STATE_REQUESTING);

        synchronized(this) {
            CWTPEvent initPacket = new CWTPEvent(initpdu.toByteArray(),
                    CWTPEvent.TR_INVOKE_REQ);
            wtp = session.getWTPSocket().tr_invoke(this, initPacket, false,
                    IWTPTransaction.CLASS_TYPE_2);
        }   
    }

    //////////////////////////////////////////////// from upper layer: S-*.req/res

    /**
     * s-methodInvokeData.req
     */
    public synchronized void s_methodInvokeData(byte[] payload, boolean moreData) {
        if (state == STATE_REQUESTING) {
            CWTPEvent initPacket = new CWTPEvent(payload,
                    CWTPEvent.TR_INVOKEDATA_RES);
            initPacket.setMoreData(moreData);

            /** @todo if request headers provided and MoreData == false*/
        }
    }

    /**
     * s-methodresultdata.res
     */
    public synchronized void s_methodResultData() {
        if (state == STATE_COMPLETING) {
            setState(STATE_NULL);

            CWTPEvent initPacket = new CWTPEvent(new byte[0],
                    CWTPEvent.TR_RESULTDATA_RES);

            /** @todo initPacket.setExitInfo(ackHeaders); **/
            try {
                wtp.process(initPacket);
            } catch (EWTPAbortedException e) {
                logger.warn("Event processing aborted",e);
                abort(CWSPSession.ABORT_PROTOERR);
            }
        }
    }

    /**
     * S-MethodAbort.req
     * @return S-MethodAbort.ind
     */
    public synchronized boolean s_methodAbort() {
        if ((session.getState() == CWSPSession.STATE_CONNECTING) ||
                (session.getState() == CWSPSession.STATE_CONNECTED) ||
                (session.getState() == CWSPSession.STATE_RESUMING)) {
            if ((state == STATE_REQUESTING) || (state == STATE_WAITING) ||
                    (state == STATE_WAITING2) || (state == STATE_COMPLETING)) {
                setState(STATE_NULL);
                wtp.abort(CWSPSession.ABORT_PEERREQ);
                s_methodAbort_ind(CWSPSession.ABORT_PEERREQ);

                return true;
            }
        }

        return false;
    }

    /**
     * s-methodresult.res
     * @param data
     * @param contentType
     * @param exitInfoAckHeaders
     */
    public synchronized void s_methodResult(CWSPHeaders exitInfoAckHeaders) {
        if (session.getState() == CWSPSession.STATE_CONNECTED) {
            if (state == STATE_COMPLETING) {
                setState(STATE_NULL);

                CWTPEvent initPacket = new CWTPEvent(new byte[0],
                        CWTPEvent.TR_RESULT_RES);

                /** @todo initPacket.setExitInfo(ackHeaders); **/
                try {
                    wtp.process(initPacket);
                } catch (EWTPAbortedException e) {
                    logger.warn("Event processing aborted", e);
                    abort(CWSPSession.ABORT_PROTOERR);
                }
            }
        }
        // Remove the method from the session queue
        session.removeMethod(this);
    }

    ////////////////////////////////////////////////// to upper Layer: S-*.ind/cnf
    public synchronized void s_methodAbort_ind(short reason) {
        logger.debug("s_methodAbort_ind - Reason: " + reason);
    }

    public synchronized void s_methodInvokeData_cnf() {
        logger.debug("s_methodInvokeData_cnf");
    }

    public synchronized void s_methodInvokeData_ind(CWSPHeaders headers,
        byte[] payload, String contentType, boolean moreData) {
        logger.debug("s_methodInvokeData_cnf");
    }

    public synchronized void s_methodInvoke_cnf() {
        logger.debug("s_methodInvoke_cnf");
    }

    public synchronized void s_methodResult_ind(CWSPReply pdu, boolean moreData) {
        CWSPHeaders headers = pdu.getHeaders();
        byte[] payload = pdu.getPayload();
        String contentType =  pdu.getContentType();
        
        if (logger.isDebugEnabled()) {
            logger.debug("s_methodResult_ind");
            logger.debug("  Data Length: " + payload.length);
            logger.debug("  contentType: " + contentType);
            logger.debug("  moreData   : " + moreData);
            logger.debug("  version    : " + version);

            if (payload.length > 0) {
                logger.debug(" --- PAYLOAD ---\n"+Utils.hexDump(payload));
            }
        }

        // Old interface...
        upperlayer.s_methodResult_ind(payload, contentType, moreData);

        if (version == 2) {
            try {
                waitingResultContent.write(payload);

                if (!moreData) {
                    waitingResultContent.flush();
                    CWSPResult result = new CWSPResult(this, headers, contentType, waitingResultContent.toByteArray());
                    // Convert the WSP status into a HTTP status code
                    int status = pdu.getStatus();
                    if( status == 0x50 ) {
                        status = 416;
                    } else if( status == 0x51 ) {
                        status = 417;
                    } else {
                        if( status >= 0x60 ) {
                            status -= 0x10;
                        }
                        status = (status >> 4)*100+(status&0x0f);
                    }
                    result.setStatus(status);
                    ((IWSPUpperLayer2) upperlayer).s_methodResult_ind(result);
                    waitingResultContent.reset();
                } else {
                    waitingResult = new CWSPResult(this, headers, contentType, null);
                }
            } catch (IOException e) {
                logger.error("Oops", e);
            }
        }
    }

    public synchronized void s_methodResultData_ind(byte[] responseBody,
        boolean moreData) {
        if (version == 2) {
            try {
                waitingResultContent.write(responseBody);

                if (!moreData) {
                    waitingResultContent.flush();
                    waitingResult.setPayload(waitingResultContent.toByteArray());
                    ((IWSPUpperLayer2) upperlayer).s_methodResult_ind(waitingResult);
                    waitingResult = new CWSPResult(this, null, null, null);
                    waitingResultContent.reset();
                }
            } catch (IOException e) {
                logger.warn("IOException in s_methodResultData_ind", e);
            }
        }
    }

    /////////////////////////////////////////// from the layer below: TR-*.ind/cnf
    public void tr_process(CWTPEvent p) {
        logger.debug(CWTPEvent.types[p.getType()] + " in " + states[state]);

        switch (p.getType()) {
        case 0x03: //--------------------------------------- TR_INVOKE_CNF

            if ((session.getState() == CWSPSession.STATE_CONNECTING) ||
                    (session.getState() == CWSPSession.STATE_RESUMING)) {
                abort(CWSPSession.ABORT_PROTOERR);
            } else if (session.getState() == CWSPSession.STATE_CONNECTED) {
                if (state == STATE_REQUESTING) {
                    if (!p.getMoreData()) {
                        // entire method completed
                        setState(STATE_WAITING);
                        s_methodInvoke_cnf();
                    } else {
                        setState(STATE_REQUESTING);
                        s_methodInvoke_cnf();
                    }
                }
            }

            break;

        case 0x05: //--------------------------------------- TR_RESULT_IND

            if ((session.getState() == CWSPSession.STATE_CONNECTING) ||
                    (session.getState() == CWSPSession.STATE_RESUMING)) {
                abort(CWSPSession.ABORT_PROTOERR);
            }
            else if (session.getState() == CWSPSession.STATE_CONNECTED) {
                if (state == STATE_WAITING) {
                    try {
                        CWSPPDU pdu = CWSPPDU.getPDU(p.getUserData());

                        /*
                        if (p.getUserData().length > session.getMRU()){
                            logger.warn("over!");
                          setState(STATE_NULL);
                          wtp.abort(session.ABORT_MRUEXCEEDED);
                          s_methodAbort_ind(session.ABORT_MRUEXCEEDED);
                        }
                        else
                        */
                        if (pdu.getType() == CWSPPDU.PDU_TYPE_REPLY) {
                            CWSPReply pdu2 = (CWSPReply) pdu;

                            if (p.getMoreData()) {
                                setState(STATE_WAITING2);

                                CWTPEvent initPacket = new CWTPEvent(new byte[0],
                                        CWTPEvent.TR_RESULT_RES);

                                try {
                                    wtp.process(initPacket);
                                } catch (EWTPAbortedException e) {
                                    logger.warn("Event processing aborted", e);
                                    abort(CWSPSession.ABORT_PROTOERR);
                                }

                                s_methodResult_ind(pdu2, p.getMoreData());
                            } else {
                                setState(STATE_COMPLETING);
                                s_methodResult_ind(pdu2, p.getMoreData());
                            }
                        } else {
                            setState(STATE_NULL);
                            wtp.abort(CWSPSession.ABORT_PROTOERR);
                            s_methodAbort_ind(CWSPSession.ABORT_PROTOERR);
                        }
                    } catch (EWSPCorruptPDUException e) {
                        logger.warn("Corrupt PDU", e);
                    }
                }
            }

            break;

        case 0x0B: //--------------------------------------- TR_INVOKEDATA_CNF

            if (state == STATE_REQUESTING) {
                if (!p.getMoreData()) {
                    // entire method completed
                    setState(STATE_WAITING);
                    s_methodInvokeData_cnf();
                } else {
                    setState(STATE_REQUESTING);
                    s_methodInvokeData_cnf();
                }
            }

            break;

        case 0x0D: //--------------------------------------- TR_RESULTDATA_IND

            if (state == STATE_WAITING2) {
                // if SDU size > MRU:
                if (p.getUserData().length > session.getMRU()) {
                    setState(STATE_NULL);
                    wtp.abort(CWSPSession.ABORT_MRUEXCEEDED);
                    s_methodAbort_ind(CWSPSession.ABORT_MRUEXCEEDED);
                } else {
                    // if data fragment PDU
                    try {
                        CWSPPDU pdu = CWSPPDU.getPDU(p.getUserData());

                        if ((pdu.getType() == CWSPPDU.PDU_TYPE_DATA_FRAGMENT) &&
                                (p.getFrameBoundary() == true) &&
                                (p.getMoreData() == false)) {
                            setState(STATE_COMPLETING);

                            CWSPDataFragment pdu3 = (CWSPDataFragment) pdu;
                            s_methodInvokeData_ind(pdu3.getHeaders(),
                                pdu3.getPayload(), pdu3.getContentType(),
                                p.getMoreData());
                        }
                    }
                    // if response body
                     catch (EWSPCorruptPDUException e) {
                        if (p.getMoreData()) {
                            setState(STATE_WAITING2);

                            CWTPEvent initPacket = new CWTPEvent(new byte[0],
                                    CWTPEvent.TR_RESULTDATA_RES);

                            try {
                                wtp.process(initPacket);
                            } catch (EWTPAbortedException e2) {
                                logger.warn("Event processing aborted", e2);
                                abort(CWSPSession.ABORT_PROTOERR);
                            }

                            s_methodResultData_ind(p.getUserData(),
                                p.getMoreData());
                        } else if (p.getFrameBoundary() == false) {
                            setState(STATE_COMPLETING);
                            s_methodResultData_ind(p.getUserData(),
                                p.getMoreData());
                        }
                    }
                }
            }

            break;
        } // end of switch
    }

    /**
     * tr-abort.ind
     * used by *WTP* layer to abort
     * @param abortReason
     */
    public void tr_abort(short abortReason) {
        if ((session.getState() == CWSPSession.STATE_CONNECTING) ||
                (session.getState() == CWSPSession.STATE_CONNECTED) ||
                (session.getState() == CWSPSession.STATE_RESUMING)) {
            if (state != STATE_NULL) {
                setState(STATE_NULL);

                if (abortReason == CWSPSession.ABORT_DISCONNECT) {
                    session.disconnect();
                } else if (abortReason == CWSPSession.ABORT_SUSPEND) {
                    session.suspend();
                } else {
                    s_methodAbort_ind(abortReason);
                }
            }
        }
    }

    //////////////////////////////////////////////////////////////// pseudo events

    /**
     * used by CWSPSession - *WSP* layer
     * @param reason
     */
    public void abort(short reason) {
        if (state != STATE_NULL) {
            setState(STATE_NULL);
            wtp.abort(reason);
            s_methodAbort_ind(reason);
        }
    }

    ////////////////////////////////////////////////////////////////////// HELPERS
    private void setState(short state) {
        logger.debug(states[this.state] + " >>> " + states[state]);
        this.state = state;
    }
}
