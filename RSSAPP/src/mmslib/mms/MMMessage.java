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
 
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
 
/**
 * This class represents a multimedia message. A multimedia message consists
 * of several parts, each containing headers and body (as in a mime message). 
 * <p>
 * The resulting message is encoded according to [MMSEncapsulation] with well-known
 * headers and corresponding values encoded according to [WAPWSP].
 * <p>
 * This class fully represents a MMS message (as specified in [MMSEncapsulation]), 
 * encoding and decoding functions are done by the MMEncoder and MMDecoder classes. 
 * <p>
 * References: 
 * <p>
 * <a href="http://www1.wapforum.org/tech/documents/WAP-209-MMSEncapsulation-20020105-a.pdf">
 * [MMSEncapsulation] WAP-209-MMSEncapsulation-20020105-a 
 * </a><br>
 * <a href="http://www1.wapforum.org/tech/documents/WAP-230-WSP-20010705-a.pdf">
 * [WAPWSP] WAP-230-WSP-20010705-a</a> 
 * <p>
 * 
 * <strong>Example 1: Composing a message</strong><p>
 * <pre>
 * 	MMMessage msg = new MMMessage();
 * 	msg.setMessageType(MMMessage.MESSAGE_TYPE_M_SEND_REQ);
 * 	msg.setTransactionId("0001");
 * 	msg.setFrom("+358405134265/TYPE=PLMN");
 * 	msg.setSubject("This is an example");
 * 
 * 	// add content
 * 	msg.addPart("text/smil", "<smil></smil>".getBytes(), false, null, null)
 * 
 * 	// print out (or do whatever)
 * 	System.out.println(msg); 
 * </pre>
 * <p>
 *
 * <strong>Example 2: Reading a message from file (decoding)</strong><p>
 * <pre>
 * 	String filename = "input.mms";
 * 	File f = new File(filename);
 *	
 * 	try {
 * 		FileInputStream fis = new FileInputStream(f);
 *			
 *		byte[] data = new byte[(int) f.length()];
 *		fis.read(data);
 *		fis.close();
 *			
 *		MMMessage msg = MMDecoder.decode(data);
 *
 *	} catch (Exception e) {
 *		System.out.println(e);
 *	} 
 * </pre>
 * <p>
 * <strong>Example 3: Writing (encoding) a message</strong><p> 
 * <pre>
 *	MMessage msg = ... // a created MMMessage
 *   
 * 	try {
 * 		File f = new File("output.mms");
 *		byte[] out = msg.encode();
 *		FileOutputStream fos = new FileOutputStream(f);
 *		fos.write(out);
 *		fos.flush();
 *		fos.close(); 
 * 	} catch (Exception e) {
 * 		System.out.println(e);
 * 	}
 * </pre>
 * <p>
 * 
 * @author Anders Lindh
 * @copyright Copyright FlyerOne Ltd 2005
 * @version $Revision: 1.1.1.1 $ $Date: 2005/04/14 09:04:10 $
 * @see MimeMessage
 *
 */
public class MMMessage extends MimeMessage implements Serializable {

	protected ArrayList bcc = null;
	protected ArrayList cc = null;
	protected String contentLocation = null;
	protected Date date = null;
	protected Boolean deliveryReport = null;
	protected Date deliveryTime = null;
	protected boolean deliveryTimeAbsolute = true;
	protected Date expiry = null;	
	protected boolean expiryAbsolute = true;
	protected String from = null;
	protected int messageClass = -1;
	protected String messageId = null;
	protected int messageType = -1;
	protected long messageSize = -1;
	protected int version = -1;
	protected int priority = -1;
	protected Boolean readReply = null;
	protected Boolean reportAllowed = null;
	protected int responseStatus = -1;
	protected String responseText = null;
	protected Boolean senderVisibility = null;
	protected int status = -1;
	protected String subject = null;
	protected ArrayList to = null; 
	protected String transactionId = null;	
	
	//protected boolean bModified = false; // wether this class has been modofied
	//protected byte[] rawContent = null;

	/**
	 * Set bcc (blind carbon copy). This is a shotcut to clearBcc(), addBccAddress(...)
	 */	
	public void setBcc(String s) { 
		clearBcc();
		addBccAddress(s);
	}
	
	/**
	 * Get Bcc (blind carbon copy) addresses, returns an empty ArrayList if none is set
	 */
	public ArrayList getBcc() { if (bcc == null) return new ArrayList(); else return bcc; }
	
