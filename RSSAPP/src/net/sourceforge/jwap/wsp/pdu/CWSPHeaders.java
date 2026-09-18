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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import net.sourceforge.jwap.util.Logger;
import net.sourceforge.jwap.util.Utils;
import net.sourceforge.jwap.wsp.WSPDecoder;
import net.sourceforge.jwap.wsp.header.CodePage;
import net.sourceforge.jwap.wsp.header.Header;
import net.sourceforge.jwap.wsp.header.HeaderParseException;
import net.sourceforge.jwap.wsp.header.WAPCodePage;



/**
 * This class represents WSP Headers
 *
 * @author Michel Marti
 */
public class CWSPHeaders 
{
    private static Logger logger = Logger.getLogger(CWSPHeaders.class);

    // List of Headers
    private Vector headers = new Vector();

    // Default codepage (WAP)
    private CodePage codePage;

    /**
     * Construct WSP Headers using the default WAP codepage
     */
    public CWSPHeaders() {
        this(WAPCodePage.getInstance());
    }

    /**
     * Construct WSP Headers using a specific codepage
     */
    public CWSPHeaders(CodePage codePage) {
        this.codePage = codePage;
    }

    
    /**
     * Construct WSP Headers by decoding a byte array
     * @param decoder the WSPDecoder for decoding
     * @param count the length of the headers within the data 
     * @param codePage the codepage for decoding the headers
     * @throws HeaderParseException if decoding fails
     */
    public CWSPHeaders(WSPDecoder decoder, int length, CodePage codePage)
        throws HeaderParseException
    {
        this(decoder,length, null, codePage);
    } 
    
