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
import java.util.Date;

/**
 * This class represents a m-notification-ind message. This OTA message informs
 * the handset that there is a message waiting to be fetched. 
 * <p>
 * push-id is used as X-MMS-Transaction-ID
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
 * @author Anders Lindh
 * @copyright Copyright FlyerOne Ltd 2005
 * @version $Revision: 1.1.1.1 $ $Date: 2005/04/14 09:04:10 $
 */ 
public class MNotificationIndMessage extends PpgMessage {

	protected String subject = null;
	protected int msgclass = -1;  // Personal|Advertisement|Informational|Auto | Token text
	protected long size = 0;		// message size (aprox.)
	protected String contentLocation = null;
	
	
	/**
	 * Constructor for MNotificationIndMessage
	 * 
	 * @param pushId Value used as X-MMS-Transaction-Id
	 * @param address Recipient address
	 * @param msgclass X-MMS-Message-Class
	 * @param size X-MMS-Message-Size
	 * @param contentLocation URL of content
	 * 
	 */	
	public MNotificationIndMessage(String pushId, String address, int msgclass, long size, String contentLocation) { 
		super(pushId, address); 
		setAddress(address);
		setMessageClass(msgclass);
		setMessageSize(size);
		setContentLocation(contentLocation);
		setContentType("application/vnd.wap.mms-message");
	}

	/**
	 * Set Subject
	 */	
	public String getSubject() { return subject; }

	/**
	 * Get Subject
	 */		
	public void setSubject(String s) { subject = s; }

	/**
	 * Get X-MMS-Message-Class
	 */	
	public int getMessageClass() { return msgclass; }
	
	/**
	 * Set X-MMS-Message-Class
	 */
	public void setMessageClass(int mc) { msgclass = mc; }

	/**
	 * Get X-MMS-Message-Size
	 */	
	public long getMessageSize() { return size; }
	
	/**
	 * Set X-MMS-Message-Size
	 */
	public void setMessageSize(long size) { this.size = size; }
	
	/**
	 * Set Content-Location, should be a valid URL
	 */
	public void setContentLocation(String loc) { contentLocation = loc; }

	/**
	 * Get Content-Location
	 */
	public String getContentLocation() { return contentLocation; }

	/**
	 * Return a string representation of this message
	 */
	public String toString() {
		byte[] doc = createMNotificationInd();
		if (doc == null) return null;
		
		setContentType("application/vnd.wap.mms-message"); 
		setContentBase64(true);
		setContent(doc); 
		
		return super.toString();
	}
	
	/**
	 * Return a byte[] containing the message
	 */
	public byte[] getBytes() {
		byte[] doc = createMNotificationInd();
		if (doc == null) return null;

		setContentType("application/vnd.wap.mms-message"); 
		setContentBase64(true);
		setContent(doc); 
		
		return super.getBytes();
	}
	
	/**
	 * Encode message a specified in WAP-209-MMSEncapsulation-20020105
	 */
	protected byte[] createMNotificationInd() {				
		// check that we have required info
		if (getPushId() == null) return null;
		if (getMessageClass() == -1) return null;
		if (getMessageSize() == 0) return null;
		if (getDeliverBefore() == null) return null;
		if (getContentLocation() == null) return null;
		
		// do the encoding		
		MMMessage mms = new MMMessage(MMConstants.MESSAGE_TYPE_M_NOTIFICATION_IND, getPushId());
		mms.setMessageClass(getMessageClass());
		mms.setMessageSize(getMessageSize());
		mms.setContentLocation(getContentLocation());
		mms.setContentType("application/vnd.wap.mms-message");		
		
		try {
			return mms.encode();
		} catch (Exception e) {
			return null;
		}
	}
	
}
