/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is the Tambur MMS library.
 *
 * The Initial Developer of the Original Code is FlyerOne Ltd.
 * Portions created by the Initial Developer are Copyright (C) 2005
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * 	Anders Lindh <alindh@flyerone.com>
 *
 * ***** END LICENSE BLOCK ***** */

package mmslib.mms;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.ListIterator;
import java.util.Locale;
import java.util.SimpleTimeZone;

import org.apache.log4j.Logger;

/**
 * This class creates MMMessages from various formats (e.g. encapsulated, string, etc.)
 * 
 * @author Anders Lindh
 * @copyright Copyright FlyerOne Ltd 2005
 * @version $Revision: 1.1.1.1 $ $Date: 2005/04/14 09:04:10 $
 */
public class MMDecoder {

	/**
	 * logger 
	 */
	protected transient static Logger log = Logger.getLogger(MMDecoder.class);
		
	/**
	 * Decode message, i.e. build message from a byte array (decapsulate)
	 * 
	 * @param buf A byte[] containing a binary representation of the message
	 * @return whether successfull
	 */
	public static MMMessage decode(byte[] buf) throws MMDecodingException {
		ByteArrayInputStream in = new ByteArrayInputStream(buf);
		
		MMMessage res = new MMMessage();
		
		log.debug("Decoding message");
		
		try {
			// first X-Mms-Message-Type, X-Mms-Transaction-Id and X-Mms-Version (in this order)
			// these codes can be found in [MMSEncapsulation]
		
			// decode well-known headers
			while (decodeMessageHeader(res, in, null, null)) {};
			in.read();

			int cnt = 1;
			while (in.available() != 0) { // process any existing parts
				int hlen = decodeUintvar(in); // length of headers + content-type
				int dlen = decodeUintvar(in); // length of body								
	
				if ((hlen == 0) && (dlen == 0)) continue;

				log.debug("\tDecoding part: " + cnt++);
				log.debug("\t\tHeaders: " + hlen + ", content: " + dlen);
			
				if (hlen < 0 || hlen > 1024 * 5) { 	// max 5kb of headers				
					log.debug("Invalid size for headers: " + hlen);
					break;
				}

				String contenttype = null;
				byte[] headers = new byte[hlen];
				in.read(headers);

				ByteArrayInputStream his = new ByteArrayInputStream(headers);
				if (hlen != 0) contenttype = decodeContentType(his);
				if (contenttype == null) {
					log.error("Unknown contenttype for part; aborting");
					break;
				}
				log.debug("\t\tContent-type: " + contenttype);
			
				byte[] data = new byte[dlen];
				if (dlen > 0) in.read(data);
						
				ArrayList hNames = new ArrayList();
				ArrayList hValues = new ArrayList();
				while (decodeWSPHeader(his, hNames, hValues)) {}; // decode any additional headers
			
				res.addPart(contenttype, data, false, hNames, hValues);
				
				if (dlen == 0) break;
			}
						
		} catch (MMDecodingException e) {
			throw e;
		} catch (Exception ee) {
			ee.printStackTrace();
			throw new MMDecodingException(ee.toString());
		}
		
		return res;
	}

