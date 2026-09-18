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

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 *	This class performs conversions of MMMessages to other formats
 *	(i.e. to encapsulated, to string, etc.)
 *
 * @author Anders Lindh
 * @copyright Copyright FlyerOne Ltd 2005
 * @version $Revision: 1.1.1.1 $ $Date: 2005/04/14 09:04:10 $
 */
public class MMEncoder {
	
	/**
	 * logger 
	 */ 
	protected transient static Logger log = Logger.getLogger(MMEncoder.class);


	/**
	 * Encode the message, i.e. create a valid mms encapsulated byte array
	 * 
	 * @param msg MMMmessage to be encoded 
	 * @return byte[] containing the binary representation of this message
	 * @throws MMEncodingException
	 */
	public static byte[] encode(MMMessage msg) throws MMEncodingException {
		ByteArrayOutputStream res = new ByteArrayOutputStream();

		log.debug("Encoding message");
		validateMMMessage(msg);
		
		// first X-Mms-Message-Type, X-Mms-Transaction-Id and X-Mms-Version (in this order)
		// these codes can be found in [MMSEncapsulation]

		try {
			encodeInt(res, 0x0C); // message type 
			encodeInt(res, msg.getMessageType()); // (m-notification-ind)

			if (msg.getTransactionId() != null) {
				encodeInt(res, 0x18); // transaction-id
				encodeString(res, msg.getTransactionId());
			}

			encodeInt(res, 0x0d); // version 
			encodeInt(res, msg.version); // 1.0

			// add existing headers

			if (msg.getBcc() != null) {
				// encode all recipients
				Iterator i = msg.getBcc().iterator();
				while (i.hasNext()) {
					String s = (String) i.next();
					encodeInt(res, 0x01); // bcc
					encodeString(res, s);
				}
			}

			if (msg.getCc() != null) {
				// encode all recipients
				Iterator i = msg.getCc().iterator();
				while (i.hasNext()) {
					String s = (String) i.next();
					encodeInt(res, 0x02); // cc
					encodeString(res, s);
				}
			}

			if (msg.getContentLocation() != null) {
				encodeInt(res, 0x03); // content-location
				encodeString(res, msg.getContentLocation());
			}

			if (msg.getDate() != null) {
				encodeInt(res, 0x05); // date
				encodeDate(res, msg.getDate());
			}

			if (msg.getFrom() != null) {
				encodeInt(res, 0x09); // from
				encodeFrom(res, msg.getFrom());
			}

			if (msg.getTo() != null) {
				// encode all recipients
				Iterator i = msg.getTo().iterator();
				while (i.hasNext()) {
					String s = (String) i.next();
					encodeInt(res, 0x17); // to
					encodeString(res, s);
				}
			}

			if (msg.getSubject() != null) {
				encodeInt(res, 0x16); // subject
				encodeString(res, msg.getSubject());
			}

			if (msg.deliveryReport != null) {
				encodeInt(res, 0x06); // delivery-report
				encodeBoolean(res, msg.deliveryReport);
			}

			if (msg.getSenderVisibility() != null) {
				encodeInt(res, 0x14); // sender-visibility
				encodeBoolean(res, msg.getSenderVisibility());
			}

			if (msg.readReply != null) {
				encodeInt(res, 0x10); // read-reply
				encodeBoolean(res, msg.readReply);
			}

			if (msg.getMessageClass() > -1) {
				if (msg.getMessageClass() >= MMConstants.MESSAGE_CLASSES.length)
					throw new MMEncodingException("Invalid X-MMS-Message-Class");

				encodeInt(res, 0x0A); // message-class
				encodeInt(res, msg.getMessageClass());
			}

			if (msg.getExpiry() != null) {
				encodeInt(res, 0x08); // expiry
				encodeDateVariable(res, msg.getExpiry(), msg.expiryAbsolute);
			}

			if (msg.getDeliveryTime() != null) {
				encodeInt(res, 0x07); // delivery-time
				encodeDateVariable(res, msg.getDeliveryTime(), msg.deliveryTimeAbsolute);
			}

			if (msg.getStatus() > -1) {
				if (msg.getStatus() >= MMConstants.STATUSES.length)
					throw new MMEncodingException("Invalid X-MMS-Status");

				encodeInt(res, 0x15); // status
				encodeInt(res, msg.getStatus());
			}

			if (msg.isMessageIdAvailable()) {
				encodeInt(res, 0x0B); // message id
				encodeString(res, msg.getMessageId());
			}

			if (msg.getMessageSize() != -1) {
				encodeInt(res, 0x0E); // message-size
				encodeLong(res, msg.getMessageSize());
			}

			if (msg.getPriority() > -1) {
				if (msg.getPriority() >= MMConstants.PRIORITIES.length)
					throw new MMEncodingException("Invalid X-MMS-Priority");

				encodeInt(res, 0x0F); // priority
				encodeInt(res, msg.getPriority());
			}

			if (msg.reportAllowed != null) {
				encodeInt(res, 0x11); // report-allowed
				encodeBoolean(res, msg.reportAllowed);
			}

			if (msg.getResponseStatus() > -1) {
				if (msg.getResponseStatus() >= MMConstants.RESPONSE_STATUSES.length)
					throw new MMEncodingException("Invalid X-MMS-Response-Status");

				encodeInt(res, 0x12); // response-status
				encodeInt(res, msg.getResponseStatus());
			}

			if (msg.getResponseText() != null) {
				encodeInt(res, 0x13); // response-text
				encodeString(res, msg.getResponseText());
			}

			// encode any custom headers
			Iterator i = msg.getAttributes();

			while (i.hasNext()) {
				String name = (String) i.next();
				String value = msg.getAttribute(name);

				int a = 0;
				for (a = 0; a < MMConstants.knownMMSHeaders.length; a++)
					if (((String) MMConstants.knownMMSHeaders[a][0])
						.equalsIgnoreCase(name))
						break;

				if (a < MMConstants.knownMMSHeaders.length)
					continue;

				encodeMessageHeader(res, name, value);
			}

			if (msg.getContentType() != null) { // content-type, the last header
				encodeInt(res, 0x04);
				encodeContentType(res, msg.getContentType());
			}

			if (!msg.parts.isEmpty()) {
				log.debug("\tEncoding parts:");
				
				// a uintvar telling how many parts there are in this message
				encodeUintvar(res, msg.parts.size());

				// now add each part in message. We'll have to encode the headers if possible
				// Parts in the message follow the following rule:
				// Size of headers, size of data, headers, data

				i = msg.parts.iterator();
				int cnt = 1;
				while (i.hasNext()) {
					//debug(DEBUG_ENABLED, "	Part: " + cnt++);
					log.debug("\t\tPart: " + cnt++);

					MimeMessage.MimePart part = (MimeMessage.MimePart) i.next();
					byte[] c = part.getContent();

					// encode headers (in different stream - we need the size)
					ByteArrayOutputStream headers = new ByteArrayOutputStream();

					// first content-type
					ArrayList attribNames = part.getAttributeNames();
					ArrayList attribValues = part.getAttributeValues();

					String value = part.getContentType();

					if (value != null) {
						encodeContentType(headers, value);
						//debug(DEBUG_ENABLED, "	   Content-Type: " + value);
						log.debug("\t\tContent-type:" + value);
					}

					Iterator penum = attribNames.iterator();
					while (penum.hasNext()) {
						String name = (String) penum.next();
						value = part.getAttribute(name);
						if (name.equalsIgnoreCase("Content-type")) continue;
						encodeWSPHeader(headers, name, value);
					}

					byte[] h = headers.toByteArray();

					// write sizes
					encodeUintvar(res, h.length);
					encodeUintvar(res, c.length);
					
					log.debug("\t\tHeaders: " + h.length + ", content: " + c.length);

					res.write(h); // write headers
					res.write(c); // write content
				}
			}
			res.flush();
		} catch (Exception e) {
			throw new MMEncodingException("Failed to encapsulate: " + e.toString());
		}

		return res.toByteArray();
	}
	
	
	/**
	 * Convert given MMMessage into a valid mime formatted message
	 * 
	 * @param msg the message to be converted
	 * @throws MMEncodingException
	 */
	public static String toString(MMMessage msg) throws MMEncodingException {			
		
		MimeMessage mime = new MimeMessage();
		validateMMMessage(msg);
		
		// first X-Mms-Message-Type, X-Mms-Transaction-Id and X-Mms-Version (in this order)		
		try {
			mime.setAttribute("X-MMS-Message-Type", msg.getMessageTypeStr()); // (m-notification-ind)

			if (msg.getTransactionId() != null) 
				mime.setAttribute("X-MMS-Transaction-Id", msg.getTransactionId());

			mime.setAttribute("X-MMS-Version", MMDecoder.versionToString(msg.version)); 

			// add existing headers

			if (msg.isBccAvailable()) {
				// encode all recipients
				String bcc = "";
				Iterator i = msg.getBcc().iterator();
				while (i.hasNext()) {
					String s = (String) i.next();
					
					if (bcc.equals(""))
						bcc += s; else bcc += ", " + s;
				}
				
				mime.setAttribute("Bcc", bcc);
			}

			if (msg.isCcAvailable()) {
				// encode all recipients
				String cc = "";
				Iterator i = msg.getCc().iterator();
				while (i.hasNext()) {
					String s = (String) i.next();
					if (cc.equals(""))
						cc += s; else cc += ", " + s;
				}
				mime.setAttribute("Cc", cc);
			}

			if (msg.isContentLocationAvailable()) 
				mime.setAttribute("X-MMS-Content-Location", msg.getContentLocation());			

			if (msg.isDateAvailable()) 
				mime.setAttribute("Date", MMDecoder.formatDate(msg.getDate()));

			if (msg.isFromAvailable()) 
				mime.setAttribute("From", msg.getFrom());

			if (msg.isToAvailable()) {
				// encode all recipients
				String to = "";
				Iterator i = msg.getTo().iterator();
				while (i.hasNext()) {
					String s = (String) i.next();
					if (to.equals(""))
						to += s; else to += ", " + s;
				}
				mime.setAttribute("To", to);
			}

			if (msg.isSubjectAvailable()) 
				mime.setAttribute("Subject", msg.getSubject());

			if (msg.isDeliveryReportAvailable()) 
				mime.setAttribute("X-MMS-Delivery-Report", String.valueOf(msg.deliveryReport));

			if (msg.isSenderVisibilityAvailable()) 
				mime.setAttribute("X-MMS-Sender-Visibility", String.valueOf(msg.getSenderVisibility()));

			if (msg.isReadReplyAvailable())
				mime.setAttribute("X-MMS-Read-Reply", String.valueOf(msg.readReply));

			if (msg.isMessageClassAvailable()) {
				if (msg.getMessageClass() >= MMConstants.MESSAGE_CLASSES.length)
					throw new MMEncodingException("Invalid X-MMS-Message-Class");
				mime.setAttribute("X-MMS-Message-Class", msg.getMessageClassStr());
			}

			if (msg.isExpiryAvailable()) 
				mime.setAttribute("X-MMS-Expiry", MMDecoder.formatDate(msg.getExpiry()));
			
			if (msg.isDeliveryTimeAvailable())
				mime.setAttribute("X-MMS-Delivery-Time", MMDecoder.formatDate(msg.getDeliveryTime()));

			if (msg.isStatusAvailable()) {
				if (msg.getStatus() >= MMConstants.STATUSES.length)
					throw new MMEncodingException("Invalid X-MMS-Status");
				mime.setAttribute("X-MMS-Status", msg.getStatusStr());
			}

			if (msg.isMessageIdAvailable())
				mime.setAttribute("Message-ID", msg.getMessageId());

			if (msg.getMessageSize() != -1)
				mime.setAttribute("Message-Size", String.valueOf(msg.getMessageSize()));

			if (msg.isPriorityAvailable()) {
				if (msg.getPriority() >= MMConstants.PRIORITIES.length)
					throw new MMEncodingException("Invalid X-MMS-Priority");
				mime.setAttribute("X-MMS-Priority", msg.getPriorityStr());
			}

			if (msg.reportAllowed != null)
				mime.setAttribute("X-MMS-Report-Allowed", String.valueOf(msg.reportAllowed));

			if (msg.isResponseStatusAvailable()) {
				if (msg.getResponseStatus() >= MMConstants.RESPONSE_STATUSES.length)
					throw new MMEncodingException("Invalid X-MMS-Response-Status");
				mime.setAttribute("X-MMS-Response-Status", msg.getResponseStatusStr());
			}

			if (msg.isResponseTextAvailable()) 
				mime.setAttribute("X-MMS-Response-Text", msg.getResponseText());

			// encode any custom headers
			Iterator i = msg.getAttributes();

			while (i.hasNext()) {
				String name = (String) i.next();
				String value = msg.getAttribute(name);

				int a = 0;
				for (a = 0; a < MMConstants.knownMMSHeaders.length; a++)
					if (((String) MMConstants.knownMMSHeaders[a][0])
						.equalsIgnoreCase(name))
						break;

				if (a < MMConstants.knownMMSHeaders.length)
					continue;

				mime.setAttribute(name, value);
			}

			if (msg.getContentType() != null) // content-type, the last header
				mime.setContentType(msg.getContentType());

			if (!msg.parts.isEmpty()) {
				i = msg.parts.iterator();
				while (i.hasNext())
					mime.addPart((MimeMessage.MimePart) i.next());
			}			
		} catch (Exception e) {
			throw new MMEncodingException("Failed to encapsulate: " + e.toString());
		}

		return mime.toString();
	} 

