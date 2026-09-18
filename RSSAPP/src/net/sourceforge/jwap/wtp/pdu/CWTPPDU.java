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
package net.sourceforge.jwap.wtp.pdu;

import java.util.Vector;

import net.sourceforge.jwap.util.BitArrayInputStream;
import net.sourceforge.jwap.util.Logger;
import net.sourceforge.jwap.wtp.CWTPInitiator;


public abstract class CWTPPDU {
    // This is a class, that helps us with decoding bits
    private static BitArrayInputStream bin = new BitArrayInputStream();
    protected static Logger logger = Logger.getLogger(CWTPPDU.class);
    public static final byte PDU_TYPE_INVOKE = 0x01;
    public static final byte PDU_TYPE_RESULT = 0x02;
    public static final byte PDU_TYPE_ACK = 0x03;
    public static final byte PDU_TYPE_ABORT = 0x04;
    public static final byte PDU_TYPE_SEGM_INVOKE = 0x05;
    public static final byte PDU_TYPE_SEGM_RESULT = 0x06;
    public static final byte PDU_TYPE_NEG_ACK = 0x07;
    public static final String[] types = {
        "", "PDU_TYPE_INVOKE", "PDU_TYPE_RESULT", "PDU_TYPE_ACK",
        "PDU_TYPE_ABORT", "PDU_Type_SEGM_INVOKE", "PDU_TYPE_SEGM_RESULT",
        "PDU_TYPE_NEG_ACK"
    };

    /////////////////////////////////////////////////////////////////////////////
    ///////////////////////// COMMON HEADER FIELDS //////////////////////////////

    /**
     * 1 TPIs (Transport Information Item) in variable part
     * 0 NO TPIs (Transport Information Item) in variable part <---
     *
     * btw: TPIs:
     * 0x00 Error
     * 0x01 Info
     * 0x02 Option
     * 0x03 Packet Seqence Number (PSN) (N/A if segment. and re-ass. is implemented)
     * 0x04 SDU Boundary (N/A if extended segment. and re-ass. is implemented)
     * 0x05 Frame boundary (N/A if extended segment. and re-ass. is implemented)
     */
    protected boolean CON; // 1 bit

    /**
     * Group Trailer and Transmission Trailer Flag
     * GTR/TTR
     *  0   0  Not last packet
     *  0   1  Last packet
     *  1   0  Last packet of packet group
     *  1   1  segmentation and reassambly NOT supported
     */
    protected boolean GTR; // 1 bit
    protected boolean TTR; // 1 bit

    /**
     * @todo Packet Sequence number
     * The position of the packet in a segmented message
     * NOT available if GTR/TTR = 11 <---
     */

    // packSeqNo

    /**
     * PDUType, 4 bit
     * 0x01 Invoke
     * 0x02 Result
     * 0x03 Ack
     * 0x04 Abort
     */
    protected byte pduType;

    /**
     * Reserved
     * 0 <---
     */
    protected boolean RES1;
    protected boolean RES2;

    /**
     * re-transmission indicator (RID)
     * 0 <---
     */
    protected boolean RID;

    /**
     * Transaction Identifier (TID)
     * uint16
     */
    protected int TID;
    protected byte[] payload;

    /**
     * Maximum size of payload in bearer (UDP)
     */
    protected final int MTU = 1024; //556 mid, empirisch 1408 oder 912?//moi sua tu 800=>2000

    /**
     * CWTPSegmInvokes (if payload > MTU)
     */
    protected Vector segments = new Vector();

    /////////////////////////////////////////////////////////////////////////////
    //////////////////////////////// CONSTRUCTORS ///////////////////////////////
    public CWTPPDU(byte[] payload, int TID, byte type) {
        this(TID, type);
        this.payload = payload;
    }

    public CWTPPDU(int TID, byte type) {
        CON = false;
        GTR = false; // when SAR is not supported by remote, we need to set this true! Niko
        TTR = true;
        RES1 = false;
        RES2 = false;
        RID = false;
        this.TID = TID;
        pduType = type;

        //logger.debug("PDU constructed: " + types[type]);
    }

