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
package net.sourceforge.jwap.wsp.multipart;

import net.sourceforge.jwap.util.Logger;
import net.sourceforge.jwap.wsp.WSPDecoder;
import net.sourceforge.jwap.wsp.header.CodePage;
import net.sourceforge.jwap.wsp.header.Encoding;
import net.sourceforge.jwap.wsp.header.HeaderParseException;
import net.sourceforge.jwap.wsp.header.WAPCodePage;
import net.sourceforge.jwap.wsp.pdu.CWSPHeaders;
import net.sourceforge.jwap.wsp.pdu.CWSPPDU;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.Enumeration;
import java.util.Vector;


/**
 * This class can be used for constructing WSP Multipart payload according to WAP-230-WSP.
 *
 * @author Michel Marti
 */
public class MultiPartData {
    private static final Logger logger = Logger.getLogger(MultiPartData.class);
    private Vector parts;

    /**
     * Construct a WSP Multipart object with no content (yet).
     */
    public MultiPartData() {
        parts = new Vector();
    }

    /**
     * Reconstruct a Multipart object by parsing the specified byte array. The
     * headers will be decoded according to WAP version 1.1
     * @param data a byte array containing the Multipart payload.
     * 
     */
    public MultiPartData(byte[] data) throws IOException {
      this(data,1,1);
    }
    
    /**
     * Reconstruct a Multipart object by parsing the specified byte array.
     * @param data a byte array containing the Multipart payload
     * @param major WAP major version (used for header decoding)
     * @param minor WAP minor version (used for header decoding)
     */
    public MultiPartData(byte[] data, int major, int minor) throws IOException {
        this();
        
        if (data == null) {
            return;
        }

        WSPDecoder dc = new WSPDecoder(data);
        CodePage codePage = WAPCodePage.getInstance(major, minor);
        
        long parts = dc.getUintVar();
        if(logger.isDebugEnabled()) {
            logger.debug("# of parts: " + parts);
        }

        while (!dc.isEOF()) {
            long hlen = dc.getUintVar();
            long dlen = dc.getUintVar();

            if (logger.isDebugEnabled()) {
                logger.debug("hlen: " + hlen + ", dlen: " + dlen);
            }

            int startCT = dc.seek(0);

            // Decode the content-type
            String ctype = CWSPPDU.getContentType(dc);

            // read in headers
            int ctLen = dc.seek(0) - startCT;
            int hl = (int) (hlen - ctLen);

            if(logger.isDebugEnabled()) {
                logger.debug("Content-Type length: " + ctLen + ", header length: " + hl);
            }

            int hstart = dc.seek(0); dc.seek(hl);
            // Copy the data
            byte[] edata = dc.getBytes((int) dlen);
            MultiPartEntry entry = new MultiPartEntry(ctype, codePage, edata);
            addPart(entry);

            // Decode the header
            if (hl > 0) {
                dc.pos(hstart);
                CWSPHeaders hdrs = new CWSPHeaders(dc, hl, codePage);
                dc.seek(edata.length);
                for (Enumeration e = hdrs.getHeaderNames();
                        e.hasMoreElements();) {
                    String key = (String) e.nextElement();

                    for (Enumeration e2 = hdrs.getHeaders(key);
                            e2.hasMoreElements();) {
                        String val = (String) e2.nextElement();
                        entry.addHeader(key, val);
                    }
                }
            }
        }
    }

    public void addPart(MultiPartEntry entry) {
        if (parts != null) {
            parts.add(entry);
        }
    }

    public Enumeration elements() {
        return parts.elements();
    }

    /**
     * Get the number of Multipart entries
     */
    public int size()
    {
      return parts.size();  
    }
    
    public byte[] getBytes() throws IOException, HeaderParseException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(Encoding.uintVar(parts.size()));

        for (Enumeration e = parts.elements(); e.hasMoreElements();) {
            MultiPartEntry entry = (MultiPartEntry) e.nextElement();
            out.write(entry.getBytes());
        }

        out.flush();

        return out.toByteArray();
    }
}