	/**
	 * encode headers
	 */
	protected static void encodeMessageHeader(
		ByteArrayOutputStream res,
		String name,
		String value)
		throws Exception {
		// try to find header in table
		int i = 0;
		for (i = 0; i < MMConstants.knownMMSHeaders.length; i++)
			if (((String) MMConstants.knownMMSHeaders[i][0]).equalsIgnoreCase(name))
				break;

		if (i < MMConstants.knownMMSHeaders.length) { // found
			encodeInt(res, (i + 1));
			// index in array, the value used to represent field

			Integer intval = new Integer(0);
			try {
				intval = Integer.valueOf(value);
			} catch (Exception e) {
			}

			// check what type of field we're talking about
			switch (((Integer) MMConstants.knownMMSHeaders[i][1]).intValue()) {
				case 0 : // TYPE_STRING
					encodeString(res, value);
					break;
				case 1 : // TYPE_SHORTINT
					encodeInt(res, intval.intValue());
					break;
				case 2 : // TYPE_UINTVAR
					encodeUintvar(res, intval.intValue());
					break;
				case 3 : // TYPE_LONG
					encodeLong(res, intval.intValue());
					break;
				case 4 : // TYPE_BOOLEAN
					encodeBoolean(res, value);
					break;
				case 5 : // TYPE_DATE
					encodeDate(res, value);
					break;
				case 17 : // TYPE_DATE_VARIABLE
					encodeDateVariable(res, value, true);
					break;
				case 6 : // TYPE_MSGCLASS
					encodeMessageClass(res, value);
					break;
				case 7 : // TYPE_MSGTYPE
					encodeMessageType(res, value);
					break;
				case 8 : // TYPE_PRIORITY
					encodePriority(res, value);
					break;
				case 9 : // TYPE_RESPONSESTATUS
					encodeResponseStatus(res, value);
					break;
				case 10 : // TYPE_STATUS
					encodeStatus(res, value);
					break;
				case 11 : // TYPE_CONTENTTYPE
					encodeContentType(res, value);
					break;
				case 12 : // TYPE_VERSION
					encodeInt(res, MMConstants.MMS_VERSION_1_0);
					break;
				case 13 : // TYPE_FROM
					encodeFrom(res, value);
					break;
				default :
					encodeString(res, value); // store string representation
			}
		} else {
			encodeString(res, name);
			encodeString(res, value);
		}
	}

