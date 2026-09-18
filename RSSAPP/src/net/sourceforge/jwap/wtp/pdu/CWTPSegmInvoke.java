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

import net.sourceforge.jwap.util.*;


public class CWTPSegmInvoke extends CWTPPDU {
    private short sequenceNo;

    public CWTPSegmInvoke(short sequenceNo, byte[] payload, int tid) {
        super(payload, tid, PDU_TYPE_SEGM_INVOKE);
        this.sequenceNo = sequenceNo;
    }

    public byte[] toByteArray() {
        BitArrayOutputStream result = new BitArrayOutputStream();
        result.write(CON);
        result.write(pduType, 4);
        result.write(GTR);
        result.write(TTR);
        result.write(RID);
        result.write(TID, 16);
        result.write(sequenceNo, 8);
        result.write(payload);

        //    logger.debug(result.toString());
        return result.toByteArray();
    }

    public static void main(String[] args) {
        // CWTPSegmInvoke CWTPSegmInvoke1 = new CWTPSegmInvoke();
    }
}
