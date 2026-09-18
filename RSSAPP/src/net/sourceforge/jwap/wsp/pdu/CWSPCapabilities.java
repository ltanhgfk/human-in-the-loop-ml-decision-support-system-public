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

import java.util.*;


/**
 * Capabilities allow the client to negotiate characteristics
 * and extended behaviours of the protocol (see section 8.3 of the WSP spec.).
 */
public class CWSPCapabilities {
    /**
     * Table 37
     */
    public static final short TYPE_clientSDUSize = 0x00;
    public static final short TYPE_serverSDUSize = 0x01;
    public static final short TYPE_protocolOptions = 0x02;
    public static final short TYPE_methodMOR = 0x03;
    public static final short TYPE_pushMOR = 0x04;
    public static final short TYPE_extendedMethods = 0x05;
    public static final short TYPE_headerCodePages = 0x06;
    public static final short TYPE_aliases = 0x07;
    public static final short TYPE_clientMessageSize = 0x08;
    public static final short TYPE_serverMessageSize = 0x09;

    /**
     * section 8.3.2.3
     */
    public static final short OPTION_CONF_PUSH_FACILITY = 0x80;
    public static final short OPTION_PUSH_FACILITY = 0x40;
    public static final short OPTION_SESSION_RESUME_FACILITY = 0x20;
    public static final short OPTION_ACK_HEADERS = 0x10;
    public static final short OPTION_LARGE_DATA_TRANSFER = 0x08;

    /**
     * section 8.3.2.1
     * unintvar
     */
    private long clientSDUSize = 1400;
    private boolean bClientSDUSize = false;
    private long serverSDUSize = 1400;
    private boolean bServerSDUSize = false;

    /**
     * section 8.3.2.3
     * one octet
     */
    private long protocolOptions = 0x00;
    private boolean bProtocolOptions = false;

    /**
     * section 8.3.2.4
     * uint8
     */
    private long methodMOR = 1;
    private boolean bMethodMOR = false;
    private long pushMOR = 1;
    private boolean bPushMOR = false;

    /**
     * section 8.3.2.5
     * uint8
     * multiple octets
     */
    private Hashtable extendedMethods = new Hashtable();

    /**
     * section 8.3.2.6
     * uint8
     * multiple octets
     */
    private Hashtable headerCodePages = new Hashtable();

    /**
     * section 8.3.2.7
     * multiple octets
     * encoded as redirect address in sec. 8.2.2.3
     *
     * BearerTypeIncluded, PortNumberIncluded, AddressLength
     */
    Vector aliases = new Vector();

    /**
     * section 8.3.2.2
     * unintvar
     */
    private long clientMessageSize = 1400;
    private boolean bClientMessageSize = false;
    private long serverMessageSize = 1400;
    private boolean bServerMessageSize = false;

    /////////////////////////////////////////////////////////////////////////////
    //////////////////////////////// GET/SET ////////////////////////////////////
    public void setClientSDUSize(long clientSDUSize) {
        this.clientSDUSize = clientSDUSize;
        this.bClientSDUSize = true;
    }

    public long getClientSDUSize() {
        return clientSDUSize;
    }

    public void setServerSDUSize(long serverSDUSize) {
        this.serverSDUSize = serverSDUSize;
        this.bServerSDUSize = true;
    }

    public long getServerSDUSize() {
        return serverSDUSize;
    }

    /**
     * set a protocoloption
     * use constans in this class beginning with "OPTION_"
     */
    public void setProtocolOption(short option, boolean on_off) {
        if (on_off) {
            if (!getProtocolOption(option)) {
                this.bProtocolOptions = true;
                protocolOptions += option;
            }
        } else {
            if (getProtocolOption(option)) {
                protocolOptions -= option;
            }
        }
    }

    /**
     * is the Option <code>option</code> set?
     * use constants in this class beginnig with "OPTION_"
     */
    public boolean getProtocolOption(short option) {
        BitSet s = BitArrayOutputStream.getBitSet(getProtocolOptions());

        return s.get((int) (Math.log(option) / Math.log(2)));
    }

    public byte getProtocolOptions() {
        BitArrayOutputStream m = new BitArrayOutputStream();
        m.write(protocolOptions, 8);

        byte[] b = m.toByteArray();

        return b[0];
    }

