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
 * This Class represents an Acknowledgement PDU.
 * According to the WTP specification in section 8 this PDU
 * can be encoded into a byte array.
 * <br><br>
 * 3 bytes are used. You can not input payload of upper layers.
 * To encode the PDU call toByteArray().
 * <br><br>
 * There are to ways of creation: <b>Either</b> you construct a Object
 * manually by calling the constructor <b>or</b> you use CWTPFactory
 * to decode a byte Array.
 */
public class CWTPAck extends CWTPPDU {
    /**
     * Tve/Tok Flag
     * responder -> initiator: "do you have outstanding transactions with this TID?"
     * initiator -> resopnder: "I have outstanding transaction(s) with this TID!"
     */
    private boolean tve_tok;

    /**
     * @param TID the Transaction ID according to the spec
     */
    public CWTPAck(int TID) {
        super(TID, PDU_TYPE_ACK);
        tve_tok = false;
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
        result.write(tve_tok);
        result.write(RES1);
        result.write(RID);
        result.write(TID, 16);

        if (payload != null) {
            result.write(payload);
        }

        //    logger.debug(result.toString());
        return result.toByteArray();
    }

    /////////////////////////////////////////////////////////////////////////////
    //////////////////////////////// GET/SET ////////////////////////////////////
    public boolean getTve_tok() {
        return tve_tok;
    }

    public void setTve_tok(boolean tve_tok) {
        this.tve_tok = tve_tok;
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
        "tve_tok:  " + tve_tok + System.getProperty("line.separator") +
        "RES1:     " + RES1 + System.getProperty("line.separator") +
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
        CWTPAck a = new CWTPAck((short) 7576);
        a.toByteArray();
        System.out.println(a.toString());
    }
}
