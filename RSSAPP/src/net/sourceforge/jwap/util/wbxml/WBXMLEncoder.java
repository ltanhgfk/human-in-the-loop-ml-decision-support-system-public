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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;



/**
 * <p>This class is used to encode an XML file into WBXML. </p> 
 * 
 * @author <a href="mailto:suvarna@witscale.com">Suvarna Kadam</a>
 * @see net.sourceforge.jwap.util.wbxml.XMLContentHandler
 * @see net.sourceforge.jwap.util.wbxml.TokenRepository
 */

public class WBXMLEncoder {
	private static WBXMLEncoder anInstance;
	private Document xmlDocument;
	private XMLContentHandler handler ;
	private ByteArrayOutputStream tokenStream;
	private int wbxmlVersion = 3; // WBXML version 1.3
	private WBXMLEncoder() {
		initialize();
	}

	private void initialize() {
		tokenStream = new ByteArrayOutputStream();
	}
	public static WBXMLEncoder getInstance() {
		if (anInstance == null)
			anInstance = new WBXMLEncoder();
		return anInstance;
	}
	public OutputStream encode(InputStream xmlStream) {
//		extractCharEncoding(xmlStream);
		try {
			handler = new XMLContentHandler(getEncodingFrom(xmlStream));
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setFeature("http://xml.org/sax/features/validation", false);
		//	reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
			reader.setContentHandler(handler);
		//	reader.setErrorHandler(handler);
		//	reader.setDTDHandler(handler);
		//	reader.setEntityResolver(EntityResolver);
            reader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);			
			InputSource xmlSource = new InputSource(xmlStream);
	//		reader.setFeature("http://xml.org/sax/features/validation",true);
	      	reader.parse(xmlSource);
//			javax.xml.parsers.SAXParser parser = javax.xml.parsers.SAXParserFactory.newInstance().newSAXParser(); 
//			parser.getXMLReader().setFeature("http://xml.org/sax/features/validation", false);
//			InputSource xmlSource = new InputSource(xmlStream);
//			parser.getXMLReader().setFeature("http://xml.org/sax/features/validation", false);
//			parser.parse(xmlSource,handler);
		} catch (SAXException saxExp) {
			saxExp.printStackTrace();
		} catch (IOException ioExp) {
			ioExp.printStackTrace();
		} 
		encodeProlog();
		encodeStringTable();
		encodeBody();
		return tokenStream;
	}
	
	private String getEncodingFrom(InputStream xmlStream) {
	/*	BufferedReader reader = new BufferedReader(new InputStreamReader(xmlStream));
		try {
			String line = reader.readLine().trim();
			while (line.length() == 0 || !line.startsWith("<?xml"))
				line = reader.readLine();
			if (line.startsWith("<?xml")) {
				String remaining = line.split("encoding=\"")[1];
				String encoding = remaining.substring(0, remaining.indexOf('"'));
				reader.close();
				return encoding;
			}
		} catch (IOException ioExp) {
			ioExp.printStackTrace();
		}
		*/
		return "utf-8";
	}
	/**
	 * Please refer BNF of document structure.
	 * start = version publicid charset strtbl body
	 * 
	 **/

	public void encodeProlog() {
//		tokenStream.write(wbxmlVersion);
//		tokenStream.write(PublicIdentifiers.getInstance().getPublicIdentifierValue("UNKNOWN"));
//	//	tokenStream.write(TokenRepository.getInstance().getCharEncoding());
//		tokenStream.write(0); // string table length to do...
	}
	public void encodeBody() {
		Iterator tokens = handler.getTokens();
		while (tokens.hasNext()) {
			Token aToken= (Token) tokens.next();
			tokenStream.write(aToken.getValue());			
		}
	}

	public void encodeStringTable() {
	}
}

