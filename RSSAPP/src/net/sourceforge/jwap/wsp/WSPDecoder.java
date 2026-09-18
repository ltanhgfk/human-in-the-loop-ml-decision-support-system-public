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

import java.io.ByteArrayOutputStream;
import java.util.Date;


/**
 * This class can be used to extract WSP Primitives from a byte array. The decoder
 * maintains a pointer (index) into the byte array which is increased when
 * calling one of the getter-methods. E.g. calling getUint8() to read a
 * octet will increase the index by one.
 *
 * @author Michel Marti
 */
public class WSPDecoder {
    private byte[] data;
    private int offset;

    private WSPDecoder() {
    }

    /**
     * Construct a new WSP Decoder
     * @param data the data to operate on
     */
    public WSPDecoder(byte[] data) {
        this.data = data;
    }

    public byte getOctet() {
        return data[offset++];
    }

    public short getUint8() {
        return (short) (data[offset++] & 0xff); // Make it unsigned
    }

    public int getUint16() {
        return (getUint8() << 8) | getUint8();
    }

    public int getUint32() {
        return (getUint16() << 16) | getUint16();
    }

    public long getUintVar() {
        int octet = 0;
        long result = 0;

        do {
            octet = getOctet();
            result <<= 7;
            result |= (octet & 0x7f);
        } while ((octet & 0x80) != 0);

        return result;
    }

    public String getCString() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        for (int i = getOctet(); i != 0; i = getOctet()) {
            out.write(i);
        }

        String str = new String(out.toByteArray());

        return ("".equals(str) ? null : str);
    }

    public String getTextString() {
        int o = getOctet();
        if( o != 127 ) {
            seek(-1);
        }    
        return getCString();
    }
    
    public byte[] getBytes(int count) {
        byte[] ret = new byte[count];
        System.arraycopy(data, offset, ret, 0, ret.length);
        offset += count;

        return ret;
    }

    public boolean isEOF() {
        return offset >= data.length;
    }

    public int getRemainingOctets() {
        int remain = data.length - offset;
        return remain<0?0:remain;
    }

    /**
     * Modify the array index
     * @param offset the offset (might be negative to seek backwards)
     * @return the new position of the index
     */
    public int seek(int offset) {
        this.offset += offset;
        if(this.offset < 0) {
            this.offset=0;
        } 
        return this.offset;
    }

    public int pos(int position) {
        int last = offset;
        offset=position;
        return last;    
    }

    public int pos() {
        return offset;    
    }
    
    public String getString(int length) {
        return new String(data, offset, length);
    }

    /**
     * Returns a short- or long-integer 
     */
    public long getIntegerValue() {
        long len = getUint8();
        long ret = 0;
        if( (len & 0x80) != 0 ) { // Short-Integer
            ret = len & 0x7f;
        } else {
            seek(-1);
            ret = getLongInteger();
        }
        return ret;
    }
    
    public long getValueLength()
    {
      long len = 0;
      len = getUint8();
      if( len == 31 ) {
        len = getUintVar();
      }
      return len;
    }
    
    public short getShortInteger() {
      return (short) (getUint8()&0x7f);      
    }
    
    public long getLongInteger() {
        long len = getUint8();
        long ret = 0;
        while(len > 0) {
            len--;
            ret  |= ((long)getUint8() << (len*8));
        }
        return ret;
    }

    public Date getDateValue() {
        long delta = getLongInteger();
        return new Date(delta*1000);
    }


    /** isShortInteger tests if the next value is a short int.
     */
    public boolean isShortInteger() {
	return (data[offset] <0 ); // n.b. bytes are signed
    }

    /** isString tests if the next value is a string.
     */
    public boolean isString() {
	return (data[offset] >=0x20 && data[offset] <0x80);
    }
}
