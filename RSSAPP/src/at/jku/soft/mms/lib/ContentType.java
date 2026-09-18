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

import net.sourceforge.jwap.wsp.*;
import net.sourceforge.jwap.wsp.*;
import java.util.*;
import org.apache.log4j.*;

import at.jku.soft.mms.util.DataBlock;

public class ContentType
{
    static Logger logger = Logger.getLogger(ContentType.class);

    public ContentType() 
    {
	myType=0;
    }

    public final static String knownTypes[] = {
        "*/*",
        "text/*",
        "text/html",
        "text/plain",
        "text/x-hdml",
        "text/x-ttml",
        "text/x-vCalendar",
        "text/x-vCard",
        "text/vnd.wap.wml",
        "text/vnd.wap.wmlscript",
        "text/vnd.wap.wta-event",
        "multipart/*",
        "multipart/mixed",
        "multipart/form-data",
        "multipart/byterantes",
        "multipart/alternative",
        "application/*",
        "application/java-vm",
        "application/x-www-form-urlencoded",
        "application/x-hdmlc",
        "application/vnd.wap.wmlc",
        "application/vnd.wap.wmlscriptc",
        "application/vnd.wap.wta-eventc",
        "application/vnd.wap.uaprof",
        "application/vnd.wap.wtls-ca-certificate",
        "application/vnd.wap.wtls-user-certificate",
        "application/x-x509-ca-cert",
        "application/x-x509-user-cert",
        "image/*",
        "image/gif",
        "image/jpeg",
        "image/tiff",
        "image/png",
        "image/vnd.wap.wbmp",
        "application/vnd.wap.multipart.*",
        "application/vnd.wap.multipart.mixed",
        "application/vnd.wap.multipart.form-data",
        "application/vnd.wap.multipart.byteranges",
        "application/vnd.wap.multipart.alternative",
        "application/xml",
        "text/xml",
        "application/vnd.wap.wbxml",
        "application/x-x968-cross-cert",
        "application/x-x968-ca-cert",
        "application/x-x968-user-cert",
        "text/vnd.wap.si",
        "application/vnd.wap.sic",
        "text/vnd.wap.sl",
        "application/vnd.wap.slc",
        "text/vnd.wap.co",
        "application/vnd.wap.coc",
        "application/vnd.wap.multipart.related",
        "application/vnd.wap.sia",
        "text/vnd.wap.connectivity-xml",
        "application/vnd.wap.connectivity-wbxml",
        "application/pkcs7-mime",
        "application/vnd.wap.hashed-certificate",
        "application/vnd.wap.signed-certificate",
        "application/vnd.wap.cert-response",
        "application/xhtml+xml",
        "application/wml+xml",
        "text/css",
        "application/vnd.wap.mms-message",
        "application/vnd.wap.rollover-certificate",
        "application/vnd.wap.locc+wbxml",
        "application/vnd.wap.loc+xml",
        "application/vnd.syncml.dm+wbxml",
        "application/vnd.syncml.dm+xml",
        "application/vnd.syncml.notification",
        "application/vnd.wap.xhtml+xml",
        "application/vnd.wv.csp.cir",
        "application/vnd.oma.dd+xml",
        "application/vnd.oma.drm.message",
        "application/vnd.oma.drm.content",
        "application/vnd.oma.drm.rights+xml",
        "application/vnd.oma.drm.rights+wbxml",
    };


    /** List of well-known parameters according to 
     * WAP-203-WSP-20000504-a.pdf, Table 38. */
    public static final String parameterNames[] = {
	"Q",
	"Charset",
	"Level",
	"Type",
	"Name",
	"Filename",
	"Differences",
	"Padding",
	"Type", // (when used as parameter of Content-Type: multipart/related)
	"Start", //(with multipart/related)
	"Start-info", // (with multipart/related)
	"Comment",
	"Domain",
	"Max-Age",
	"Path",
	"Secure",
    };
    public static final int WSP_PARAM_Q=0;
    public static final int WSP_PARAM_CHARSET=1;
    public static final int WSP_PARAM_LEVEL=2;
    public static final int WSP_PARAM_TYPE =3;
    public static final int WSP_PARAM_NAME =0x05;
    public static final int WSP_PARAM_FILENAME =0x06;
    //differences: 7
    //padding: 8
    public static final int WSP_PARAM_TYPE_RELATED =0x09;
    public static final int WSP_PARAM_START=0x0a;
    public static final int WSP_PARAM_START_INFO=0x0b;