    public void setMethodMOR(long methodMOR) {
        this.methodMOR = methodMOR;
        this.bMethodMOR = true;
    }

    public long getMethodMOR() {
        return methodMOR;
    }

    public void setPushMOR(long pushMOR) {
        this.pushMOR = pushMOR;
        this.bPushMOR = true;
    }

    public long getPushMOR() {
        return pushMOR;
    }

    public Hashtable getExtendedMethods() {
        return extendedMethods;
    }

    public boolean addExtendedMethod(long pduType, String name) {
        Long toAdd = new Long(pduType);
        Enumeration keys = extendedMethods.keys();

        while (keys.hasMoreElements()) {
            Long akt = (Long) (keys.nextElement());

            if (toAdd.equals(akt)) {
                return false;
            }
        }

        extendedMethods.put(new Long(pduType), name);

        return true;
    }

    public boolean removeExtendedMethod(long pduType) {
        Long search = new Long(pduType);
        Enumeration keys = extendedMethods.keys();

        while (keys.hasMoreElements()) {
            Long akt = (Long) (keys.nextElement());

            if (search.equals(akt)) {
                extendedMethods.remove(akt);

                return true;
            }
        }

        return false;
    }

    public Hashtable getHeaderCodePages() {
        return headerCodePages;
    }

    /**
     * add a header code page
     */
    public boolean addHeaderCodePage(long pageCode, String name) {
        Long toAdd = new Long(pageCode);
        Enumeration keys = headerCodePages.keys();

        while (keys.hasMoreElements()) {
            Long akt = (Long) (keys.nextElement());

            if (toAdd.equals(akt)) {
                return false;
            }
        }

        headerCodePages.put(toAdd, name);

        return true;
    }

    /**
     * remove a header code page if it exists.
     */
    public boolean removeHeaderCodePage(long pageCode) {
        Long search = new Long(pageCode);
        Enumeration keys = headerCodePages.keys();

        while (keys.hasMoreElements()) {
            Long akt = (Long) (keys.nextElement());

            if (search.equals(akt)) {
                headerCodePages.remove(akt);

                return true;
            }
        }

        return false;
    }

    public Vector getAliases() {
        return aliases;
    }

    /**
     * add a alias (alternate address of the sender)
     */
    public boolean addAlias(boolean bearerTypeIncluded,
        boolean portNumberIncluded, short bearerType, int portNumber,
        String bearerAddressToUse) {
        CWSPAddress toAdd = new CWSPAddress(bearerTypeIncluded,
                portNumberIncluded, bearerType, portNumber, bearerAddressToUse);

        for (int i = 0; i < aliases.size(); i++) {
            CWSPAddress search = (CWSPAddress) aliases.elementAt(i);

            if (search.equals(toAdd)) {
                return false;
            }
        }

        aliases.add(toAdd);

        return true;
    }

    /**
     * remove a alias using the values as given
     */
    public boolean removeAlias(boolean bearerTypeIncluded,
        boolean portNumberIncluded, short bearerType, int portNumber,
        String bearerAddressToUse) {
        CWSPAddress toRemove = new CWSPAddress(bearerTypeIncluded,
                portNumberIncluded, bearerType, portNumber, bearerAddressToUse);

        for (int i = 0; i < aliases.size(); i++) {
            CWSPAddress search = (CWSPAddress) aliases.elementAt(i);

            if (search.equals(toRemove)) {
                aliases.remove(search);

                return true;
            }
        }

        return false;
    }

    public long getClientMessageSize() {
        return clientMessageSize;
    }

    public void setClientMessageSize(long size) {
        clientMessageSize = size;
        this.bClientMessageSize = true;
    }

    public long getServerMessageSize() {
        return serverMessageSize;
    }

    public void setServerMessageSize(long size) {
        serverMessageSize = size;
        this.bServerMessageSize = true;
    }

