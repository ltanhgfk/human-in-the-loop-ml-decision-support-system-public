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

import net.sourceforge.jwap.util.*;


public class CWSPConnect extends CWSPPDU {
    private short version;

    public CWSPConnect() {
        super();
        version = 0x10;
        pduType = PDU_TYPE_CONNECT;
        capabilities.setClientSDUSize(307200);//65535//
        capabilities.setServerSDUSize(307200);//65535//
    }

    public byte[] toByteArray() {
        BitArrayOutputStream result = new BitArrayOutputStream();
        result.write(pduType, 8);
        result.write(version, 8);

        byte[] cap = capabilities.getBytes();
        byte[] head = null;
        if( headers != null ) {
             head = headers.getBytes();
        }
        result.writeUintVar(cap.length);
        result.writeUintVar(head == null?0:head.length);
        result.write(cap);
        if( head != null ) {
            result.write(head);
        }

        return result.toByteArray();
    }

    public short getVersion() {
        return version;
    }

    public void setVersion(short version) {
        this.version = version;
    }

    /**
     * Test method
     */
    public static void main(String[] args) {
        CWSPConnect c = new CWSPConnect();
        c.getCapabilities().setClientSDUSize(261120);
        c.getCapabilities().setServerSDUSize(261120);
        c.getCapabilities().setProtocolOption(CWSPCapabilities.OPTION_PUSH_FACILITY,
            true);
        c.getCapabilities().setMethodMOR(2);
        c.getCapabilities().setPushMOR(1);

        byte[] a = c.toByteArray();
        System.out.println(BitArrayOutputStream.getBitString(a));
    }
}
