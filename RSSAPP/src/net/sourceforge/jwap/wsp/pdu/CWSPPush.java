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
package net.sourceforge.jwap.wsp.pdu;

import net.sourceforge.jwap.util.BitArrayOutputStream;


public class CWSPPush extends CWSPPDU {
    private String contentType;

    public CWSPPush() {
        pduType = PDU_TYPE_PUSH;
        this.payload = null;
        this.contentType = "application/unknown";
    }

    public CWSPPush(byte[] data, String contentType) {
        pduType = PDU_TYPE_PUSH;
        this.payload = data;
        this.contentType = contentType;
    }

    public byte[] toByteArray() {
        BitArrayOutputStream result = new BitArrayOutputStream();
        result.write(TID, 8);
        result.write(pduType, 8);

        //--------------------------------//
        byte[] head = headers.getBytes();
        byte[] ctype = contentType.getBytes();
        result.writeUintVar(head.length + ctype.length + 1);
        result.write(ctype);
        result.write(0x00, 8);
        result.write(head);
        result.write(payload);

        //--------------------------------//
        return result.toByteArray();
    }
}
