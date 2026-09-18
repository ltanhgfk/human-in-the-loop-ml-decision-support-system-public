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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.sourceforge.jwap.util.TransTable;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * to do's :- string table length is in multiple byte format, currently it is assumed to be single byte
 * index in string table should also be read as multiple byte format
 * character encoding supported is only utf-8, 
 * for other encodings the termination character in string table can be different...
 * 
 *  
 * @author <a href="mailto:suvarna@witscale.com">Suvarna Kadam</a>
 */

public class WBXMLDecoder {
	private static WBXMLDecoder instance;
	private DataInputStream wbxmlStream;
	private Document xmlDocument;
	private String publicId = "UNKNOWN";
	private byte publicIdIndex = -1;
	private String encoding;
	private StringBuffer stringTable;

	private byte parentBitMask = (byte) 0x40;
	// The bit representation 10000000
	private byte attributeBitMask = (byte) 0x80;
	// The bit representation 01000000
	private TokenRepository tokenRepository;
	public static WBXMLDecoder getInstance() {
		if (instance == null)
			instance = new WBXMLDecoder();
		return instance;
	}

	private WBXMLDecoder() {
		initialize();
	}

	private void initialize() {

	}

	public Document decode(InputStream wbxmlStream) {
		this.wbxmlStream = new DataInputStream(wbxmlStream);
		try {
			xmlDocument =
				DocumentBuilderFactory
					.newInstance()
					.newDocumentBuilder()
					.newDocument();
			decodeProlog();
			decodeBody();
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return xmlDocument;

	}

	/**
	 *  currently encoding cannot be specifies as DOM does not have any API to access the XML declaration. 
	 * by default utf-16 is used.. If encoding is to be used ...
	 * xmlDocument.getImplementation().String piData = "version=\"1.0\" encoding=\"" + encoding + "\"";
	 * ProcessingInstruction xmlDeclaration = xmlDocument.createProcessingInstruction("xml", piData);
	 * xmlDocument.insertBefore(xmlDeclaration, xmlDocument.getDocumentElement());
	 **/

	private void decodeProlog() throws IOException {
		byte version = wbxmlStream.readByte();
		readPublicID();
		byte charset = wbxmlStream.readByte(); // to do for mutiple byte
		encoding =  TransTable.getTable("charsets").code2str(charset);
		byte strtblSize = wbxmlStream.readByte();
		// TODO for multi-byte and negative size
		stringTable = new StringBuffer(strtblSize);
		for (int i = 0; i < strtblSize; i++)
			stringTable.append((char) wbxmlStream.readByte());
		if (publicIdIndex != -1)
			readPublicIDFromStringTable();
		// Now initialze the token repository as per the public identifier specified
		String publicIDInHex =
			PublicIdentifiers.getInstance().getPublicIdentifierValueHex(
				publicId);
		tokenRepository = TokenRepository.getInstance(publicIDInHex);
	}

	private void readPublicIDFromStringTable() {
		char c = 0x0;
		int endIndex =
			stringTable.toString().indexOf(new String(new char[] { c }), publicIdIndex);
		if (endIndex == -1)
			publicId = stringTable.substring(publicIdIndex);
		else
			publicId = stringTable.substring(publicIdIndex, endIndex);

	}

	private void readPublicID() throws IOException {
		byte[] multipleBytes = new byte[4];
		byte nextByte = wbxmlStream.readByte();
		if (nextByte == 0) {
			// public id is encoded as string in form:-  0 indexInstringtable
			publicIdIndex = wbxmlStream.readByte();

		} else { // public id is encoded in multi-byte integer format(mb_u_int32)
			int i = 0;
			StringBuffer strBuf = new StringBuffer();
			while ((nextByte & 0x80) == 0x80) { //msb IS 1
				String str = Integer.toBinaryString(nextByte & 0x7F);
				if (str.length() < 7) {
					int zerosToPumpIn = 7 - str.length();
					for (int j = 0; j < zerosToPumpIn; j++)
						strBuf.append('0');

				}
				strBuf.append(str);
				nextByte = wbxmlStream.readByte();
			}
			String str = Integer.toBinaryString(nextByte & 0x7F);
			if (str.length() < 7) {
				int zerosToPumpIn = 7 - str.length();
				for (int j = 0; j < zerosToPumpIn; j++)
					strBuf.append('0');

			}
			strBuf.append(str);
			int publicIdValue =
				Integer.valueOf(strBuf.toString(), 2).intValue();
			publicId =
				PublicIdentifiers.getInstance().getPublicIdentifier(
					publicIdValue);
		}

	}

	private void decodeBody() throws IOException {
		writeRootElement();
	}

	private void writeRootElement() throws IOException {
		byte maskedTokenValue = wbxmlStream.readByte();
		byte actualTokenValue = getTokenValue(maskedTokenValue);
		String rootElementName = tokenRepository.getTagName(actualTokenValue);
		try {
			DocumentBuilder builder =
				DocumentBuilderFactory.newInstance().newDocumentBuilder();
			String systemId =
				PublicIdentifiers.getInstance().getSystemIdentifier(publicId);
			DocumentType docType =
				builder.getDOMImplementation().createDocumentType(
					rootElementName,
					publicId,
					systemId);
			xmlDocument =
				builder.getDOMImplementation().createDocument(
					"",
					rootElementName,
					docType);
			if (hasAttributes(maskedTokenValue))
				setAttributes(xmlDocument.getDocumentElement());
			if (hasContent(maskedTokenValue))
				writeChildElement(xmlDocument.getDocumentElement());

		} catch (Exception exp) {
			exp.printStackTrace();
		}

	}

	private void writeChildElement(Element parent) throws IOException {
		byte maskedTokenValue = wbxmlStream.readByte();
		while (maskedTokenValue != 01) { // END (of parent element)
			byte actualTokenValue = getTokenValue(maskedTokenValue);
			if (isInlineStrToken(maskedTokenValue)) // element has text content
				writeContentAsInlineStr(parent);
			else {
				if (isEntityToken(actualTokenValue)) // element has entity
					writeEntityContent(parent);
				else if (maskedTokenValue == GlobalTokens.OPAQUE)
					// element contains opaque data
					writeOpaqueContent(parent);
				else if (isStringTableReferenceToken(maskedTokenValue)) {
					byte indexInStringTable = wbxmlStream.readByte();
					writeContentFromStrTable(parent, indexInStringTable);
				} else if (actualTokenValue == GlobalTokens.SWITCH_PAGE) {
					byte codepageNo = wbxmlStream.readByte();
					TokenRepository.setCurrentCodepage(codepageNo);
					// TODO specify namespace with xmlns attribute
				} else {
					String elementName =
						tokenRepository.getTagName(actualTokenValue);

					Element childElement =
						xmlDocument.createElement(elementName);
					//childElement.setPrefix(tokenRepository.getCurrentNamespace());
					parent.appendChild(childElement);
					if (hasAttributes(maskedTokenValue))
						setAttributes(childElement);
					if (hasContent(maskedTokenValue)) {
						writeChildElement(childElement);
						maskedTokenValue = wbxmlStream.readByte();
						continue;
					}
				}

			}
			maskedTokenValue = wbxmlStream.readByte();
		}
	}

	private void writeContentFromStrTable(
		Element parent,
		int indexInStringTable) {
		char c = 0x0;
		int endIndex =
			stringTable.toString().indexOf(
				new String(new char[] { c }),
				indexInStringTable);
		String content = stringTable.substring(indexInStringTable, endIndex);
		Text txtContent = xmlDocument.createTextNode(content);
		parent.appendChild(txtContent);
	}

	private boolean hasAttributes(byte tokenValue) {
		return ((tokenValue & attributeBitMask) == attributeBitMask);
	}

	private boolean hasContent(byte tokenValue) {
		return (tokenValue & parentBitMask) == parentBitMask;
	}

	private void setAttributes(Element element) throws IOException {
		byte attrTokenValue = wbxmlStream.readByte();
		String currentAttrName = "";
		while (attrTokenValue != 01) { // END (of attribue list)
			if (isInlineStrToken(attrTokenValue))
				writeAttrValueAsInlineStr(element, currentAttrName);
			else {
				if (isEntityToken(attrTokenValue))
					writeEntityAsAttribute(element, currentAttrName);
				else if (isStringTableReferenceToken(attrTokenValue)) {
					byte indexInStringTable = wbxmlStream.readByte();
					writeAttributeFromStrTable(
						element,
						currentAttrName,
						indexInStringTable);
				} else {
					if (isAttrNameToken(attrTokenValue))
						currentAttrName =
							writeAttribute(element, attrTokenValue);
					else {
						if (isAttrValueToken(attrTokenValue))
							writeAttrValue(
								element,
								currentAttrName,
								attrTokenValue);
					}
				}
			}
			attrTokenValue = wbxmlStream.readByte();
		}

	}

	private String writeAttribute(Element element, byte attrTokenValue) {
		String[] attributeNameAndPrefix =
			tokenRepository.getAttributeNameAndPrefix(attrTokenValue);
		String attributeValue = "";
		String attributeName = attributeNameAndPrefix[0].toString();
		boolean hasPrefix = attributeNameAndPrefix[1] != null;
		if (hasPrefix)
			attributeValue = attributeNameAndPrefix[1].trim();
		Attr attrNode = xmlDocument.createAttribute(attributeName);
		attrNode.setValue(attributeValue);
		element.setAttributeNode(attrNode);
		return attributeName;
	}

	private void writeAttrValue(
		Element element,
		String attrName,
		byte attrTokenValue)
		throws IOException {
		String partialAttrValue = element.getAttribute(attrName);
		String attrValue =
			partialAttrValue
				+ tokenRepository.getAttributeValue(attrTokenValue);
		element.setAttribute(attrName, attrValue);
	}

	private void writeAttributeFromStrTable(
		Element element,
		String attrName,
		byte indexInStringTable) {
		String partialAttrValue = element.getAttribute(attrName);
		char c = 0x0;
		int endIndex =
			stringTable.toString().indexOf(
				new String(new char[] { c }),
				indexInStringTable);
		String attrValue =
			partialAttrValue
				+ stringTable.substring(indexInStringTable, endIndex);
		element.setAttribute(attrName, attrValue);

	}

	private void writeAttrValueAsInlineStr(Element element, String attrName)
		throws IOException {
		byte aChar = wbxmlStream.readByte();
		byte[] content = new byte[1024];
		int count = 0;
		while (aChar != 0x0) {
			content[count++] = aChar;
			aChar = wbxmlStream.readByte();
		}

		String previous_value = element.getAttribute(attrName);
		StringBuffer attrValue =
			new StringBuffer(
				previous_value.equals("null") ? "" : previous_value);
		attrValue =
			attrValue.append(new String(content, 0, count, this.encoding));
		element.setAttribute(attrName, attrValue.toString());
	}

	private void writeEntityAsAttribute(Element element, String attrName)
		throws IOException {
		String previous_value = element.getAttribute(attrName);
		StringBuffer attrValue =
			new StringBuffer(
				previous_value.equals("null") ? "" : previous_value);

		byte aChar = wbxmlStream.readByte();
		while ((aChar & (byte) 0x80) == 0x80) {
			// is aChar's continuation flag(MSB) is on)
			aChar = (byte) (aChar & 0x7f); //extract remaining 7 bits;
			String str1 = Integer.toString(aChar, 2);
			attrValue.append(str1);
			aChar = wbxmlStream.readByte();
		}

		String str2 = Integer.toString(aChar, 2);
		// last byte in multiple byte format
		while (str2.length() < 7)
			str2 = "0" + str2;
		attrValue.append(str2);
		int multipleByteValue = Integer.parseInt(attrValue.toString(), 2);
		element.setAttribute(attrName, "&#" + multipleByteValue + ";");
	}

	private void writeContentAsInlineStr(Element element) throws IOException {
		byte aChar = wbxmlStream.readByte();
		byte[] content = new byte[1024];
		int count = 0;
		while (aChar != 0x0) {
			content[count++] = aChar;
			aChar = wbxmlStream.readByte();
		}
		String attrValue = new String(content, 0, count, this.encoding);
		Text txtContent = xmlDocument.createTextNode(attrValue);
		element.appendChild(txtContent);
	}

	private boolean isEntityToken(byte tokenValue) {
		return tokenValue == GlobalTokens.ENTITY;
		// todo for other global tokens
	}

	private boolean isStringTableReferenceToken(byte tokenValue) {
		return tokenValue == GlobalTokens.STR_T;
	}

	private void writeEntityContent(Element element) throws IOException {
		StringBuffer attrValue = new StringBuffer();
		byte aChar = wbxmlStream.readByte();
		while ((aChar & (byte) 0x80) == 0x80) {
			// is aChar's continuation flag(MSB) is on)
			aChar = (byte) (aChar & 0x7f); //extract remaining 7 bits;
			String str1 = Integer.toString(aChar, 2);
			attrValue.append(str1);
			aChar = wbxmlStream.readByte();
		}

		String str2 = Integer.toString(aChar, 2);
		// last byte in multiple byte format
		while (str2.length() < 7)
			str2 = "0" + str2;
		attrValue.append(str2);
		int multipleByteValue = Integer.parseInt(attrValue.toString(), 2);
		Text txtContent =
			xmlDocument.createTextNode(Integer.toString(multipleByteValue));
		element.appendChild(txtContent);
	}

	private void writeOpaqueContent(Element element) throws IOException {
		//	StringBuffer opaqueData = new StringBuffer();
		byte lengthOfOpaqueData = wbxmlStream.readByte();
		// TODO for multi-byte length
		byte[] opaqueDataBuf = new byte[lengthOfOpaqueData];
		for (int i = 0; i < lengthOfOpaqueData; i++) {
			opaqueDataBuf[i] = wbxmlStream.readByte();

		}
		byte b = wbxmlStream.readByte();
		while (b != 1) {
			b = wbxmlStream.readByte();
		}
		String opaqueDataStr = new String(opaqueDataBuf, this.encoding);
		Text txtContent = xmlDocument.createTextNode("");
		//System.out.println("Opaque____" + opaqueDataStr);
		element.appendChild(txtContent);
	}
	private boolean isInlineStrToken(byte tokenValue) {
		return tokenValue == GlobalTokens.STR_ISTR_I;
	}

	private boolean isAttrNameToken(byte tokenValue) {
		return (tokenValue >= 0); // tokenValue is positive implies MSB is 0
	}

	private boolean isAttrValueToken(byte tokenValue) {
		return (tokenValue < 0);
		// tokenValue is negative that implies MSB is 1
	}

	private byte getTokenValue(byte maskedTokenValue) {
		byte unmaskedTokenValue = (byte) (maskedTokenValue & ((byte) 0x3f));
		// 3f =~ 0011 1111
		return unmaskedTokenValue;
	}

}
