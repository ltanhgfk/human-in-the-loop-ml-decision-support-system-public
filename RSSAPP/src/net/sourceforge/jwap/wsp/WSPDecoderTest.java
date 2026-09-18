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

import junit.framework.TestCase;

import net.sourceforge.jwap.wsp.header.Encoding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.Arrays;


/**
 * jUnit test-cases for the WSPDecoder
 */
public class WSPDecoderTest extends TestCase {
    public WSPDecoderTest(String arg0) {
        super(arg0);
    }

    public void testGetOctet() {
        byte[] data = { 0, 10, (byte) 200 };
        WSPDecoder d = new WSPDecoder(data);
        assertEquals(0, d.getOctet());
        assertEquals(10, d.getOctet());
        assertEquals((byte) 200, d.getOctet());

        try {
            d.getOctet();
            fail();
        } catch (ArrayIndexOutOfBoundsException ex) {
        }
    }

    public void testGetUint8() {
        byte[] data = { (byte) 200 };
        WSPDecoder d = new WSPDecoder(data);
        assertEquals(200, d.getUint8());

        try {
            d.getOctet();
            fail();
        } catch (ArrayIndexOutOfBoundsException ex) {
        }
    }

    public void testGetUint16() {
        byte[] data = { (byte) 0xaa, (byte) 0xbb };
        WSPDecoder d = new WSPDecoder(data);
        assertEquals(0xaabb, d.getUint16());

        try {
            d.getOctet();
            fail();
        } catch (ArrayIndexOutOfBoundsException ex) {
        }
    }

    public void testGetUint32() {
        byte[] data = { (byte) 0xaa, (byte) 0xbb, 0x01, 0x10 };
        WSPDecoder d = new WSPDecoder(data);
        assertEquals(0xaabb0110, d.getUint32());

        try {
            d.getOctet();
            fail();
        } catch (ArrayIndexOutOfBoundsException ex) {
        }
    }

    public void testGetUintVar() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(Encoding.uintVar(0));
        out.write(Encoding.uintVar(0xff));
        out.write(Encoding.uintVar(0x200));
        out.write(Encoding.uintVar(0x2000));
        out.write(Encoding.uintVar(0x20000));

        byte[] data = out.toByteArray();
        WSPDecoder d = new WSPDecoder(data);
        assertEquals(0x0, d.getUintVar());
        assertEquals(0xff, d.getUintVar());
        assertEquals(0x200, d.getUintVar());
        assertEquals(0x2000, d.getUintVar());
        assertEquals(0x20000, d.getUintVar());

        try {
            d.getOctet();
            fail();
        } catch (ArrayIndexOutOfBoundsException ex) {
        }
    }

    public void testGetCString() {
        WSPDecoder d = new WSPDecoder(new byte[] { 0 });
        assertEquals(null, d.getCString());

        try {
            d.getOctet();
            fail();
        } catch (ArrayIndexOutOfBoundsException ex) {
        }

        d = new WSPDecoder("Hello World\0abc\0\0def\0".getBytes());
        assertEquals("Hello World", d.getCString());
        assertEquals("abc", d.getCString());
        assertEquals(null, d.getCString());
        assertEquals("def", d.getCString());

        try {
            d.getOctet();
            fail();
        } catch (ArrayIndexOutOfBoundsException ex) {
        }
    }

    public void testGetBytes() {
        WSPDecoder d = new WSPDecoder(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 });
        assertTrue(Arrays.equals(new byte[] { 1, 2 }, d.getBytes(2)));
        assertTrue(Arrays.equals(new byte[] { 3, 4, 5, 6 }, d.getBytes(4)));
    }
    
    public void testGetIntegerValue() {
        long[] numbers = new long[]{0,127,128, 300,4096, Long.MAX_VALUE};
        for( int i=0; i< numbers.length; i++) {
            byte[] data = Encoding.integerValue(numbers[i]);
            // System.out.println(Utils.hexDump(numbers[i]+": ", data));
            WSPDecoder d = new WSPDecoder(data);
            long l = d.getIntegerValue();
            assertEquals(numbers[i],l);
        }        
    }
}