	/**
	 * encode headers
	 */
	protected static void encodeWSPHeader(ByteArrayOutputStream res, String name, String value) throws Exception {
		// try to find header in table
		int i = 0;
		for (i = 0; i < MMConstants.knownWSPHeaders.length; i++)
			if (((String) MMConstants.knownWSPHeaders[i][0]).equalsIgnoreCase(name))
				break;

		if (i < MMConstants.knownWSPHeaders.length) { // found
			encodeInt(res, i);
			// index in array, the value used to represent field

			Integer intval = new Integer(0);
			try {
				intval = Integer.valueOf(value);
			} catch (Exception e) {
			}

			// check what type of field we're talking about
			switch (((Integer) MMConstants.knownWSPHeaders[i][1]).intValue()) {
				case 0 : // TYPE_STRING
					encodeString(res, value);
					break;
				case 1 : // TYPE_SHORTINT
					encodeInt(res, intval.intValue());
					break;
				case 2 : // TYPE_UINTVAR
					encodeUintvar(res, intval.intValue());
					break;
				case 3 : // TYPE_LONG
					encodeLong(res, intval.intValue());
					break;
				case 4 : // TYPE_BOOLEAN
					encodeBoolean(res, value);
					break;
				case 5 : // TYPE_DATE
					encodeDate(res, value);
					break;
				case 6 : // TYPE_MSGCLASS
					encodeMessageClass(res, value);
					break;
				case 7 : // TYPE_MSGTYPE
					encodeMessageType(res, value);
					break;
				case 8 : // TYPE_PRIORITY
					encodePriority(res, value);
					break;
				case 9 : // TYPE_RESPONSESTATUS
					encodeResponseStatus(res, value);
					break;
				case 10 : // TYPE_STATUS
					encodeStatus(res, value);
					break;
				case 11 : // TYPE_CONTENTTYPE
					encodeContentType(res, value);
					break;
				case 12 : // TYPE_VERSION
					encodeInt(res, intval.intValue());
					break;
				case 13 : // TYPE_FROM
					encodeFrom(res, value);
					break;
				default :
					encodeString(res, value); // store string representation
			}
		} else {
			encodeString(res, name);
			encodeString(res, value);
		}
	}