	/**
	 * Create the message from a String containing a valid MIME multipart encoded
	 * mms message
	 * 
	 * @throws MMDecodingException
	 */
	public static MMMessage fromString(String s) throws MMDecodingException {
		MMMessage msg = new MMMessage();
		
		MimeMessage mime = new MimeMessage(s);		
		msg.setContentType("application/vnd.wap.mms-message");
		msg.setVersion(MMConstants.MMS_VERSION_1_0);
		
		// encode headers
		ListIterator i = mime.getAttributes();
		while (i.hasNext()) {
			String name = (String) i.next();
			String value = (String) mime.getAttribute(name);

			// check if this is a well-known header (i.e. it should be mapped into some field)
			
			int c = 0;
			for (c = 0; c < MMConstants.knownMMSHeaders.length; c++) 
				if (((String)MMConstants.knownMMSHeaders[c][0]).equalsIgnoreCase(name)) break;
			
			//	check what type of field we're talking about
			switch (c) {							
				case 0:	// bcc				 
				 	msg.addBccAddress(value); break;						
				case 1:		// cc
					msg.addCcAddress(value); break;			
				case 2:		// x-mms-content-location
					msg.setContentLocation(value); break;			
				case 3:		// content-type					
					msg.setContentType(value); break;
				case 4:		// date
					msg.setDate(dateFromString(value));	break;
				case 5:		// x-mms-delivery-report					
					msg.setDeliveryReport(Boolean.valueOf(value));
					break;
				case 6:		// x-mms-delivery-time
					msg.setDeliveryTime(dateFromString(value)); break;
				case 7:		// x-mms-expiry
					msg.setExpiry(dateFromString(value)); break;
				case 8:		// from
					msg.setFrom(value); break;
				case 9:		// x-mms-message-class
					 msg.setMessageClass(getMessageClass(value));					 
					 break;				
				case 10:		// message-id
					 msg.setMessageId(value); break;				
				case 11:		// x-mms-message-type
					 msg.setMessageType(getMessageType(value)); break;				
				case 12:		// x-mms-version
					 //msg.setVersion(decodeInt(in));
					 //value = versionToString(msg.getVersion());
					 break;				
				case 13:		// message-size
					 msg.setMessageSize(Long.valueOf(value).longValue()); break;										
				case 14:		// x-mms-priority
					 msg.setPriority(getPriority(value)); break;						
				 case 15:		// x-mms-read-reply
				 	 msg.setReadReply(Boolean.valueOf(value)); break;						
				 case 16:		// x-mms-report-allowed
					 msg.setReportAllowed(Boolean.valueOf(value));
					 break;				
				 case 17:		// x-mms-response-status
					 msg.setResponseStatus(getResponseStatus(value)); break;				
				 case 18:		// x-mms-response-text
					 msg.setResponseText(value); break;				
				 case 19:		// x-mms-sender-visibility
					 msg.setSenderVisibility(Boolean.valueOf(value));
					 break;				
				 case 20:		// x-mms-status
					 msg.setStatus(getMessageStatus(value));
					 break;				
				 case 21:		// subject
					 msg.setSubject(value); break;				
				 case 22:		// to
					 msg.addToAddress(value); break;				
				 case 23:		// x-mms-transaction-id
					 msg.setTransactionId(value); break;				
				 default:		// unknown field, store textual representation
					 //throw new MMDecodingException("Invalid (unhandled) encoded header entry: " + String.valueOf(i));
					// skip all headers that start with a "X-"
					//if (!name.startsWith("X-"))
					//	msg.setAttribute(name, value);								
			 }
		}

		// add any parts
		if (!mime.parts.isEmpty()) {
			i = mime.parts.listIterator();
			while (i.hasNext())
				msg.addPart((MimeMessage.MimePart) i.next());
		}			
		
		return msg;
	} 

