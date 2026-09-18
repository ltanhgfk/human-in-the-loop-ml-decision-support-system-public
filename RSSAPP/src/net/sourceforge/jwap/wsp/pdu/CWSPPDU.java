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

import java.util.BitSet;
import java.util.Vector;

import net.sourceforge.jwap.util.BitArrayInputStream;
import net.sourceforge.jwap.util.Logger;
import net.sourceforge.jwap.wsp.WSPDecoder;
import net.sourceforge.jwap.wsp.header.WAPCodePage;


public abstract class CWSPPDU {
    /**
     * Table 34
     */
    public static final short PDU_TYPE_CONNECT = 0x01;
    public static final short PDU_TYPE_CONNECTREPLY = 0x02;
    public static final short PDU_TYPE_REDIRECT = 0x03;
    public static final short PDU_TYPE_REPLY = 0x04;
    public static final short PDU_TYPE_DISCONNECT = 0x05;
    public static final short PDU_TYPE_PUSH = 0x06;
    public static final short PDU_TYPE_CONFIRMEDPUSH = 0x07;
    public static final short PDU_TYPE_SUSPEND = 0x08;
    public static final short PDU_TYPE_RESUME = 0x09;
    public static final short PDU_TYPE_GET = 0x40;
    public static final short PDU_TYPE_GET_OPTIONS = 0x41;
    public static final short PDU_TYPE_GET_HEAD = 0x42;
    public static final short PDU_TYPE_GET_DELETE = 0x43;
    public static final short PDU_TYPE_GET_TRACE = 0x44;
    public static final short PDU_TYPE_POST = 0x60;
    public static final short PDU_TYPE_POST_PUT = 0x61;
    public static final short PDU_TYPE_DATA_FRAGMENT = 0x80;
    static Logger logger = Logger.getLogger(CWSPPDU.class);
    protected CWSPCapabilities capabilities = new CWSPCapabilities();
    protected CWSPHeaders headers = new CWSPHeaders();
    private static WAPCodePage wapCodePage = WAPCodePage.getInstance(1,2);

    /**
     * TID
     * unint8
     * in case of connectionless WSP PDUs (NOT IMPLEMENTED IN THIS RELEASE!)
     * get it from S-Unit-MethodInvoke.req::TID
     *          or S-Unit-MethodResult.req::TID
     *          or S-Unit-Push.req::push ID
     */
    protected short TID;

    /**
     * PDU Type
     * unint8
     */
    protected short pduType;
    protected byte[] payload;

    /////////////////////////////////////////////////////////////////////////////
    //////////////////////////////// CONSTRUCTORS ///////////////////////////////
    public CWSPPDU() {
    }

    public CWSPPDU(byte[] payload) {
        this.payload = payload;
    }

    /////////////////////////////////////////////////////////////////////////////
    //////////////////////////////// ABSTRACT METHOD ////////////////////////////

    /**
     * Encodes the PDU according to WAP-230-WSP-20010705-A. 
     * See <a href="http://www.wapforum.org">www.wapforum.org</a> for more information.
     */
    public abstract byte[] toByteArray();

    /////////////////////////////////////////////////////////////////////////////
    //////////////////////////////// CAPABILITIES/HEADERS ///////////////////////
    public CWSPCapabilities getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(CWSPCapabilities c) {
        capabilities = c;
    }

    public CWSPHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(CWSPHeaders h) {
        headers = h;
    }

