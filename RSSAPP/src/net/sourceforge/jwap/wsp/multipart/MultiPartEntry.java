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

import net.sourceforge.jwap.util.Utils;
import net.sourceforge.jwap.wsp.header.CodePage;
import net.sourceforge.jwap.wsp.header.Encoding;
import net.sourceforge.jwap.wsp.header.HeaderParseException;
import net.sourceforge.jwap.wsp.header.WAPCodePage;
import net.sourceforge.jwap.wsp.pdu.CWSPHeaders;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.Enumeration;


/**
 * This class represents a Multipart Entry according to WAP-230-WSP.
 *
 * @author Michel Marti
 *
 */
public class MultiPartEntry {
    // Used for content-type encoding
    private static WAPCodePage wapCodePage = WAPCodePage.getInstance();
    private String contentType;
    private byte[] data;
    private CWSPHeaders headers;

    private MultiPartEntry() {
    }

    /**
     * Construct a Multipart Entry.
     * @param contentType the content-type of the Multipart Entry
     * @param codePage the codepage to use for header encoding or null to use
     *   the default (WAP) codepage
     * @param data the payload
     */
    public MultiPartEntry(String contentType, CodePage codePage, byte[] data) {
        headers = (codePage == null) ? new CWSPHeaders()
                                     : new CWSPHeaders(codePage);
        this.contentType = contentType;
        this.data = data;
    }

    /**
     * Construct a Multipart Entry using the WAP codepage for header encoding.
     * @param contentType the content-type of the Multipart Entry
     * @param data the payload
     */
    public MultiPartEntry(String contentType, byte[] data) {
        this(contentType, null, data);
    }

    /**
     * Add a header to the Multipart Entry.
     * @param key the header name
     * @param value the header value
     */
    public void addHeader(String key, String value) {
        headers.addHeader(key, value);
    }

    public Enumeration getHeaderNames() {
        return headers.getHeaderNames();
    }

    public String getHeader(String key) {
        return headers.getHeader(key);
    }
    
    public Enumeration getHeaders(String key) {
        return headers.getHeaders(key);
    }
    
    public String getContentType() {
        return contentType;
    }

    public byte[] getPayload() {
      return data;  
    }
    
    public byte[] getBytes() throws IOException, HeaderParseException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] ct = wapCodePage.encode("content-type", contentType);
        byte[] hd = headers.getBytes();

        out.write(Encoding.uintVar(ct.length - 1 + hd.length)); // HeadersLen
        out.write(Encoding.uintVar(data.length)); // DataLen
        out.write(ct, 1, ct.length - 1); // Content-Type (without first octet)
        out.write(hd);
        out.write(data); // data

        return out.toByteArray();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("MultipartEntry[content-type:").append(contentType).append("]\n");

        for (Enumeration e = headers.getHeaderNames(); e.hasMoreElements();) {
            String key = (String) e.nextElement();

            for (Enumeration e2 = headers.getHeaders(key);
                    e2.hasMoreElements();) {
                String val = (String) e2.nextElement();
                sb.append("   ").append(key).append(':').append(val).append("\n");
            }
        }

        sb.append(Utils.hexDump("   Data: ", data));

        return sb.toString();
    }
}
