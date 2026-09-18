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
import java.io.FileInputStream;
import java.io.FileOutputStream;


import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;

/**
 * 
 * @author <a href="mailto:suvarna@witscale.com">Suvarna Kadam</a>
 */

public class WBXMLConverter {
	public static void main(String[] args) {
		try {
	
		if (args[0].equalsIgnoreCase("-encode")) {
			String xml_filename = args[1];
			String wbxml_filename = args[2];
			System.out.println("Encoding XML file " + xml_filename + "...");

			try {
				System.setProperty(
					"org.xml.sax.driver",
					"org.apache.crimson.parser.XMLReaderImpl");
				FileInputStream xmlStream = new FileInputStream(xml_filename);
				FileOutputStream fo = new FileOutputStream(wbxml_filename);
				ByteArrayOutputStream tokens =
					(ByteArrayOutputStream) WBXMLEncoder.getInstance().encode(
						xmlStream);
				xmlStream.close();
				tokens.writeTo(fo);
				tokens.close();
				fo.flush();
				fo.close();
			} catch (Exception exp) {
				System.out.println(
					"Error while encoding XML file " + xml_filename + "!");
				exp.printStackTrace();
				printUsage();
			}
			return;
		}
		if (args[0].equalsIgnoreCase("-decode")) {
			String wbxml_filename = args[1];
			String xml_filename = args[2];
			System.out.println("Decoding WBXML file " + wbxml_filename + "...");
			try {
				FileInputStream tokenStream = new FileInputStream(wbxml_filename);
				FileOutputStream xmlStream = new FileOutputStream(xml_filename);
				Document document = WBXMLDecoder.getInstance().decode(tokenStream);
				OutputFormat of = new OutputFormat(document);
				XMLSerializer serial = new XMLSerializer(xmlStream,of);
				serial.setOutputByteStream(xmlStream);
				serial.setOutputFormat(of);
				serial.asDOMSerializer();
				serial.serialize(document);
				xmlStream.close();  
				tokenStream.close();
			} catch (Exception exp) {
				System.out.println(
					"Error while decoding WBXML file " + wbxml_filename + "!\n\n");
				exp.printStackTrace();
				printUsage();
			}
			return;
		}
		} catch (Exception exp) {
					System.out.println(
						"Error!You haven't entered the required arguments for using WBXML Parser");
					printUsage();
		}
	}
	
	private static void printUsage() {
		System.out.println(
			"\nUsage:- WBXMLConverter [-encode/-decode] inputfilename outputfilename");
		System.out.println("\n    Example:- $ WBXMLConverter -encode test.xml test.wbxml");
		System.out.println("    Example:- $ WBXMLConverter -decode test.wbxml test.xml");
	}
}
