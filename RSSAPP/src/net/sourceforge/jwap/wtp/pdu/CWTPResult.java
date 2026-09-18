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
 * This Class represents an Result PDU.
 * According to the WTP specification in section 8 this PDU
 * can be encoded into a byte array.
 * <br><br>
 * The first 3 bytes of the PDU are used for the WTP Layer.
 * After this the payload follows - e.g. the bytes of a upper Layer.
 * To encode the PDU call toByteArray().
 * <br><br>
 * There are to ways of creation: <b>Either</b> you construct a Object
 * manually by calling the constructor <b>or</b> you use CWTPFactory
 * to decode a byte Array.
 */
public class CWTPResult extends CWTPPDU {
    /**
     * @param payload The Bytes belonging to the layer above
     * @param TID the Transaction ID according to the spec
     */
    public CWTPResult(byte[] payload, int TID) {
        super(payload, TID, PDU_TYPE_RESULT);
    }

    /**
     * Encodes the PDU according to the WTP spec
     *
     * @return encoded bytes
     */
    public byte[] toByteArray() {
        BitArrayOutputStream result = new BitArrayOutputStream();
        result.write(CON);
        result.write(pduType, 4);
        result.write(GTR);
        result.write(TTR);
        result.write(RID);
        result.write(TID, 16);
        result.write(payload);

        return result.toByteArray();
    }

    /////////////////////////////////////////////////////////////////////////////
    //////////////////////////////// HELPERS ////////////////////////////////////

    /**
     * constructs a string representation of the object
     * invluding all fields.
     *
     * @return The constructed String with debug information
     */
    public String toString() {
        String result = "";
        result += ("CON:      " + CON + System.getProperty("line.separator") +
        "pduType:  " + pduType + System.getProperty("line.separator") +
        "GTR:      " + GTR + System.getProperty("line.separator") +
        "TTR:      " + TTR + System.getProperty("line.separator") +
        "RID:      " + RID + System.getProperty("line.separator") +
        "TID:      " + TID + System.getProperty("line.separator") +
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
        /*CWTPResult a = new CWTPResult(
                          new CWSPReply(CWSPReply._200_OK_Success,
                                        new CMMessage("01795090731","niko@bender.net",true),
                                        "application/vnd.wap.mms-message").toByteArray(),
                          (short)242);
        System.out.println(a.toString());*/
    }
}
