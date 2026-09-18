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

/**
 * This class contains all constants related to MMMessages
 * 
 * @author Anders Lindh
 * @copyright Copyright FlyerOne Ltd 2005
 * @version $Revision: 1.1.1.1 $ $Date: 2005/04/14 09:04:10 $
 */
public class MMConstants {
	// Constants used in representing MM messages

	public static final int MESSAGE_TYPE_M_SEND_REQ = 0;
	public static final int MESSAGE_TYPE_M_SEND_CONF = 1;
	public static final int MESSAGE_TYPE_M_NOTIFICATION_IND = 2;
	public static final int MESSAGE_TYPE_M_NOTIFYRESP_IND = 3;
	public static final int MESSAGE_TYPE_M_RETRIEVE_CONF = 4;
	public static final int MESSAGE_TYPE_M_ACKNOWLEDGE_IND = 5;
	public static final int MESSAGE_TYPE_M_DELIVERY_IND = 6;	
	
	protected static final String[] MESSAGE_TYPES = {"m-send-req",
														"m-send-conf",
														"m-notification-ind",
														"m-notifyresp-ind",
														"m-retrieve-conf",
														"m-acknowledge-ind",
														"m-delivery-ind"};		

	public static final int MESSAGE_CLASS_PERSONAL = 0;
	public static final int MESSAGE_CLASS_ADVERTISEMENT = 1;
	public static final int MESSAGE_CLASS_INFORMATIONAL = 2;
	public static final int MESSAGE_CLASS_AUTO = 3;

	protected static final String[] MESSAGE_CLASSES =   {"Personal",
															"Advertisement",
															"Informational",
															"Auto"};
		
	public static final int PRIORITY_LOW = 0;
	public static final int PRIORITY_NORMAL = 1;
	public static final int PRIORITY_HIGH = 2;	

	protected static final String[] PRIORITIES =    {"Low",
														"Normal",
														"High"};
	
	public static final int RESPONSE_STATUS_OK = 0;
	public static final int RESPONSE_STATUS_ERROR_UNSPECIFIED = 1;
	public static final int RESPONSE_STATUS_ERROR_SERVICE_DENIED = 2;
	public static final int RESPONSE_STATUS_ERROR_MESSAGE_FORMAT_CORRUPT = 3;
	public static final int RESPONSE_STATUS_ERROR_SENDING_ADDRESS_UNSPECIFIED = 4;
	public static final int RESPONSE_STATUS_ERROR_MESSAGE_NOT_FOUND = 5;
	public static final int RESPONSE_STATUS_ERROR_NETWORK_PROBLEM = 6;
	public static final int RESPONSE_STATUS_ERROR_CONTENT_NOT_ACCEPTED = 7;
	public static final int RESPONSE_STATUS_ERROR_UNSUPPORTED_MESSAGE = 8;

	protected static final String[] RESPONSE_STATUSES = {"ok",
															"Error-unspecified",
															"Error-service-denied",
															"Error-message-format-corrupt",
															"Error-sending-address-unresolved",
															"Error-message-not-found",
															"Error-network-problem",
															"Error-content-not-accepted",
															"Error-unsupported-message"};
	
	public static final int STATUS_EXPIRED = 0;
	public static final int STATUS_RETRIEVED = 1;
	public static final int STATUS_REJECTED = 2;
	public static final int STATUS_DEFERRED = 3;
	public static final int STATUS_UNRECOGNIZED = 4;		
	
	protected static final String[] STATUSES = { "Expired",
													"Retrieved",
													"Rejected",
													"Deferred",
													"Unrecognised"};
	
	public static final int MMS_VERSION_1_0 = (1 << 4);
	