	/**
	 * Remove all Bcc recipients
	 */
	public void clearBcc() { this.bcc = null; }
	
	/**
	 * Add a Bcc recipient
	 */
	public void addBccAddress(String s) { if (bcc == null) bcc = new ArrayList(); bcc.add(s); }

	/**
	 * Set Cc (carbon copy) field. This is a shotcut to clearCc(), addCcAddress(...)
	 */	
	public void setCc(String s) { 
		clearCc();
		addCcAddress(s);
	}

	/**
	 * Get Cc (carbon copy) recipient addresses, returns an empty ArrayList if none is set
	 */	
	public ArrayList getCc() { if (cc == null) return new ArrayList(); else return cc; }
	
	/**
	 * Remove all Cc recipients
	 */
	public void clearCc() { this.cc = null; }
	
	/**
	 * Add a Cc recipient
	 */	
	public void addCcAddress(String s) { if (cc == null) cc = new ArrayList(); cc.add(s); }

	/**
	 * Set Content-Location
	 */	
	public void setContentLocation(String loc) { this.contentLocation = loc; }
	
	/**
	 * Return Content-Location
	 */	
	public String getContentLocation() { return this.contentLocation; }
		
	/**
	 * Set Date
	 */
	public void setDate(Date date) { this.date = date; }
	
	/**
	 * Return Date
	 */
	public Date getDate() { return this.date; }
	
	/**
	 * Return string representation of date
	 */
	public String getDateStr() { return MMDecoder.formatDate(this.date); }

	/**
	 * Set X-MMS-Delivery-Report. Set to null to disable
	 */
	public void setDeliveryReport(Boolean b) { this.deliveryReport = b; }
	
	/**
	 * Return X-MMS-Delivery-Report. False is returned if null.
	 */
	public boolean getDeliveryReport() { if (deliveryReport == null) return false; return this.deliveryReport.booleanValue(); }

	/**
	 * Set X-MMS-Delivery-Time
	 */
	public void setDeliveryTime(Date date) { this.deliveryTime = date; }
	
	public void setDeliveryTimeAbsolute(boolean b) { this.deliveryTimeAbsolute = b; }
	public boolean getDeliveryTimeAbsolute() { return this.deliveryTimeAbsolute; }
	
	/**
	 * Get X-MMS-Delivery-Time
	 */
	public Date getDeliveryTime() { return this.deliveryTime; }
	
	/**
	 * Get X-MMS-Delivery-Time as String
	 */
	public String getDeliveryTimeStr() { 
		if (deliveryTimeAbsolute) return MMDecoder.formatDate(this.deliveryTime); else
			return MMDecoder.formatDate(new Date(this.deliveryTime.getTime() + System.currentTimeMillis()));
	}

	/**
	 * Set X-MMS-Expiry
	 */
	public void setExpiry(Date date) { this.expiry = date; }
	
	/**
	 * Return X-MMS-Expiry
	 */
	public Date getExpiry() { return this.expiry; }

	public void setExpiryAbsolute(boolean b) { this.expiryAbsolute = b; }
	public boolean getExpiryAbsolute() { return this.expiryAbsolute; }
	
	/**
	 * Return X-MMS-Expiry time as String
	 */
	public String getExpiryStr() { 
		if (expiryAbsolute) return MMDecoder.formatDate(this.expiry); else
			return MMDecoder.formatDate(new Date(this.expiry.getTime() + System.currentTimeMillis()));
	}

	/**
	 * Set From address
	 */
	public void setFrom(String from) { this.from = from; }
	
	/**
	 * Get From address
	 */
	public String getFrom() { return this.from; }

	/**
	 * Set X-MMS-Message-Class
	 */
	public void setMessageClass(int c) { messageClass = c; }
	
	/**
	 * Return X-MMS-Message-Class
	 */
	public int getMessageClass() { if (messageClass == -1) return 0; else return messageClass; }
	
	/**
	 * Return string representation of X-MMS-Message-Class
	 */
	public String getMessageClassStr() { if (messageClass == -1) return null; return MMConstants.MESSAGE_CLASSES[messageClass]; }

	/**
	 * Set Message-ID
	 */
	public void setMessageId(String id) { this.messageId = id; }
	
