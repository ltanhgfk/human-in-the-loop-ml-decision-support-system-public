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

import net.sourceforge.jwap.wsp.pdu.CWSPHeaders;

/**
 * Base class for WSP GET/Post requests.
 * @see net.sourceforge.jwap.WAPClient 
 * @author Michel Marti
 *
 */
public abstract class Request 
{
    private String URL;
    private CWSPHeaders headers;
    
    protected Request(String URL) {
        this.URL=URL;
        headers = new CWSPHeaders();   
    }
    
    /**
     * Set a request header.
     * @param name the header name
     * @param value the header value
     */
    public void setHeader(String name, String value)
    {
        headers.setHeader(name,value);
    }
    
    /**
     * Get the URL for this request
     */
    public String getURL() {
        return URL;
    }

    CWSPHeaders getWSPHeaders() {
        return headers;   
    }

}