	// valid content types (http://www.wapforum.org/wina/wsp-content-type.htm)
	// array index is the encoded value
	protected static String[] CONTENT_TYPES = {"*/*",
												"text/*", 
												"text/html", 
												"text/plain",
												"text/x-hdml",
												"text/x-ttml", 
												"text/x-vCalendar",
												"text/x-vCard", 
												"text/vnd.wap.wml",
												"text/vnd.wap.wmlscript",
												"text/vnd.wap.wta-event",
												"multipart/*",
												"multipart/mixed", 
												"multipart/form-data",
												"multipart/byterantes",
												"multipart/alternative",
												"application/*", 
												"application/java-vm",
												"application/x-www-form-urlencoded", 
												"application/x-hdmlc",
												"application/vnd.wap.wmlc", 
												"application/vnd.wap.wmlscriptc",
												"application/vnd.wap.wta-eventc",
												"application/vnd.wap.uaprof", 
												"application/vnd.wap.wtls-ca-certificate",
												"application/vnd.wap.wtls-user-certificate",
												"application/x-x509-ca-cert", 
												"application/x-x509-user-cert", 
												"image/*", 
												"image/gif",
												"image/jpeg",
												"image/tiff", 
												"image/png",
												"image/vnd.wap.wbmp",
												"application/vnd.wap.multipart.*",
												"application/vnd.wap.multipart.mixed",
												"application/vnd.wap.multipart.form-data",
												"application/vnd.wap.multipart.byteranges",
												"application/vnd.wap.multipart.alternative",
												"application/xml",
												"text/xml",
												"application/vnd.wap.wbxml",
												"application/x-x968-cross-cert",
												"application/x-x968-ca-cert",
												"application/x-x968-user-cert",
												"text/vnd.wap.si",
												"application/vnd.wap.sic",
												"text/vnd.wap.sl",
												"application/vnd.wap.slc",
												"text/vnd.wap.co",
												"application/vnd.wap.coc",
												"application/vnd.wap.multipart.related",
												"application/vnd.wap.sia",
												"text/vnd.wap.connectivity-xml",
												"application/vnd.wap.connectivity-wbxml",
												"application/pkcs7-mime",
												"application/vnd.wap.hashed-certificate",
												"application/vnd.wap.signed-certificate",
												"application/vnd.wap.cert-response",
												"application/xhtml+xml",
												"application/wml+xml",
												"text/css",
												"application/vnd.wap.mms-message",
												"application/vnd.wap.rollover-certificate",
												"application/vnd.wap.locc+wbxml",
												"application/vnd.wap.loc+xml",
												"application/vnd.syncml.dm+wbxml",
												"application/vnd.syncml.dm+xml",
												"application/vnd.syncml.notification",
												"application/vnd.wap.xhtml+xml" };

 
	// types used for encoding
	private static final Integer TYPE_STRING = new Integer(0);
	private static final Integer TYPE_SHORTINT = new Integer(1);
	private static final Integer TYPE_UINTVAR = new Integer(2);
	private static final Integer TYPE_LONG = new Integer(3);
	private static final Integer TYPE_BOOLEAN = new Integer(4);
	private static final Integer TYPE_DATE = new Integer(5);
	private static final Integer TYPE_MSGCLASS = new Integer(6);
	private static final Integer TYPE_MSGTYPE = new Integer(7);
	private static final Integer TYPE_PRIORITY = new Integer(8);
	private static final Integer TYPE_RESPONSESTATUS = new Integer(9);
	private static final Integer TYPE_STATUS = new Integer(10);
	private static final Integer TYPE_CONTENTTYPE = new Integer(11);	
	private static final Integer TYPE_VERSION = new Integer(12);		
	private static final Integer TYPE_FROM = new Integer(13);
	private static final Integer TYPE_NOVALUE = new Integer(14);
	private static final Integer TYPE_CHARSET = new Integer(15);
	private static final Integer TYPE_Q = new Integer(16);
	private static final Integer TYPE_INTEGER = new Integer(17);
	private static final Integer TYPE_DATE_VARIABLE = new Integer(18);
							
