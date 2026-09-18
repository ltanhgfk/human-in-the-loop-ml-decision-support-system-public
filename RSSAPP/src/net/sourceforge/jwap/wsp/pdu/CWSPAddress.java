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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

import net.sourceforge.jwap.util.BitArrayOutputStream;
import net.sourceforge.jwap.util.Logger;
import net.sourceforge.jwap.wsp.CWSPSocketAddress;



public class CWSPAddress {
    /**
     * bearer Types according to WAP-259-WDP-20010614-a
     * only two of them implemented here!
     */
    public static final int BEARER_TYPE_GSM_CSD = 0x0A;
    public static final int BEARER_TYPE_GSM_GPRS = 0x0B;
    public boolean bearerTypeIncluded; // 1st bit
    public boolean portNumberIncluded; // 2nd bit
    public long addressLen; // 6bits
    public long bearerType; // uint8
    public long portNumber; // uint16
    public short bearerAddressToUse1; // multiple octets
    public short bearerAddressToUse2;
    public short bearerAddressToUse3;
    public short bearerAddressToUse4;

    private static Logger logger = Logger.getLogger(CWSPAddress.class);

    public CWSPAddress(boolean bearerTypeIncluded, boolean portNumberIncluded,
        short bearerType, int portNumber, String bearerAddressToUse_IP) {
        this.bearerTypeIncluded = bearerTypeIncluded;
        this.portNumberIncluded = portNumberIncluded;
        this.addressLen = 4;
        this.bearerType = bearerType;
        this.portNumber = portNumber;

        StringTokenizer t = new StringTokenizer(bearerAddressToUse_IP, ".",
                false);
        bearerAddressToUse1 = Short.parseShort(t.nextToken());
        bearerAddressToUse2 = Short.parseShort(t.nextToken());
        bearerAddressToUse3 = Short.parseShort(t.nextToken());
        bearerAddressToUse4 = Short.parseShort(t.nextToken());
    }

    /**
     * get the encoded byte array of the address
     */
    public byte[] getBytes() {
        BitArrayOutputStream out = new BitArrayOutputStream();
        out.write(bearerTypeIncluded);
        out.write(portNumberIncluded);
        out.write(addressLen, 6);
        out.write(bearerType, 8);
        out.write(portNumber, 16);
        out.write(bearerAddressToUse1, 8);
        out.write(bearerAddressToUse2, 8);
        out.write(bearerAddressToUse3, 8);
        out.write(bearerAddressToUse4, 8);

        return out.toByteArray();
    }

    /**
     * copmpares all fields of the address
     */
    public boolean equals(Object a) {
        CWSPAddress b = (CWSPAddress) a;

        if ((this.bearerTypeIncluded == b.bearerTypeIncluded) &&
                (this.portNumberIncluded == b.portNumberIncluded) &&
                (this.addressLen == b.addressLen) &&
                (this.bearerType == b.bearerType) &&
                (this.portNumber == b.portNumber) &&
                (this.bearerAddressToUse1 == b.bearerAddressToUse1) &&
                (this.bearerAddressToUse2 == b.bearerAddressToUse2) &&
                (this.bearerAddressToUse3 == b.bearerAddressToUse3) &&
                (this.bearerAddressToUse4 == b.bearerAddressToUse4)) {
            return true;
        } else {
            return false;
        }
    }

    /////////////////////////////////////////////////////////////////////////////
    //////////////////////////////// GET/SET ////////////////////////////////////
    public boolean getBearerTypeIncluded() {
        return bearerTypeIncluded;
    }

    public boolean getPortNumberIncluded() {
        return portNumberIncluded;
    }

    public long getAddressLen() {
        return addressLen;
    }

    public long getBearerType() {
        return bearerType;
    }

    public long getPortNumber() {
        return portNumber;
    }

    public InetAddress getInetAddress() {
        InetAddress ret = null;
        String addr = getBearerAddressToUse();
        try {
            ret = InetAddress.getByName(addr);
        } catch (UnknownHostException e) {
            logger.warn(addr+": Unable to convert to InetAddress", e); 
        }
        return ret;
    }

    public CWSPSocketAddress getSocketAddress() {
        InetAddress addr = getInetAddress();
        CWSPSocketAddress ret = null;
        if( addr != null ) {
            ret = new CWSPSocketAddress(addr, (int)portNumber);
        } 
        return ret;
    }
    
    public String getBearerAddressToUse() {
        StringBuffer sb = new StringBuffer();
        sb.append(bearerAddressToUse1).append(".")
            .append(bearerAddressToUse2).append(".")
            .append(bearerAddressToUse3).append(".")
            .append(bearerAddressToUse4);
        return sb.toString();
    }
}
