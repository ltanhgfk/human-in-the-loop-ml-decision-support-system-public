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
package at.jku.soft.mms.app;

import java.util.Vector;
import net.sourceforge.jwap.wsp.*;
import java.net.*;
import java.io.*;
import java.util.*;
import org.apache.log4j.*;

import at.jku.soft.mms.lib.*;

/**
 *
 * <p><b>MMSLIB:</b> A Java Implementation of the MMS Protocol</p>
 * <p>The lib builds upon the JWAP stack for the lower WAP protocol layers.</p>
 * <p><b>Licensed under the terms of GNU Lesser General Public License (LGPL): </b> <a href="http://www.sourceforge.net/projects/jwap/licence.html">Follow this link for information on this product's licence and copyright: http://www.sourceforge.net/projects/jwap/licence.html</a></p>
 * <p><b>Project Website:</b> <a href="http://www.sourceforge.net/projects/mmslib/">http://www.sourceforge.net/projects/mmslib/</a></p>
 * <p><b>Author:</b> <a href="mailto:koobla@sourceforge.net">Simon Vogl</a></p>
 * <p><b>Version:</b> 0.1 </p>
 *
 * <p><b>Class Description:</b><br> </p>
 * This class is a standalone application that connects to a MMSC (via a WAP
 * gateway) and retrieves a MMS deliver PDU, which is saved as a file in the 
 * subdirectory "application/" (which you need to create beforehand).
 */
public class Getter implements IWSPUpperLayer {

    protected static Logger logger = Logger.getLogger(Getter.class);

    /**
     * The actual WSP Session.
     * You need one instance of CWSPSession for each WAP session.
     */
    private CWSPSession session;

    /**
     * the URI we would like to get in this example
     */
    private String uriToGet;

    /**
     * You will get an instance of CWSPMethodManger for each invoked
     * method (GET or POST). In this Vector we collect all invoked methods.
     */
    private Vector invokedMethods = new Vector();

    /**
     *
     * @param wapGwAddress Adress of the WAP-Gateway
     * @param wapGwPort Port of the WAP-Gateway
     * @param uriToGet the URI we would like to GET
     */
    public Getter(InetAddress wapGwAddress,
		  int         wapGwPort,
		  String      uriToGet) {
	// GET-URI ablegen
	this.uriToGet = uriToGet;


	// neue Session über Wap-Gateway
	try{
	    session = new CWSPSession(wapGwAddress, // URI of WAP gateway
				      wapGwPort,    // port of wap gateway
				      this,         // WE would like to be informed of occuring events
				      true);        // be verbose
	    logger.debug("Connecting to WAP-Gateway");
	    session.s_connect();
	    // we will get a s.connect.cnf, when we are connected.
	    // @see #s_connect_cnf()
	} catch (SocketException e){
	    // UDP Socket problem
	    e.printStackTrace();
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

    private void saveToFile(String filename, Vector x) {
	try{
	    FileOutputStream fo;
	    fo = new FileOutputStream(filename);
	    for (int i=0;i<x.size();i++){
		fo.write( (byte[]) x.elementAt(i) );
	    }
	    fo.close();
	} catch (Exception e){
	    // Host address not ok
	    e.printStackTrace();
	    System.exit(1);
	}
    }

    //------------------ from here on we implement IWSPUpperLayer ---------------

    /**
     * called by jwap stack, when we are connectedwith the WAP gateway
     */
    public void s_connect_cnf() {
	logger.debug("connected to WAP gateway");
	logger.debug("GET " + uriToGet);
	// now we do call GET!
	// jwap returns an instance of CWSPMethodManager, that manages
	// the method call
	// We put it into the vector above.
	invokedMethods.addElement(session.s_get(uriToGet));
    }

    /**
     * called by jwap stack, to announce a response to a GET/POST method invocation
     *
     * @param payload
     * @param contentType
     * @param moreData
     */
    Vector x = new Vector();
    String ct = "xxx";
    public void s_methodResult_ind(byte[] payload, String contentType, boolean moreData) {
	if (x.size()==0) {
	    logger.debug("Antwort erhalten:");
	    logger.debug("Content Type: "+ contentType);
	    ct = contentType;
	}
	logger.debug("More Data: "+ moreData);
	logger.debug("Payload length: "+ payload.length + " bytes");

	
	  for (int i=0;i<payload.length;i++) {
	  System.out.print((char)payload[i]);
	  }
	
	// if we do not need any more invocations of a GET/POST methode, we will
	// disconnect from WAP gateway
	logger.debug("\n\nDisconnecting from WAP-Gateway");
	if (!moreData) {
	    session.s_disconnect();
	    saveToFile(ct + "_" + System.currentTimeMillis() +".mms", x); 
    
	} else {
	    x.addElement(payload);
	}
    }

    /**
     * will be incoked by jwap stack, when we are disconnected from the WAP gateway
     * @param reason
     */
    public void s_disconnect_ind(short reason) {
	logger.debug("Disconnected.");
    }

    /**
     * will be invoked by jwap stack, when we are disconnected by the WAP gateway
     * because it is redirected.
     *
     * @param redirectInfo
     */
    public void s_disconnect_ind(InetAddress[] redirectInfo) {
	logger.debug("WTP session  disc_ind");

    }

    /**
     * invoked ba the jwap stack to show, that the session is suspended
     * @param reason
     */
    public void s_suspend_ind(short reason) {
	logger.debug("WTP session susp_ind");

    }

    /**
     * invoked, when a suspended session will be resumed
     */
    public void s_resume_cnf() {
	logger.debug("WTP session resume_cnf");
    }

    public static void main(String[] args) {
	try{
	    // we use an open wap gateway by www.waptunnel.com
	    InetAddress gateway = InetAddress.getByName("YOUR_MMS_GATEWAY_HOST");
	    //      Getter getter = new Getter(gateway, 9201, "http://mmsc.one.at/mms/wapenc?location=4369914219502_uipr02");
	    String addr="http://mmsc.one.at/mms/wapenc?location=4369918010000_lapc02";
	    if (args.length>0)
		addr = args[0];
	    Getter getter = new Getter(gateway, 9201, addr);
	} catch (UnknownHostException e){
	    // Host address not ok
	    e.printStackTrace();
	}
    }
}