	// well known headers (i.e. the ones with encoding values (MMSEncapsulation)
	// value used for encoding is the index in this array, and data type as
	// second column	
	protected static final Object[][] knownMMSHeaders = {{"Bcc", TYPE_STRING},
														{"Cc", TYPE_STRING},
														{"X-MMS-Content-Location", TYPE_STRING},
														{"Content-Type", TYPE_CONTENTTYPE},
														{"Date", TYPE_DATE},
														{"X-MMS-Delivery-Report", TYPE_BOOLEAN},
														{"X-MMS-Delivery-Time", TYPE_DATE_VARIABLE},
														{"X-MMS-Expiry", TYPE_DATE_VARIABLE},
														{"From", TYPE_FROM},
														{"X-MMS-Message-Class", TYPE_MSGCLASS},
														{"Message-ID", TYPE_STRING},
														{"X-MMS-Message-Type", TYPE_MSGTYPE},
														{"X-MMS-Version", TYPE_VERSION},
														{"Message-Size", TYPE_INTEGER},
														{"X-MMS-Priority", TYPE_PRIORITY},
														{"X-MMS-Read-Reply", TYPE_BOOLEAN},
														{"X-MMS-Report-Allowed", TYPE_BOOLEAN},
														{"X-MMS-Response-Status", TYPE_RESPONSESTATUS},
														{"X-MMS-Response-Text", TYPE_STRING},
														{"X-MMS-Sender-Visibility", TYPE_BOOLEAN},
														{"X-MMS-Status", TYPE_STATUS},
														{"Subject", TYPE_STRING},
														{"To", TYPE_STRING},
														{"X-MMS-Transaction-Id", TYPE_STRING} };
	
	// known WSP headers, these are all Strings
	
	protected static final Object[][] knownWSPHeaders = {{"Accept", TYPE_STRING},
														{"Accept-Charset", TYPE_STRING},
														{"Accept-Encoding", TYPE_STRING},
														{"Accept-Language", TYPE_STRING},
														{"Accept-Ranges", TYPE_STRING},
														{"Age", TYPE_STRING},
														{"Allow", TYPE_STRING},
														{"Authorization", TYPE_STRING},
														{"Cache-Control", TYPE_STRING},
														{"Connection", TYPE_STRING},
														{"Content-Base", TYPE_STRING},
														{"Content-Encoding", TYPE_STRING},
														{"Content-Language", TYPE_STRING},
														{"Content-Length", TYPE_STRING},
														{"Content-Location", TYPE_STRING},
														{"Content-MD5", TYPE_STRING},
														{"Content-Range", TYPE_STRING},
														{"Content-Type", TYPE_STRING},
														{"Date", TYPE_DATE},
														{"Etag", TYPE_STRING},
														{"Expires", TYPE_STRING},
														{"From", TYPE_STRING},
														{"Host", TYPE_STRING},
														{"If-Modified-Since", TYPE_STRING},
														{"If-Match", TYPE_STRING},
														{"If-None-Match", TYPE_STRING},
														{"If-Range", TYPE_STRING},
														{"If-Unmodified-Since", TYPE_STRING},
														{"Location", TYPE_STRING},
														{"Last-Modified", TYPE_STRING},
														{"Max-Forwards", TYPE_STRING},
														{"Pragma", TYPE_STRING},
														{"Proxy-Authenticate", TYPE_STRING},
														{"Proxy-Authorization", TYPE_STRING},
														{"Public", TYPE_STRING},
														{"Range", TYPE_STRING},
														{"Referer", TYPE_STRING},
														{"Retry-After", TYPE_STRING},
														{"Server", TYPE_STRING},
														{"Transfer-Encoding", TYPE_STRING},
														{"Upgrade", TYPE_STRING},
														{"User-Agent", TYPE_STRING},
														{"Vary", TYPE_STRING},
														{"Via", TYPE_STRING},
														{"Warning", TYPE_STRING},
														{"WWW-Authenticate", TYPE_STRING},
														{"Content-Disposition", TYPE_STRING},
														{"X-Wap-Application-Id", TYPE_STRING},
														{"X-Wap-Content-URI", TYPE_STRING},
														{"X-Wap-Initiator-URI", TYPE_STRING},
														{"Accept-Application", TYPE_STRING},
														{"Bearer-Indication", TYPE_STRING},
														{"Push-Flag", TYPE_STRING},
														{"Profile", TYPE_STRING},
														{"Profile-Diff", TYPE_STRING},
														{"Profile-Warning", TYPE_STRING},
														{"Expect", TYPE_STRING},
														{"TE", TYPE_STRING},
														{"Trailer", TYPE_STRING},
														{"Accept-Charset", TYPE_STRING},
														{"Accept-Encoding", TYPE_STRING},
														{"Cache-Control", TYPE_STRING},
														{"Content-Range", TYPE_STRING},
														{"X-Wap-Tod", TYPE_STRING},
														{"XXXContent-IdXX", TYPE_STRING}, // somehow this doesn't work with Nokia mmsc
														{"Set-Cookie", TYPE_STRING},
														{"Cookie", TYPE_STRING},
														{"Encoding-Version", TYPE_STRING},
														{"Profile-Warning", TYPE_STRING},
														{"Content-Disposition", TYPE_STRING},
														{"X-WAP-Security", TYPE_STRING},
														{"Cache-Control"}};  
				