	/**
	 * encode parameter
	 */
	protected static void encodeParameter(
		ByteArrayOutputStream res,
		String name,
		String value)
		throws Exception {
		// try to find header in table
		int i = 0;
		for (i = 0; i < MMConstants.WELLKNOWN_PARAMETERS.length; i++)
			if (((String) MMConstants.WELLKNOWN_PARAMETERS[i][0]).equalsIgnoreCase(name))
				break;

		if (i < MMConstants.WELLKNOWN_PARAMETERS.length) { // found
			encodeInt(res, i);
			// index in array, the value used to represent field

			Integer intval = new Integer(0);
			try {
				intval = Integer.valueOf(value);
			} catch (Exception e) {
			}

			// check what type of field we're talking about
			switch (((Integer) MMConstants.WELLKNOWN_PARAMETERS[i][1]).intValue()) {
				case 0 : // TYPE_STRING
					encodeString(res, value);
					break;
				case 1 : // TYPE_SHORTINT
					encodeInt(res, intval.intValue());
					break;
				case 2 : // TYPE_UINTVAR
					encodeUintvar(res, intval.intValue());
					break;
				case 3 : // TYPE_LONG
					encodeLong(res, intval.intValue());
					break;
				case 4 : // TYPE_BOOLEAN
					encodeBoolean(res, value);
					break;
				case 5 : // TYPE_DATE
					encodeDate(res, value);
					break;
				case 6 : // TYPE_MSGCLASS
					encodeMessageClass(res, value);
					break;
				case 7 : // TYPE_MSGTYPE
					encodeMessageType(res, value);
					break;
				case 8 : // TYPE_PRIORITY
					encodePriority(res, value);
					break;
				case 9 : // TYPE_RESPONSESTATUS
					encodeResponseStatus(res, value);
					break;
				case 10 : // TYPE_STATUS
					encodeStatus(res, value);
					break;
				case 11 : // TYPE_CONTENTTYPE
					encodeContentType(res, value);
					break;
				case 12 : // TYPE_VERSION
					encodeInt(res, intval.intValue());
					break;
				case 13 : // TYPE_FROM
					encodeFrom(res, value);
					break;
				case 14 : // TYPE_NOVALUE:
					break;
				case 15 : // TYPE_CHARSET
					encodeCharset(res, value);
					break;
				case 16 : // TYPE_Q
					break;
				case 17 : // TYPE_INTEGER		
					encodeInteger(res, intval.intValue());
					break;
				default :
					encodeString(res, value); // store string representation
			}
		} else {
			encodeString(res, name);
			encodeString(res, value);
		}
	}
	/*****************************************************************************************
	 * Functions for encoding values according to [MMSEncapsulation] and [WAPWSP]
	 */