    /**
     * encode all capabilities in a byte array
     * according to WAP-230-WSP-20010705-a section 8.3 "capability encoding"
     *
     * @return The encoded bytes
     */
    public byte[] getBytes() {
        BitArrayOutputStream result = new BitArrayOutputStream();
        BitArrayOutputStream tmp = new BitArrayOutputStream();
        byte[] tmp2;

        if (this.bClientSDUSize) {
            tmp.write(TYPE_clientSDUSize + 0x80, 8);
            tmp.writeUintVar(clientSDUSize);
            tmp2 = tmp.toByteArray();
            result.writeUintVar(tmp2.length);
            result.write(tmp2);
        }

        if (bServerSDUSize) {
            tmp.reset();
            tmp.write(TYPE_serverSDUSize + 0x80, 8);
            tmp.writeUintVar(serverSDUSize);
            tmp2 = tmp.toByteArray();
            result.writeUintVar(tmp2.length);
            result.write(tmp2);
        }

        if (bClientMessageSize) {
            tmp.reset();
            tmp.write(TYPE_clientMessageSize + 0x80, 8);
            tmp.writeUintVar(clientMessageSize);
            tmp2 = tmp.toByteArray();
            result.writeUintVar(tmp2.length);
            result.write(tmp2);
        }

        if (bServerMessageSize) {
            tmp.reset();
            tmp.write(TYPE_serverMessageSize + 0x80, 8);
            tmp.writeUintVar(serverMessageSize);
            tmp2 = tmp.toByteArray();
            result.writeUintVar(tmp2.length);
            result.write(tmp2);
        }

        if (bProtocolOptions) {
            tmp.reset();
            tmp.write(TYPE_protocolOptions + 0x80, 8);
            tmp.write(protocolOptions, 8);
            tmp2 = tmp.toByteArray();
            result.writeUintVar(tmp2.length);
            result.write(tmp2);
        }

        if (bMethodMOR) {
            tmp.reset();
            tmp.write(TYPE_methodMOR + 0x80, 8);
            tmp.writeUintVar(methodMOR);
            tmp2 = tmp.toByteArray();
            result.writeUintVar(tmp2.length);
            result.write(tmp2);
        }

        if (bPushMOR) {
            tmp.reset();
            tmp.write(TYPE_pushMOR + 0x80, 8);
            tmp.writeUintVar(pushMOR);
            tmp2 = tmp.toByteArray();
            result.writeUintVar(tmp2.length);
            result.write(tmp2);
        }

        if (!extendedMethods.isEmpty()) {
            Enumeration keys = extendedMethods.keys();

            while (keys.hasMoreElements()) {
                tmp.reset();

                Object key = keys.nextElement();
                tmp.write(TYPE_extendedMethods + 0x80, 8);
                tmp.write(((Long) key).longValue(), 8);

                String value = (String) (extendedMethods.get(key));
                tmp.write(value.getBytes());
                tmp.write(0x00, 8);
                tmp2 = tmp.toByteArray();
                result.writeUintVar(tmp2.length);
                result.write(tmp2);
            }
        }

        if (!headerCodePages.isEmpty()) {
            Enumeration keys = extendedMethods.keys();

            while (keys.hasMoreElements()) {
                tmp.reset();

                Object key = keys.nextElement();
                tmp.write(TYPE_headerCodePages + 0x80, 8);
                tmp.write(((Long) key).longValue(), 8);

                String value = (String) (extendedMethods.get(key));
                tmp.write(value.getBytes());
                tmp.write(0x00, 8);
                tmp2 = tmp.toByteArray();
                result.writeUintVar(tmp2.length);
                result.write(tmp2);
            }
        }

        if (!aliases.isEmpty()) {
            for (int i = 0; i < aliases.size(); i++) {
                tmp.reset();

                CWSPAddress address = (CWSPAddress) aliases.elementAt(i);
                tmp.write(TYPE_aliases + 0x80, 8);
                tmp.write(address.getBytes());
                tmp2 = tmp.toByteArray();
                result.writeUintVar(tmp2.length);
                result.write(tmp2);
            }
        }

        return result.toByteArray();
    }

    /**
     * Test method
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        CWSPCapabilities c = new CWSPCapabilities();
        c.setProtocolOption(OPTION_PUSH_FACILITY, true);
        c.setProtocolOption(OPTION_LARGE_DATA_TRANSFER, true);

        byte b = c.getProtocolOptions();
        System.out.println(BitArrayOutputStream.getBitString(b));
    }
}