	/**
	 * Return Message-ID
	 */
	public String getMessageId() { if (messageId == null) return ""; else return this.messageId; }
	
	/**
	 * set X-MMS-Message-Type
	 */
	public void setMessageType(int type) { messageType = type; }
	
	/**
	 * Return X-MMS-Message-Type
	 */
	public int getMessageType() { return messageType; }

	/**
	 * Return string representation of X-MMS-Message-Type
	 */
	public String getMessageTypeStr() { if (messageType == -1) return null; return MMConstants.MESSAGE_TYPES[messageType]; }

	/**
	 * set X-MMS-Message-Size
	 */
	public void setMessageSize(long size) { this.messageSize = size; }
	
	/**
	 * get X-MMS-Message-Size
	 */
	public long getMessageSize() { return messageSize; }
	
	/**
	 * set X-MMS-Version
	 */
	public void setVersion(int version) { this.version = version; }
	
	/**
	 * Return X-MMS-Version
	 */
	public int getVersion() { return version; }
	
	/**
	 * Return string representation of X-MMS-Version
	 */
	public String getVersionStr() { if (version == -1) return null; return MMDecoder.versionToString(version); }	
	
	/**
	 * Set X-MMS-Priority
	 */
	public void setPriority(int p) { this.priority = p; }
	
	/**
	 * Return X-MMS-Priority
	 */
	public int getPriority() { if (priority == -1) return 0; else return priority; }

	/**
	 * Return string representation of X-MMS-Priority
	 */
	public String getPriorityStr() { if (priority == -1) return null; return MMConstants.PRIORITIES[priority]; }

	/**
	 * Set X-MMS-Read-Reply
	 */
	public void setReadReply(Boolean b) { this.readReply = b; }
	
	/**
	 * Get X-MMS-Read-Reply
	 */
	public boolean getReadReply() { if (readReply == null) return false; return this.readReply.booleanValue(); }

	/**
	 * Set X-MMS-Report-Allowed
	 */
	public void setReportAllowed(Boolean b) { this.reportAllowed = b; }
	
	/**
	 * Get X-MMS-Report-Allowed
	 */
	public boolean getReportAllowed() { if (reportAllowed == null) return false; return this.reportAllowed.booleanValue(); }

	/**
	 * Set X-MMS-Response-Status
	 */
	public void setResponseStatus(int p) { this.responseStatus = p; }
	
	/**
	 * Get X-MMS-Response-Status
	 */
	public int getResponseStatus() { return this.responseStatus; }

	/**
	 * Return string representation of X-MMS-Response-Status
	 */
	public String getResponseStatusStr() { if (responseStatus == -1) return null; return MMConstants.RESPONSE_STATUSES[responseStatus]; }

	/**
	 * Set X-MMS-Response-Text
	 */
	public void setResponseText(String id) { this.responseText = id; }
	
	/**
	 * Get X-MMS-Response-Text
	 */
	public String getResponseText() { return this.responseText; }
	
	/**
	 * Set X-MMS-Sender-Visibility
	 */
	public void setSenderVisibility(Boolean b) { this.senderVisibility = b; }
	
	/**
	 * Get X-MMS-Sender-Visibility
	 */
	public Boolean getSenderVisibility() { return this.senderVisibility; }
	
	/**
	 * Set X-MMS-Status
	 */
	public void setStatus(int p) { this.status = p; }
	
	/**
	 * Get X-MMS-Status
	 */
	public int getStatus() { return this.status; }

	/**
	 * Return string representation of X-MMS-Status
	 */
	public String getStatusStr() { if (status == -1) return null; return MMConstants.STATUSES[status]; }
	
	/**
	 * Set Subject
	 */
	public void setSubject(String s) { this.subject = s; }
	
	/**
	 * Get Subject
	 */
	public String getSubject() { return this.subject; }

	/**
	 * Set To -address. Shortcut for clearTo(), addToAddress(..)
	 */
	public void setTo(String s) { this.to = new ArrayList(); this.to.add(s); }
	
	/**
	 * Return To addresses
	 */
	public ArrayList getTo() { return to; }
	
	/**
	 * Clear all To -recipients
	 */
	public void clearTo() { this.to = null; }
	
	/**
	 * Add a To recipient
	 */
	public void addToAddress(String s) { if (to == null) to = new ArrayList(); to.add(s); }