	/**
	 * Decode message headers (MMS), i.e. the ones that are represented in this class.
	 * if headers table isn't null, the headers added to the table also
	 *
	 * @return true if there still are any headers left, false otherwise
	 */
	protected static boolean decodeMessageHeader(MMMessage msg, ByteArrayInputStream in, ArrayList headerNames, ArrayList headerValues) throws Exception {
		int i = in.read();
		if (i == -1) return false; // end of stream

		if (i <= 30) 
			return false;

		if (i == 0x20) { // some weird bug
			while (i < 128) i = in.read();
		} 
		
		// string based content
		if ((i > 31) && (i < 128)) {
			String name = decodeString(in);
			String value = decodeString(in);
			if ((name != null) && (value != null)) {
				name = (char) i + name;
				//debug(DEBUG_ENABLED, "			o: " + name + ": " + value);

				if ((headerNames != null) && (headerValues != null)) 
					setAttribute(name, value, headerNames, headerValues);
			}

			return true;		
		}
			
		if ((i & 0xFF) > 128) i &= 127;
		i--; // our arrays are zero-based, compensate for this
				
		if (i > MMConstants.knownMMSHeaders.length) throw new MMDecodingException("Invalid encoded header entry: " + String.valueOf(i));
		
		String name = null;
		String value = null;
		Boolean b = null;
		
		name = (String) MMConstants.knownMMSHeaders[i][0];	
		
		// check what type of field we're talking about
		switch (i) {							
			case 0:		// bcc
				value = decodeString(in);
				msg.addBccAddress(value);
				break;						
			case 1:		// cc
				value = decodeString(in);
				msg.addCcAddress(value);
				break;			
			case 2:		// x-mms-content-location
				value = decodeString(in);
				msg.setContentLocation(value);
				break;			
			case 3:		// content-type
				value = decodeContentType(in);
				msg.setContentType(value);			// content type is always last				
				break;
			case 4:		// date
				msg.setDate(decodeDate(in));
				value = msg.getDateStr();
				break;
			case 5:		// x-mms-delivery-report
				b = decodeBoolean(in); 
				msg.setDeliveryReport(b);
				value = b.toString();
				break;
			case 6:		// x-mms-delivery-time
				Object[] tmp = decodeDateVariable(in);
				msg.setDeliveryTime((Date) tmp[0]);
				msg.setDeliveryTimeAbsolute(((Boolean) tmp[1]).booleanValue()); 
				value = msg.getDeliveryTimeStr();
				break;
			case 7:		// x-mms-expiry
				tmp = decodeDateVariable(in);
				msg.setExpiry((Date) tmp[0]);
				msg.setExpiryAbsolute(((Boolean) tmp[1]).booleanValue()); 
				value = msg.getExpiryStr();
				break;
			case 8:		// from
				msg.setFrom(decodeFrom(in));
				value = msg.getFrom();
				break;
			case 9:		// x-mms-message-class
				msg.setMessageClass(decodeInt(in));
				if (msg.getMessageClass() < MMConstants.MESSAGE_CLASSES.length) value = MMConstants.MESSAGE_CLASSES[msg.getMessageClass()];
				break;				
			case 10:		// message-id
				value = decodeString(in);
				msg.setMessageId(value);
				break;				
			case 11:		// x-mms-message-type
				msg.setMessageType(decodeInt(in));
				if (msg.getMessageType() < MMConstants.MESSAGE_TYPES.length) value = MMConstants.MESSAGE_TYPES[msg.getMessageType()];
				break;				
			case 12:		// x-mms-version
				msg.setVersion(decodeInt(in));
				value = versionToString(msg.getVersion());
				break;				
			case 13:		// message-size
				msg.setMessageSize(decodeLong(in));
				value = String.valueOf(msg.getMessageSize());
				break;										
			case 14:		// x-mms-priority
				msg.setPriority(decodeInt(in));
				if (msg.getPriority() < MMConstants.PRIORITIES.length) value = MMConstants.PRIORITIES[msg.getPriority()];				
				break;						
			case 15:		// x-mms-read-reply
				b = decodeBoolean(in); 			
				msg.setReadReply(b);
				value = b.toString();
				break;						
			case 16:		// x-mms-report-allowed
				b = decodeBoolean(in); 			
				msg.setReportAllowed(b);
				value = b.toString();
				break;				
			case 17:		// x-mms-response-status
				msg.setResponseStatus(decodeInt(in));
				if (msg.getResponseStatus() < MMConstants.RESPONSE_STATUSES.length) value = MMConstants.RESPONSE_STATUSES[msg.getResponseStatus()];				
				break;				
			case 18:		// x-mms-response-text
				value = decodeString(in);
				msg.setResponseText(value);
				break;				
			case 19:		// x-mms-sender-visibility
				b = decodeBoolean(in); 						
				msg.setSenderVisibility(b);
				value = b.toString();
				break;				
			case 20:		// x-mms-status
				msg.setStatus(decodeInt(in));
				if (msg.getStatus() < MMConstants.STATUSES.length) value = MMConstants.STATUSES[msg.getStatus()];				
				break;				
			case 21:		// subject
				value = decodeString(in);
				msg.setSubject(value);
				break;				
			case 22:		// to
				value = decodeString(in);
				msg.addToAddress(value);
				break;				
			case 23:		// x-mms-transaction-id
				value = decodeString(in);
				msg.setTransactionId(value);
				break;				
			default:		// unknown field, store textual representation
				throw new MMDecodingException("Invalid (unhandled) encoded header entry: " + String.valueOf(i));								
		}
		
		// don't add content-type
		if ((i != 3) && (headerNames != null) && (headerValues != null) && (value != null)) {
			setAttribute(name, value, headerNames, headerValues);
		}

		// debug(DEBUG_ENABLED, "			o: " + name + ": " + value);
		
		if (name.equalsIgnoreCase("Content-type")) {			
			return false; // content-type is last
		}  
		
		return true;	
	}	
	
