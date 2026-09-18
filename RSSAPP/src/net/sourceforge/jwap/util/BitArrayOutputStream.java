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
package net.sourceforge.jwap.util;

import java.io.ByteArrayOutputStream;
import java.util.BitSet;


public class BitArrayOutputStream {
    public static final boolean debug = false;

    // to avoid Math.pow(2,b) take pot[b]
    private static final long[] pot = {
        1L, 2L, 4L, 8L, 16L, 32L, 64L, 128L, 256L, 512L, 1024L, 2048L, 4096L,
        8192L, 16384L, 32768L, 65536L, 131072L, 262144L, 524288L, 1048576L,
        2097152L, 4194304L, 8388608L, 16777216L, 33554432L, 67108864L,
        134217728L, 268435456L, 536870912L, 1073741824L, 2147483648L,
        4294967296L, 8589934592L, 17179869184L, 34359738368L, 68719476736L,
        137438953472L, 274877906944L, 549755813888L, 1099511627776L,
        2199023255552L, 4398046511104L, 8796093022208L, 17592186044416L,
        35184372088832L, 70368744177664L, 140737488355328L, 281474976710656L,
        562949953421312L, 1125899906842624L, 2251799813685248L,
        4503599627370496L, 9007199254740992L, 18014398509481984L,
        36028797018963968L, 72057594037927936L, 144115188075855872L,
        288230376151711744L, 576460752303423488L, 1152921504606846976L,
        2305843009213693952L, 4611686018427387904L, 9223372036854775807L
    }; // b = 64
    ByteArrayOutputStream outstream = new ByteArrayOutputStream();
    int aktBit = 0;
    int sByte=0;

    ////////////////////////////////////////////////////////////////////////////
    //////////////////// METHODS CONCERNING THE STREAM /////////////////////////

    /**
     * fills up the aktual byte with bits "0" for fill = false
     *                                and "1" for fill = true
     */
    public synchronized void flush(boolean fill) {
        while (aktBit != 0) {
            write(fill);
        }
    }

    public byte[] toByteArray() {
        return outstream.toByteArray();
    }

    ////////////////////////////////////////////////////////////////////////////
    //////////////////// WRITE SINGLE BITS /////////////////////////////////////

