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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import org.apache.log4j.Logger;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * This class implements multipart MIME (RFC 1521, RFC 2045) message parsing and encoding. 
 * Different parts of the message can thus be easily accessed, and messages can be created.
 * <p>
 * Compability is still limited; the intended use is for Multimedia Messages, and this class 
 * provides a light-weight ground for the MMMessage implementation. 
 * <p>
 * 
 * 
 * @author Anders Lindh
 * @copyright Copyright FlyerOne Ltd 2005
 * @version $Revision: 1.1.1.1 $ $Date: 2005/04/14 09:04:10 $
 */
public class MimeMessage implements Serializable {

	/**
	 * logger 
	 */
	protected transient Logger log = Logger.getLogger(this.getClass());

 	private static byte[] CRLF = {0x0D, 0x0A};

	/**
	 * parts in the message
	 */
	protected ArrayList parts;
	
	/**
	 * Message headers (attributes)
	 */
	protected ArrayList headerNames;
	protected ArrayList headerValues;
	
	/**
	 * Content type of message
	 */
	protected String contentType;

	/**
	 * boundary
	 */
	protected String boundary = null;

	/** 
	 * representation of parts
	 */
	public class MimePart implements Serializable {
		private byte[] content;
		
		public ArrayList headerNames;
		public ArrayList headerValues;
		protected String contentType;
		
		public MimePart(String contenttype, byte[] content, boolean base64, ArrayList names, ArrayList values) {
			this.headerNames = names;
			this.headerValues = values;
			
			if (this.headerNames == null) this.headerNames = new ArrayList();
			if (this.headerValues == null) this.headerValues = new ArrayList();			
			this.content = content;
			
			this.contentType = contenttype;
			
			if (base64) this.setAttribute("Content-Transfer-Encoding", "base64");
		}
		
		public void setAttribute(String name, String value) { 
			int idx = headerNames.indexOf(name);
		
			if (idx != -1) {
 				headerNames.set(idx, name);
				headerValues.set(idx, value);
			} else {
	 			headerNames.add(name);
				headerValues.add(value);			
			} 
		}
		
		public String getAttribute(String name) { 
			int idx = headerNames.indexOf(name);
			if (idx == -1) {
				// try to find (case insensitive)
				ListIterator i = headerNames.listIterator();
				idx = 0;
				while (i.hasNext()) {
					String n = (String) i.next();
					if 	(name.equalsIgnoreCase(n)) return (String) headerValues.get(idx); 
					idx++;
				}				
			} else return (String) headerValues.get(idx); 
			return null;
		}
		
		public ArrayList getAttributeNames() { return this.headerNames; }
		public ArrayList getAttributeValues() { return this.headerValues; }
		
		public String getContentType() { return contentType; }
		public void setContentType(String s) { contentType = s; }
		
		public void removeAttribute(String name) { 
			int idx = headerNames.indexOf(name);
			if (idx == -1) return;
				
			headerNames.remove(idx);
			headerValues.remove(idx);
		}
		
		public byte[] getContent() { return content; }
	}	
	
	/**
	 * Constructor for MimeMessage.
	 */
	public MimeMessage() {
		parts = new ArrayList();	
			
		headerValues = new ArrayList();
		headerNames = new ArrayList();
			
		// generate a boundary for this message (25 chars)
        char[] rnd = new char[25];
        for (int a = 0; a < rnd.length; a++) {
            long l = Math.round(Math.random() * 25); // 25 alphanumeric chars 
            
            long coin = Math.round(Math.random()); // some variation            
            
            char base = 'A';
            if (coin == 1) base = 'a';
            
            rnd[a] = (char) (base + l);
        }
        
        boundary = new String(rnd); 
    }
    
