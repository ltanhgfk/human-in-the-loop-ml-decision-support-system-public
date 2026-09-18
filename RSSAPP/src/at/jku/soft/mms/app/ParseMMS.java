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
package at.jku.soft.mms.app;

import java.util.Vector;
import net.sourceforge.jwap.wsp.*;
import net.sourceforge.jwap.wsp.pdu.*;
import java.net.*;
import java.io.*;
import java.util.*;
import org.apache.log4j.*;

import at.jku.soft.mms.lib.*;

public class ParseMMS
{


  static Logger logger = Logger.getLogger(ParseMMS.class);

  /**
   *
   * @param wapGwAddress Adress of the WAP-Gateway
   * @param wapGwPort Port of the WAP-Gateway
   * @param uriToGet the URI we would like to GET
   */
  public ParseMMS(String filename) {
      byte b[];

      b = loadFromFile(filename);

      // neue Session über Wap-Gateway
      try{
	  MMSPDU mms = new MMSPDU();
	  mms.setPayload(b);
      } catch (Exception e){
	  // UDP Socket problem
	  e.printStackTrace();
      }
  }


  private byte[] loadFromFile(String filename) {
    try{
	byte b[];
	byte skip;
	FileInputStream fo;
	fo = new FileInputStream(filename);
	b = new byte[fo.available()];
	fo.read(b);
	return b;
    } catch (Exception e){
	e.printStackTrace();
	System.exit(1);
	return null;
    }
  }

  private void saveToFile(String filename, byte[]b) {
    try{
      FileOutputStream fo;
      fo = new FileOutputStream(filename);
      fo.write(b);
      fo.close();
    } catch (Exception e){
      // Host address not ok
      e.printStackTrace();
      System.exit(1);
    }
  }

  public static void main(String[] args) {

      ParseMMS getter = new ParseMMS(args[0]);
  }

}