	/**
	 * encode a long value
	 */
	protected static void encodeLong(ByteArrayOutputStream res, long l)
		throws Exception {
		byte len = 0;

		if (l < 256)
			len = 1;
		else if (l < 256 * 256)
			len = 2;
		else if (l < 256 * 256 * 256)
			len = 3;
		else
			len = 4;

		res.write((byte) len); // length of multi-octet integer 

		int i = (len - 1) * 8;
		while (i >= 0) {
			res.write((byte) ((l >> i) & 0xFF));
			i -= 8;
		}

	}

	/**
	 * encode a integer (short or long)
	 */
	protected static void encodeInteger(ByteArrayOutputStream res, int i)
		throws Exception {
		if (i >= 128) { // long form
			encodeLong(res, i);
			return;
		}

		encodeInt(res, i);
	}

	/**
	 * encode a Uintvar (variable length int). Max 32bits
	 */
	protected static void encodeUintvar(ByteArrayOutputStream res, int l)
		throws Exception {
		byte[] buf = new byte[5]; // max 5 bytes

		// split into groups of 7 bits, start with most significant bits

		buf[4] = (byte) (l & 0x7F);
		l = l >> 7;

		for (int i = 3; i >= 0; i--) {
			buf[i] = (byte) (l & 0x7F);
			buf[i] |= 0x80;
			l = l >> 7;
		}

		for (int i = 0; i < buf.length; i++)
			if ((buf[i] & 0xFF) != 0x80) // don't write empty octets
				res.write(buf[i]);

	}

	/**
	 * encode a Date class into a long (4 octets)
	 */
	protected static void encodeDate(ByteArrayOutputStream res, Date date)
		throws Exception {
		long l = date.getTime() / 1000; // seconds since 1.1.1970, 00:00:00 GMT

		//res.write((byte) 0x06); // length of multi-octet integer (4 bytes)

		//if (absolute) encodeInt(res, 128); else
		//  encodeInt(res, 129);

		encodeLong(res, l);

	}

	/**
	 * encode a Date class into a long (4 octets)
	 * String as input
	 * 
	 * to-do: fix date string
	 */
	protected static void encodeDate(ByteArrayOutputStream res, String time)
		throws Exception {
		Date date = null;

		SimpleDateFormat[] dateFormats =
			{
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"),
			// as specified in the specs
			new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss z", Locale.US),
				new SimpleDateFormat()};
		// default locale

		int i = 0;
		boolean success = false;

		while (!success) {
			if (i > dateFormats.length - 1) {
				//log.error("encodeDate: Unable to parse: " + time);
				break;
			}
			try {
				SimpleDateFormat formatter = dateFormats[i++];
				date = formatter.parse(time);
			} catch (Exception e) {
				//log.debug(
				//	"encodeDate: "
				//		+ e.toString()
				//		+ ", using pattern: "
				//		+ dateFormats[i
				//		- 1].toPattern());
				continue;
			}
			success = true;
		}

		if (success)
			encodeDate(res, date);
	}

