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
 * This Class represents an Invoke PDU.
 * According to the WTP specification in section 8 this PDU
 * can be encoded into a byte array.
 * <br><br>
 * The first 4 bytes of the PDU are used for the WTP Layer.
 * After this the payload follows - e.g. the bytes of a upper Layer.
 * To encode the PDU call toByteArray().
 * <br><br>
 * There are to ways of creation: <b>Either</b> you construct a Object
 * manually by calling the constructor <b>or</b> you use CWTPFactory
 * to decode a byte Array.
 */
public class CWTPInvoke extends CWTPPDU {
    /**
     * Transaction Class (TCL)
     * 2 bit
     */
    private byte TCL;

    /**
     * TIDnew Flag
     * 1 bit
     */
    private boolean TIDnew;

    /**
     * Version
     * 2 bit
     */
    private byte version;

    /**
     * U/P flag
     * do you want user acknowledgement?
     * 1 bit
     */
    private boolean U_P;

    /**
     * @param payload The Bytes belonging to the layer above
     * @param TID the Transaction ID according to the spec
     */
    public CWTPInvoke(byte[] payload, int TID, byte transactionclass) {
        super(payload, TID, PDU_TYPE_INVOKE);
        this.TTR = true;

        // do we have to segment the payload?
        if (payload.length > MTU) {
            this.TTR = false;
            this.GTR = true;
            this.payload = new byte[MTU];

            for (int i = 0; i < MTU; i++) {
                this.payload[i] = payload[i];
            }

            // count the segments we need
            int countSegments = (int) (Math.ceil(((double) (payload.length)) / MTU));

            /*System.out.println("Payload " + payload.length
                                + " countSegments:" + countSegments
                                + " (payload.length/MTU):" + (((double)(payload.length))/MTU)
                                + " (Math.ceil(payload.length/MTU)):" + (Math.ceil(payload.length/MTU)));;
            */

            // for each payload segment
            for (int i = 1; i < countSegments; i++) {
                //length of this payload segment
                int tmpLength = (payload.length - (i * MTU));

                if (tmpLength > MTU) {
                    tmpLength = MTU;
                }

                // read payload
                byte[] tmpPayload = new byte[tmpLength];

                for (int m = 0; m < tmpLength; m++) {
                    tmpPayload[m] = payload[(i * MTU) + m];
                }

                //System.out.println("Segment no " + i + ": from byte " + (i*MTU)
                //                   + " till byte " + (i*MTU+tmpLength-1));
                CWTPSegmInvoke tmpSegment = new CWTPSegmInvoke((short) i,
                        tmpPayload, this.TID);
                tmpSegment.setGTR(true);
                tmpSegment.setTTR(false);

                // is this the last segment?
                if (i == (countSegments - 1)) {
                    tmpSegment.setTTR(true);
                    tmpSegment.setGTR(false);
                }

                // add Segment to vector
                segments.addElement(tmpSegment);
            }
        }

        version = 0x00;
        TCL = transactionclass;
        U_P = true;

        //if (TID == 0){
        //  TIDnew = true;

        /**@todo TIDnew auf true setzen, wenn neue TID wieder 0 **/

        //}
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
        result.write(GTR);
        result.write(TTR);
        result.write(RID);
        result.write(TID, 16);
        result.write(version, 2);
        result.write(TIDnew);
        result.write(U_P);
        result.write(RES1);
        result.write(RES2);
        result.write(TCL, 2);
        result.write(payload);

        //    logger.debug(result.toString());
        return result.toByteArray();
    }

    /////////////////////////////////////////////////////////////////////////////
    //////////////////////////////// SET/GET ////////////////////////////////////
    public byte getTCL() {
        return TCL;
    }

    public void setTCL(byte TCL) {
        this.TCL = TCL;
    }

    public boolean getTIDnew() {
        return TIDnew;
    }

    public void setTIDnew(boolean TIDnew) {
        this.TIDnew = TIDnew;
    }

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public boolean getU_P() {
        return U_P;
    }

    public void setU_P(boolean U_P) {
        this.U_P = U_P;
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
        "Version:  " + version + System.getProperty("line.separator") +
        "TIDnew:   " + TIDnew + System.getProperty("line.separator") +
        "U/P:      " + U_P + System.getProperty("line.separator") +
        "RES1:     " + RES1 + System.getProperty("line.separator") +
        "RES2:     " + RES2 + System.getProperty("line.separator") +
        "TCL:      " + TCL + System.getProperty("line.separator") +
        System.getProperty("line.separator") + "ENCODED:" +
        System.getProperty("line.separator") +
        BitArrayOutputStream.getBitString(toByteArray()));

        return result;
    }
}
