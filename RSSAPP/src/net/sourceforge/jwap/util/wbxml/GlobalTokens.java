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

package net.sourceforge.jwap.util.wbxml;

/**
 * Global Tokens are common across all document types and are present in all code spaces and all code pages.
 *  All tokens have hexadecimal values. 
 * 
 * @author <a href="mailto:suvarna@witscale.com">Suvarna Kadam</a>
 */


public interface GlobalTokens {
	final public static byte SWITCH_PAGE = (byte) 0x00;
	final public static byte END = (byte) 0x01;
	final public static byte ENTITY = (byte) 0x02;
	final public static byte STR_ISTR_I = (byte) 0x03;
	final public static byte LITERAL = (byte) 0x04;
	final public static byte EXT_I_0 = (byte) 0x40;
	final public static byte EXT_I_1 = (byte) 0x41;
	final public static byte EXT_I_2 = (byte) 0x42;
	final public static byte PI = (byte) 0x43;
	final public static byte LITERAL_C = (byte) 0x44;
	final public static byte EXT_T_0 = (byte) 0x80;
	final public static byte EXT_T_1 = (byte) 0x81;
	final public static byte EXT_T_2 = (byte) 0x82;
	final public static byte STR_T = (byte) 0x83;
	final public static byte LITERAL_A = (byte) 0x84;
	final public static byte EXT_0 = (byte) 0xC0;
	final public static byte EXT_1 = (byte) 0xC1;
	final public static byte EXT_2 = (byte) 0xC2;
	final public static byte OPAQUE = (byte) 0xC3;
	final public static byte LITERAL_AC = (byte) 0xC4;

}
