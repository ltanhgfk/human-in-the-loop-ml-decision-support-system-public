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
package net.sourceforge.jwap.examples;

import net.sourceforge.jwap.wsp.*;

import java.net.*;

import java.util.Vector;


/**
 * This Class shows you how to use the jwap open WAP stack.
 * First of all you should implement net.sourceforge.jwap.wsp.IWSPUpperLayer.
 */
public class CWapGetExample implements IWSPUpperLayer {
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
    public CWapGetExample(InetAddress wapGwAddress, int wapGwPort,
        String uriToGet) {
        // GET-URI ablegen
        this.uriToGet = uriToGet;

        // neue Session über Wap-Gateway
        try {
            session = new CWSPSession(wapGwAddress, // URI of WAP gateway
                    wapGwPort, // port of wap gateway
                    this, // WE would like to be informed of occuring events
                    true); // be verbose
            System.out.println("Connecting to WAP-Gateway");
            session.s_connect();

            // we will get a s.connect.cnf, when we are connected.
            // @see #s_connect_cnf()
        } catch (SocketException e) {
            // UDP Socket problem
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            // we use an open wap gateway by www.waptunnel.com 207.232.99.109
            InetAddress gateway = InetAddress.getByName("YOUR_WAP_GATEWAY_HOST");
            CWapGetExample getter = new CWapGetExample(gateway, 9201,
                    "http://wap.nokia.de/menu.wml");
        } catch (UnknownHostException e) {
            // Host address not ok
            e.printStackTrace();
        }
    }

    //------------------ from here on we implement IWSPUpperLayer ---------------

    /**
     * called by jwap stack, when we are connectedwith the WAP gateway
     */
    public void s_connect_cnf() {
        System.out.println("connected to WAP gateway");
        System.out.println("GET " + uriToGet);

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
    public void s_methodResult_ind(byte[] payload, String contentType,
        boolean moreData) {
        System.out.println("Antwort erhalten:");
        System.out.println("Content Type: " + contentType);
        System.out.println("" + payload.length + " bytes");

        // if we do not need any more invocations of a GET/POST methode, we will
        // disconnect from WAP gateway
        System.out.println("Disconnecting from WAP-Gateway");
        session.s_disconnect();
    }

    /**
     * will be incoked by jwap stack, when we are disconnected from the WAP gateway
     * @param reason
     */
    public void s_disconnect_ind(short reason) {
        System.out.println("Disconnected.");
    }

    /**
     * will be invoked by jwap stack, when we are disconnected by the WAP gateway
     * because it is redirected. Unfortunately we do not provide you with the new Port.
     * If you need that information use IWSPUpperLayer2
     *
     * @param redirectInfo
     */
    public void s_disconnect_ind(InetAddress[] redirectInfo) {
        if (redirectInfo.length > 0){
	        try {
	            System.out.println("redirected to " + redirectInfo[0].getHostAddress());
	        
	        	session = new CWSPSession(
	        	        redirectInfo[0], // URI of WAP gateway
	        	        9201, // port of wap gateway
	        	        this, // WE would like to be informed of occuring events
	        	        true); // be verbose
	        	System.out.println("Connecting to WAP-Gateway");
	        	session.s_connect();
	        } catch (SocketException e) {
	            // UDP Socket problem
	            e.printStackTrace();
	        }
        }
    }

    /**
     * invoked ba the jwap stack to show, that the session is suspended
     * @param reason
     */
    public void s_suspend_ind(short reason) {
    }

    /**
     * invoked, when a suspended session will be resumed
     */
    public void s_resume_cnf() {
    }
}