	protected static void encodeDateVariable(
		ByteArrayOutputStream res,
		String time,
		boolean absolute)
		throws Exception {
		Date date = null;

		SimpleDateFormat[] dateFormats =
			{
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"),
			// as specified in the specs
			new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss z", Locale.US),
				new SimpleDateFormat()};
		// default locale

		int i = 0;
		boolean success = false;

		while (!success) {
			if (i > dateFormats.length - 1) {
				//log.error("encodeDateVariable: Unable to parse: " + time);
				break;
			}
			try {
				SimpleDateFormat formatter = dateFormats[i++];
				date = formatter.parse(time);
			} catch (Exception e) {
				//log.debug(
				//	"encodeDateVariable: "
				//		+ e.toString()
				//		+ ", using pattern: "
				//		+ dateFormats[i
				//		- 1].toPattern());
				continue;
			}
			success = true;
		}

		if (success)
			encodeDateVariable(res, date, absolute);
	}

	/**
	 * encode a Date class into a long (4 octets)
	 */
	protected static void encodeDateVariable(
		ByteArrayOutputStream res,
		Date date,
		boolean absolute)
		throws Exception {
		long l = date.getTime() / 1000; // seconds since 1.1.1970, 00:00:00 GMT

		if (!absolute) {

			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			baos.write(0x81); // not absolute
			encodeLong(baos, l);
			byte[] tmp = baos.toByteArray();

			res.write(tmp.length);
			res.write(tmp);

		} else {
			res.write((byte) 0x06); // length of multi-octet integer (4 bytes)

			encodeInt(res, 128);

			encodeLong(res, l);
		}
	}

	/**
	 * encode a string
	 */
	protected static void encodeString(ByteArrayOutputStream res, String s)
		throws Exception {
		byte[] b = s.getBytes();
		if ((b == null) || (b.length == 0))
			return; // no content

		if (b[0] >= 128) {
			res.write((byte) 127); // quote
			res.write(b); //, 1, b.length-1);
		} else
			res.write(b);

		res.write((byte) 0x00); // end of string		
	}

	/**
	 * encode a short int
	 */
	protected static void encodeInt(ByteArrayOutputStream res, int i)
		throws Exception {
		byte b = (byte) (i & 0xFF);

		if (b < 128)
			b |= 128;
		res.write(b);
	}

	/**
	 * encode a boolean (with Boolean as input)
	 */
	protected static void encodeBoolean(ByteArrayOutputStream res, Boolean b)
		throws Exception {
		if (b.booleanValue())
			res.write(128);
		else // true
			res.write(129); // false
	}

	/**
	 * encode a boolean (with String as input)
	 */
	protected static void encodeBoolean(ByteArrayOutputStream res, String b)
		throws Exception {
		if (b.equalsIgnoreCase("true"))
			res.write(128);
		else // true
			res.write(129); // false
	}

	/**
	 * encode content type
	 * 
	 * format for content types is defined in [WAPWSP], 8.4.2.24:
	 * 
	 * Content-type-value = Constrained-media | Content-general-form
	 * Content-general-form = Value-length Media-type
	 * Media-type = (Well-known-media | Extension-Media) *(Parameter)
	 * 
	 * @param ct Content-type, with parameters separated with ";" (semi-colon)
	 */
	protected static void encodeContentType(ByteArrayOutputStream res, String ct)
		throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		boolean hasParameters = false;

		StringTokenizer st = new StringTokenizer(ct, ";");
		int i = 0;

		String token = st.nextToken(); // content-type

		for (i = 0; i < MMConstants.CONTENT_TYPES.length; i++)
			if (MMConstants.CONTENT_TYPES[i].equalsIgnoreCase(token)) {
				encodeInt(baos, i);
				break;
			}
		if (i == MMConstants.CONTENT_TYPES.length) { // not well known encoding found
			encodeString(baos, token); // encoded version not found		    
			//res.write(token.getBytes());		    			  		  
		}

		// encode parameters

		while (st.hasMoreTokens()) {
			token = st.nextToken(); // name-value pair
			String name = token.substring(0, token.indexOf('=')).trim();
			String value = token.substring(token.indexOf('=') + 1).trim();

			if (value.startsWith("\""))
				value = value.substring(1, value.length() - 1);
			encodeParameter(baos, name, value);
			hasParameters = true;
		}

