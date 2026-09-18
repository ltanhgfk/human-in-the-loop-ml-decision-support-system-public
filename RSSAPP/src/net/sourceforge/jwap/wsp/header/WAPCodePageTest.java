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

import junit.framework.TestCase;


/**
 * jUnit test-cases for the WAP codepage
 */
public class WAPCodePageTest extends TestCase {
    WAPCodePage cp;

    public WAPCodePageTest(String name) {
        super(name);
    }

    public void testAccept() throws HeaderParseException {
        byte[] hdr = cp.encode("Accept", "application/vnd.wap.wmlc");
        assertEquals(new byte[] { (byte) 0x80, (byte) 0x94 }, hdr);
    }

    public void testAcceptLanguage() throws HeaderParseException {
        byte[] hdr = cp.encode("Accept-Language", "en;q=0.7");
        assertEquals(new byte[] { (byte) 0x83, 0x02, (byte) 0x99, 0x47 }, hdr);

        hdr = cp.encode("Accept-Language", "en,sv");
        assertEquals(new byte[] {
                (byte) 0x83, (byte) 0x99, (byte) 0x83, (byte) 0xF0
            }, hdr);
    }

    public void testAcceptCharset() throws HeaderParseException {
        byte[] hdr = cp.encode("Accept-Charset", "UTF-8;q=0.7");
        assertEquals(new byte[] { (byte) 0x81, 0x02, (byte) 0xea, 0x47 }, hdr);
    }

    public void testAcceptRanges() throws HeaderParseException {
        // TODO: complete testcase
        byte[] hdr = cp.encode("Accept-Ranges", "bytes");
        hdr = cp.encode("Accept-Ranges", "none");
    }

    public void testConnection() throws HeaderParseException, IOException {
        byte[] hdr = cp.encode("Connection", "Close");
        assertEquals(new byte[] { (byte) 0x89, (byte) 0x80 }, hdr);
        hdr = cp.encode("Connection", "keep-alive");
        assertEquals(new byte[] {
                (byte) 0x89, 'k', 'e', 'e', 'p', '-', 'a', 'l', 'i', 'v', 'e', 0
            }, hdr);
    }

    public void testCacheControl() 
    {
        byte[] hdr = cp.encode("cache-control", "no-cache");
        assertEquals(new byte[] {(byte) 0x88, (byte) 128}, hdr);
        hdr = cp.encode("cache-control", "no-store");
        assertEquals(new byte[] {(byte) 0x88, (byte) 129}, hdr);
        hdr = cp.encode("cache-control", "public");
        assertEquals(new byte[] {(byte) 0x88, (byte) 134}, hdr);
        hdr = cp.encode("cache-control", "private");
        assertEquals(new byte[] {(byte) 0x88, (byte) 135}, hdr);
        hdr = cp.encode("cache-control", "no-transform");
        assertEquals(new byte[] {(byte) 0x88, (byte) 136}, hdr);
        hdr = cp.encode("cache-control", "must-revalidate");
        assertEquals(new byte[] {(byte) 0x88, (byte) 137}, hdr);
        hdr = cp.encode("cache-control", "proxy-revalidate");
        assertEquals(new byte[] {(byte) 0x88, (byte) 138}, hdr);
    }
    
    public void testContentDisposition() throws HeaderParseException {
        byte[] hdr = cp.encode("Content-Disposition", "form-data");
        assertEquals(new byte[] { (byte) 0xae, (byte) 0x01, (byte) 128 }, hdr);

        hdr = cp.encode("Content-Disposition", "Attachment");
        assertEquals(new byte[] { (byte) 0xae, (byte) 0x01, (byte) 129 }, hdr);

        hdr = cp.encode("Content-Disposition", "Inline");
        assertEquals(new byte[] { (byte) 0xae, (byte) 0x01, (byte) 130 }, hdr);

        hdr = cp.encode("Content-Disposition", "UNK");
        assertEquals(new byte[] { (byte) 0xae, 4, 'U', 'N', 'K', 0 }, hdr);
    }

    public void testDecodeServer() throws HeaderParseException, IOException 
    {
        byte[] data = new byte[]{ (byte) 0xa6, 'A','p','a','c','h','e','/','1','.','3','.','2','9',0};
        Header hdr = cp.decode(data);
        assertNotNull(hdr);
        assertEquals("Server", hdr.getName());
        assertEquals("Apache/1.3.29", hdr.getValue());
    }
    
    public void testDecodeContentLength() throws HeaderParseException, IOException
    {
        byte[] data = new byte[]{(byte) 0x8d,0x02,0x02,(byte) 0xce};   
        Header hdr = cp.decode(data);
        assertNotNull(hdr);
        assertEquals("content-length", hdr.getName().toLowerCase());
        assertEquals("718", hdr.getValue());
    }
    
    public void testDecodeDate() throws HeaderParseException, IOException
    {
        byte[] data = new byte[]{(byte) 0x92,0x04, 0x40, (byte) 0x97, (byte) 0xa9, 0x5b};    
        Header hdr = cp.decode(data);
        assertNotNull(hdr);
        assertEquals("Date", hdr.getName());
        assertTrue(hdr.getValue().toString().startsWith("Tue, 4 May 2004 16:31:55"));
    }
    
    public void testDecodeContentType() throws HeaderParseException, IOException
    {
      byte[] data = cp.encode("Content-Type", "text/html");
      Header hdr = cp.decode(data);
      assertNotNull(hdr);
      assertEquals("Content-Type", hdr.getName());
      assertEquals("text/html", hdr.getValue());
      
      data = cp.encode("Content-Type", "application/unknown");
      hdr = cp.decode(data);
      assertNotNull(hdr);
      assertEquals("Content-Type", hdr.getName());
      assertEquals("application/unknown", hdr.getValue());

      data = cp.encode("Content-Type", "text/html; Charset=ISO-8859-4");
      hdr = cp.decode(data);
      assertNotNull(hdr);
      assertEquals("Content-Type", hdr.getName());
      assertEquals("text/html; Charset=ISO-8859-4", hdr.getValue());

      data = cp.encode("Content-Type","");
      hdr = cp.decode(data);
      assertEquals("Content-Type", hdr.getName());
      assertEquals("",hdr.getValue());
    }
    
    public void testDecodeConnection() throws HeaderParseException, IOException {
        byte[] data = cp.encode("Connection", "close");
        Header hdr = cp.decode(data);
        assertNotNull(hdr);
        assertEquals("Connection", hdr.getName());
        assertEquals("CLOSE", hdr.getValue());

        data = cp.encode("connection", "keep-alive");
        hdr = cp.decode(data);
        assertNotNull(hdr);
        assertEquals("Connection", hdr.getName());
        assertEquals("keep-alive", hdr.getValue());
    }
    
    public void setUp() throws IOException {
        cp = WAPCodePage.getInstance();
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
