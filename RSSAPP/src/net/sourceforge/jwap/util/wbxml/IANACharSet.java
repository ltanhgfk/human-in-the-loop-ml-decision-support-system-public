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

package net.sourceforge.jwap.util.wbxml;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;



/**
 *
 * This class represents the collection of character sets defined by IANA (www.iana.org)
 * The official names for the character sets are stored against the MIBenum value which is a 
 * unique value for use in MIBs to identify coded character sets.
 *  
 * @author <a href="mailto:suvarna@witscale.com">Suvarna Kadam</a>
 */

public class IANACharSet {
	public static Hashtable charSet = new Hashtable();
	static {
		try {
					Document tokenDoc =
						DocumentBuilderFactory
							.newInstance()
							.newDocumentBuilder()
							.parse(
							"./IANACharacterSet.xml");
						NodeList charactersets = tokenDoc.getElementsByTagName("character-set");
						for (int i = 0; i < charactersets.getLength(); i++) {
							Element character_set = (Element) charactersets.item(i); 
							charSet.put(character_set.getAttribute("name"), new Integer(character_set.getAttribute("MIBenum")));				
						}
		
				} catch (Exception exp) {
					exp.printStackTrace();
				}
	
	}

	public static int getMIBEnum(String encodingName) {
		return ((Integer) charSet.get(encodingName)).intValue();
	}
	public static String getEncoding(int mibNumber) {
		Enumeration enumeration = charSet.keys();
		while (enumeration.hasMoreElements()) {
			String encoding = (String) enumeration.nextElement();
			if (((Integer) charSet.get(encoding)).intValue() == mibNumber)
				return encoding;
		}
		return "UTF-8";
	}
}

