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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;

import net.sourceforge.jwap.wsp.CWSPMethodManager;
import net.sourceforge.jwap.wsp.CWSPSession;
import net.sourceforge.jwap.wsp.IWSPUpperLayer;


/**
 * This class is used to send (POST) data via a WAP Gateway.
 */
public class CWapPostExample implements IWSPUpperLayer {
    // the URL of the WAP-Gateway and its port.
    private InetAddress wapgw;
    private int wapgwport;

    // the URI of the server to post to
    private String posturi;

    // the bytes to be posted
    private byte[] bytes;
    private String contentType;

    // holds the WAP session we open to send the message (from connect till disconnect)
    private CWSPSession s;

    // holds the Method transaction for posting (from m_send_req till m_send_conf)
    private CWSPMethodManager m;

    /**
     * Constructs a WAP sender object.
     *
     * @param wapgw the wap gateway to use
     * @param wapgwport the port of the wap gateway
     */
    public CWapPostExample(InetAddress wapgw, int wapgwport, String posturi,
        byte[] dataToSend, String contentType) {
        this.bytes = dataToSend;
        this.posturi = posturi;
        this.wapgw = wapgw;
        this.wapgwport = wapgwport;
        this.contentType = contentType;

        init();
    }

    /**
     * It automatically connects to the WAP gateway.
     * After the connect.conf it sends the WAP POST.
     * After all it disconnects from the gateway.
     *
     * @param args none (ignored)
     */
    public static void main(String args) {
        try {
            CWapPostExample poster = new CWapPostExample(InetAddress.getByName(
                        "IpAddressOfYourWapGateway"), 9201,
                    "http://IpOfYourMMSC",
                    "This data should represent a correctly encoded Multimedia Message according to MMSE".getBytes(),
                    "application/vnd.wap.mms-message");
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method connects to the wap gateway and opens a WSP session.
     */
    private void init() {
        try {
            s = new CWSPSession(wapgw, wapgwport, this);
            System.out.println("connecting to WAP gateway " + wapgw);
            s.s_connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
    * Implementation of IWSPUpperLayer.
    * Is called by the WAP stack after being connected.
    * We then send the Message in a POST.
    */
    public void s_connect_cnf() {
        try {
            System.out.println("connected to WAP gateway " + wapgw);

            System.out.println("sending data (WSP POST)");
            m = s.s_post(bytes, contentType, posturi);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Implementation of IWSPUpperLayer.
     * this method is called by the wap stack after a response of a method is received.
     * @param payload the payload from the response
     * @param contentType the content type of the response
     * @param moreData will there come more data belonging to this result?
     */
    public void s_methodResult_ind(byte[] payload, String contentType,
        boolean moreData) {
        // decode the response here...
        String nl = System.getProperty("line.separator");

        System.out.println("Response Content Type: " + contentType + nl +
            payload.toString());

        System.out.println("disconnecting from WAP gateway " + wapgw);
        m.s_methodResult(null);
        s.s_disconnect();
    }

    /**
     * Implementation of IWSPUpperLayer. NOT USED.
     * @param reason
     */
    public void s_suspend_ind(short reason) {
    }

    /**
     * Implementation of IWSPUpperLayer. NOT USED.
     */
    public void s_resume_cnf() {
    }

    /**
     * Implementation of IWSPUpperLayer.
     * Called by the WAP stack if we are disconnected.
     * @param reason the reason, why we are disconnected
     */
    public void s_disconnect_ind(short reason) {
        System.out.println("disconnected from WAP gateway " + wapgw);
    }

    /**
     * Implementation of IWSPUpperLayer. NOT USED.
     * if we receive a redirect from the server.
     * @param redirectInfo
     */
    public void s_disconnect_ind(Vector redirectInfo) {
        System.out.println("disconnecting from WAP gateway " + wapgw);
        this.m.s_methodResult(null);
        s.s_disconnect();
    }

    public void s_disconnect_ind(InetAddress[] redirectInfo) {
        System.out.println("---redirected to " + redirectInfo.toString());
    }
}
