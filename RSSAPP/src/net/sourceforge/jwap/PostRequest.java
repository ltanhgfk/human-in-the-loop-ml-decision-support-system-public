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
package net.sourceforge.jwap;

/**
 * Use this class for executing a WSP POST request.
 * @see net.sourceforge.jwap.WAPClient
 * 
 * @author Michel Marti
 */
public class PostRequest extends Request 
{
    private byte[] requestBody;
    private String contentType;
    
    /**
     * Construct a new WSP POST request
     * @param URL the URL to request 
     */
    public PostRequest(String URL) {
        super(URL);   
    }
    
    /**
     * Set the request body for this WSP POST request
     */
    public void setRequestBody(byte[] requestBody) {
        this.requestBody=requestBody;
    }
    
    /**
     * Returns the request body for this WSP POST request
     */
    public byte[] getRequestBody() {
        return requestBody;   
    }
    
    /**
     * Set the content-type 
     */
    public void setContentType(String contentType) {
        this.contentType=contentType;   
    }
    
    /**
     * Returns the content-type
     */
    public String getContentType() {
        return contentType;
    }
    
    /**
     * Returns the length of the request-body
     */
    public long getContentLength() {
        return requestBody == null?0:requestBody.length;   
    }
}
