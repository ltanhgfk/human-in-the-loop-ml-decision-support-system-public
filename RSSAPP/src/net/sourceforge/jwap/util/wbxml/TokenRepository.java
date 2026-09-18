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

/**
 * 
 * @author <a href="mailto:suvarna@witscale.com">Suvarna Kadam</a>
 */

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TokenRepository {
	private static Properties urnMappings;
	private static TokenRepository instance;
	private static byte CODEPAGE_DEFAULT = (byte) 0;
	private static byte currentCodepage = CODEPAGE_DEFAULT;
	//	private byte wbxmlEncoding;
	//	private byte charEncoding;
	//	private String publicID;
	private Document tokenDoc;
	//	private Hashtable tags;
	//	private Hashtable attributeNames;
	//private Hashtable attributeNameTokens; // for decoder
	//	private Hashtable attributeValues;
	//	private Hashtable attributePrefixes;
	private Hashtable[][] codepages;
	private TokenRepository() {

	}

	public static TokenRepository getInstance(String publicIDInHex) {
		if (instance == null) {
			instance = new TokenRepository();
			instance.initializeFor(publicIDInHex);
		}
		return instance;

	}

	public void initializeFor(String publicIDInHex) {
		try {
			Properties tokenRepositoryMappings = new Properties();
			tokenRepositoryMappings.load(
				new FileInputStream("./tokenRepositoryMappings.properties"));
			String fileName =
				tokenRepositoryMappings.getProperty(publicIDInHex);
			tokenDoc =
				DocumentBuilderFactory
					.newInstance()
					.newDocumentBuilder()
					.parse(
					fileName);
			initializeURNMappings();
			initializeHeaderInfo();
			currentCodepage = CODEPAGE_DEFAULT;
		} catch (Exception exp) {
			exp.printStackTrace();
		}
	}

	private void initializeURNMappings() {
		urnMappings = new Properties();
		try {
			urnMappings.load(
				new FileInputStream("./urnCodepageMappings.properties"));
		} catch (FileNotFoundException fnfExp) {
			fnfExp.printStackTrace();
		} catch (IOException ioExp) {
			ioExp.printStackTrace();
		}
	}

	private void initializeHeaderInfo() {
		int total_codePages =
			tokenDoc.getElementsByTagName("codepage").getLength();
		codepages = new Hashtable[total_codePages][5];
		for (int i = 0; i < codepages.length; i++) {
			Element codepage = getCodepage(i);
			initializeTags(codepage, i);
			initializeAttributeNames(codepage, i);
			initializeAttributeNameTokens(codepage, i);
			initializeAttributeValues(codepage, i);
		}
		//		publicID = codePage.getAttribute("publicID");
		//		String wbxmlEncodingStr = codePage.getAttribute("wbxmlVersion");
		//		int dotPosition = wbxmlEncodingStr.indexOf('.');
		//		byte majorVersion =
		//			Byte.parseByte(wbxmlEncodingStr.substring(0, dotPosition));
		//		majorVersion = (byte) (majorVersion - 1);
		//		byte minorVersion =
		//			Byte.parseByte(
		//				wbxmlEncodingStr.substring(wbxmlEncodingStr.indexOf('.') + 1));
		//		wbxmlEncoding = (byte) ((majorVersion << 4) + minorVersion);
		//		String charEncodingStr = codePage.getAttribute("default_encoding");
		//		charEncoding =
		//			(byte) IANACharSet.getMIBEnum(charEncodingStr.toUpperCase());
	}

	private void initializeTags(Element codepage, int codePageNo) {
		Hashtable tags = new Hashtable();
		NodeList tagNodes = codepage.getElementsByTagName("tag");
		Element aTag;
		for (int i = 0; i < tagNodes.getLength(); i++) {
			aTag = (Element) tagNodes.item(i);
			tags.put(
				aTag.getAttribute("name").toLowerCase(),
				aTag.getAttribute("token-value"));
		}
		codepages[codePageNo][0] = tags;

	}

	private Element getCodepage(int codePageNo) {
		NodeList allCodepages = tokenDoc.getElementsByTagName("codepage");
		for (int i = 0; i < allCodepages.getLength(); i++) {
			Element codepage = (Element) allCodepages.item(i);
			if (Integer.parseInt(codepage.getAttribute("number").trim())
				== codePageNo)
				return codepage;
		}
		return null;
	}

	private void initializeAttributeNames(Element codepage, int codePageNo) {
		Hashtable attributeNames = new Hashtable();
		Hashtable attributePrefixes = new Hashtable();
		NodeList attributeNameNodes =
			codepage.getElementsByTagName("attribute-start");
		Element anAttributeNameNode;
		for (int i = 0; i < attributeNameNodes.getLength(); i++) {
			anAttributeNameNode = (Element) attributeNameNodes.item(i);
			String name = anAttributeNameNode.getAttribute("name");
			String prefix = anAttributeNameNode.getAttribute("prefix");
			String key = name + prefix;
			attributeNames.put(
				key.toLowerCase(),
				anAttributeNameNode.getAttribute("token-value"));
			if (attributePrefixes.containsKey(name))
				 ((Vector) attributePrefixes.get(name)).addElement(prefix);
			else {
				Vector prefixes = new Vector();
				prefixes.addElement(prefix);
				attributePrefixes.put(name, prefixes);
			}
		}
		codepages[codePageNo][1] = attributeNames;
		codepages[codePageNo][2] = attributePrefixes;
	}

	private void initializeAttributeNameTokens(
		Element codepage,
		int codePageNo) {
		Hashtable attributeNameTokens = new Hashtable();
		NodeList attributeNameNodes =
			tokenDoc.getElementsByTagName("attribute-start");
		Element anAttributeNameNode;
		for (int i = 0; i < attributeNameNodes.getLength(); i++) {
			anAttributeNameNode = (Element) attributeNameNodes.item(i);
			String token =
				String.valueOf(
					getByteValue(
						anAttributeNameNode.getAttribute("token-value")));
			String[] nameAndPrefixes = new String[2];
			String name = anAttributeNameNode.getAttribute("name");
			String prefix = anAttributeNameNode.getAttribute("prefix");
			nameAndPrefixes[0] = name;
			nameAndPrefixes[1] = prefix;
			attributeNameTokens.put(token.toLowerCase(), nameAndPrefixes);
		}
		codepages[codePageNo][4] = attributeNameTokens;
	}

	private void initializeAttributeValues(Element codepage, int codePageNo) {
		Hashtable attributeValues = new Hashtable();
		NodeList attributeValueNodes =
			codepage.getElementsByTagName("attribute-value");
		Element anAttributeValueNode;
		for (int i = 0; i < attributeValueNodes.getLength(); i++) {
			anAttributeValueNode = (Element) attributeValueNodes.item(i);
			attributeValues.put(
				anAttributeValueNode.getAttribute("name").toLowerCase(),
				anAttributeValueNode.getAttribute("token-value"));
		}
		codepages[codePageNo][3] = attributeValues;
	}
	public byte getTagToken(String tagName) {
		Hashtable tags = codepages[currentCodepage][0];
		return getByteValue(tags.get(tagName.toLowerCase()).toString());
	}
	private byte getByteValue(String value) {
		try {
			//byte tokenValue = Byte.parseByte(value, 16);
			byte tokenValue = Integer.valueOf(value, 16).byteValue();
			return tokenValue;
		} catch (Exception exp) {
			System.out.println("token not found!!!, returning literal");
			return GlobalTokens.LITERAL;
		}
	}

	public String getPublicIdentifier() {
		Element codePage =
			(Element) tokenDoc.getElementsByTagName("codepage").item(0);
		return codePage.getAttribute("publicID");

	}

	public String getTagName(byte tokenValue) {
		Hashtable tags = codepages[currentCodepage][0];
		Iterator iterator = tags.entrySet().iterator();
		return getKeyFromValue(iterator, tokenValue);
	}

	public String[] getAttributeNameAndPrefix(byte tokenValue) {
		Hashtable attributeNameTokens = codepages[currentCodepage][4];
		String token = String.valueOf(tokenValue);
		return (String[]) attributeNameTokens.get(token);
	}

	public byte getAttributeNameToken(String attributeName, String prefix) {
		Hashtable attributeNames = codepages[currentCodepage][1];
		String token =
			(String) attributeNames.get((attributeName + prefix).toLowerCase());
		if (token == null)
			return GlobalTokens.LITERAL;
		return getByteValue(token);
	}

	public Vector getAttributePrefixes(String attributeName) {
		Hashtable attributePrefixes = codepages[currentCodepage][2];
		return (Vector) attributePrefixes.get(attributeName);
	}

	public String getAttributeValue(byte tokenValue) {
		Hashtable attributeValues = codepages[currentCodepage][3];
		Iterator iterator = attributeValues.entrySet().iterator();
		return getKeyFromValue(iterator, tokenValue);
	}

	public byte getAttributeValueToken(String attributeValue) {
		Hashtable attributeValues = codepages[currentCodepage][3];
		String hexValue =
			attributeValues.get(attributeValue.toLowerCase()).toString();
		return getByteValue(hexValue);
	}

	private String getKeyFromValue(Iterator iterator, byte tokenValue) {
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
			if (getByteValue(entry.getValue().toString()) == tokenValue)
				return entry.getKey().toString();

		}
		return null;
	}

	/**
	* Returns the attributeValues.
	* @return Hashtable
	*/
	public Hashtable getAttributeValues() {
		Hashtable attributeValues = codepages[currentCodepage][3];
		return attributeValues;
	}

	public static void switchCodepage(String namespaceURN) {
		if (namespaceURN == null)
			currentCodepage = CODEPAGE_DEFAULT;
		else {
			byte codepage =
				Integer
					.valueOf(urnMappings.get(namespaceURN).toString())
					.byteValue();
			currentCodepage = codepage;
		}
	}

	public static void setCurrentCodepage(byte codePageNo) {
		currentCodepage = codePageNo;
	}

	public static byte getCurrentCodepage() {
		return currentCodepage;
	}

	/**
	 * @return String
	 */
	public String getCurrentNamespace() {
		// TODO Auto-generated method stub
		return null;
	}

}