	/**
	 * well-known parameters
	 */
	protected static final Object[][] WELLKNOWN_PARAMETERS =   { {"Q", TYPE_Q},
																{"Charset", TYPE_CHARSET},
																{"Level", TYPE_VERSION},
																{"Type2", TYPE_SHORTINT},
																{"Type-THISDOESNOTEXIST", TYPE_SHORTINT},																
																{"Name", TYPE_STRING},			// deprecated
																{"Filename", TYPE_STRING},		// deprecated
																{"Differences", TYPE_STRING},   // <- check this
																{"Padding", TYPE_SHORTINT},
																{"Type", TYPE_STRING}, 
																{"Start", TYPE_STRING},			// deprecated
																{"Start-info", TYPE_STRING},	// deprecated
																{"Comment", TYPE_STRING},		// deprecated
																{"Domain", TYPE_STRING},		// deprecated
																{"Max-Age", TYPE_DATE}, // <- delta seconds
																{"Path", TYPE_STRING},			// deprecated
																{"Secure", TYPE_NOVALUE}, 
																{"SEC", TYPE_SHORTINT},
																{"MAC", TYPE_STRING},
																{"Creation-date", TYPE_DATE}, 
																{"Modification-date", TYPE_DATE},
																{"Read-date", TYPE_DATE},
																{"Size", TYPE_SHORTINT},
																{"Name", TYPE_STRING},
																{"Filename", TYPE_STRING},
																{"Start", TYPE_STRING},
																{"Start-info", TYPE_STRING},
																{"Comment", TYPE_STRING},
																{"Domain", TYPE_STRING},
																{"Path", TYPE_STRING} };

	/**
	 * known charsets
	 */
	protected static final Object[][] WELLKNOWN_CHARSETS =	{	{"big5", new Integer(0x07EA)},
																{"iso-10646-ucs-2", new Integer(0x03E8)},
																{"us-ascii", new Integer(0x03)},
																{"iso-8859-1", new Integer(0x04)},
																{"iso-8859-2", new Integer(0x05)},
																{"iso-8859-3", new Integer(0x06)},
																{"iso-8859-4", new Integer(0x07)},
																{"iso-8859-5", new Integer(0x08)},
																{"iso-8859-6", new Integer(0x09)},
																{"iso-8859-7", new Integer(0x0A)},
																{"iso-8859-8", new Integer(0x0B)},
																{"iso-8859-9", new Integer(0x0C)},
																{"shift_JIS", new Integer(0x11)},
																{"utf-8", new Integer(0x6A)} 
															};
	
}
