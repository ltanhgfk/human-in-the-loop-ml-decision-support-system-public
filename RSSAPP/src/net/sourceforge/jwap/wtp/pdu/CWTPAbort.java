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

import net.sourceforge.jwap.util.BitArrayOutputStream;


/**
 * This Class represents an Abort PDU.
 * According to the WTP specification in section 8 this PDU
 * can be encoded into a byte array.
 * <br><br>
 * 4 bytes are used. You can not input payload of upper layers.
 * To encode the PDU call toByteArray().
 * <br><br>
 * There are to ways of creation: <b>Either</b> you construct a Object
 * manually by calling the constructor <b>or</b> you use CWTPFactory
 * to decode a byte Array.
 */
public class CWTPAbort extends CWTPPDU {
    public static final short ABORT_TYPE_PROVIDER = 0x00;
    public static final short ABORT_TYPE_USER = 0x01;
    public static final short ABORT_REASON_UNKNOWN = 0x00;
    public static final short ABORT_REASON_PROTOERR = 0x01;
    public static final short ABORT_REASON_INVALIDTID = 0x02;
    public static final short ABORT_REASON_NOTIMPLEMENTEDCL2 = 0x03;
    public static final short ABORT_REASON_NOTIMPLEMENTEDSAR = 0x04;
    public static final short ABORT_REASON_NOTIMPLEMENTEDUACK = 0x05;
    public static final short ABORT_REASON_WTPVERSIONONE = 0x07;
    public static final short ABORT_REASON_CPTEMPEXCEEDED = 0x07;
    public static final short ABORT_REASON_NORESPONSE = 0x08;
    public static final short ABORT_REASON_MESSAGETOOLARGE = 0x09;
    public static final short ABORT_REASON_NOTIMPLEMENTEDESAR = 0x0A;

    /**
     * Abort Type
     * use static constants in CWTPAbort
     */
    private long abortType;

    /**
     * Abort Reason
     * use static constants in CWTPAbort
     */
    private long abortReason;

    /**
     * Defaults:<br>
     * abortType = ABORT_TYPE_USER;<br>
     * abortReason = ABORT_REASON_UNKNOWN;
     *
     * @param TID the Transaction ID according to the spec.
     */
    public CWTPAbort(int TID) {
        super(TID, PDU_TYPE_ABORT);
        abortType = ABORT_TYPE_USER;
        abortReason = ABORT_REASON_UNKNOWN;
    }

    /**
     * encodes the PDU according to the WTP spec
     *
     * @return encoded bytes
     */
    public byte[] toByteArray() {
        BitArrayOutputStream result = new BitArrayOutputStream();
        result.write(CON);
        result.write(pduType, 4);
        result.write(abortType, 3);
        result.write(TID, 16);
        result.write(abortReason, 8);

        //    logger.debug(result.toString());
        return result.toByteArray();
    }

    /////////////////////////////////////////////////////////////////////////////
    //////////////////////////////// SET/GET ////////////////////////////////////
    public long getAbortType() {
        return abortType;
    }

    public void setAbortType(long abortType) {
        this.abortType = abortType;
    }

    public short getAbortReason() {
        return (short) abortReason;
    }

    public void setAbortReason(short abortReason) {
        this.abortReason = abortReason;
    }

    /////////////////////////////////////////////////////////////////////////////
    //////////////////////////////// HELPERS ////////////////////////////////////

    /**
     * constructs a string representation of the object
     * including all fields.
     *
     * @return The constructed String with debug information
     */
    public String toString() {
        String result = "";
        result += ("CON:         " + CON +
        System.getProperty("line.separator") + "PDUType:     " + pduType +
        System.getProperty("line.separator") + "AbortType:   " + abortType +
        System.getProperty("line.separator") + "AbortReason: " + abortReason +
        System.getProperty("line.separator") + "TID:         " + TID +
        System.getProperty("line.separator") +
        System.getProperty("line.separator") + "ENCODED:" +
        System.getProperty("line.separator") +
        BitArrayOutputStream.getBitString(toByteArray()));

        return result;
    }

    /**
     * Test method
     * @param args ignored
     */
    public static void main(String[] args) {
        CWTPAbort a = new CWTPAbort((short) 7576);
        a.toByteArray();
        System.out.println(a.toString());
    }
}