	/**
	 * Decode headers, and add them to given hashtable
	 * 
	 * @return true if there still are any headers left, false otherwise
	 */
	protected static boolean decodeWSPHeader(ByteArrayInputStream in, ArrayList names, ArrayList values) throws Exception {
		String name = null;
		String value = null;
	
		if (in.available() == 0) return false; // end-of-stream
	
		int i = in.read();
	
		if (i < 128) { // string
			if (i != 127) name = (char) i + decodeString(in); else
			  name = decodeString(in);
			value = decodeString(in);
			  
		} else {	
			if ((i & 0xFF) > 128) i &= 127;
				
			if (i > MMConstants.knownWSPHeaders.length) throw new MMDecodingException("Invalid WSP header entry: " + String.valueOf(i));
			
			name = (String) MMConstants.knownWSPHeaders[i][0];	
			int type = ((Integer) MMConstants.knownWSPHeaders[i][1]).intValue();
			
			value = decodeToken(in, type);			
		}

		// debug(DEBUG_ENABLED, "			o: " + name + ": " + value);

		setAttribute(name, value, names, values);

		return true;	
	}

	/**
	 * Decode well-known parameters, return as String
	 * 
	 * @return true if there still are any headers left, false otherwise
	 */
	protected static String decodeParameters(ByteArrayInputStream in) throws Exception {
		String res = "";
		byte[] buf = new byte[1];
		
		while (in.available() != 0) {
			in.read(buf);
			int i = (int) (buf[0] & 0xFF);
	  			
			if (i < 128) { // string
				res += decodeString(in);
				continue;
			} else i &= 127; // short form
				
			if (i > MMConstants.WELLKNOWN_PARAMETERS.length) continue; // invalid headers entry			
			String name = (String) MMConstants.WELLKNOWN_PARAMETERS[i][0];	
			int type = ((Integer) MMConstants.WELLKNOWN_PARAMETERS[i][1]).intValue();
						
			String value = decodeToken(in, type);
			res += "; " + name + "=\"" + value + "\"";		
		}		

		return res;
	}
	
	/**
	 * decode next token from stream
	 */
	protected static String decodeToken(ByteArrayInputStream in, int type) throws Exception {
		String value = null;
		// check what type of field we're talking about
		switch (type) {				
			case 0:		// TYPE_STRING
				value = decodeString(in);
				break;
			case 1:		// TYPE_SHORTINT
				value = String.valueOf(decodeInt(in));
				break;
			case 2:		// TYPE_UINTVAR
				value = String.valueOf(decodeUintvar(in));
				break;
			case 3:		// TYPE_LONG
				value = String.valueOf(decodeLong(in));				
				break;	
			case 4:		// TYPE_BOOLEAN
				value = String.valueOf(decodeBoolean(in));			
				break;	
			case 5:		// TYPE_DATE
				value = decodeDateStr(in);
				break;	
			case 6:		// TYPE_MSGCLASS
				value = decodeMessageClass(in);
				break;	
			case 7:		// TYPE_MSGTYPE
				value = decodeMessageType(in);
				break;
			case 8:		// TYPE_PRIORITY
				value = decodePriority(in);
				break;
			case 9:		// TYPE_RESPONSESTATUS
				value = decodeResponseStatus(in);
				break;				
			case 10:		// TYPE_STATUS
				value = decodeStatus(in);
				break;			
			case 11:		// TYPE_CONTENTTYPE
				value = decodeContentType(in);
				break;	
			case 12:		// TYPE_VERSION
				value = decodeVersion(in);
				break;	
			case 13:		// TYPE_FROM
				value = decodeFrom(in);
				break;			
			case 14:		// TYPE_NOVALUE:
				value = "";
				break;
			case 15: 		// TYPE_CHARSET
				value = decodeCharset(in);
				break;
			case 16:		// TYPE_Q
				value = "";
				break;		// TYPE_INTEGER		
			case 17:
				value = String.valueOf(decodeInteger(in));
				break;
			default: value = decodeString(in); // store string representation
		}
		
		return value;
	}


	
	
	
	/*****************************************************************************************
	 * Functions for decoding values according to [MMSEncapsulation] and [WAPWSP]
	 */
	
