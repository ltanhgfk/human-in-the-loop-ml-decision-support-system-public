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
package mmslib.mms;

import java.util.*;

import model.ChuyenGia;
import model.ChuyenGiaChuyenMon;
import model.Gateway;
import model.MMS;
import net.sourceforge.jwap.wsp.*;
import java.net.*;
import java.nio.charset.Charset;
import java.io.*;
import net.sourceforge.jwap.util.*;
import java.lang.*;

import at.jku.soft.mms.lib.MMSPDU;
import com.nokia.mms.*;
import dao.DBAction;

public class MMSSender implements IWSPUpperLayer{

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
   
   private int mmsidforsend = -1;
   private String receiver = "";

   /**
    * Constructs a WAP sender object.
    *
    * @param wapgw the wap gateway to use
    * @param wapgwport the port of the wap gateway
    */
   public MMSSender(InetAddress wapgw, int wapgwport, String posturi,
                          byte[] dataToSend, String contentType, int mmsid, String receiver){
      this.bytes = dataToSend;
      this.posturi = posturi;
      this.wapgw = wapgw;
      this.wapgwport = wapgwport;
      this.contentType = contentType;
      this.mmsidforsend = mmsid;
      this.receiver = receiver;
      

      if ( bytes == null) {
    	  encode();
      }
      init();
   }

   /**
    * This method connects to the wap gateway and opens a WSP session.
    */
   private void init()
   {
     try
     {
        s = new CWSPSession(wapgw, wapgwport, this);
        System.out.println("connecting to WAP gateway " + wapgw);
        s.s_connect();
     }
     catch (Exception e)
     {
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

    private void encode() 
    {    	
    	System.out.println("mmsid: " + this.mmsidforsend);
    	
    	MMS mmsinfo = new MMS();
    	mmsinfo = mmsinfo.getMMSById(this.mmsidforsend);
    	
		//create the Multimedia Message
    	com.nokia.mms.MMMessage mm = new com.nokia.mms.MMMessage();
		
		mm.setVersion(IMMConstants.MMS_VERSION_10);
		mm.setMessageType(IMMConstants.MESSAGE_TYPE_M_SEND_REQ);
		mm.setTransactionId("250784");
		mm.setDate(new Date(System.currentTimeMillis()));
		mm.setFrom("YOUR_GATEWAY_NUMBER/TYPE=PLMN");//khong can vi khi chon gateway nao thi tu dong chon so thue bao cua gateway do
		mm.addToAddress("+"+this.receiver+"/TYPE=PLMN");
		System.out.println("number: " + "+"+this.receiver+"/TYPE=PLMN");
		/* khong duoc dung boi vi cac chuyen gia co the dung thue bao nha mang khac nhau
		 * ChuyenGia cg = new ChuyenGia();
		ChuyenGiaChuyenMon em = new ChuyenGiaChuyenMon();
		ArrayList<ChuyenGiaChuyenMon> aem = new ArrayList<ChuyenGiaChuyenMon>();
		aem = em.getExpertMajorByMajorId(mmsinfo.getmajoridByMachine());
		
		for(int j=0;j<aem.size();j++)
		{
			cg = cg.getExpertById(aem.get(j).getExpertId());
			//kiem tra xem chuyen gia da duoc chap nhan de tra loi cau hoi chua
			if(cg.getstatus())
			{
				//mm.addToAddress("+84"+cg.getmobile()+"/TYPE=PLMN");	//coi lai vi so dien thoai
				mm.addToAddress(cg.getmobile()+"/TYPE=PLMN");	//coi lai vi so dien thoai
			}
		}*/		

		mm.setSubject("Cau hoi ma so: "+ this.mmsidforsend);
		mm.setMessageClass(IMMConstants.MESSAGE_CLASS_PERSONAL);
		mm.setPriority(IMMConstants.PRIORITY_HIGH);
		mm.setContentType(IMMConstants.CT_APPLICATION_MULTIPART_MIXED);	
		
		//    In case of multipart related message and a smil presentation available
		//    mm.setContentType(IMMConstants.CT_APPLICATION_MULTIPART_RELATED);
		//    mm.setMultipartRelatedType(IMMConstants.CT_APPLICATION_SMIL);
		//    mm.setPresentationId("<A0>"); // where <A0> is the id of the content containing the SMIL presentation
		
		MMContent textpart = new MMContent();
		String msgStruct ="Gui chuyen gia tin nhan ma so: "+ mmsinfo.getid();
		if(mmsinfo.getmsg()!=null)
		{
			msgStruct = Integer.toString(mmsinfo.getid()) + "-" + mmsinfo.getmsg();
		}
		byte [] textbuf = msgStruct.getBytes(Charset.forName("UTF-8"));
		textpart.setContent(textbuf,0,textbuf.length);
		textpart.setContentId("<0>");
		textpart.setType(IMMConstants.CT_TEXT_PLAIN);
		mm.addContent(textpart);
		
		MMContent mediapart = new MMContent();
		byte [] mediabuf = readFile("../RSSWEB/WebContent/images/rices/"+mmsinfo.getimage());
		mediapart.setContent(mediabuf,0,mediabuf.length);
		mediapart.setContentId("<1>");
		mediapart.setType(IMMConstants.CT_IMAGE_JPEG);
		mm.addContent(mediapart);
	
		// instantiate an encoder object
		com.nokia.mms.MMEncoder encoder = new com.nokia.mms.MMEncoder();
		
		// set the message to be encoded
		encoder.setMessage(mm);
		
		// encode the message
		try {
		    encoder.encodeMessage();
		}
		catch (MMEncoderException e) 
		{
		    System.err.println("An error occurred encoding the message.");
		    e.printStackTrace();
		    System.exit(1);
		}
		
		bytes=encoder.getMessage();
		System.out.println("encoded");
    }

   /**
   * Implementation of IWSPUpperLayer.
   * Is called by the WAP stack after being connected.
   * We then send the Message in a POST.
   */
   public  void s_connect_cnf()
   {
      try
      {
         System.out.println("connected to WAP gateway " + wapgw);

         System.out.println("sending data (WSP POST)");
         m = s.s_post(bytes,contentType,posturi);

      }
      catch (Exception e )
      {
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
   public void s_methodResult_ind(byte[] payload,String contentType,boolean moreData)
   {
      // decode the response here...

      String nl = System.getProperty("line.separator");
      
      System.out.println("Response More Data: " +  moreData );
      System.out.println("Response Content Type: " +  contentType );

     
      saveToFile("./mms/"+System.currentTimeMillis() +".pdu", payload); 
    
      if (!moreData) {
	  System.out.println("disconnecting from WAP gateway " + wapgw);
	  m.s_methodResult(null);
	  s.s_disconnect();

	  MMSPDU pdu = new MMSPDU();
	  
	  pdu.setPayload(payload);
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
  /*
  public static void main(String[] args)
    {
	try {
	    InetAddress gateway = InetAddress.getByName("YOUR_MMS_GATEWAY_HOST");

	    MMSSender poster = 
		new MMSSender(
			   gateway, 
			   9201, 
			   "http://203.162.21.114/mmsc",
			   null,
			   "application/vnd.wap.mms-message",112);
	}
	catch (Exception ex) {
	    ex.printStackTrace();
	}
   }*/

}