		byte[] dta = baos.toByteArray();
		baos = null;

		// write length
		if ((hasParameters) && (dta.length > 1)) {
			if (dta.length > 30) {
				res.write(31);
				encodeUintvar(res, dta.length);
			} else
				res.write(dta.length);
		}
		res.write(dta);
	}

	/**
	 * get message class from string, it's a byte if pre-defined
	 */
	protected static void encodeMessageClass(
		ByteArrayOutputStream res,
		String msgClass)
		throws Exception {

		if (msgClass.equalsIgnoreCase("Personal"))
			encodeInt(res, MMConstants.MESSAGE_CLASS_PERSONAL);
		else if (msgClass.equalsIgnoreCase("Advertisement"))
			encodeInt(res, MMConstants.MESSAGE_CLASS_ADVERTISEMENT);
		else if (msgClass.equalsIgnoreCase("Informational"))
			encodeInt(res, MMConstants.MESSAGE_CLASS_INFORMATIONAL);
		else if (msgClass.equalsIgnoreCase("Auto"))
			encodeInt(res, MMConstants.MESSAGE_CLASS_AUTO);
		else {
			encodeString(res, msgClass);
		}
	}

	/**
	 * get message type from string, it's a byte if predefined
	 */
	protected static void encodeMessageType(ByteArrayOutputStream res, String msgType)
		throws Exception {

		if (msgType.equalsIgnoreCase("m-send-req"))
			encodeInt(res, MMConstants.MESSAGE_TYPE_M_SEND_REQ);
		else if (msgType.equalsIgnoreCase("m-send-conf"))
			encodeInt(res, MMConstants.MESSAGE_TYPE_M_SEND_CONF);
		else if (msgType.equalsIgnoreCase("m-notification-ind"))
			encodeInt(res, MMConstants.MESSAGE_TYPE_M_NOTIFICATION_IND);
		else if (msgType.equalsIgnoreCase("m-notifyresp-ind"))
			encodeInt(res, MMConstants.MESSAGE_TYPE_M_NOTIFYRESP_IND);
		else if (msgType.equalsIgnoreCase("m-retrieve-conf"))
			encodeInt(res, MMConstants.MESSAGE_TYPE_M_RETRIEVE_CONF);
		else if (msgType.equalsIgnoreCase("m-acknowledge-ind"))
			encodeInt(res, MMConstants.MESSAGE_TYPE_M_ACKNOWLEDGE_IND);
		else if (msgType.equalsIgnoreCase("m-delivery-ind"))
			encodeInt(res, MMConstants.MESSAGE_TYPE_M_DELIVERY_IND);
		else {
			encodeString(res, msgType);
		}
	}

	/**
	 * get message class from string, it's a byte if pre-defined
	 */
	protected static void encodePriority(ByteArrayOutputStream res, String pri)
		throws Exception {

		if (pri.equalsIgnoreCase("Low"))
			encodeInt(res, MMConstants.PRIORITY_LOW);
		else if (pri.equalsIgnoreCase("Normal"))
			encodeInt(res, MMConstants.PRIORITY_NORMAL);
		else if (pri.equalsIgnoreCase("High"))
			encodeInt(res, MMConstants.PRIORITY_HIGH);
		else {
			encodeString(res, pri);
		}
	}

	/**
	 * get response status, it's a byte if predefined
	 */
	protected static void encodeResponseStatus(ByteArrayOutputStream res, String resp)
		throws Exception {
		if (resp.equalsIgnoreCase("ok"))
			encodeInt(res, MMConstants.RESPONSE_STATUS_OK);
		else if (resp.equalsIgnoreCase("Error-unspecified"))
			encodeInt(res, MMConstants.RESPONSE_STATUS_ERROR_UNSPECIFIED);
		else if (resp.equalsIgnoreCase("Error-service-denied"))
			encodeInt(res, MMConstants.RESPONSE_STATUS_ERROR_SERVICE_DENIED);
		else if (resp.equalsIgnoreCase("Error-message-format-corrupt"))
			encodeInt(res, MMConstants.RESPONSE_STATUS_ERROR_MESSAGE_FORMAT_CORRUPT);
		else if (resp.equalsIgnoreCase("Error-sending-address-unresolved"))
			encodeInt(res, MMConstants.RESPONSE_STATUS_ERROR_SENDING_ADDRESS_UNSPECIFIED);
		else if (resp.equalsIgnoreCase("Error-message-not-found"))
			encodeInt(res, MMConstants.RESPONSE_STATUS_ERROR_MESSAGE_NOT_FOUND);
		else if (resp.equalsIgnoreCase("Error-network-problem"))
			encodeInt(res, MMConstants.RESPONSE_STATUS_ERROR_NETWORK_PROBLEM);
		else if (resp.equalsIgnoreCase("Error-content-not-accepted"))
			encodeInt(res, MMConstants.RESPONSE_STATUS_ERROR_CONTENT_NOT_ACCEPTED);
		else if (resp.equalsIgnoreCase("Error-unsupported-message"))
			encodeInt(res, MMConstants.RESPONSE_STATUS_ERROR_UNSUPPORTED_MESSAGE);
		else {
			encodeString(res, resp);
		}
	}

	/**
	 * get response status, it's a byte if predefined
	 */
	protected static void encodeStatus(ByteArrayOutputStream res, String resp)
		throws Exception {
		if (resp.equalsIgnoreCase("Expired"))
			encodeInt(res, MMConstants.STATUS_EXPIRED);
		else if (resp.equalsIgnoreCase("Retrieved"))
			encodeInt(res, MMConstants.STATUS_RETRIEVED);
		else if (resp.equalsIgnoreCase("Rejected"))
			encodeInt(res, MMConstants.STATUS_REJECTED);
		else if (resp.equalsIgnoreCase("Deferred"))
			encodeInt(res, MMConstants.STATUS_DEFERRED);
		else if (resp.equalsIgnoreCase("Unrecognised"))
			encodeInt(res, MMConstants.STATUS_UNRECOGNIZED);
		else {
			encodeString(res, resp);
		}
	}

	/**
	 * encode from element
	 */
	protected static void encodeFrom(ByteArrayOutputStream res, String from)
		throws Exception { 
		encodeUintvar(res, from.length() + 2);
		// address-present-token + str length + null
		encodeInt(res, 0); // address-present-token
		encodeString(res, from);
	}

	/**
	 * encode charset
	 */
	protected static void encodeCharset(ByteArrayOutputStream res, String value)
		throws Exception {

		for (int c = 0; c < MMConstants.WELLKNOWN_CHARSETS.length; c++) {
			if (((String) MMConstants.WELLKNOWN_CHARSETS[c][0]).equalsIgnoreCase(value)) {
				encodeInteger(
					res,
					((Integer) MMConstants.WELLKNOWN_CHARSETS[c][1]).intValue());
				return;
			}
		}

		// no well-known substitute		
		encodeString(res, value);
	}
	
	/**
	 * validate given message
	 */
	protected static void validateMMMessage(MMMessage msg) throws MMEncodingException {
		if (!msg.isTransactionIdAvailable())
			throw new MMEncodingException("X-MMS-Transaction-ID missing or invalid");
		if (!msg.isVersionAvailable())
			throw new MMEncodingException("X-MMS-Version missing or invalid");

		// validate
		switch (msg.getMessageType()) {
			case MMConstants.MESSAGE_TYPE_M_SEND_REQ :
				if (!msg.isFromAvailable())
					throw new MMEncodingException("From (sender) missing or invalid");
				if (!msg.isContentTypeAvailable())
					throw new MMEncodingException("Content-Type missing or invalid");
				break;
			case MMConstants.MESSAGE_TYPE_M_SEND_CONF :
				if (!msg.isResponseStatusAvailable())
					throw new MMEncodingException("X-MMS-Response-Status missing or invalid");
				break;
			case MMConstants.MESSAGE_TYPE_M_NOTIFICATION_IND :
				if (!msg.isMessageClassAvailable())
					throw new MMEncodingException("X-MMS-Message-Class missing or invalid");
				if (!msg.isContentLocationAvailable())
					throw new MMEncodingException("Content-Location missing or invalid");
				break;
			case MMConstants.MESSAGE_TYPE_M_NOTIFYRESP_IND :
				if (!msg.isStatusAvailable())
					throw new MMEncodingException("X-MMS-Status missing or invalid");
				break;
			case MMConstants.MESSAGE_TYPE_M_RETRIEVE_CONF :
				if (!msg.isDateAvailable())
					throw new MMEncodingException("Date missing or invalid");
				if (!msg.isContentTypeAvailable())
					throw new MMEncodingException("Content-Type missing or invalid");
				break;
			case MMConstants.MESSAGE_TYPE_M_ACKNOWLEDGE_IND :
				break;
			case MMConstants.MESSAGE_TYPE_M_DELIVERY_IND :
				if (!msg.isMessageIdAvailable())
					throw new MMEncodingException("Message-ID missing or invalid");
				if (!msg.isToAvailable())
					throw new MMEncodingException("To (recipient) missing or invalid");
				if (!msg.isStatusAvailable())
					throw new MMEncodingException("X-MMS-Status missing or invalid");
				break;
			default :
				throw new MMEncodingException("Unknown X-MMS-Message-Type");

		}

	}

}