    /**
     * Construct WSP Headers by decoding a byte array.
     * @param decoder the WSPDecoder for decoding
     * @param count the length of the headers within the data
     * @param stopOn the header after which decoding stops (may be null) 
     * @param codePage the codepage for decoding the headers
     * @throws HeaderParseException if decoding fails
     */
    public CWSPHeaders(WSPDecoder decoder, int length, String stopOn, CodePage codePage)
        throws HeaderParseException
    { 
        this(codePage);
        if( decoder == null ) {
            return;
        }
        
        if(decoder.getRemainingOctets() < length ) {
            logger.warn(length+": Length exceeds remaining octets in decoder");
            length = decoder.getRemainingOctets();   
        }

        if(logger.isDebugEnabled()) {
            int pos = decoder.seek(0);
            byte[] bytes = decoder.getBytes(length);
            decoder.pos(pos);
            logger.debug("\n"+Utils.hexDump("data to decode: ", bytes));    
        }
        
        if(stopOn!=null) {
            stopOn=stopOn.toLowerCase();
        }
                
        try {
            Header header;
            int offset = decoder.seek(0);
            
            while(decoder.seek(0) < (offset+length) ) {
                
                int start = decoder.seek(0);
                int octet = decoder.getUint8();
                int hlen = 0;
				// logger.debug("1st octet: "+Integer.toHexString(octet));
                
                if (octet == 127) { // Shift-Delimiter
                    // shift-delimiter + page identity
                    int pageIdentity = decoder.getUint8();
                    if( logger.isDebugEnabled()) {
                        logger.debug("Shift-delimiter detected, page identity: "+ pageIdentity);
                    }

                    if (pageIdentity != 1) {
                        throw new HeaderParseException(
                            "Code page switching not supported. (page identity: " +
                            pageIdentity + ")");
                    }
                    // ignore code page switching to default code page
                } else if( octet > 0 && octet <= 31 ) { // Short-cut-shift-delimiter
                    int shortCut = octet;
                    if(logger.isDebugEnabled()) {
                        logger.debug("Short-cut-shift-delimiter detected, page identity: "+shortCut);
                    }

                    if (shortCut != 1) {
                        throw new HeaderParseException(
                            "Code page switching not supported. (short-cut-shift-delimiter: " +
                            shortCut + ")");
                    }
                    // ignore code page switching to default code page
                    
                } else {
                    // message header
                    if ((octet & 0x80) == 0x80) { // short-integer
                        logger.debug("Well-Known-header detected");
                        // well-known-field-name + wap-value
                        int wellKnownHeader = octet & 0x7F;
                        
                        // header value
                        hlen = getHeaderValueSize(decoder);
                        hlen++; // We want the first octet as-well
                        
                    } // well-known-field-name + wap-value
                    else {
                        logger.debug("Application-header detected");
                        decoder.seek(-1);
                        // application header -> header name as string and value as string
                        // header name
                        hlen = getHeaderValueSize(decoder);
                        decoder.seek(hlen);

                        // value
                        hlen+= getHeaderValueSize(decoder);
                    }
                    decoder.pos(start);
                    byte[] encodedHeader = decoder.getBytes(hlen);
                    if(logger.isDebugEnabled()) {
                        logger.debug("\n"+Utils.hexDump("encoded header: ", encodedHeader));
                    }

                    try {
                        header = codePage.decode(encodedHeader);

                        if (header != null) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Decoded header: " + header);
                            }
                            headers.addElement(header);
                            if( stopOn != null && header.getName().toLowerCase().equals(stopOn) ) {
                                if(logger.isDebugEnabled()) {
                                    logger.debug(stopOn+": stop-on header reached, decode done");
                                }
                                break;
                            }
                        } else {
                            logger.warn(
                                "CodePage.decode() returned a null header: \n" +
                                Utils.hexDump(encodedHeader));
                        }
                    } catch (Exception unknown) {
                        // Since header parsing is not yet complete, warn and go-ahead
                        logger.warn("Exception while decoding header", unknown);

                        if (logger.isDebugEnabled()) {
                            logger.debug(Utils.hexDump(encodedHeader));
                        }
                    }
                }
                 // message header
            }
        } catch (IndexOutOfBoundsException e) {
            // ignore and return
        }
    }


    /**
     * Sets a WSP header with the given name and value. If the header had
     * already been set, the new value overwrites the previous one. The containsHeader
     * method can be used to test for the presence of a header before setting its value.
     */
    public void setHeader(String name, String value) {
        if (name == null) {
            return;
        }

        removeHeader(name);

        if (value != null) {
            headers.addElement(new Header(name, value));
        }
    }

    public void addHeader(String name, String value) {
        if ((name == null) || (value == null)) {
            return;
        }

        headers.addElement(new Header(name, value));
    }

    /**
     * Sets a WSP header with the given name and integer value. If the header
     * had already been set, the new value overwrites the previous one. The
     * containsHeader  method can be used to test for the presence of a header
     * before setting its value.
     */
    public void setIntHeader(String name, int value) {
        if (name == null) {
            return;
        }

        removeHeader(name);
        headers.addElement(new Header(name, new Integer(value)));
    }

    public void addIntHeader(String name, int value) {
        if (name == null) {
            return;
        }

        headers.addElement(new Header(name, new Integer(value)));
    }

    /**
     * Sets a WSP header with the given name and date-value. The date is specified
     * in terms of milliseconds since the epoch. If the header had already been
     * set, the new value overwrites the previous one. The containsHeader
     * method can be used to test for the presence of a header before setting
     * its value.
     */
    public void setDateHeader(String name, long value) {
        if (name == null) {
            return;
        }

        removeHeader(name);
        headers.addElement(new Header(name, new Long(value)));
    }

    public void addDateHeader(String name, long value) {
        if (name == null) {
            return;
        }

        headers.addElement(new Header(name, new Long(value)));
    }

    public String getHeader(String name) {
        if (name != null) {
            for (Enumeration e = headers.elements(); e.hasMoreElements();) {
                Header he = (Header) e.nextElement();
    
                if (name.equalsIgnoreCase(he.getName())) {
                    Object o = he.getValue();
    
                    return (o == null) ? null : o.toString();
                }
            }
        }
        return null;
    }

    public Enumeration getHeaders(String name) {
        Vector v = new Vector();

        if (name != null) {
            for (Enumeration e = headers.elements(); e.hasMoreElements();) {
                Header he = (Header) e.nextElement();

                if (name.equalsIgnoreCase(he.getName())) {
                    Object value = he.getValue();
                    if( value != null ) {
                        v.add(he.getValue().toString());
                    }
                }
            }
        }

        return v.elements();
    }

    public boolean containsHeader(String name) {
        if (name!=null) {
            for (Enumeration e = headers.elements(); e.hasMoreElements();) {
                Header he = (Header) e.nextElement();
    
                if (name.equalsIgnoreCase(he.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public Enumeration getHeaderNames() {
        Vector v = new Vector();

        for (Enumeration e = headers.elements(); e.hasMoreElements();) {
            Header he = (Header) e.nextElement();

            if (!v.contains(he.getName())) {
                v.add(he.getName());
            }
        }

        return v.elements();
    }

    private void removeHeader(String name) {
        // Remove all occurences of the header first
        if( name != null ) {
            for (Enumeration e = headers.elements(); e.hasMoreElements();) {
                Header he = (Header) e.nextElement();
  
                if (name.equalsIgnoreCase(he.getName())) {
                    headers.removeElement(he);
                }
            }
        }
    }

    /*
     * Get the WSP representation of the headers
     */
    public byte[] getBytes() throws HeaderParseException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            for (Enumeration e = headers.elements(); e.hasMoreElements();) {
                Header he = (Header) e.nextElement();
                String hn = he.getName();
                Object hv = he.getValue();
                byte[] enc;

                if( hv == null ) {
                    logger.debug(hn+": Ignoring header since it has no value"); 
                }
                else if (hv instanceof String) {
                    enc = codePage.encode(hn, (String) hv);

                    if (enc != null) {
                        out.write(enc);
                    } else {
                        logger.warn(
                            "codePage.encode() returned null for header " +
                            he.toString());
                    }
                } else if (hv instanceof Long) {
                    enc = codePage.encode(hn, new Date(((Long) hv).longValue()));

                    if (enc != null) {
                        out.write(enc);
                    } else {
                        logger.warn(
                            "codePage.encode() returned null for header " +
                            he.toString());
                    }
                } else if (hv instanceof Integer) {
                    enc = codePage.encode(hn, ((Integer) hv).intValue());

                    if (enc != null) {
                        out.write(enc);
                    } else {
                        logger.warn(
                            "codePage.encode() returned null for header " +
                            he.toString());
                    }
                } else {
                    if (logger.isDebugEnabled()) {
                      logger.debug(hv.getClass().getName()+": Unknown type of header value, using string encoding");
                    } 
                    enc = codePage.encode(hn, hv.toString());

                    if (enc != null) {
                        out.write(enc);
                    } else {
                        logger.warn(
                            "codePage.encode() returned null for header " +
                            he.toString());
                    }
                }
            }

            out.flush();
        } catch (IOException unknown) {
            // We assume that this never occurs when writing to a ByteArrayOutputStream...
        }

        return out.toByteArray();
    }

    /**
     * Return the size of a header field value
     */
    public static int getHeaderValueSize(WSPDecoder decoder) {
        int pos = decoder.seek(0);
        int val = decoder.getUint8();
        int ret = 0;

        // logger.debug("First octet of field value: "+val);

        if (val < 31) {
            // logger.debug("Number of octets to follow: "+val);
            ret = val + 1; 
        }
        else if (val == 31) { // an UIntVar follows...
            //logger.debug("UIntVar follows");
            ret = (int) decoder.getUintVar();
            ret += decoder.seek(0)-pos;
        }
        else if ( val < 128 ) {
            // logger.debug("Text-String, terminated by zero "); 
            val = 0;
            decoder.seek(-1);
            while ( decoder.getOctet() != 0) {
                val++;
            }

            val++; //skip the trailing \0
            ret=val;
        }
        else { // Else -> Encoded 7-bit value; this header has no more data
            //logger.debug("7-bit value; this header has no more data"); 
            ret=1;
        }
        
        decoder.pos(pos); // rollback to initial decoder position
        return ret;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();

        for (Enumeration e = getHeaderNames(); e.hasMoreElements();) {
            String key = (String) e.nextElement();

            for (Enumeration e2 = getHeaders(key); e2.hasMoreElements();) {
                String val = (String) e2.nextElement();
                sb.append("[").append(key).append(": ").append(val).append("]");
            }
        }

        return sb.toString();
    }
}
