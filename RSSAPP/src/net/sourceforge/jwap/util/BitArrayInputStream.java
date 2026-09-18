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

import java.util.*;


/**
 *
 */
public class BitArrayInputStream {
    private static final boolean debug = false;

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

    public BitArrayInputStream() {
    }

    /**
     * returns if the given bit ist set in byte <code>b</code>
     * 0 is the very left and 7 the very right bit in the byte
     */
    public boolean getBoolean(byte b, int bit) {
        BitSet bitset = getBitSet(b);

        return bitset.get(bit);
    }

    /**
     * returns a byte value of <code>length</code> bits
     * in byte <code>b</code>
     * beginning with bit <code>offset</code>
     * where 0 is the very left and 7 the very right bit in the byte
     */
    public byte getByte(byte b, int offset, int length) {
        BitSet bitset = getBitSet(b);
        byte result = 0;

        for (int i = 0; i < length; i++) {
            int bit = offset + (length - i - 1);

            if (bitset.get(bit)) {
                if (i == 8) {
                    result -= 128;
                } else {
                    result += pot[i];
                }
            }
        }

        return result;
    }

    /**
     * returns a short value of <code>length</code> bits
     * in byte <code>b</code>
     * beginning with bit <code>offset</code>
     * where 0 is the very left and 7 the very right bit in the byte
     */
    public short getShort(byte b1, byte b2, int offset, int length) {
        if (debug) {
            System.out.println("b1: " + BitArrayInputStream.getBitString(b1));
            System.out.println("b2: " + BitArrayInputStream.getBitString(b2));
        }

        BitSet bitset1 = getBitSet(b1);
        BitSet bitset2 = getBitSet(b2);
        short result = 0;
        int bit = length - 1;

        if ((offset < 0) || (length < 0)) {
            throw new IllegalArgumentException(
                "offset < 0 or length < 0 not allowed");
        }

        if ((offset + length) > 16) {
            throw new IllegalArgumentException("Length+length > 16 not allowed");
        }

        // wenn erstes Bit gesetzt und es werden 16 bits betrachtet
        if ((length == 16) && (offset == 0) && bitset1.get(0)) {
            result -= 32768;
            length--;
            offset++;
            bit--;
        }

        // erstes Byte
        int offset1 = offset;
        int length1 = length;

        if ((length + offset) >= 7) {
            length1 = 8 - offset;
        }

        if (debug) {
            System.out.println("offset1: " + offset1);
            System.out.println("length1: " + length1);
        }

        if ((length1 > 0) && (offset1 < 8) && (offset1 >= 0)) {
            for (int i = 0; i < length1; i++) {
                if (debug) {
                    System.out.println("offset1 +i: " + (offset1 + i) +
                        " |bit: " + bit);
                }

                if (bitset1.get(offset1 + i)) {
                    result += pot[bit];
                }

                bit--;
            }
        }

        // zweites Byte
        int length2 = (length + offset) - 8;
        int offset2 = offset - 8;

        if (offset2 < 0) {
            offset2 = 0;
        }

        if (debug) {
            System.out.println("offset2: " + offset2);
            System.out.println("length2: " + length2);
        }

        if ((length2 > 0) && (offset2 < 8) && (offset2 >= 0)) {
            for (int i = 0; i < length2; i++) {
                if (debug) {
                    System.out.println("offset2 +i: " + (offset2 + i) +
                        " |bit: " + bit);
                }

                if (bitset2.get(offset2 + i)) {
                    result += pot[bit];
                }

                bit--;
            }
        }

        return result;
    }

