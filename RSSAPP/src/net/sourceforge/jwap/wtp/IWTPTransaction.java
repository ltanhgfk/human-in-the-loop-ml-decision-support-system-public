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
package net.sourceforge.jwap.wtp;

import net.sourceforge.jwap.wtp.pdu.*;


public interface IWTPTransaction {
    public static final byte CLASS_TYPE_0 = 0x00;
    public static final byte CLASS_TYPE_1 = 0x01;
    public static final byte CLASS_TYPE_2 = 0x02;

    public void process(CWTPPDU pdu) throws EWTPAbortedException;

    public void process(CWTPEvent p) throws EWTPAbortedException;

    public void process(EWTPCorruptPDUException e);

    public void abort();

    public void abort(short abortReason);

    public void close(short reasonCode);

    public void setClassType(byte classType) throws IllegalArgumentException;

    public byte getClassType();

    public int getTID();

    public boolean isAborted();

    public short getAbortCode();
}
