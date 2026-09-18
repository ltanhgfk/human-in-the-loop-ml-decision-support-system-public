/**
 * MMSLIB - A Java Implementation of the MMS Protocol
 * Copyright (C) 2004 Simon Vogl 
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
package at.jku.soft.mms.lib;

import net.sourceforge.jwap.wsp.*;
import net.sourceforge.jwap.wsp.pdu.*;
//import net.sourceforge.jwap.util.*;
import java.io.*;
import java.util.*;
import org.apache.log4j.*;

import at.jku.soft.mms.util.DataBlock;

public class MMSPDU 
{
    static int debug=0;
    static Logger logger = Logger.getLogger(MMSPDU.class);

    protected MMSHeaders mmsHeaders;

    private String  mmsContentLocation;
    private long    mmsExpiry;
    private boolean mmsExpiryRelative;

    /** keep a copy of the raw coded body, even if multipart. \
     * This is useful of efficient creation of replies (hopefully).
     */
    public byte[] body;
    public Vector parts;

    public MMSPDU() {
	mmsHeaders = new MMSHeaders();
	body=null;
    }

    public MMSPDU(MMSPDU pdu) {
	// @todo: copy headers
	mmsHeaders = new MMSHeaders(pdu.getMmsHeaders());
	body=null;
    }

    public MMSPDU(MMSHeaders head) {
	mmsHeaders = head;
	body=null;
    }

    public MMSHeaders getMmsHeaders() { return mmsHeaders; }

    public void setHeaders(MMSHeaders head) { this.mmsHeaders = head; }

    public boolean hasBody() 
    {
	int t = mmsHeaders.getMessageType();
	if (body != null || ( parts != null && parts.size()>0)) {
	    return true;
	} else {
	    return false;
	}
    }

    private int headerLength=0; // set by parseMMSHeaders
 
    /** setPayload gets the contents of the pdu as a byte array (as received
     * via WAP or from a file), and initializes all data fields by parsing the
     * contents of the binary PDU. 
     *
     */
    public void setPayload(byte[] payload)
    {
	try {
	    WSPDecoder dec = new WSPDecoder(payload);
	
	    mmsHeaders = parseMMSHeaders(dec);	

	    // if has body...
	    if (mmsHeaders.getMessageType() == 0x04 
		|| mmsHeaders.getMessageType() == 0x00 ) {
		body = new byte[payload.length-headerLength];
		System.arraycopy(payload, headerLength, 
				 body, 0, payload.length-headerLength);

		if ( mmsHeaders.isMultiPart() ) {
		    //parseMultiPartEntries(payload,headerLength,payload.length-headerLength);
		    parseMultiPartEntries(dec);
		} else {
		    logger.error("NEED TO SET THE BODY TO THE REST OF THE CONTENT!");
		}
	    }
	} catch(Exception e) {
	    System.out.flush();
	    e.printStackTrace();
	}
    }


    protected MMSHeaders parseSingleMmsHeader(WSPDecoder dec, MMSHeaders head)
    {
	if ( dec.isShortInteger()) {
	    int hname = dec.getShortInteger();
		    
	    hname &=0x7f;
	    //logger.debug("[@ 0x" + dec.pos() + "] found a "
	    //		 + head.nameForToken(hname));
	    switch (hname) {
	    case MMSHeaders.HEADER_X_Mms_Content_Location: 
		{		    
		    String uri = dec.getCString();
		    mmsContentLocation = uri;
		}
		break;
	    case MMSHeaders.HEADER_X_Mms_Message_Type: 
		{
		    int val = (int)dec.getIntegerValue();
		    head.setMessageType(val);
		}
		break;
	    case MMSHeaders.HEADER_X_Mms_Expiry: 
		{
		    int l = (int)dec.getValueLength();
		    short relative = dec.getShortInteger();
			
		    mmsExpiryRelative = (relative == 0x01);
		    if ( mmsExpiryRelative  ) { // expiry in seconds
			logger.debug("    relative expiry");
			long exp = dec.getLongInteger();
			logger.debug("    expires: "+exp+ " ~ " + (exp/86400) + " days");
		    } else {
			long exp = dec.getLongInteger();
			logger.warn("    absolute expiry not implemented!");
		    }
		}
		break;
	    case MMSHeaders.HEADER_To:
		{
		    String to = dec.getCString();
		    head.setTo(to);
		}
		break;
	    case MMSHeaders.HEADER_From:
		{
		    // read from 
		    long l = dec.getValueLength();
		    short insertAddr = dec.getShortInteger();

		    if ( insertAddr == 0x00 ) { // then it is present
			String from = dec.getCString();
			head.setFrom(from);
		    } // else not available. insert yourself.
		}
		break;
	    case MMSHeaders.HEADER_X_Mms_Transaction_Id:
		{
		    String tid = dec.getCString();
		    head.setTransactionId(tid);
		}
		break;
	    case MMSHeaders.HEADER_X_Mms_Response_Status:
		{
		    //response-status-value
		    int val = (int)dec.getIntegerValue();
		    head.setResponseStatus(val);
		    if (val != 0x00) {
			System.out.println("ERROR "+val);
		    }
		}
		break;
	    case MMSHeaders.HEADER_Content_Type: 
		parseMMSHeaderContentType(head.getContentType(), dec);
		head.setComplete();
		break;
	    case MMSHeaders.HEADER_X_Mms_Delivery_Report :
		head.setReadReport( (int)dec.getIntegerValue() );
		break;
	    case MMSHeaders.HEADER_X_Mms_Message_Class :
		head.setReadReport( (int)dec.getIntegerValue() );
		break;
	    case MMSHeaders.HEADER_X_Mms_Priority :
		head.setReadReport( (int)dec.getIntegerValue() );
		break;
	    case MMSHeaders.HEADER_X_Mms_Read_Report :
		head.setReadReport( (int)dec.getIntegerValue() );
		break;
	    case MMSHeaders.HEADER_Bcc:
		break;
	    case MMSHeaders.HEADER_Message_ID:
		head.setMessageId( dec.getCString() );
		break;
	    case MMSHeaders.HEADER_X_Mms_MMS_Version:
		int ver = (int)dec.getShortInteger();
		int maj,min;
		String version;
		maj = ver>>4;
		min = ver & 0x0f;
		if (min == 0x0f) { 
		    version = Integer.toString(maj);
		} else {
		    version = "" + maj+"."+min;
		}
		head.setVersion(version);
		head.setMmsVersion(maj, min);
		break;
	    case MMSHeaders.HEADER_Date:
		{
		long val = dec.getIntegerValue();
		head.setDate(val);
		}
		break;
	    case MMSHeaders.HEADER_Content_ID: {
		byte b = dec.getOctet();
		String cid = dec.getCString();
		if (b!= 34) { // its encoded as a quoted string, but who knows.
		    cid = (char)b + cid;
		}
		logger.debug("    content-id "+cid);		
		head.setContentId(cid);
		} 
		break;
	    default:
		logger.info("[@ 0x" + Integer.toHexString(dec.pos()-1) 
			    + "] found a "
			    + head.nameForToken(hname));
		if ( dec.isString() ) {
		    String s = dec.getCString();
		    logger.debug("    strval "+s);
		} else {
		    long val = dec.getIntegerValue();
		    logger.debug("    intval "+val);
		    logger.debug("    intval 0x"+Long.toHexString(val));
		}
	    }		    
	} else if ( dec.isString()) {
	    // header field is string-encoded.
	    String header_name  = dec.getCString();
	    String header_value = dec.getCString();
	    logger.info("@" + Integer.toHexString(dec.pos()) 
			+ " " +  header_name + ": " + header_value); 
	    // we ignore custom headers at the moment.
	    // should go into a map in mmsHeaders.
	} else {
	    head.setComplete();
	    
	}
	return head;
    }


    protected MMSHeaders parseMMSHeaders(WSPDecoder dec)
    {
	MMSHeaders mmsHeaders = new MMSHeaders();

	boolean contentFound = false;

	if (dec.isEOF()){
	    return mmsHeaders;
	}
	try{
	    while ( ! dec.isEOF() && 		    
		    ! mmsHeaders.isComplete() ) {
		mmsHeaders = parseSingleMmsHeader(dec, mmsHeaders);
	    }
	} catch(Exception e){
	    // ignore and return
	    e.printStackTrace();
	    return mmsHeaders;
	}

	System.out.println("-------\n" + mmsHeaders + "\n----------");

	headerLength = dec.pos();

	return mmsHeaders;
    }
    

    
    //    protected void parseMMSHeaderContentType(ContentType ct, byte[] bytes, int offset, int count)
    protected void parseMMSHeaderContentType(ContentType ct, WSPDecoder dec)
    {
	int len = 0;
	int skip;
	int end = dec.pos();
	// constrained media: we start with a constrained encoding,
	// i.e. either text (ascii 32-127, or a known-media (128-255).
	// for content-general-form, we start with a length indicator
	// i.e. either 0..31 (0..30 - length or 31:esc+len:=uintvar)

	logger.debug("----contentType@[0x"+Long.toHexString(dec.pos())+"] " + dec.isString() + " " + dec.isShortInteger() );


	if (! dec.isString() && ! dec.isShortInteger() ) { 
	    // Value-length
	    len = (int)dec.getValueLength();

	    logger.debug("general form " + len);
	    end = dec.pos()+len;
	    
	    //Media-type
	    if ( dec.isShortInteger() ) {
		// Well-known-Media
		int cType = (int)dec.getShortInteger();
		ct.setContentType(cType);
		System.out.println("Content-Type: " + ct.knownTypes[cType]);
	    } else {
		// Extension-Media
		String cont = dec.getCString();
		ct.setContentType(cont);
		System.out.println("Content-Type: "+cont);		
	    }
	    // *(Parameter:)
	    while (dec.pos() < end ) { // parse optional parameters
		int opt = dec.getShortInteger();

		switch (opt) {
		case ContentType.WSP_PARAM_NAME:
		    ct.setName(dec.getCString());
		    break;
		case ContentType.WSP_PARAM_CHARSET:
		    int charset = (int)dec.getIntegerValue();
		    System.out.println("Charset: 0x" + Integer.toHexString(charset));
		    break;
		case ContentType.WSP_PARAM_TYPE_RELATED:		    
		    if ( dec.isShortInteger()) {
			int t = dec.getShortInteger();
			ct.setType(ContentType.knownTypes[t]);
		    } else { // string follows
			String type = dec.getCString();
			logger.debug("    Content-Type: "+ type);
			ct.setType(type);
		    } 
		    break;
		case 0x0a: // Start -> string
		    String start = dec.getCString();
		    logger.debug("    Start: " + start);
		    ct.setStart(start);
		    break;
		default:
		    logger.warn("Unknown Content-type Parameter "+opt);
		    if (dec.isString() ) {
			String s = dec.getCString();
			logger.warn("    strval "+s);
		    } else {
			long val = dec.getIntegerValue();
			logger.warn("    intval "+val);
			logger.warn("    intval 0x"+Long.toHexString(val));
		    }
		}
	    }
	} else { // short format - we get the content type alone
	    if (dec.isString() ) {
		String s = dec.getCString();
		ct.setContentType(s);
	    } else {
		long val = dec.getIntegerValue();
		ct.setContentType((int)val);
	    }
	}
    }


    //public void parseMultiPartEntries(byte[]bytes, int offset, int count)
    public void parseMultiPartEntries(WSPDecoder dec)
    {
	parts = new Vector();
	int len;
	// This is a class, that helps us with decoding bits

        long nEntries = dec.getUintVar();
	logger.debug("nEntries " + nEntries);

	// newer standards (wsp>1.3) say: decode while there is data...
	while ( dec.getRemainingOctets() >1  ) {
	    MultiPartEntry part = new MultiPartEntry();
	    System.out.println("----part@[0x"+Long.toHexString(dec.pos())+"] " );
	    long headerLen = dec.getUintVar();
	    long dataLen = dec.getUintVar();

	    int pos = dec.pos();

	    parseMultiPartHeader(part, dec, (int)headerLen);

	    part.payload = dec.getBytes((int)dataLen);

	    parts.addElement(part);



	    /* remove this....
	     */
	    /*
	    System.out.println("Multipart entry cid " + part.header.getContentId());
	    if (part.header.getContentType().hasName()) {
		System.out.println("Multipart entry name " + part.header.getContentType().getName());
		try {
		    java.io.FileOutputStream fo = 
			new java.io.FileOutputStream(part.header.getContentType().getName());
		    fo.write(payload);
		    fo.flush();
		    fo.close();
		}catch(Exception e) {}
		    
	    }
	    */
	} 
    }


    public void parseMultiPartHeader(MultiPartEntry part, WSPDecoder dec, int headerLen)
    {
	long headersLeft;
	long skip;
	int len=headerLen;
	int end = dec.pos() + headerLen;

	logger.debug("-------parse header  @ [0x"+Long.toHexString(dec.pos())+"] " );	
	
	logger.debug("-------parse content type");
	parseMMSHeaderContentType(part.header.getContentType(), dec );

	logger.debug("-------parse header fields");

	while ( dec.pos() < end) { // parse part headers	      
	    logger.debug("    [0x"+Long.toHexString(dec.pos())+"] " );
	    part.header = (MultiPartHeader)parseSingleMmsHeader(dec, part.header);
	}
	logger.debug("DONE reading Headers.");
    }
    

    private void saveToFile(String filename, byte[]b) 
    {
	try{
	    FileOutputStream fo;
	    fo = new FileOutputStream(filename);
	    fo.write(b);
	    fo.close();
	} catch (Exception e){
	    // Host address not ok
	    e.printStackTrace();
	    System.exit(1);
	}
    }    

       
    public byte[] toByteArray() 
    {
	int len=200; // rough estimate
	if (body!=null) len += body.length;

	DataBlock db = new DataBlock(len);
	Enumeration enum1;

	System.out.println(mmsHeaders);

	mmsHeaders.emit(db);

	if (hasBody() ) {
	    if (mmsHeaders.isMultiPart()) {
		//body.emit(db);

		// **** temp code start
		db.addUIntVar(parts.size());
		
		enum1 = parts.elements();
		/*
		while(enum.hasMoreElements()) {
		    
		    curPart = (MMSPart)enum.nextElement();
		    
		    partHeader = curPart.getHeader();
		    partBody = curPart.getData();
		    
		    db.addUIntVar(partHeader.length);	// add the header length 	
		    db.addUIntVar(partBody.length);	// add the data length
		    
		    db.add(partHeader);	// see add(byte[])
		    db.add(partBody);
		}
		*/
	    // ****
	    } else {
		db.add(body);
	    }
	}
    
	return db.getAsByteArray();
    }

}