    /**
     * create message from string
     */
    public MimeMessage(String message) {
    	parts = new ArrayList();
    	
    	StringBuffer tmp = new StringBuffer(message);
    	
    	// clean up headers 
		int i = 0;

		while ((i = message.indexOf("\r\r\n")) != -1) {
			tmp.replace(i, i+3, "\r\n");
			message = tmp.toString();
		}
		
		while ((i = message.indexOf("\r\n\t")) != -1) {
			tmp.replace(i, i+3, " ");
			message = tmp.toString();
		}
    	
    	StringReader sr = new StringReader(message);
    	BufferedReader br = new BufferedReader(sr);
    	
		String line = null;
		
		ArrayList headerNames = new ArrayList();
		ArrayList headerValues = new ArrayList();
		
		try {
			while ((line = br.readLine()) != null) {
	
				if (line.equals("")) break;
				if (line.indexOf(':') != -1) {
					String name = line.substring(0, line.indexOf(':')).trim();
					String value = line.substring(line.indexOf(':')+1, line.length()).trim();
					
					// extract boundary if this is our content type
					if (name.equalsIgnoreCase("Content-Type")) {
						int s = value.indexOf("boundary");						
						if (s != -1) {
							s += 9;
							boundary = value.substring(s, value.length());
							if (boundary.indexOf(';') != -1)
								boundary = boundary.substring(0, boundary.indexOf(';'));
							if (boundary.startsWith("\""))
								boundary = boundary.substring(1, boundary.length()-1);
							//while (boundary.charAt(0) == '-')
							//	boundary = boundary.substring(1, boundary.length());							
						} else boundary = null;
						
						setContentType(value.substring(0, value.indexOf(" ")));
						continue;
					}
			
					headerNames.add(name);
					headerValues.add(value);
				}
			}    	
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		
		this.headerValues = headerValues;
		this.headerNames = headerNames;
		
		if (boundary == null) {
			log.debug("No parts in mime message");
			return; 
		} // no parts
		
		// start extracting parts
		try {
			while ((line = br.readLine()) != null) {
				if (line.startsWith("--" + boundary)) continue;
				if (line.equals("")) continue;
					
				ArrayList hNames = new ArrayList();
				ArrayList hValues = new ArrayList();					
				boolean base64 = false;
					
				//if (line.endsWith("--")) break; // end-of-content

				String ct = null;
					
				// extract headers
				while (true) {
					if ((line == null) || (line.equals(""))) break;
					
					if (line.indexOf(':') != -1) {
						String name = line.substring(0, line.indexOf(':')).trim();
						String value = line.substring(line.indexOf(':')+1, line.length()).trim();
							
						if (name.equalsIgnoreCase("Content-Type")) ct = value; else
						if (name.equalsIgnoreCase("Content-Transfer-Encoding")) {
							if (value.equalsIgnoreCase("base64")) base64 = true;
						} else {
							hNames.add(name);
							hValues.add(value); 
						}
					}
					
					line = br.readLine();
						
				}

				if (ct == null) {
					// System.out.println("Skipping part without content-type");
					break;
				}			
							
				// extract content
				String content = "";
				while ((line = br.readLine()) != null) {
					if (line.equals("")) break;
					if (line.startsWith("--" + boundary)) break;
					content += line;
				}	
					
				byte data[] = content.getBytes();
					
				if (base64 == true) {
					BASE64Decoder b64 = new BASE64Decoder();
					data = b64.decodeBuffer(content);
				}
					
				addPart(ct, data, base64, hNames, hValues);
			}
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
			
		    	
    }

	public String getContentType() { return contentType; }
	public void setContentType(String s) { contentType = s; }

	/**
	 * add a part to the message
	 */
	public void addPart(String contenttype, byte[] content, boolean base64, ArrayList names, ArrayList values) {
		MimePart part = new MimePart(contenttype, content, base64, names, values);
		parts.add(part);
	}	
	
	public void addPart(MimePart part) {
		parts.add(part);
	}
	
	/**
	 * get part
	 */
	public MimePart getPart(int idx) {
		return (MimePart) parts.get(idx);
	}
	
	/**
	 * Remove a part
	 */
	public void removePart(int idx) {
		parts.remove(idx);
	}

	/**
	 * get part
	 */
	public int getPartCount() {
		return parts.size();
	}
	
	/** 
	 * add a header
	 */
	public void setAttribute(String name, String value) {
		int idx = headerNames.indexOf(name);
		
		if (idx != -1) {
 			headerNames.set(idx, name);
			headerValues.set(idx, value);
		} else {
 			headerNames.add(name);
			headerValues.add(value);			
		}
	}
	
	/**
	 * get a header
	 */
	public String getAttribute(String name) {
		int idx = headerNames.indexOf(name);
		if (idx != -1)
			return (String) headerValues.get(idx); else
			return null;
	}
	
	/**
	 * get iteration over all headers
	 */
	public ListIterator getAttributes() {
		return headerNames.listIterator();
	}
	
	/**
	 * remove named header
	 */
	public void removeAttribute(String name) {
		int idx = headerNames.indexOf(name);
		if (idx == -1) return;
				
		headerNames.remove(idx);
		headerValues.remove(idx);
	}
	
	/**
	 * serialize message to a string
	 * 
	 * @return String containing MIME compatible representation of message, if
	 * message contains no parts, null is returned
	 */
	public String toString() {
		byte[] b = getBytes();
		if (b == null) return null;
		return new String(b);
	}
	
	/**
	 * Return boundary used in creating a String representation
	 */
	public String getBoundary() {
		return boundary;
	}
	
	public byte[] getBytes() {
		return getBytes(true);
	}
	
	/**
	 * return a byte array representation of this object
	 */
	public byte[] getBytes(boolean includeHeaders) {		
		ByteArrayOutputStream res = new ByteArrayOutputStream();
		
		ArrayList mimeheaderValues = new ArrayList();
		ArrayList mimeheaderNames = new ArrayList();		
		
		if (getAttribute("MIME-Version") == null) {
			mimeheaderNames.add("MIME-Version");
			mimeheaderValues.add("1.0");
		}
		
		String ct = null; // getContentType();
			
		if ((ct != null) && (ct.indexOf(" ") != -1)) { // insert boundary
			// ct = "multipart/mixed; " + ct.substring(ct.indexOf(" ")+1, ct.length());			
			ct.trim();
			// squeese the rest of the parameters, and replace ":" with "="
			while (ct.indexOf(":") != -1)
				ct = ct.substring(0, ct.indexOf(":")) + "=" + ct.substring(ct.indexOf(":")+1, ct.length()).trim();
		}	

		if (ct != null) ct = ct + "; boundary=\"" + boundary + "\"; "; else
			ct = "multipart/mixed; boundary=\"" + boundary + "\"";
						
		try {	
			if (includeHeaders) {					
				serializeAttributes(res, mimeheaderNames, mimeheaderValues);					
				serializeAttributes(res, headerNames, headerValues);				
				res.write(("Content-Type: " + ct).getBytes());
				res.write(CRLF);
				res.write(CRLF);
			}			
			
			if (!parts.isEmpty()) {
				Iterator i = parts.iterator();
				while (i.hasNext()) {
					MimePart part = (MimePart) i.next();
					byte[] c = part.content;

					String cte = part.getAttribute("Content-Transfer-Encoding");
					boolean base64 = false;
					
					if ((cte != null) && (cte.equalsIgnoreCase("base64"))) base64 = true;
					
					//if (base64 == false) { // try to guess
						//if (part.getContentType().startsWith("image/")) base64 = true; else
						//if (part.getContentType().startsWith("application/octet")) base64 = true; else
						//if (part.getContentType().startsWith("audio/")) base64 = true;
					//}
					
					base64 = true; // just to be safe
					
					if (base64) {		
						if (cte == null)
							part.setAttribute("Content-Transfer-Encoding", "base64");
							
			            BASE64Encoder b64 = new BASE64Encoder();
		                c = b64.encodeBuffer(part.content).getBytes();
					}
			
					res.write(("--" + boundary).getBytes());
					res.write(CRLF);
					
					res.write(("Content-Type: " + part.getContentType()).getBytes("iso-8859-1"));
					res.write(CRLF);
					serializeAttributes(res, part.getAttributeNames(), part.getAttributeValues());
					res.write(CRLF);

					if (c != null) res.write(c);
					res.write(CRLF);
				}
		
				res.write(("--" + boundary + "--").getBytes()); 
				res.write(CRLF);
			}
		} catch (Exception e) {
			log.error("MimeMessage.getBytes(): " + e);
			e.printStackTrace();
			return null;
		}	
					
		return res.toByteArray();		
	}
	
	/**
	 * create a e-mail compatible mime message out of this message
	 */
	public javax.mail.internet.MimeMessage toMimeMessage(javax.mail.Session session) {
		//javax.mail.internet.MimeMessage email = new javax.mail.internet.MimeMessage(session);
             	
        try {
			byte[] b = getBytes();
			if (b == null) return null;
			
			ByteArrayInputStream bais = new ByteArrayInputStream(b);
		
			javax.mail.internet.MimeMessage email = new javax.mail.internet.MimeMessage(session, bais);
			return email;
        } catch (Exception e) {
        	log.error("toMimeMessage: " + e);
        }
        
        return null; 
	}	

	/**
	 * serialize attributes to given OutputStream
	 */	
	protected void serializeAttributes(ByteArrayOutputStream res, ArrayList names, ArrayList values) throws Exception{
		if ((names == null) || (names.isEmpty()) || (res == null)) return;
		if ((values == null) || (values.isEmpty())) return;

		Iterator i = names.iterator();

		if (i == null) return;
		int idx = 0;
		while (i.hasNext()) {
			
			String name = (String) i.next();
			String val = (String) values.get(idx++);
			
			if ((name == null) || (val == null)) continue;

			res.write((name).getBytes());			
			res.write((": ").getBytes());	
			res.write((val).getBytes());					
			res.write(CRLF);
		}
	}
	
}
