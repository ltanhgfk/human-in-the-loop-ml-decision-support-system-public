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
package net.sourceforge.jwap.wsp;

import net.sourceforge.jwap.wsp.pdu.*;


/**
 * Objects of this type represent a result from a POST or GET method.
 *
 * @author Niko Bender
 */
public class CWSPResult {
    private CWSPHeaders headers;
    private CWSPMethodManager methodManager;
    private String contentType;
    private byte[] payload;
    private int status;
    
    private CWSPResult(){}
    /**
     * <p></p>
     *
     * @param methodManager
     * @param headers
     * @param contentType
     * @param payload
     */
    public CWSPResult(CWSPMethodManager methodManager, CWSPHeaders headers, String contentType, byte[] payload) {
        super();
        this.headers = headers;
        this.contentType = contentType;
        this.payload = payload;
        this.methodManager = methodManager;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public CWSPHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(CWSPHeaders headers) {
        this.headers = headers;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }
    
    public CWSPMethodManager getMethodManager() {
        return methodManager;   
    }

    public int getStatus() {
        return status;   
    }
    
    void setStatus(int status) {
        this.status=status;
    }
    
}