    public short getType() {
        return pduType;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public String toString() {
        return BitArrayInputStream.getBitString(this.toByteArray());
    }

    public boolean equals(CWSPPDU pdu) {
        byte[] a1 = this.toByteArray();
        byte[] a2 = pdu.toByteArray();

        if (a1.length != a2.length) {
            return false;
        } else {
            for (int i = 0; i < a1.length; i++) {
                if (a1[i] != a2[i]) {
                    logger.error("Can not decode received PDU - Byte " + i);

                    return false;
                }
            }

            return true;
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    public static synchronized CWSPPDU getPDU(byte[] bytes)
        throws EWSPCorruptPDUException {
        // This is a class, that helps us with decoding bits
        WSPDecoder bin = new WSPDecoder(bytes);

        //the PDU to be returned
        CWSPPDU end = null;

        //decode PDU type

        /** for connectionless mode: 1st byte is tid:
        short tid = bin.getUInt8(bytes[0]);
        short pduType = bin.getUInt8(bytes[1]);
        byte[] payload = null;
        int aktbyte = 2;
        */
        short pduType = bin.getUint8();

        switch (pduType) {
        /** @todo ConfirmedPush PDU */
        case CWSPPDU.PDU_TYPE_CONNECT:

            CWSPConnect pdu1 = new CWSPConnect();

            // decode Version
            pdu1.setVersion(bin.getUint8());

            // decode length of capabilities
            long caplength = bin.getUintVar();

            // decode length of headers
            long headlength = bin.getUintVar();

            // decode capabilities
            pdu1.setCapabilities(getCapabilities(bytes, bin.seek(0), caplength));
            bin.seek((int) caplength);

            // decode headers
            pdu1.setHeaders(new CWSPHeaders(bin, (int) headlength, wapCodePage));
            end = pdu1;

            break;

        case CWSPPDU.PDU_TYPE_CONNECTREPLY:

            // decode SessionID
            CWSPConnectReply pdu2 = new CWSPConnectReply(bin.getUintVar());

            // decode length of capabilities
            caplength = bin.getUintVar();

            // decode length of headers
            headlength = bin.getUintVar();

            // decode capabilities
            pdu2.setCapabilities(getCapabilities(bytes, bin.seek(0), caplength));
            bin.seek((int) caplength);

            // decode headers
            pdu2.setHeaders(new CWSPHeaders(bin, (int) headlength, wapCodePage));
            end = pdu2;

            break;

        case CWSPPDU.PDU_TYPE_DATA_FRAGMENT:

            // decode length of headers
            headlength = bin.getUintVar();

            // decode headers
            CWSPHeaders headers = new CWSPHeaders(bin, (int) headlength, wapCodePage);

            // the balance is payload
            byte[] payload = bin.getBytes(bin.getRemainingOctets());

            CWSPDataFragment pdu3 = new CWSPDataFragment(payload);
            pdu3.setHeaders(headers);
            end = pdu3;

            break;

        case CWSPPDU.PDU_TYPE_DISCONNECT:

            CWSPDisconnect pdu4 = new CWSPDisconnect(bin.getUintVar());
            end = pdu4;

            break;

        case CWSPPDU.PDU_TYPE_GET: //5

            int urilength = (int) bin.getUintVar();
            String uri = bin.getString(urilength);
            CWSPGet pdu5 = new CWSPGet(uri);
            pdu5.setHeaders(new CWSPHeaders(bin,bin.getRemainingOctets(), 
                wapCodePage));
            end = pdu5;

            break;

        case CWSPPDU.PDU_TYPE_POST:

            // decode length of URI
            urilength = (int) bin.getUintVar();

            // decode length of headers + contenttype
            headlength = bin.getUintVar();

            // decode URI
            uri = bin.getString(urilength);

            // decode contenttype
            int cpos = bin.seek(0);
            String contenttype = getContentType(bin);
            headlength -= (bin.seek(0) - cpos);

            // decode headers
            headers = new CWSPHeaders(bin, (int) headlength, wapCodePage);

            // the balance is payload
            payload = bin.getBytes(bin.getRemainingOctets());

            CWSPPost pdu6 = new CWSPPost(payload, contenttype, uri);
            pdu6.setHeaders(headers);
            end = pdu6;

            break;

        case CWSPPDU.PDU_TYPE_REDIRECT:

            CWSPRedirect pdu8 = new CWSPRedirect();

            // decode flags
            pdu8.setFlags(bin.getUint8());

            // decode addresses
            pdu8.setAddresses(getAddresses(bytes, bin.seek(0),
                    bin.getRemainingOctets()));

            end = pdu8;

            break;

        case CWSPPDU.PDU_TYPE_REPLY:

            // status
            long status = bin.getUint8();

            // length of header + contenttype
            headlength = bin.getUintVar();

            // decode contenttype
            cpos = bin.seek(0);
            contenttype = getContentType(bin);
            headlength -= (bin.seek(0) - cpos);

            // decode headers
            headers = new CWSPHeaders(bin, (int) headlength, wapCodePage);

            // the balance is payload
            payload = bin.getBytes(bin.getRemainingOctets());

            CWSPReply pdu9 = new CWSPReply(status, payload, contenttype);
            pdu9.setHeaders(headers);
            end = pdu9;

            break;

        case CWSPPDU.PDU_TYPE_RESUME:

            // decode sessionID
            long sessionID = bin.getUintVar();

            // decode length of capabilities
            caplength = bin.getUintVar();

            // decode capabilities
            CWSPCapabilities capabilities = getCapabilities(bytes, bin.seek(0),
                    caplength);
            bin.seek((int) caplength);

            // decode headers
            headers = new CWSPHeaders(bin, bin.getRemainingOctets(), 
                wapCodePage);

            CWSPResume pdu10 = new CWSPResume(sessionID);
            pdu10.setCapabilities(capabilities);
            pdu10.setHeaders(headers);
            end = pdu10;

            break;

        case CWSPPDU.PDU_TYPE_SUSPEND:
            sessionID = bin.getUintVar();

            CWSPSuspend pdu11 = new CWSPSuspend(sessionID);
            end = pdu11;

            break;

        case CWSPPDU.PDU_TYPE_PUSH:

            CWSPPush pdu12 = new CWSPPush();
            headlength = bin.getUintVar();

            cpos = bin.seek(0);
            contenttype = getContentType(bin);
            headlength -= (bin.seek(0) - cpos);

            logger.debug("Push headers found. Len: " + headlength +
                "  content type:" + contenttype);

            // decode headers
            pdu12.setHeaders(new CWSPHeaders(bin,(int) headlength, 
                wapCodePage));

            // decode payload
            pdu12.payload = bin.getBytes(bin.getRemainingOctets());
            end = pdu12;

            break;

        default:

            // unnown PDU types
            throw new EWSPCorruptPDUException(
                "Unknown PDU-Type! By the way: *is* it WSP? pduType=" +
                pduType);
        }

        // finally, set TID (for all types):
        //    end.TID=tid;
        //logger.debug(">>> decoded and re-encoded:");
        //logger.debug(end.toString());
        return end;
    }

    private static synchronized CWSPCapabilities getCapabilities(byte[] bytes,
        int offset, long count) {
        CWSPCapabilities result = new CWSPCapabilities();

        if (count <= 0) {
            return result;
        }

        try {
            /** @todo decode capabilities */
        } catch (IndexOutOfBoundsException e) {
            // ignore and return null
            return result;
        }

        return result;
    }

    private static synchronized Vector getAddresses(byte[] bytes, int offset,
        long count) {
        // This is a class, that helps us with decoding bits
        BitArrayInputStream bin = new BitArrayInputStream();
        Vector result = new Vector();

        if (count <= 0) {
            return result;
        }

        int aktbyte = offset;

        while (aktbyte < (count + offset)) {
            BitSet b = BitArrayInputStream.getBitSet(bytes[aktbyte]);
            boolean bearerTypeIncluded = b.get(0);
            boolean portNumberIncluded = b.get(1);
            short addresslen = bin.getByte(bytes[aktbyte], 2, 6);
            short bearerType = bin.getUInt8(bytes[++aktbyte]);
            int portNumber = bin.getUInt16(bytes[++aktbyte], bytes[++aktbyte]);
            String address = "";

            if (addresslen != 4) {
                logger.warn("Unknown bearer type when decoding addresses!");
            } else {
                // the ipv4 address is encoded in 4 bytes
                // get each byte and concat the ipv4 address as a string 
                for (int i = 0; i < addresslen; i++) {
                    short ip = bin.getUInt8(bytes[++aktbyte]);
                    address = address + ip;

                    if (i < (addresslen - 1)) {
                        address = address + ".";
                    }
                }
            }

            //String address = new String(bytes, ++aktbyte, addresslen);
            CWSPAddress a = new CWSPAddress(bearerTypeIncluded,
                    portNumberIncluded, bearerType, portNumber, address);
            aktbyte += addresslen;
            result.addElement(a);
        }

        return result;
    }

    public static synchronized String getContentType(WSPDecoder dc) {
        int hs = CWSPHeaders.getHeaderValueSize(dc);
        byte[] header = dc.getBytes(hs);
        return wapCodePage.decodeContentType(header);
    }
}