    /**
     * writes a bit down to the stream.
     * is used by all the other methods in this class.
     */
    public synchronized void write(boolean b) {
        if(b) {
            sByte |= (1 << (7-aktBit));
        }
        aktBit++;

        // if byte is full, write down into outstream
        if (aktBit == 8) {
            outstream.write(sByte);
            aktBit = sByte = 0;
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    //////////////////// WRITE METHODS /////////////////////////////////////////

    /**
     * writes <code>count</code> bits from byte <code>_b</code> beginning on the right hand.
     * <code>Count</code> has NOT to be in byte-size (e.g. 8, 16, 24, 32,...)
     */
    
    public synchronized void write(byte _b, int count) {
        int b = _b;

        // bit #0 (encoding positive/negative numbers)
        if (b < 0) {
            if (count == 8) {
                write(true);
                count = 7;
            }

            b += 128;

            // if _b >= 0
        } else {
            if (count == 8) {
                write(false);
                count = 7;
            }
        }

        for (int i = 7; i >= count; i--) {
            if (b >= pot[i]) {
                b -= pot[i];
            }
        }

        // bits #1 - #7
        for (int i = count - 1; i >= 0; i--) {
            if (b >= pot[i]) {
                b -= pot[i];
                write(true);
            } else {
                write(false);
            }
        }
    }
    
    // Optimized write, doesn't work yet...
    private synchronized void _write(byte _b, int count) {
        int b = _b&0xFF, m = 0x80;
        for( int i=0; i<count; i++ ) {
            write( (b & (m>>i)) != 0 );
        }
    }

    public synchronized void reset() {
        outstream.reset();
        sByte = aktBit = 0;
    }

    public synchronized void write(int b, int count) {
        // bit #0 (encoding positive/negative numbers)
        if (b < 0) {
            if (count == 16) {
                write(true);
                count = 15;
            }

            b += 32768;

            // if b >= 0
        } else {
            if (count == 16) {
                write(false);
                count = 15;
            }
        }

        for (int i = 15; i >= count; i--) {
            if (b >= pot[i]) {
                b -= pot[i];
            }
        }

        // bits #1 - #7
        for (int i = count - 1; i >= 0; i--) {
            if (b >= pot[i]) {
                b -= pot[i];
                write(true);
            } else {
                write(false);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    //////////////////// WRITE UINT* METHODS ///////////////////////////////////

    /**
     * writes <code>count</code> bits of an integer into the stream beginning on the right hand.
     * <code>Count</code> has NOT to be in byte-size (e.g. 8, 16, 24, 32,...)
     *
     * also use it for a uint8  (use bits=8)
     *                or unit16 (use bits=16)
     *                or uint32 (use bits=32)
     * according to WAP-230-WSP-10010705-a secition 8.1.2
     */
    public synchronized void write(long b, int count) {
        // bit #0 (encoding positive/negative numbers)
        if (b < 0) {
            if (count == 32) {
                write(true);
                count = 31;
            }

            b += 2147483648L;

            // if b >= 0
        } else {
            if (count == 32) {
                write(false);
                count = 31;
            }
        }

        for (int i = 31; i >= count; i--) {
            if (b >= pot[i]) {
                b -= pot[i];
            }
        }

        // bits #1 - #7
        for (int i = count - 1; i >= 0; i--) {
            if (b >= pot[i]) {
                b -= pot[i];
                write(true);
            } else {
                write(false);
            }
        }
    }

    /**
     * write a unintvar
     * according to WAP-230-WSP-10010705-a secition 8.1.2
     */
    public synchronized void writeUintVar(long b) throws NumberFormatException {
        if (b < 0) {
            throw new NumberFormatException("No negative values supported");
        } else if (b == 0) {
            for (int i = 0; i < 8; i++) {
                write(false);
            }
        } else {
            int i = 63;

            while (b < pot[i]) {
                i--;
            }

            int length = i;

            // i+1 ist jetzt die Anzahl der benötigten bits
            int fill = 7 - ((i + 1) % 7); //nur 7 bits payload

            if (fill == 7) {
                fill = 0;
            }

            if (debug) {
                System.out.println("used bits: " + (i + 1) + " | to fill: " +
                    fill);
            }

            // set the continue bit
            if ((i + 1) <= 7) {
                write(false);
            } else {
                write(true);
            }

            // fill up with "0"
            for (int s = 0; s < fill; s++) {
                write(false);
            }

            for (; i >= 0; i--) {
                //write continue bit
                if ((((i + 1) % 7) == 0) && (length > 7)) {
                    if ((i + 1) <= 7) {
                        write(false);
                    } else {
                        write(true);
                    }
                }

                //write payload
                if (b >= pot[i]) {
                    b -= pot[i];
                    write(true);
                } else {
                    write(false);
                }
            }
        }
    }

    /**
     * writes <code>count</code> bits from byte <code>_b</code> beginning on the right hand.
     * <code>Count</code> has NOT to be in byte-size (e.g. 8, 16, 24, 32,...)
     */
    public synchronized void write(byte[] b) {
        for (int i = 0; i < b.length; i++) {
            this.write(b[i], 8);
        }
    }

    /**
     * writing multiple octets according to
     * WAP-230-WSP-20010705-a section 8.4.2.1 "Basic rules for header syntax"
     */

    /*public synchronized void writeMultOct(long b) throws NumberFormatException{
      if (b<0){
        throw new NumberFormatException("No negative values supported");
      } else if (b>127){
        // LONG INTEGER ////////////////////////////////////////////////
        boolean length = false;
        for(int i=63; i>=0; i--){
          if (b >= pot[i]){
            if (length == false) {
              // writing length as short integer
              length = true;
              long thelength=(long)(Math.ceil(((double)i+1)/8));
              writeMultOct(thelength);

              // fill first bits in first byte with "0"
              int swap = 8-((i+1)%8);
              System.out.println("i:" + i + " swap:" + swap + " length:" + thelength);
              if (swap != 8){
                for (int a=0; a<swap; a++){
                  write(false);
                }
              }
            }
            b -= pot[i];
            write(true);
          }
          else if(length == true){
            write(false);
          }
        }
      } else {
        // SHORT INTEGER ////////////////////////////////////////////////
        write(true);
        // bits #1 - #7
        for(int i=6; i>=0; i--){
          if (b >= pot[i]){
            b -= pot[i];
            write(true);
          } else {
            write(false);
          }
        }
      }
    }
    */

    ////////////////////////////////////////////////////////////////////////////
    //////////////////// HELPER METHODS ////////////////////////////////////////

    /**
     * constructs a string representation of the outputstream.
     */
    public synchronized String toString() {
        String result = "";
        byte[] array = outstream.toByteArray();

        for (int i = 0; i < array.length; i++) {
            result += (getBitString(array[i]) + "\r\n");
        }

        result+=getBitString((byte) (sByte&0xFF));

        return result;
    }

    /**
     *  Constructs a binary representation of the byte <code>m</code>
     */
    public static String getBitString(byte m) {
        String result = "";

        if (m < 0) {
            result += "1";
            m += 128;

            // if m >= 0
        } else {
            result += "0";
        }

        for (int i = 6; i >= 0; i--) {
            if (m >= pot[i]) {
                m -= pot[i];
                result += "1";
            } else {
                result += "0";
            }
        }

        return result;
    }

    /**
     *  Constructs a binary representation of the byte-array <code>m</code>
     */
    public static String getBitString(byte[] m) {
        String result = "";

        for (int i = 0; i < m.length; i++) {
            result += (getBitString(m[i]) +
            System.getProperty("line.separator"));
        }

        return result;
    }

    /**
     *  Constructs a BitSet of the byte <code>m</code>
     */
    public static BitSet getBitSet(byte m) {
        BitSet result = new BitSet(8);

        if (m < 0) {
            result.set(0);
            m += 128;
        }

        for (int i = 6; i >= 0; i--) {
            if (m >= pot[i]) {
                m -= pot[i];
                result.set(7 - i);
            }
        }

        return result;
    }

    /**
     * Test method
     */
    public static void main(String[] args) {
        BitArrayOutputStream m = new BitArrayOutputStream();

        //m.write(23L, 8);
        //m.write(18736L, 16);
        m.write("Hallo".getBytes());

        /*try{
          m.writeUintVar(19L);
        } catch (NumberFormatException e){
          e.printStackTrace();
        }*/
        System.out.println(m.toString());
    }
}
