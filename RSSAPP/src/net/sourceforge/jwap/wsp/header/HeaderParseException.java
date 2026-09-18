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


/**
 * This exception will be thrown if encoding/decoding of headers fails.
 *
 * @author Michel Marti
 */
public class HeaderParseException extends RuntimeException {
    private Throwable throwable;

    public HeaderParseException() {
        super();
    }

    public HeaderParseException(String msg) {
        super(msg);
    }

    public HeaderParseException(String msg, Throwable t) {
        super(msg);
        throwable = t;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public String getMessage() {
        if ((throwable == null) || (throwable.getMessage() == null)) {
            return super.getMessage();
        }

        return new StringBuffer(super.getMessage()).append(" ")
                                                   .append(throwable.getMessage())
                                                   .toString();
    }
}