	/**
	 * decode a long value
	 */
	protected static long decodeLong(ByteArrayInputStream res) throws Exception {
		int len = 0;
		byte[] buf = new byte[1];
		
		// first byte specifies length		
		res.read(buf);
		len = buf[0];
		
		int r = 0;
		// most significant bit first
		for (int i = 0; i < len; i++) {
			res.read(buf);
			
			r = r << 8;
			r |= (buf[0] & 0xFF);
		}
		return r;
	}
	
	/**
	 * encode a short int
	 */
	protected static int decodeInt(ByteArrayInputStream res) throws Exception {
		byte[] b = new byte[1];
		res.read(b);
		
		if ((b[0] & 0xFF) >= 128) b[0] &= 127;	
		
		return (int) (b[0] & 0xFF);
	}

	/**
	 * decode a integer (short or long)
	 */
	protected static int decodeInteger(ByteArrayInputStream res) throws Exception {
		byte[] b = new byte[1];
		res.read(b);
		if ((b[0] & 0xFF) >= 128) { // short form
			b[0] &= 127;	
			return (int) (b[0] & 0xFF);
		}
		int len = b[0];
		
		int r = 0;
		// most significant bit first
		for (int i = 0; i < len; i++) {
			res.read(b);
			
			r = r << 8;
			r |= (b[0] & 0xFF);
		}
		return r;
	}	

	/**
	 * decode a Uintvar (variable length int). Max 32bits
	 */
	protected static int decodeUintvar(ByteArrayInputStream res) throws Exception {
		byte[] buf = new byte[1];
		int r = 0;
		
		do {
			res.read(buf);
						
			r = (r << 7) | (buf[0] & 0x7F);
		} while ((buf[0] & 0x80) != 0);

		return r;
	}
	
	/**
	 * encode a Date class into a long (4 octets)
	 */
	protected static Date decodeDate(ByteArrayInputStream res) throws Exception {
		long l = decodeLong(res) * 1000;
		return new Date(l);
	}
	
	/**
	 * encode a Date class into a String
	 */
	protected static String decodeDateStr(ByteArrayInputStream res) throws Exception {
		Date d = decodeDate(res);
			
		return formatDate(d); 
	}

	/**
	 * encode a Date class into a long (4 octets)
	 */
	protected static Object[] decodeDateVariable(ByteArrayInputStream res) throws Exception {
		Object[] result = new Object[2];
		
		int len = decodeInt(res);
		
		byte[] tmp = new byte[len]; 
		res.read(tmp);
		
		ByteArrayInputStream bais = new ByteArrayInputStream(tmp);
		
		if (len > 4) {
			int abs = decodeInt(bais);
			long l = decodeLong(bais) * 1000;
			result[0] = new Date(l);
			result[1] = new Boolean(abs == 0);
		} else {
			long l = decodeLong(bais) * 1000;
			result[0] = new Date(l);
			result[1] = new Boolean(false);
		}
		return result;
	}
	
	/**
	 * encode a Date class into a String
	 */
	protected static String decodeDateStrVariable(ByteArrayInputStream res) throws Exception {
		Object[] tmp = decodeDateVariable(res);
			
		return formatDate((Date) tmp[0]); 
	}
	
			
	/**
	 * decode a string
	 */
	protected static String decodeString(ByteArrayInputStream res) throws Exception {
		StringBuffer r = new StringBuffer();
		
		byte[] b = new byte[1];
		if (res.read(b) == -1) return null; // eos
		
		if (((b[0] & 0xFF) != 127) && (b[0] != 0)) r.append((char) b[0]);	// quote

		while ((b[0] & 0xFF) != 0) {
			if (res.read(b) == -1) break; // eos			
			if (b[0] != 0) r.append((char) b[0]);
		}
		
		if (r.length() == 0) return null;
		
		return new String(r);
	}
	
	
	/**
	 * decode a boolean (with Boolean as input)
	 */
	protected static Boolean decodeBoolean(ByteArrayInputStream res) throws Exception {
		byte[] b = new byte[1];
		res.read(b);
		
		if ((b[0] & 0xFF) == 128) return new Boolean(true); else
		  return new Boolean(false);	
	}
	
