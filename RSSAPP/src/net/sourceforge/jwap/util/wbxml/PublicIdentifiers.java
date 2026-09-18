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

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>This class represents the normative list of public identifiers issued, and maintained, by the WAP Forum.
 * Public Identifiers values represent well-known document type public identifiers. 
 * The first 128 values are reserved for use in future WAP specifications. 
 * All values are in hexadecimal.</p>
 * 
 * @author <a href="mailto:suvarna@witscale.com">Suvarna Kadam</a>
 */

public class PublicIdentifiers {
	private Hashtable publicIdentifiers;
	private Hashtable systemIdentifiers;
	private static PublicIdentifiers instance;
	private PublicIdentifiers() {
		initialize();
	}
	public static PublicIdentifiers getInstance() {
		if (instance == null)
			instance = new PublicIdentifiers();
		return instance;
	}

	/**
	 * The public identifier table is initialized to the  well-known public identifiers issued by the WAP Forum.
	 * These are valid at the time the WBXML V1.3 specification was released.
	 *
	 */

	private void initialize() {
		publicIdentifiers = new Hashtable();
		publicIdentifiers.put("0", "STRING_TABLE");
		publicIdentifiers.put("1", "UNKNOWN");
		publicIdentifiers.put("2", "-//WAPFORUM//DTD WML 1.0//EN");
		// (WML 1.0)
		publicIdentifiers.put("3", "-//WAPFORUM//DTD WTA 1.0//EN");
		// (WTA Event 1.0)		 deprecated
		publicIdentifiers.put("4", "-//WAPFORUM//DTD WML 1.1//EN");
		//(WML 1.1)";		
		publicIdentifiers.put("5", "-//WAPFORUM//DTD SI 1.0//EN");
		// (Service Indication 1.0)
		publicIdentifiers.put("6", "-//WAPFORUM//DTD SL 1.0//EN");
		// (Service Loading 1.0)		
		publicIdentifiers.put("7", "-//WAPFORUM//DTD CO 1.0//EN");
		// (Cache Operation 1.0)
		publicIdentifiers.put("8", "-//WAPFORUM//DTD CHANNEL 1.1//EN");
		//(Channel 1.1)
		publicIdentifiers.put("9", "-//WAPFORUM//DTD WML 1.2//EN");
		// (WML 1.2)	
		publicIdentifiers.put("A", "-//WAPFORUM//DTD WML 1.3//EN"); //(WML 1.3)
		publicIdentifiers.put("B", "-//WAPFORUM//DTD PROV 1.0//EN");
		// (Provisioning 1.0)
		publicIdentifiers.put("C", "-//WAPFORUM//DTD WTA-WML 1.2//EN");
		// (WTA-WML 1.2)
		publicIdentifiers.put("D", "-//WAPFORUM//DTD CHANNEL 1.2//EN");
		// (Channel 1.2)
		publicIdentifiers.put("E", "-//OMA//DTD DRMREL 1.0//EN");
		// (DRM REL 1.0)		
		publicIdentifiers.put("1100", "-//PHONE.COM//DTD ALERT 1.0//EN");
		// Registered Values			
		publicIdentifiers.put("FD1", "-//SYNCML//DTD SyncML 1.0//EN");
		publicIdentifiers.put("FD2", "-//SYNCML//DTD DevInf 1.0//EN");
		publicIdentifiers.put("FD3", "-//SYNCML//DTD SyncML 1.1//EN");
		publicIdentifiers.put("FD4", "-//SYNCML//DTD DevInf 1.1//EN");
		// no id for metinf
		systemIdentifiers = new Hashtable();
		systemIdentifiers.put("STRING_TABLE", "");
		systemIdentifiers.put("UNKNOWN", "");
		systemIdentifiers.put("-//WAPFORUM//DTD WML 1.0//EN", ""); // (WML 1.0)
		systemIdentifiers.put("-//WAPFORUM//DTD WTA 1.0//EN", "");
		// (WTA Event 1.0)		 deprecated
		systemIdentifiers.put(
			"-//WAPFORUM//DTD WML 1.1//EN",
			"http://www.wapforum.org/DTD/wml_1_1.dtd");
		//(WML 1.1)";		
		systemIdentifiers.put(
			"-//WAPFORUM//DTD SI 1.0//EN",
			"http://www.wapforum.org/DTD/si.dtd");
		// (Service Indication 1.0)
		systemIdentifiers.put(
			"-//WAPFORUM//DTD SL 1.0//EN",
			"http://www.wapforum.org/DTD/sl.dtd");
		// (Service Loading 1.0)		
		systemIdentifiers.put("-//WAPFORUM//DTD CO 1.0//EN", "");
		// (Cache Operation 1.0)
		systemIdentifiers.put("-//WAPFORUM//DTD CHANNEL 1.1//EN", "");
		//(Channel 1.1)
		systemIdentifiers.put(
			"-//WAPFORUM//DTD WML 1.2//EN",
			"http://www.wapforum.org/DTD/wml12.dtd");
		// (WML 1.2)	
		systemIdentifiers.put(
			"-//WAPFORUM//DTD WML 1.3//EN",
			"http://www.wapforum.org/DTD/wml13.dtd");
		//(WML 1.3)
		systemIdentifiers.put(
			"-//WAPFORUM//DTD PROV 1.0//EN",
			"http://www.wapforum.org/DTD/prov.dtd");
		// (Provisioning 1.0)
		systemIdentifiers.put(
			"-//WAPFORUM//DTD WTA-WML 1.2//EN",
			"http://www.wapforum.org/DTD/wta-wml12.dtd");
		// (WTA-WML 1.2)
		systemIdentifiers.put(
			"-//WAPFORUM//DTD CHANNEL 1.2//EN",
			"http://www.wapforum.org/DTD/channel12.dtd");
		// (Channel 1.2)
		systemIdentifiers.put("-//OMA//DTD DRMREL 1.0//EN", "");
		// (DRM REL 1.0)		
		systemIdentifiers.put("-//PHONE.COM//DTD ALERT 1.0//EN", "");
		// Registered Values			
	}

	public String getPublicIdentifier(int publicIdentifier) {
		String key = Integer.toHexString(publicIdentifier).toUpperCase();
		String value = (String) publicIdentifiers.get(key);
		value = (value == null) ? (String) publicIdentifiers.get("1") : value;
		return value;
	}

	public String getSystemIdentifier(String publicIdentifier) {
		return (String) systemIdentifiers.get(publicIdentifier);
	}

	public int getPublicIdentifierValue(String publicId) {
		Iterator iter = publicIdentifiers.entrySet().iterator();
		String hexValue = getKeyFromValue(iter, publicId);
		byte tokenValue = Integer.valueOf(hexValue, 16).byteValue();
		return tokenValue;
	}

	public String getPublicIdentifierValueHex(String publicId) {
		Iterator iter = publicIdentifiers.entrySet().iterator();
		String hexValue = getKeyFromValue(iter, publicId);
		return hexValue;
	}
	private String getKeyFromValue(Iterator iterator, String publicId) {
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
			if (entry.getValue().toString().equalsIgnoreCase(publicId))
				return entry.getKey().toString();

		}
		return "1";
	}
	public int getDefaultPublicIdentifier() {
		return 1;
	}

}