    /**
     * decodes bytes in CWTPPDU objects
     *
     * @param length the actual number of bytes that are used in
     *               the byte buffer.
     */
    public static CWTPPDU decode(byte[] bytes, int length)
        throws EWTPCorruptPDUException {
        //the PDU returned
        CWTPPDU end = null;

        //decode PDU type
        byte pduType = bin.getByte(bytes[0], 1, 4);
        int tid = 0;
        byte[] payload = null;

        switch (pduType) {
        case CWTPPDU.PDU_TYPE_ABORT:
            tid = bin.getShort(bytes[1], bytes[2], 0, 16);

            CWTPAbort abort = new CWTPAbort(tid);
            abort.setCON(bin.getBoolean(bytes[0], 0));
            abort.setAbortType(bin.getByte(bytes[0], 5, 3));
            abort.setAbortReason(bin.getByte(bytes[3], 0, 8));
            end = abort;

            break;

        case CWTPPDU.PDU_TYPE_ACK:
            tid = bin.getShort(bytes[1], bytes[2], 0, 16);

            CWTPAck ack = new CWTPAck(tid);
            ack.setTve_tok(bin.getBoolean(bytes[0], 5));
            ack.setRES1(bin.getBoolean(bytes[0], 6));
            ack.setRID(bin.getBoolean(bytes[0], 7));
            end = ack;

            break;

        case CWTPPDU.PDU_TYPE_INVOKE:
            tid = bin.getShort(bytes[1], bytes[2], 0, 16);
            payload = new byte[length - 4];

            for (int i = 0; i < (length - 4); i++) {
                payload[i] = bytes[i + 4];
            }

            CWTPInvoke invoke = new CWTPInvoke(payload, tid,
                    CWTPInitiator.CLASS_TYPE_0);
            invoke.setCON(bin.getBoolean(bytes[0], 0));
            invoke.setGTR(bin.getBoolean(bytes[0], 5));
            invoke.setTTR(bin.getBoolean(bytes[0], 6));
            invoke.setRID(bin.getBoolean(bytes[0], 7));
            invoke.setVersion(bin.getByte(bytes[3], 0, 2));
            invoke.setTIDnew(bin.getBoolean(bytes[3], 2));
            invoke.setU_P(bin.getBoolean(bytes[3], 3));
            invoke.setRES1(bin.getBoolean(bytes[3], 4));
            invoke.setRES2(bin.getBoolean(bytes[3], 5));
            invoke.setTCL(bin.getByte(bytes[3], 6, 2));
            end = invoke;

            break;

        case CWTPPDU.PDU_TYPE_RESULT:
            tid = bin.getShort(bytes[1], bytes[2], 0, 16);
            payload = new byte[length - 3];

            for (int i = 0; i < (length - 3); i++) {
                payload[i] = bytes[i + 3];
            }

            CWTPResult result = new CWTPResult(payload, tid);
            result.setCON(bin.getBoolean(bytes[0], 0));
            result.setGTR(bin.getBoolean(bytes[0], 5));
            result.setTTR(bin.getBoolean(bytes[0], 6));
            result.setRID(bin.getBoolean(bytes[0], 7));
            end = result;

            break;

        case CWTPPDU.PDU_TYPE_SEGM_RESULT:
            tid = bin.getShort(bytes[1], bytes[2], 0, 16);
            payload = new byte[length - 4];

            System.arraycopy(bytes,4,payload,0,payload.length);

            CWTPSegmResult res = new CWTPSegmResult((short) (0xff & bytes[3]),
                    payload, tid);
            res.setCON(bin.getBoolean(bytes[0], 0));
            res.setGTR(bin.getBoolean(bytes[0], 5));
            res.setTTR(bin.getBoolean(bytes[0], 6));
            res.setRID(bin.getBoolean(bytes[0], 7));
            end = res;

            break;

        default:

            // unnown PDU types
            logger.debug("Warning! Unknown PDU Type " + pduType);
            throw new EWTPCorruptPDUException(
                "Unknown PDU-Type! By the way: *is* it WTP?");
        }

        return end;
    }

    /////////////////////////////////////////////////////////////////////////////
    //////////////////////////////// ABSTRACT METHOD ////////////////////////////

    /**
     * Encodes the PDU according to WAP-224-WTP-20010710-a
     * @return encoded bytes
     */
    public abstract byte[] toByteArray();

    /////////////////////////////////////////////////////////////////////////////
    //////////////////////////////// SET/GET ////////////////////////////////////
    public boolean getCON() {
        return CON;
    }

    public void setCON(boolean CON) {
        this.CON = CON;
    }

    public boolean getGTR() {
        return GTR;
    }

    public void setGTR(boolean GTR) {
        this.GTR = GTR;
    }

    public boolean getTTR() {
        return TTR;
    }

    public void setTTR(boolean TTR) {
        this.TTR = TTR;
    }

    public byte getPDUType() {
        return pduType;
    }

    public void setPDUType(byte PDUType) {
        this.pduType = PDUType;
    }

    public boolean getRES1() {
        return RES1;
    }

    public void setRES1(boolean RES) {
        this.RES1 = RES;
    }

    public boolean getRES2() {
        return RES2;
    }

    public void setRES2(boolean RES) {
        this.RES2 = RES;
    }

    public boolean getRID() {
        return RID;
    }

    public void setRID(boolean RID) {
        this.RID = RID;
    }

    public int getTID() {
        return TID;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public Vector getSegments() {
        return segments;
    }

    public void setSegments(Vector segments) {
        this.segments = segments;
    }
}
