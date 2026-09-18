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
package net.sourceforge.jwap.wsp.pdu;

import net.sourceforge.jwap.util.BitArrayInputStream;
import net.sourceforge.jwap.util.BitArrayOutputStream;


public class CWSPReply extends CWSPPDU {
    /**
     * Table 36
     */
    public static final short _100_Continue = 0x10;
    public static final short _101_Switching_Protocols = 0x11;
    public static final short _200_OK_Success = 0x20;
    public static final short _201_Created = 0x21;
    public static final short _202_Accepted = 0x22;
    public static final short _203_Non_Authoritative_Information = 0x23;
    public static final short _204_No_Content = 0x24;
    public static final short _205_Reset_Content = 0x25;
    public static final short _206_Partial_Content = 0x26;
    public static final short _300_Multiple_Choices = 0x30;
    public static final short _301_Moved_Permanently = 0x31;
    public static final short _302_Moved_temporarily = 0x32;
    public static final short _303_See_Other = 0x33;
    public static final short _304_Not_modified = 0x34;
    public static final short _305_Use_Proxy = 0x35;
    public static final short _307_Temporary_Redirect = 0x37;
    public static final short _400_Bad_Request = 0x40;
    public static final short _401_Unauthorized = 0x41;
    public static final short _402_Payment_required = 0x42;
    public static final short _403_Forbidden = 0x43;
    public static final short _404_Not_Found = 0x44;
    public static final short _405_Method_not_allowed = 0x45;
    public static final short _406_Not_Acceptable = 0x46;
    public static final short _407_Proxy_Authentication_required = 0x47;
    public static final short _408_Request_Timeout = 0x48;
    public static final short _409_Conflict = 0x49;
    public static final short _410_Gone = 0x4A;
    public static final short _411_Length_Required = 0x4B;
    public static final short _412_Precondition_failed = 0x4C;
    public static final short _413_Request_entity_too_large = 0x4D;
    public static final short _414_Request_URI_too_large = 0x4E;
    public static final short _415_Unsupported_media_type = 0x4F;
    public static final short _416_Requested_Range_Not_Satisfiable = 0x50;
    public static final short _417_Expectation_Failed = 0x51;
    public static final short _500_Internal_Server_Error = 0x60;
    public static final short _501_Not_Implemented = 0x61;
    public static final short _502_Bad_Gateway = 0x62;
    public static final short _503_Service_Unavailable = 0x63;
    public static final short _504_Gateway_Timeout = 0x64;
    public static final short _505_HTTP_version_not_supported = 0x65;

    /**
     * status
     * uint8
     * use constants in this class beginning with "_"
     */
    private long status;

    /**
     * ContentType
     * mult. octets
     */
    private String contentType;

    public CWSPReply(long status, byte[] payload, String contentType) {
        this.pduType = CWSPPDU.PDU_TYPE_REPLY;
        this.status = status;
        this.payload = payload;
        this.contentType = contentType;
    }

    public byte[] toByteArray() {
        BitArrayOutputStream result = new BitArrayOutputStream();
        result.write(pduType, 8);

        //--------------------------------//
        result.write(status, 8);

        byte[] cap = capabilities.getBytes();
        byte[] head = headers.getBytes();
        byte[] ctype = contentType.getBytes();
        result.writeUintVar(head.length + ctype.length + 1);
        result.write(ctype);
        result.write(0x00, 8);
        result.write(head);
        result.write(payload);

        //--------------------------------//
        return result.toByteArray();
    }

    public byte getStatus() {
        BitArrayOutputStream m = new BitArrayOutputStream();
        m.write(status, 8);

        byte[] b = m.toByteArray();

        return b[0];
    }

    /**
     * use constants in this class!
     */
    public void setStatus(short status) {
        this.status = status;
    }

    public void setContentType(String type) {
        contentType = type;
    }

    public String getContentType() {
        return contentType;
    }

    public String toString() {
        BitArrayInputStream bin = new BitArrayInputStream();

        return "Status:      " + status + System.getProperty("line.separator") +
        "ContentType: " + contentType + System.getProperty("line.separator") +
        "encoded: " + System.getProperty("line.separator") +
        BitArrayInputStream.getBitString(this.toByteArray());
    }
}