	/**
	 * Decode content type
	 * 
	 * format for content types is defined in [WAPWSP], 8.4.2.24:
	 * 
	 * Content-type-value = Constrained-media | Content-general-form
	 * Content-general-form = Value-length Media-type
	 * Media-type = (Well-known-media | Extension-Media) *(Parameter)
	 */
	protected static String decodeContentType(ByteArrayInputStream res) throws Exception {		
		byte[] buf = new byte[1];
	  	
		if (res.read(buf) == -1) return null; // eos
	  	
		int i = (int) (buf[0] & 0xFF);
	  	
		String ct = "";
		if (i == 0) return null; // none
	  	
		if (i < 128) { // Content-general-form
			int len = i;
	  		
			if (i > 31) { // string
				ct = (char) i + decodeString(res);
			} else {
				if (i == 31) // length quote, when length > 30
					len = decodeUintvar(res); 
	  	  			
				byte[] dta = new byte[len];
				res.read(dta);
			
				ByteArrayInputStream in = new ByteArrayInputStream(dta);
				in.read(buf);
			
				int c = (int) (buf[0] & 0xFF);
				if (c < 128) {
					String tmp = decodeString(in);
					if (tmp != null) ct = (char) c + tmp; else return null;
				} else ct = MMConstants.CONTENT_TYPES[(c & 0x7F)]; 			
  	
				// the inputstream might contain some parameter, parse them
				ct += decodeParameters(in);					
			}
	  					
	  		
			return ct;

		} else i &= 127; // short form
		
		if (i < MMConstants.CONTENT_TYPES.length)
			ct = MMConstants.CONTENT_TYPES[i];
		else {		
			ct = (char) i + decodeString(res);	// encoded version not found
		}		
		
		return ct;
	}

	/**
	 * get message class from string, it's a byte if pre-defined
	 */
	protected static String decodeMessageClass(ByteArrayInputStream res) throws Exception {
		int i = (decodeInt(res) & 0xFF);
	
		if (i < MMConstants.MESSAGE_CLASSES.length) return MMConstants.MESSAGE_CLASSES[i];
			
		return (char) i + decodeString(res);	// encoded version not found						
	}
	
	/**
	 * get message class (int) given a string 
	 */
	protected static int getMessageClass(String s) {
		for (int i = 0; i < MMConstants.MESSAGE_CLASSES.length; i++)
			if (MMConstants.MESSAGE_CLASSES[i].equalsIgnoreCase(s)) return i;
		return -1;
	}
	

	/**
	 * get message type from string, it's a byte if predefined
	 */
	protected static String decodeMessageType(ByteArrayInputStream res) throws Exception {
		int i = (decodeInt(res) & 0xFF);
			
		if (i < MMConstants.MESSAGE_TYPES.length) return MMConstants.MESSAGE_TYPES[i];
		
		return (char) i + decodeString(res);	// encoded version not found		
	}
	
	/**
	 * get message type (int) given a string 
	 */
	protected static int getMessageType(String s) {
		for (int i = 0; i < MMConstants.MESSAGE_TYPES.length; i++)
			if (MMConstants.MESSAGE_TYPES[i].equalsIgnoreCase(s)) return i;
		return -1;
	}

	/**
	 * get message class from string, it's a byte if pre-defined
	 */
	protected static String decodePriority(ByteArrayInputStream res) throws Exception {
		int i = (decodeInt(res) & 0xFF);
	
		if (i < MMConstants.PRIORITIES.length) return MMConstants.PRIORITIES[i];
			
		return (char) i + decodeString(res);	// encoded version not found		
	}
	
