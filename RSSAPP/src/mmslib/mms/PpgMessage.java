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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

/**
 *
 * This class represents content that is submitted to the push proxy gateway.
 * <p>
 * Messages sent to the Push Proxy consist of two parts; the first one has the
 * PAP document (push access protocol), the other the content. Content can be an SI or
 * SL document (XML doc), or a encoded m-notification-ind message. 
 * <p>
 * This class serves as a base for different types of noficiations. We implement
 * SI (service indications) and M-Notification-Ind messages on top of this class.
 * <p>
 * References: 
 * <p>
 * <a href="http://www.wapforum.org/DTD/pap_2.0.dtd">
 * WAP-247-PAP-20010429-a (PAP)
 * </a><br>
 * 
 * @author Anders Lindh
 * @copyright Copyright FlyerOne Ltd 2005
 * @version $Revision: 1.1.1.1 $ $Date: 2005/04/14 09:04:10 $
 */
public class PpgMessage extends MimeMessage {

	protected final static String pushIdPrefix = "@tambur.org";

	protected String applicationId = null; // destination app id

	protected String pushId = null;
	protected Date deliverBefore = null; // YYYY-MM-DDThh:mm:ssZ
	protected Date deliverAfter = null;
	protected String sourceReference = "Tambur";
	
	// address element
	protected String address = null;
	
	// quality of service
	protected String priority = "medium"; // high|medium|low
	
	// hard-coded (and maybe not used)
	protected String deliveryMethod = "unconfirmed"; // confirmed|precomfirmed|uncomfirmed|notspecified
	protected String network = null;
	protected String networdRequired = "false"; // true|false
	protected String bearer = null; 
	protected String bearerRequired = "false"; // true|false

	protected String progressNotes = "false";
	
	protected byte[] content = null;
	protected String contenttype;
	protected boolean base64 = false;

	/**
	 * Constructors for PpgMessage.
	 */
	public PpgMessage() { super(); }

	public PpgMessage(String pushId, String address) { 
		super(); 

		setPushId(pushId);
		setAddress(address);
	}
	
	public PpgMessage(byte[] content, String contenttype) {
		super();
		setContent(content);
		setContentType(contenttype);	
	}	

	public String getApplicationId() { return applicationId; }
	public void setApplicationId(String appId) { this.applicationId = appId; }
	 
	public String getPushId() { return pushId; }
	public void setPushId(String pushId) { this.pushId = pushId + pushIdPrefix; }
	
	public Date getDeliverBefore() { return deliverBefore; }
	public void setDeliverBefore(Date date) { this.deliverBefore = date; }

	public Date getDeliverAfter() { return deliverAfter; }
	public void setDeliverAfter(Date date) { this.deliverAfter = date; }
	
	public String getSourceReference() { return sourceReference; }
	public void setSourceReference(String ref) { this.sourceReference = ref; }
	
	public String getAddress() { return address; }
	public void setAddress(String addr) { address = "WAPPUSH=" + addr; }

	// quality of service
	
	public String getPriority() { return priority; }
	public void setPriority(String pri) { this.priority = pri; }

			
	public void setContent(byte[] content) { this.content = content; }
	public byte[] getContent() { return content; }
	
	public void setContentType(String contenttype) { this.contenttype = contenttype; }
	public String getContentType() { return contenttype; }

	public void setContentBase64(boolean base64) { this.base64 = base64; }
	public boolean getContentBase64() { return base64; }
	
	/**
	 * return a string representation (i.e. a multipart MIME encoded document)
	 */ 
	public String toString() {
		return new String(getBytes());
	}
	
	/**
	 * return a byte array representation (i.e. a multipart MIME encoded document)
	 */ 
	public byte[] getBytes() {
		// create pap document
		Document doc = createPap();
		if (doc == null) return null;
		
		String pap = toXMLString(doc);
	
		addPart("application/xml", pap.getBytes(), false, null, null);
		if (content != null) addPart(contenttype, content, base64, null, null);
		
		return super.getBytes();
	}
	
	/**
	 * creates a PAP document 
	 */
	protected Document createPap() {
		// check that we have minimum required info (addr, push-id)
		if ((address == null) || (pushId == null))
			return null;
		
		Document doc = new Document(new Element("pap"));
		
		Element pm = new Element("push-message");
		pm.setAttribute("push-id", getPushId());
		
		if (getDeliverBefore() != null)
			pm.setAttribute("deliver-before-timestamp", formatDate(getDeliverBefore()));
		if (getDeliverAfter() != null)
			pm.setAttribute("deliver-after-timestamp", formatDate(getDeliverAfter()));
		if (getSourceReference() != null)	
			pm.setAttribute("source-reference", getSourceReference());		
				 
		Element address = new Element("address");		
		address.setAttribute("address-value", getAddress());
		pm.addContent(address);
		
		Element qos = new Element("quality-of-service");
		qos.setAttribute("priority", getPriority());

	/*	qos.setAttribute("delivery-method", deliveryMethod);
		qos.setAttribute("network-required", "true");
		qos.setAttribute("network", "gsm");
		qos.setAttribute("bearer-required", "true");
		qos.setAttribute("bearer", "sms"); */
		
		pm.addContent(qos);					
		
		doc.getRootElement().addContent(pm);
		
		return doc;		
	}
	
	/**
	 * form a Date to correct format
	 * 
	 * @todo: time should be UTC
	 */
	protected String formatDate(Date date) {
		// transform to UTC (GMT)
		SimpleTimeZone stz = new SimpleTimeZone(0, "GMT");
		Calendar cal = Calendar.getInstance(stz);
		
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ss'Z'");
		formatter.setCalendar(cal);
 		return formatter.format(date); 		
	}

	/**
	 * convert jdom doc to string
	 */	
    protected String toXMLString(org.jdom.Document metaXML) {
        String xmlString = null;
        try {
            XMLOutputter outputter = new XMLOutputter("");
            outputter.setEncoding("ISO-8859-1");
            xmlString = outputter.outputString(metaXML);
        } catch (Exception e) {
            return null;
        }
        return xmlString;
    }


}
