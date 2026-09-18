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

import net.sourceforge.jwap.util.wbxml.WBXMLDecoder;
import net.sourceforge.jwap.wsp.CWSPSession;
import net.sourceforge.jwap.wsp.IWSPUpperLayer;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

import org.w3c.dom.Document;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import java.util.Vector;


/**
 * This Class demonstrates how can get WML using the jwap open WAP stack.
 */
public class WMLExample implements IWSPUpperLayer {
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
    public WMLExample(InetAddress wapGwAddress, int wapGwPort, String uriToGet) {
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
            WMLExample getter = new WMLExample(gateway, 9201,
                    "http://wap.jamba.de");

            //CWapExample getter = new CWapExample(gateway, 9201, "http://wap.web.de");
            //  CWapExample getter = new CWapExample(gateway, 9201, "http://www.nokia.com");
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
        writeBytesToFile(payload);

        // if we do not need any more invocations of a GET/POST methode, we will
        // disconnect from WAP gateway
        System.out.println("Disconnecting from WAP-Gateway");
        session.s_disconnect();
    }

    private void writeBytesToFile(byte[] payload) {
        System.out.println("writing to a file");

        try {
            FileOutputStream wapContent = new FileOutputStream("jamba.wbxml");
            wapContent.write(payload);
            wapContent.close();
        } catch (Exception exp) {
            exp.printStackTrace();
        }

        // Decode the bytes in WBXML file to get wml file
        FileInputStream tokenStream;

        try {
            tokenStream = new FileInputStream("jamba.wbxml");

            FileOutputStream xmlStream = new FileOutputStream("jamba.wml");
            Document document = WBXMLDecoder.getInstance().decode(tokenStream);
            OutputFormat of = new OutputFormat(document);
            XMLSerializer serial = new XMLSerializer(xmlStream, of);
            serial.setOutputByteStream(xmlStream);
            serial.setOutputFormat(of);
            serial.asDOMSerializer();
            serial.serialize(document);
            xmlStream.close();
            tokenStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
     * because it is redirected.
     *
     * @param redirectInfo
     */
    public void s_disconnect_ind(InetAddress[] redirectInfo) {
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