	/**
	 * get message type (int) given a string 
	 */
	protected static int getPriority(String s) {
		for (int i = 0; i < MMConstants.PRIORITIES.length; i++)
			if (MMConstants.PRIORITIES[i].equalsIgnoreCase(s)) return i;
		return -1;
	}
	
	/**
	 * get response status, it's a byte if predefined
	 */
	protected static String decodeResponseStatus(ByteArrayInputStream res) throws Exception {
		int i = (decodeInt(res) & 0xFF);

		if (i < MMConstants.RESPONSE_STATUSES.length) return MMConstants.RESPONSE_STATUSES[i];
	
		return (char) i + decodeString(res);	// encoded version not found		
	}	
	
	/**
	 * get message type (int) given a string 
	 */
	protected static int getResponseStatus(String s) {
		for (int i = 0; i < MMConstants.RESPONSE_STATUSES.length; i++)
			if (MMConstants.RESPONSE_STATUSES[i].equalsIgnoreCase(s)) return i;
		return -1;
	}	
	
	/**
	 * get response status, it's a byte if predefined
	 */
	protected static String decodeStatus(ByteArrayInputStream res) throws Exception {
		int i = decodeInt(res);

		if (i < MMConstants.STATUSES.length) return MMConstants.STATUSES[i];

		return (char) i + decodeString(res);	// encoded version not found				
	}	
	
	/**
	 * get message status (int) given a string 
	 */
	protected static int getMessageStatus(String s) {
		for (int i = 0; i < MMConstants.STATUSES.length; i++)
			if (MMConstants.STATUSES[i].equalsIgnoreCase(s)) return i;
		return -1;
	}
	
	
	/**
	 * get mms version
	 */
	protected static String decodeVersion(ByteArrayInputStream in) throws Exception {
		int i = decodeInt(in);
		
		String major = String.valueOf((i >> 4));
		String minor = "0";
		
		return major + "." + minor;
	}
	
	/**
	 * get from
	 */
	protected static String decodeFrom(ByteArrayInputStream res) throws Exception {
		int i = decodeUintvar(res); // length
		int token = decodeInt(res);
		
		if ((token & 0xFF) == 0) // address-present-token
			return decodeString(res);
			
		return null;		
	}	
		
	/**
	 * get charset
	 */
	protected static String decodeCharset(ByteArrayInputStream res) throws Exception {
		int i = decodeInteger(res);
		
		for (int c = 0; c < MMConstants.WELLKNOWN_CHARSETS.length; c++) {
			if (((Integer) MMConstants.WELLKNOWN_CHARSETS[c][1]).intValue() == i)
				return (String) MMConstants.WELLKNOWN_CHARSETS[c][0];
		}
		
		// something we don't understand
			
		return decodeString(res);		
	}
	
	/**
	 * Misc helper functions
	 */
	
	protected static void setAttribute(String name, String value, ArrayList names, ArrayList values) {
			int idx = names.indexOf(name);
			if (idx != -1) {
				names.set(idx, name);
				values.set(idx, value);
			} else {
				names.add(name);
				values.add(value);
			}
		}

	protected static String versionToString(int version) {
			if (version == 0) version = MMConstants.MMS_VERSION_1_0;
		
			String major = String.valueOf((version >> 4));
			String minor = "0";
		
			return major + "." + minor;	
		}
	
	protected static String formatDate(Date d) {
			// transform to UTC (GMT)
			SimpleTimeZone stz = new SimpleTimeZone(0, "GMT");
			Calendar cal = Calendar.getInstance(stz);
		
			SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ss'Z'");
			formatter.setCalendar(cal);
		
			return formatter.format(d); 
		}	
		
	protected static Date dateFromString(String s) {
		Date date = null;
		
		SimpleDateFormat[] dateFormats = {
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"), // as specified in the specs
				new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss z", Locale.US),
				new SimpleDateFormat()}; // default locale

		int i = 0;
		boolean success = false;

		while (!success) {
			if (i > dateFormats.length - 1) 				
				break;
			
			try {
				SimpleDateFormat formatter = dateFormats[i++];
				date = formatter.parse(s);
			} catch (Exception e) {				
				continue;
			}
			success = true;
		}
		
		return date;
	}
		
}