    private int myType; // -1 if custom, or one of the above
    private String theType; // String value of the type

    public void setContentType(int idx) {
	myType = idx;
	theType = knownTypes[idx];
    }

    public void setContentType(String type) {
	myType = -1;
	theType = type;
    }

    /** isValid returns false as long as the
     * content type has not been initialized.
     */
    public boolean isValid() { return ! ( myType==0 && theType==null); };
    
    public int getContentTypeAsInt() {
    	return myType;
    }
    
    public String getContentType() {
    	return (myType == -1)? theType: knownTypes[myType];
    }    

    public static String getKnownType(int idx) 
    {
	return knownTypes[idx];
    }

    public boolean isKnownType() 
    {
	return myType!=-1;
    }
    
    public boolean isMultiPart() 
    {
	if (myType<0) {
	    if (theType!=null && theType.startsWith("multipart/")) {
		return true;
	    }
	} else {
	    if ( knownTypes[myType].startsWith("multipart")
		 || knownTypes[myType].startsWith("application/vnd.wap.multipart")) {
		return true;
	    }
	}
	return false;
    }

    private String pName;
    public boolean hasName() { return pName!=null; }
    public String getName() { return pName; }
    public void setName(String n) {pName=n;}

    private String pFilename;
    public boolean hasFilename() { return pFilename!=null; }
    public String getFilename() { return pFilename; }
    public void setFilename(String n) {pFilename=n;}

    private String pType;
    public boolean hasType() { return pType!=null; }
    public String getType() { return pType; }
    public void setType(String n) {pType=n;}

    private String pStart;
    public boolean hasStart() { return pStart!=null; }
    public String getStart() { return pStart; }
    public void setStart(String n) {pStart=n;}

    public String toString() 
    {
	String c;
	if (myType>=0) {
	    c= "Content-Type: " + knownTypes[myType];
	} else {
	    c= "Content-Type: " + theType;
	}
	if (hasName()) 
	    c += "\r\nName: " + getName();
	if (hasFilename())
	    c += "\r\nFilename: " + getFilename();
	if (hasType())
	    c += "\r\nType: " + getType();
	if (hasStart())
	    c += "\r\nStart: " + getStart();
	return c;

    }   

    public boolean hasParameters()
    {
	return hasFilename() || hasType() || hasStart();
    }
    
    public void emit(DataBlock db) 
    {
	// emit the content type information w/o header name
	if (hasParameters()) {
	    // collect content type information...
	    DataBlock temp = new DataBlock();
	    // content type
	    if (isKnownType()) temp.addShortInt(myType);
	    else temp.addString(theType);
	    
	    // some parameters
	    if (hasFilename()) {
		System.out.println(getFilename());
		temp.addShortInt(WSP_PARAM_NAME);
		temp.addString(getFilename());			
	    }
		
	    if (hasType()) {
		temp.addShortInt(WSP_PARAM_TYPE_RELATED);
		temp.addString(getType()); 
		// this would add "application/smil"	
	    }
		
	    if (hasStart()) {
		System.out.println(getStart());			
		temp.addShortInt(WSP_PARAM_START);
		temp.addString(getStart());
	    }
		
	    byte[] bTmp = temp.getAsByteArray();
		
	    db.add((byte)0x1b); // esc - indicates general form content type
	    db.addUIntVar(bTmp.length);
	    db.add(bTmp);

	    //add length
	    //add content type itself
	    //add parameter values


	} else {
	    // add content type
	    if (isKnownType()) {
		db.addShortInt(myType);
	    } else {
		db.addString(theType);
	    }
	}
    }
}