	/**
	 * Set X-MMS-Transaction-ID
	 */
	public void setTransactionId(String s) { this.transactionId = s; }
	
	/**
	 * Get X-MMS-Transaction-Id
	 */
	public String getTransactionId() { return this.transactionId; }


	/**
	 * check if Content-Location is available (i.e. not null)
	 */	
	public boolean isContentLocationAvailable() { return contentLocation != null; }

	/**
	 * check if Content-Type is available (i.e. not null)
	 */	
	public boolean isContentTypeAvailable() { return contentType != null; }

	/**
	 * check if Date is available
	 */
	public boolean isDateAvailable() { return date != null; }
	
	/**
	 * check if X-MMS-Delivery-Report is available (i.e. not null)
	 */	
	public boolean isDeliveryReportAvailable() { return deliveryReport != null; }
	
	/**
	 * check if X-MMS-Delivery-Time is available (i.e. not null)
	 */
	public boolean isDeliveryTimeAvailable() { return deliveryTime != null; }
	
	/**
	 * check if X-MMS-Expiry is available (i.e. not null)
	 */	
	public boolean isExpiryAvailable() { return expiry != null; }
	
	/**
	 * check if From is available (i.e. not null)
	 */		
	public boolean isFromAvailable() { return from != null; }	
	
	/**
	 * check if X-MMS-Message-Class is available
	 */			
	public boolean isMessageClassAvailable() { return messageClass != -1; }	
	
	/**
	 * check if Message-ID is available
	 */				
	public boolean isMessageIdAvailable() { return messageId != null; }
	
	/**
	 * check if X-MMS-Message-Type is available
	 */					
	public boolean isMessageTypeAvailable() { return messageType != -1; }	
	
	/**
	 * check if X-MMS-Message-Priority is available
	 */						
	public boolean isPriorityAvailable() { return priority != -1; }	
	
	/**
	 * check if X-MMS-Read-Reply is available (i.e. not null)
	 */							
	public boolean isReadReplyAvailable() { return readReply != null; }	
	
	/**
	 * check if X-MMS-Response-Status is available (i.e. not null)
	 */								
	public boolean isResponseStatusAvailable() { return responseStatus != -1; }	
	
	/**
	 * check if X-MMS-Response-Text is available (i.e. not null)
	 */									
	public boolean isResponseTextAvailable() { return responseText != null; }	
	
	/**
	 * check if X-MMS-Sender-Visibility is available (i.e. not null)
	 */										
	public boolean isSenderVisibilityAvailable() { return senderVisibility != null; }	
	
	/**
	 * check if X-MMS-Status is available
	 */											
	public boolean isStatusAvailable() { return status != -1; }	
	
	/**
	 * check if Subject is available (i.e. not null)
	 */												
	public boolean isSubjectAvailable() { return subject != null; }	
	
	/**
	 * check if To is available 
	 */													
	public boolean isToAvailable() { return to != null; }	

	/**
	 * check if Cc is available 
	 */													
	public boolean isCcAvailable() { return cc != null; }	

	/**
	 * check if Bcc is available 
	 */													
	public boolean isBccAvailable() { return bcc != null; }	

	
	/**
	 * check if X-MMS-Transaction-ID is available 
	 */														
	public boolean isTransactionIdAvailable() { return transactionId != null; }	
	
	/**
	 * check if X-MMS-Version is available 
	 */															
	public boolean isVersionAvailable() { return version != -1; }	
	

	/**
	 * Default no-args constructor, create an empty MMMessage
	 */
	public MMMessage() {
		super();
	}
		
	/**
	 * Creates a MMMessage with given message type and transaction id
	 */
	public MMMessage(int messageType, String transactionId) {
		super();
		setMessageType(messageType);
		setTransactionId(transactionId);		
		setVersion(MMConstants.MMS_VERSION_1_0);
	}
		
	/**
	 * Create a string (mime) representation of this message
	 * 
	 * @return String containing a mime encoded message
	 */
	public String toString() {		
		try {		
			return MMEncoder.toString(this);
		} catch (Exception e) {
			e.printStackTrace();	
		}
		return null;
	} 
	
	/**
	 * Encode the message, i.e. create a valid mms encapsulated byte array
	 * 
	 * @return byte[] containing the binary representation of this message
	 * @throws MMEncodingException
	 */
	public byte[] encode() throws MMEncodingException {
		return MMEncoder.encode(this);
	}
	
}