    public int getInt(byte b1, byte b2, byte b3, byte b4, int offset, int length) {
        if (debug) {
            System.out.println("b1: " + BitArrayInputStream.getBitString(b1));
            System.out.println("b2: " + BitArrayInputStream.getBitString(b2));
            System.out.println("b3: " + BitArrayInputStream.getBitString(b3));
            System.out.println("b4: " + BitArrayInputStream.getBitString(b4));
        }

        byte[] bytes = { b1, b2, b3, b4 };
        BitSet bitset = getBitSet(bytes[0]);
        int result = 0;
        int bit = length - 1;

        if ((offset < 0) || (length < 0)) {
            throw new IllegalArgumentException(
                "offset < 0 or length < 0 not allowed");
        }

        if ((offset + length) > 32) {
            throw new IllegalArgumentException("Length+length > 32 not allowed");
        }

        // wenn erstes Bit gesetzt und es werden 32 bits betrachtet
        if ((length == 32) && (offset == 0) && bitset.get(0)) {
            result = -2147483648;
            length--;
            offset++;
            bit--;
        }

        int offsetlocal = offset;
        int lengthlocal = length;

        // erstes Byte
        if ((length + offset) >= 7) {
            lengthlocal = 8 - offset;
        }

        if (debug) {
            System.out.println("offsetlocal: " + offsetlocal);
            System.out.println("lengthlocal: " + lengthlocal);
        }

        if ((lengthlocal > 0) && (offsetlocal < 8) && (offsetlocal >= 0)) {
            for (int i = 0; i < lengthlocal; i++) {
                if (debug) {
                    System.out.println("offsetlocal +i: " + (offset + i) +
                        " |bit: " + bit);
                }

                if (bitset.get(offsetlocal + i)) {
                    result += pot[bit];
                }

                bit--;
            }
        }

        // zweites bis viertes Byte
        for (int m = 1; m < 4; m++) {
            bitset = getBitSet(bytes[m]);
            lengthlocal = (length + offset) - (8 * m);
            offsetlocal = offset - (8 * m);

            if (offsetlocal < 0) {
                offsetlocal = 0;
            }

            if (lengthlocal > 8) {
                lengthlocal = 8;
            }

            if (debug) {
                System.out.println("offsetlocal: " + offsetlocal);
                System.out.println("lengthlocal: " + lengthlocal);
            }

            if ((lengthlocal > 0) && (offsetlocal < 8) && (offsetlocal >= 0)) {
                for (int i = 0; i < lengthlocal; i++) {
                    if (debug) {
                        System.out.println("offsetlocal +i: " +
                            (offsetlocal + i) + " |bit: " + bit);
                    }

                    if (bitset.get(offsetlocal + i)) {
                        result += pot[bit];
                    }

                    bit--;
                }
            }
        }

        return result;
    }

    public long getUIntVar(byte[] bytes, int offset) {
        int lastone = (offset + getLengthOfUIntVar(bytes, offset)) - 1;

        if (debug) {
            System.out.println("Last one: " + lastone);
        }

        int exp = 0; // pot[exp]
        long result = 0;

        for (int aktbyte = lastone; aktbyte >= offset; aktbyte--) {
            // aktbyte ist aktuelles byte
            if (debug) {
                System.out.println("byte: " + aktbyte);
            }

            BitSet set = getBitSet(bytes[aktbyte]);

            for (int i = 7; i > 0; i--) {
                if (debug) {
                    System.out.println("Bit: " + i);
                }

                if (set.get(i)) {
                    result += pot[exp];
                }

                exp++;
            }
        }

        return result;
    }

    public short getUInt8(byte b, int offset, int length) {
        return getShort((byte) 0, b, 8 + offset, length);
    }

    public short getUInt8(byte b) {
        return getUInt8(b, 0, 8);
    }

    public int getUInt16(byte b1, byte b2, int offset, int length) {
        return getInt((byte) 0, (byte) 0, b1, b2, 16, length);
    }

    public int getUInt16(byte b1, byte b2) {
        return getUInt16(b1, b2, 16, 16);
    }

    ////////////////////////////HELPERS//////////////////////////
    public int getLengthOfUIntVar(byte[] bytes, int offset) {
        for (int i = offset; i < bytes.length; i++) {
            if (!(getBitSet(bytes[i]).get(0))) {
                return i - offset + 1;
            }
        }

        return 0;
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
            result += ("" + i + ": " + getBitString(m[i]) +
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
        BitArrayInputStream b = new BitArrayInputStream();
        BitArrayOutputStream o = new BitArrayOutputStream();
        o.write(3, 8);
        o.write(33123435, 32);
        System.out.println(o.toString());

        byte[] bytes = o.toByteArray();
        System.out.println("" +
            b.getInt(bytes[1], bytes[2], bytes[3], bytes[4], 0, 32));
    }
}
