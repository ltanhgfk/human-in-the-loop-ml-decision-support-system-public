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
import net.sourceforge.jwap.wsp.header.CodePage;
import net.sourceforge.jwap.wsp.header.WAPCodePage;


public class CWSPPost extends CWSPPDU {
    /**
     * mult. octets
     */
    private String uri;

    /**
     * mult. octets
     */
    private String contentType;

    // Used for encoding the content-type of POST requests
    private static CodePage wapCodePage = WAPCodePage.getInstance();
    
    public CWSPPost(byte[] payload, String contentType, String uri) {
        super();
        this.uri = uri;
        this.payload = payload;
        this.pduType = CWSPPDU.PDU_TYPE_POST;
        this.contentType = contentType;
    }

    public byte[] toByteArray() {
        BitArrayOutputStream result = new BitArrayOutputStream();
        result.write(pduType, 8);

        //--------------------------------//
        byte[] cap = capabilities.getBytes();
        byte[] head = headers.getBytes();
        
        // Encode the content-type and strip the first octet
        byte[] tmp = wapCodePage.encode("Content-Type", 
            contentType==null?"octet/unspecified":contentType);
        byte[] ctype = new byte[tmp.length-1];
        System.arraycopy(tmp,1,ctype,0,ctype.length);
        
        byte[] uria = uri.getBytes();
        result.writeUintVar(uria.length);
        result.writeUintVar(head.length + ctype.length);
        result.write(uria);
        result.write(ctype);
        result.write(head);
        result.write(payload);

        //--------------------------------//
        return result.toByteArray();
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
