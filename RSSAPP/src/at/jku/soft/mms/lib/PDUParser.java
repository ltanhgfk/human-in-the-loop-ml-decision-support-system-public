/**
 * MMSLIB - A Java Implementation of the MMS Protocol
 * Copyright (C) 2004 Simon Vogl 
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
package at.jku.soft.mms.lib;

import java.util.Vector;
import java.net.*;
import java.io.*;
import java.util.*;
import org.apache.log4j.*;

public class PDUParser {

    protected static Logger logger = Logger.getLogger(PDUParser.class);

    /**
     *
     * @param wapGwAddress Adress of the WAP-Gateway
     * @param wapGwPort Port of the WAP-Gateway
     * @param uriToGet the URI we would like to GET
     */
    public PDUParser(String file) 
    {
	byte b[] = readFile(file);

	try{
	    MMSPDU pdu = new MMSPDU();
	    pdu.setPayload(b);

	    System.out.flush();
	    System.out.println(pdu.toString());

	} catch (Exception e){
	    // UDP Socket problem
	    e.printStackTrace();
	}
    }


    private byte[] readFile(String file) 
    {
	byte b[] = null;
	try {
	File f = new File(file);
	long len = f.length();
	b = new byte[(int)len];
	FileInputStream fi = new FileInputStream(f);
	fi.read(b);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return b;
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


    public static void main(String[] args) 
    {
	try{
	    PDUParser getter = new PDUParser(args[0]);
	} catch (Exception e){
	    e.printStackTrace();
	}
    }
}
