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
package net.sourceforge.jwap.wsp.header;

import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;


/**
 * Helper class for tokenizing header fields. A HeaderToken consists of a token value
 * and optional parameters.
 * <p>
 * <b>Example usage</b>
 * <pre>
 * for( Enumeration e = HeaderToken.tokenize("en; q=0.8, fr; q=0.2"); e.hasMoreElements(); )
 * {
 *   HeaderToken ht = (HeaderToken) e.nextElement();
 *   String token = token.getToken(); // "en" for first iteration, "fr" for second iteration
 *   String qval = token.getParameter("q"); // "0.8" for first iteration, "0.2" for second iteration
 * }
 * </pre>
 *
 * @author Michel Marti
 *
 */
public class HeaderToken {
    private static final Enumeration EMPTY_ENUM = new Vector().elements();
    private String token;
    private Properties parameters;

    private HeaderToken(String token) {
        parameters = new Properties();
        this.token = token.trim();
    }

    /**
     * Returns an enumeration of HeaderToken objects by tokenizing the specified
     * String.
     *
     * @param value a header field
     * @return an enumeration of HeaderToken's
     */
    public static Enumeration tokenize(String value) {
        if (value == null) {
            return EMPTY_ENUM;
        }
        value=value.trim();
        Vector v = new Vector();
        
        if( value.equals("") ) {
          v.add(new HeaderToken(""));
          return v.elements();
        }
        
        StringTokenizer strtok = new StringTokenizer(value, ",");

        while (strtok.hasMoreTokens()) {
            String token = strtok.nextToken();
            StringTokenizer st2 = new StringTokenizer(token, ";");
            token = st2.nextToken();

            HeaderToken ht = new HeaderToken(token);
            v.add(ht);

            while (st2.hasMoreTokens()) {
                String param = st2.nextToken();
                String key;
                String val;
                int p = param.indexOf("=");

                if (p > 0) {
                    key = param.substring(0, p);
                    val = param.substring(p + 1);
                } else {
                    key = param;
                    val = "";
                }

                ht.addParameter(key.toLowerCase(), val);
            }
        }

        return v.elements();
    }

    public String getParameter(String key) {
        return parameters.getProperty(key);
    }

    public boolean hasParameters() {
        return parameters.size() > 0;
    }

    public String getToken() {
        return token;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(token).append("=").append(parameters);

        return sb.toString();
    }

    private void addParameter(String key, String value) {
        parameters.put(key.trim(), value.trim());
    }
}
