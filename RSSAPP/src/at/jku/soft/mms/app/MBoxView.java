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

import java.util.*;
import net.sourceforge.jwap.wsp.*;
import java.net.*;
import java.io.*;
import net.sourceforge.jwap.util.*;
import java.lang.*;

import at.jku.soft.mms.lib.*;
import at.jku.soft.mms.util.*;

/** Sender performs a WSP Post request, sending a PDU to the MMSC and
 * retrieving an answer. This application specifically sends mms-send-req
 * pdus and gets a mms-send-conf returned, with information about success
 * or failure of the operation.
 */
public class MBoxView implements IWSPUpperLayer{

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
    
    public int sendStatus=123;
    public String filename;
    public String msgId;

    /**
     * Constructs a WAP sender object.
     *
     * @param wapgw the wap gateway to use
     * @param wapgwport the port of the wap gateway
     */
    public MBoxView(InetAddress wapgw, int wapgwport){

	MMSPDU view = MMSPDUFactory.makeMmsMboxViewReq();

	MMSHeaders head = view.getMmsHeaders();

	this.bytes = view.toByteArray();

	saveToFile("mboxviewreq.pdu", bytes); 

	this.posturi = "http://mmsc.one.at/mms/wapenc?location=4369914219500";
	this.wapgw = wapgw;
	this.wapgwport = wapgwport;
	this.contentType = "application/vnd.wap.mms-message";
    }
    
    /**
     * This method connects to the wap gateway and opens a WSP session.
     */
    private void init()
    {
	try {
	    s = new CWSPSession(wapgw, wapgwport, this);
	    System.out.println("connecting to WAP gateway " + wapgw);
	    s.s_connect();
	} catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}

    }

    /**
     * Implementation of IWSPUpperLayer.
     * Is called by the WAP stack after being connected.
     * We then send the Message in a POST.
     */
    public  void s_connect_cnf()
    {
	try {
	    System.out.println("connected to WAP gateway " + wapgw);
	   
	    System.out.println("sending data (WSP POST)");
	    m = s.s_post(bytes,
			 contentType,
			 posturi);
	   
	} catch (Exception e ) {
	    e.printStackTrace();
	    System.exit(1);
	}
    }

    /**
     * Implementation of IWSPUpperLayer.
     * this method is called by the wap stack after a response of a method is received.
     * @param payload the payload from the response
     * @param contentType the content type of the response
     * @param moreData will there come more data belonging to this result?
     */
    public void s_methodResult_ind(byte[] payload,
				   String contentType,
				   boolean moreData)
    {
	// decode the response here...
	System.out.println("Response More Data: " +  moreData );
	System.out.println("Response Content Type: " +  contentType );
     
	saveToFile(contentType + "_" + System.currentTimeMillis() +".pdu", payload); 
    
	if (!moreData) {
	    System.out.println("disconnecting from WAP gateway " + wapgw);
	    m.s_methodResult(null);
	    s.s_disconnect();

	    MMSPDU pdu = new MMSPDU();
	    MMSHeaders head = pdu.getMmsHeaders();
	    pdu.setPayload(payload);
	    if (head.getMessageType() == MMSHeaders.Mms_M_Send_Resp) {
		int rstat = head.getResponseStatus();
		sendStatus = rstat;
		msgId = head.getMessageId();
		System.out.println(" ResponseStatus: " + rstat );
		System.out.println(head.getTransactionId() + " " + sendStatus + " " + msgId );
		System.out.println(filename + " " + sendStatus + " " + msgId );
		System.exit(sendStatus);
	    }
	    System.out.flush();
	}
    }

    /**
     * Implementation of IWSPUpperLayer. NOT USED.
     * @param reason
     */
    public  void s_suspend_ind(short reason){}

    /**
     * Implementation of IWSPUpperLayer. NOT USED.
     */
    public  void s_resume_cnf(){}

    /**
     * Implementation of IWSPUpperLayer.
     * Called by the WAP stack if we are disconnected.
     * @param reason the reason, why we are disconnected
     */
    public  void s_disconnect_ind(short reason)
    {
	System.out.flush();
	System.out.println("disconnected from WAP gateway " + wapgw);
    }

    /**
     * Implementation of IWSPUpperLayer. NOT USED.
     * if we receive a redirect from the server.
     * @param redirectInfo
     */
    public  void s_disconnect_ind(Vector redirectInfo){
	System.out.println("disconnecting from WAP gateway " + wapgw);
	this.m.s_methodResult(null);
	s.s_disconnect();
    }

    public void s_disconnect_ind(InetAddress[] redirectInfo){
	System.out.println("---redirected to " + redirectInfo.toString());
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


    /**
     * It automatically connects to the WAP gateway.
     * After the connect.conf it sends the WAP POST.
     * After all it disconnects from the gateway.
     *
     * @param args none (ignored)
     */
    public static void main(String[] args)
    {
	try {
	    InetAddress gateway = InetAddress.getByName("YOUR_MMS_GATEWAY_HOST");

	    MBoxView poster = 
		new MBoxView(
			   gateway, 
			   9201);
	    poster.init();

	}
	catch (Exception ex) {
	    ex.printStackTrace();
	    System.exit(1);
	}
    }

}
