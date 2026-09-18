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

import java.io.IOException;
import java.io.OutputStream;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.Date;
import java.util.StringTokenizer;


/**
 * This class represents a Header Codepage.
 *
 * @author Michel Marti
 */
public abstract class CodePage {
    private static final Class[] ENC_SIG = {
        OutputStream.class, Short.TYPE, String.class
    };
    private static final Class[] DEC_SIG = { byte[].class };
    private int pageCode;
    private String pageName;
    private boolean shortCut;

    protected CodePage(int code, boolean shortCut, String name) {
        if ((code < 1) || (code > 255)) {
            throw new IllegalArgumentException(code + ": Illegal page code.");
        }

        if (shortCut && (code > 31)) {
            throw new IllegalArgumentException(code +
                ": Illegal shortcut page code.");
        }

        this.shortCut = shortCut;
        pageCode = code;
        pageName = name;
    }

    /**
     * Encode a header.
     * @param key the header name
     * @param value the header value
     * @return the header encoded as byte array
     * @throws HeaderParseException if the header cannot be encoded
     */
    public abstract byte[] encode(String key, String value)
        throws HeaderParseException;

    /**
     * Encode a date header.
     * @param key the header name
     * @param value the value
     * @return the header encoded as byte array
     * @throws HeaderParseException if the header cannot be encoded
     */
    public abstract byte[] encode(String key, Date value)
        throws HeaderParseException;

    /**
     * Encode a long header.
     * @param key the header name
     * @param value the header value
     * @return the header encoded as byte array
     * @throws HeaderParseException if the header cannot be encoded
     */
    public abstract byte[] encode(String key, long value)
        throws HeaderParseException;

    /**
     * Convert (decode) a byte array containing a Header.
     * @param data the data to decode
     * @return a Header object
     * @throws HeaderParseException if the data cannot be decoded
     */
    public abstract Header decode(byte[] data)
        throws HeaderParseException, IOException;

    public int getPageCode() {
        return pageCode;
    }

    public String getPageName() {
        return pageName;
    }

    public boolean isShortCut() {
        return shortCut;
    }

    /*
     * Get the Shift sequence for this code page
     */
    public byte[] getBytes() {
        byte[] b = null;

        if (isShortCut()) {
            b = new byte[1];
            b[0] = (byte) getPageCode();
        } else {
            b = new byte[2];
            b[0] = 127;
            b[1] = (byte) getPageCode();
        }

        return b;
    }

    protected void encodeHeader(String header, OutputStream out, short wk,
        String value)
        throws SecurityException, NoSuchMethodException, 
            IllegalArgumentException, IllegalAccessException, 
            InvocationTargetException, IOException {
        // Method must be named "handleHeaderName()
        StringBuffer mname = new StringBuffer("encode");
        camelCase(mname, header);

        Method m = getClass().getMethod(mname.toString(), ENC_SIG);
        Object[] args = { out, new Short(wk), value };

        try {
            m.invoke(this, args);
        } catch (InvocationTargetException ite) {
            Throwable t = ite.getTargetException();

            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else if (t instanceof IOException) {
                throw (IOException) t;
            } else {
                throw ite;
            }
        }
    }

    protected String decodeHeaderField(String header, byte[] value)
        throws SecurityException, NoSuchMethodException, 
            IllegalArgumentException, IllegalAccessException, 
            InvocationTargetException {
        StringBuffer mname = new StringBuffer("decode");
        camelCase(mname, header);

        Method m = getClass().getMethod(mname.toString(), DEC_SIG);
        Object[] args = { value };

        return (String) m.invoke(this, args);
    }

    private void camelCase(StringBuffer sb, String name) {
        StringTokenizer strtok = new StringTokenizer(name, "-");

        while (strtok.hasMoreTokens()) {
            String p = strtok.nextToken().toLowerCase();
            char fc = p.charAt(0);
            sb.append(Character.toUpperCase(fc));

            if (p.length() > 1) {
                sb.append(p.substring(1));
            }
        }
    }
}
