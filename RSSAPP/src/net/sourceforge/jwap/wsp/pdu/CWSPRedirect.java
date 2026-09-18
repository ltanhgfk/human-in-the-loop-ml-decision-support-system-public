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
import java.util.BitSet;
import java.util.Vector;

import net.sourceforge.jwap.util.BitArrayOutputStream;
import net.sourceforge.jwap.wsp.CWSPSocketAddress;


public class CWSPRedirect extends CWSPPDU {
    /**
     * Table 6
     * section 8.2.2.3
     */
    public static final short FLAG_Permanent_Redirect = 0x80;
    public static final short FLAG_Reuse_Security_Session = 0x40;

    // uint8
    private short flags = 0x00;

    /**
     * Redirect address(es)
     * usage of CWSPAddress
     */
    private Vector addresses = new Vector();

    public CWSPRedirect() {
        super();
        pduType = CWSPPDU.PDU_TYPE_REDIRECT;
    }

    public byte[] toByteArray() {
        BitArrayOutputStream tmp = new BitArrayOutputStream();
        BitArrayOutputStream result = new BitArrayOutputStream();
        result.write(pduType, 8);
        result.write(flags, 8);

        if (!addresses.isEmpty()) {
            for (int i = 0; i < addresses.size(); i++) {
                tmp.reset();

                CWSPAddress address = (CWSPAddress) addresses.elementAt(i);
                result.write(address.getBytes());
            }
        }

        return result.toByteArray();
    }

    /**
     * set flag according to sect. 8.2.2.3
     * @param short option the flag to set
     *                     use constants in this class!
     * @param boolean on_off switch on/off the flag
     */
    public void setFlag(short option, boolean on_off) {
        if (on_off) {
            if (!getFlag(option)) {
                flags += option;
            }
        } else {
            if (getFlag(option)) {
                flags -= option;
            }
        }
    }

    /**
     * is flag <code>option</code> set?
     */
    public boolean getFlag(short option) {
        BitSet s = BitArrayOutputStream.getBitSet(getFlags());

        return s.get((int) (Math.log(option) / Math.log(2)));
    }

    /**
     * return all flags as an byte
     */
    public byte getFlags() {
        BitArrayOutputStream m = new BitArrayOutputStream();
        m.write(flags, 8);

        byte[] b = m.toByteArray();

        return b[0];
    }

    public void setFlags(short flags) {
        this.flags = flags;
    }

    //public Vector getAddresses(){
    //  return addresses;
    //}
    public InetAddress[] getInetAddresses() {
        InetAddress[] result = new InetAddress[addresses.size()];

        for (int i = 0; i < addresses.size(); i++) {
            result[i] = ((CWSPAddress) (addresses.elementAt(i))).getInetAddress();
        }

        return result;
    }

    public CWSPSocketAddress[] getSocketAddresses() {
        CWSPSocketAddress[] result = new CWSPSocketAddress[addresses.size()];

        for (int i = 0; i < addresses.size(); i++) {
            result[i] = ((CWSPAddress) (addresses.elementAt(i))).getSocketAddress();
        }

        return result;
    }
    
    /**
     * adds a redirect address
     * @return boolean true = added
     *                 false = NOT added (already added)
     */
    public boolean addAddress(boolean bearerTypeIncluded,
        boolean portNumberIncluded, short bearerType, int portNumber,
        String bearerAddressToUse) {
        CWSPAddress toAdd = new CWSPAddress(bearerTypeIncluded,
                portNumberIncluded, bearerType, portNumber, bearerAddressToUse);

        for (int i = 0; i < addresses.size(); i++) {
            CWSPAddress search = (CWSPAddress) addresses.elementAt(i);

            if (search.equals(toAdd)) {
                return false;
            }
        }

        addresses.add(toAdd);

        return true;
    }

    /**
     * removes a redirect address
     * @return boolean true = removed
     *                 false = address does not exist
     */
    public boolean removeAddress(boolean bearerTypeIncluded,
        boolean portNumberIncluded, short bearerType, int portNumber,
        String bearerAddressToUse) {
        CWSPAddress toRemove = new CWSPAddress(bearerTypeIncluded,
                portNumberIncluded, bearerType, portNumber, bearerAddressToUse);

        for (int i = 0; i < addresses.size(); i++) {
            CWSPAddress search = (CWSPAddress) addresses.elementAt(i);

            if (search.equals(toRemove)) {
                addresses.remove(search);

                return true;
            }
        }

        return false;
    }

    public void setAddresses(Vector v) {
        addresses = v;
    }
}
