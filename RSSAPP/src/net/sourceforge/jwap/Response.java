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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;

import net.sourceforge.jwap.util.TransTable;
import net.sourceforge.jwap.wsp.CWSPResult;
import net.sourceforge.jwap.wsp.pdu.CWSPHeaders;

/**
 * This class represents a Response on a WSP GET or POST request
 * @see net.sourceforge.jwap.WAPClient#execute(Request)
 * @author Michel Marti
 */
public class Response 
{
    private static final Enumeration EMPTY_ENUM = new Vector().elements();
    
    private int status;
    private byte[] responseBody;
    private CWSPHeaders headers;
    private String contentType;
    private static TransTable codes = TransTable.getTable("http-status-codes");
    
    Response(CWSPResult result) {
        status = result.getStatus();
        responseBody = result.getPayload();
        headers = result.getHeaders();
        contentType = result.getContentType();
    }
    
    /*
     * Returns the HTTP status code
     */
    public int getStatus() {
        return status;
    }
    
    /**
     * Returns the status-text corresponding to the response-status, e.g.
     * for status-code 200 this method returns "OK". 
     */
    public String getStatusText() {
        String ret = codes.code2str(getStatus());
        if( ret == null ) {
            ret = "Unknown";
        }
        return ret;
    }
    
    /**
     * Determine if the response status signals success (HTTP 2xx).
     */
    public boolean isSuccess() {
        return ( status >= 200 && status < 300);     
    }
    
    /*
     * Returns the response-body
     */
    public byte[] getResponseBody() {
        return responseBody;   
    }
    
    /**
     * Returns the response-body as input stream
     */
    public InputStream getResponseBodyAsInputStream() {
        return new ByteArrayInputStream(responseBody==null?new byte[0]:responseBody);    
    }

    /**
     * Get the value of a response-header
     * @param name the header name
     * @return the header value or null if not present
     */    
    public String getHeader(String name) {
        return headers==null?null:headers.getHeader(name);
    }
    
    /**
     * Get a list of all header names for this response.
     * @return an enumeration of Strings
     */
    public Enumeration getHeaderNames() {
        return headers==null?EMPTY_ENUM:headers.getHeaderNames();
    }
    
    /**
     * Get a list of all values for a header
     * @param header the header name
     * @return an enumeration of strings
     */
    public Enumeration getHeaders(String header) {
        return headers==null?EMPTY_ENUM:headers.getHeaders(header);    
    }
    
    /**
     * Get the response content-type
     */
    public String getContentType() {
        return contentType;   
    }
    
    /**
     * Get the response content-length
     */
    public long getContentLength() {
        String cl = getHeader("content-length");
        return cl==null?0:Long.parseLong(cl);
    }
    
    public String toString() {
          StringBuffer buf = new StringBuffer();
          buf.append("Status          : ").append(getStatus()).append(" - ").append(getStatusText());
          buf.append("\nContentType     : ").append(getContentType());
          buf.append("\nContentLength   : ").append(getContentLength());
          buf.append("\nHeaders         :");
          for (Enumeration enum1 = getHeaderNames(); enum1.hasMoreElements(); ){
                String key = (String)enum1.nextElement();
                buf.append("\n   ").append(key).append(": ").append(getHeader(key));
          }
          return buf.toString();
     }
}
