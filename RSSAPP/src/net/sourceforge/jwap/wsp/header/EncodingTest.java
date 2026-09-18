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

import junit.framework.TestCase;


/**
 * jUnit-Testcases for the Encoding class
 */
public class EncodingTest extends TestCase {
    public EncodingTest(String name) {
        super(name);
    }

    public void testUintVar() {
        assertEquals(new byte[] { (byte) 0x82, (byte) 0x8F, 0x25 },
            Encoding.uintVar(0x87a5));
        assertEquals(new byte[] { 0x00 }, Encoding.uintVar(0x00));
        assertEquals(new byte[] { (byte) 0x81, 0x00 }, Encoding.uintVar(0x80));
    }

    public void testLongInteger() {
        assertEquals(new byte[] { 0x01, 0x00 }, Encoding.longInteger(0));
        assertEquals(new byte[] { 0x01, 0x01 }, Encoding.longInteger(1));
        assertEquals(new byte[] { 0x03, (byte) 0xab, 0x0f, (byte) 0xc2 },
            Encoding.longInteger(0xab0fc2));
    }

    public void testShortInteger() {
        assertEquals(new byte[] { (byte) 0x82 },
            Encoding.shortInteger((short) 2));
        assertEquals(new byte[] { (byte) 0x80 },
            Encoding.shortInteger((short) 0));
        assertEquals(new byte[] { (byte) 0xff },
            Encoding.shortInteger((short) 127));
    }

    public void testIntegerValue() {
        assertEquals(new byte[] { (byte) 0x80}, Encoding.integerValue(0));    
        assertEquals(new byte[] { (byte) 0xff}, Encoding.integerValue(127));
        assertEquals(new byte[] { (byte) 0x01, (byte) 0x80}, Encoding.integerValue(0x80));
    }
    
    public void testQualityFactor() {
        assertEquals(new byte[] { 0x0b }, Encoding.qualityFactor((float) 0.1));
        assertEquals(new byte[] { 0x64 }, Encoding.qualityFactor((float) 0.99));
        assertEquals(new byte[] { (byte) 0x83, 0x31 },
            Encoding.qualityFactor((float) 0.333));
    }

    private static void assertEquals(byte[] b1, byte[] b2) {
        String s1 = null;
        String s2 = null;

        if (b1 != null) {
            StringBuffer sb = new StringBuffer();

            for (int i = 0; i < b1.length; i++) {
                String h = Integer.toHexString(b1[i] & 0xff);

                if (h.length() < 2) {
                    sb.append("0");
                }

                sb.append(h).append(" ");
            }

            s1 = sb.toString();
        }

        if (b2 != null) {
            StringBuffer sb = new StringBuffer();

            for (int i = 0; i < b2.length; i++) {
                String h = Integer.toHexString(b2[i] & 0xff);

                if (h.length() < 2) {
                    sb.append("0");
                }

                sb.append(h).append(" ");
            }

            s2 = sb.toString();
        }

        assertEquals(s1, s2);
    }
}
