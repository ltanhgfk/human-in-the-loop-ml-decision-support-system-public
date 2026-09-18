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
import java.net.URL;
import java.util.Date;

import org.jdom.Document;
import org.jdom.Element;

/**
 * This class representes a wap-push Service Indication.
 * <p>
 * push-id is used as si-id 
 * <p>
 * References: 
 * <p>
 * <a href="http://www1.wapforum.org/tech/documents/WAP-167-ServiceInd-20010731-a.pdf">
 * WAP-167-ServiceInd-20010731-a 
 * </a>
 * <p> 
 * 
 * @author Anders Lindh
 * @copyright Copyright FlyerOne Ltd 2005
 * @version $Revision: 1.1.1.1 $ $Date: 2005/04/14 09:04:10 $
 */
public class ServiceIndicationMessage extends PpgMessage {

	protected String href = null;
	protected String message = null;

	/**
	 * Constructor for ServiceIndicationMessage.
	 */
	public ServiceIndicationMessage() {
		super();
	}
	
	public ServiceIndicationMessage(String pushId, String address) { 
		super(pushId, address); 
	}
	
	/**
	 * Set Href (i.e. target)
	 */
	public String getHref() { return href; }
	
	/**
	 * Get Href
	 */
	public void setHref(String href) { this.href = href; }

	/**
	 * Get message
	 */
	public String getMessage() { return message; }
	
	/**
	 * Set message
	 */
	public void setMessage(String message) { this.message = message; }

	/**
	 * convert to string (MIME representation)
	 */
	public String toString() {
		Document doc = createSI();
		if (doc == null) return null;
		
		String content = toXMLString(doc);
		setContent(content.getBytes());
		setContentType("text/vnd.wap.si");
		
		return super.toString();
	}
	
	public byte[] getBytes() {
		Document doc = createSI();
		if (doc == null) return null;
		
		String content = toXMLString(doc);
		setContent(content.getBytes());
		setContentType("text/vnd.wap.si");
		
		return super.getBytes();
	}
	
	/**
	 * create an encoded version of this message
	 * (according to WAP-167-ServiceInd-20010731-a. 8.1)
	 * @return
	 */
	public byte[] encode() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		baos.write(0x02); // version number - WBXML version 1.2
		baos.write(0x05); // SI 1.0 Public Identifier
		baos.write(0x6A); // Charset=UTF-8 (MIBEnum 106)
		baos.write(0x00); // String table length
		baos.write(0x45); // si, with content
		baos.write(0xC6); // indication, with content and attributes
		
		// parse url
		
		URL url = new URL(href);
		String host = url.getHost();
		String h = href;
		
		if (h.startsWith("http://www.")) {
			 baos.write(0x0D);
			 h = h.substring(11);
			 host = host.substring(4); 
		} else if (h.startsWith("https://www.")) {
			baos.write(0x0F); 
			h = h.substring(12);
			host = host.substring(5);
		} else if (h.startsWith("http://")) {
			baos.write(0x0C);
			h = h.substring(7); 
		} else if (h.startsWith("https://")) {
			baos.write(0x0E); 
			h = h.substring(8);
		} else {
			baos.write(0x0B);
		}	
		
		baos.write(0x03); // inline string follows
		
		boolean hasKnownEnding = false;
		if (host.endsWith(".com") || host.endsWith(".net") || host.endsWith(".org") || host.endsWith(".edu"))
			hasKnownEnding = true;
			
		if (hasKnownEnding) {
			String tmp = host.substring(0, host.lastIndexOf("."));
			baos.write(tmp.getBytes());
			baos.write(0x00); // end of inline string
			
			if (host.endsWith(".com")) baos.write(0x85); else
			if (host.endsWith(".edu")) baos.write(0x86); else
			if (host.endsWith(".net")) baos.write(0x87); else
			if (host.endsWith(".org")) baos.write(0x88);
			
			baos.write(0x03); // inline string follows
			String path = url.getPath();
			if (url.getQuery() != null) {
				path += "?" + url.getQuery();
			}
			if (path.length() > 0) path = path.substring(1);
			baos.write(path.getBytes());			
			baos.write(0x00);  // end of inline string
		} else {					
			baos.write(h.getBytes());
			baos.write(0x00); // end of inline string			
		}
		
	/*	baos.write(0x0A); // created
		baos.write(0xC3); // opaque data
		baos.write(0x07); // length
		// TODO: get date
		 
		baos.write(0x10); // si-expires
		baos.write(0xC3); // opaque data
		baos.write(0x07); // length
		// TODO: get date */
				
		baos.write(0x01); // END (of indication attribute list)
		
		baos.write(0x03); // inline string follows
		baos.write(message.getBytes());
		baos.write(0x00);  // end of inline string
		
		baos.write(0x01); // END (of indication element)
		baos.write(0x01); // END (of si element)

		byte[] body = baos.toByteArray();

		// do headers
		ByteArrayOutputStream headers = new ByteArrayOutputStream();
		
		headers.write(0xAE); //	Content Type=application/vnd.wap.sic (0x80 | 0x2E)
		//headers.write(0x96); //	Host, WAP - 230, Appendix A, table 39 (wsp)
		//headers.write(host.getBytes()); // 
		headers.write(0x00); // end of string
		//headers.write(0x8D); //	Content-Length (wsp, table 39)
		//headers.write(body.length);
		headers.write(0xB4); //	Push-Flag (wsp, table 39)
		headers.write(0x80); // no flags set (wsp, chapter 8.4.2.59 & chapter

		byte[] head = headers.toByteArray();
		
		// finalize
		ByteArrayOutputStream res = new ByteArrayOutputStream();

		res.write(0x01); // Transaction id (push id)
		res.write(0x06); // PDU Type (Push PDU)
		res.write(head.length); // header length
		res.write(head);
		res.write(body);
			
		return res.toByteArray();
	}
		
	
	/**
	 * create a SI XML doc
	 */
	protected Document createSI() {
		// check that we have required info
		if (href == null) return null;
		if (message == null) return null;		
		
		// create doc		
		Document doc = new Document(new Element("si"));

		Element indication = new Element("indication");
		indication.setAttribute("href", getHref());
		indication.setAttribute("si-id", getPushId());
		indication.setAttribute("created", formatDate(new Date())); // now
		if (getDeliverBefore() != null)
			indication.setAttribute("expires", formatDate(getDeliverBefore())); 		
		indication.setText(message);
				
		doc.getRootElement().addContent(indication);				
		
		return doc;
	}
		
}
