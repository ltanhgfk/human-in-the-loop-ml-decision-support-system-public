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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import net.sourceforge.jwap.wsp.*;
import java.net.*;
import java.io.*;
import org.apache.log4j.*;
import model.*;
import mmslib.mms.MMDecoder;
import mmslib.mms.MMMessage;


public class MmsGetter implements IWSPUpperLayer
{
    protected static Logger logger = Logger.getLogger(MmsGetter.class);
    /**
     * The actual WSP Session.
     * You need one instance of CWSPSession for each WAP session.
     */
    private CWSPSession session;
    /**
     * the URI we would like to get in this example
     */
    private String uriToGet;
    private int mmsidforget = -1;
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
    public MmsGetter(InetAddress wapGwAddress,int wapGwPort,String uriToGet, int mmsid)  
    {		
		this.uriToGet = uriToGet;	
		this.mmsidforget = mmsid;
		try{
	    if ( ! uriToGet.startsWith("http://")) {
			File f = new File(uriToGet);
			long len = f.length();
			byte b[] = new byte[(int)len];
			FileInputStream fi = new FileInputStream(f);
			fi.read(b);
			decode(b);
			return;
	    }	    
	    session = new CWSPSession
	    			 (wapGwAddress, // URI of WAP gateway
				      wapGwPort,    // port of wap gateway
				      this,         // WE would like to be informed of occuring events
				      true);        // be verbose
	    System.out.println("Connecting to WAP-Gateway");
	    session.s_connect();
		} catch (Exception e){
		    // UDP Socket problem
		    e.printStackTrace();
		}
    }
    
    private void saveToFile(String filename, byte[]b)
    {
		try{
		    FileOutputStream fo;
		    fo = new FileOutputStream(filename);
		    fo.write(b);
		    fo.close();
		} catch (Exception e){		    
		    e.printStackTrace();
		    System.exit(1);
		}
    }

    //------------------ from here on we implement IWSPUpperLayer ---------------
    /**
     * called by jwap stack, when we are connectedwith the WAP gateway
     */
    public void s_connect_cnf()
    {
		System.out.println("connected to WAP gateway");
		System.out.println("GET " + uriToGet);
		invokedMethods.addElement(session.s_get(uriToGet));
    }

    /**
     * called by jwap stack, to announce a response to a GET/POST method invocation
     *
     * @param payload
     * @param contentType
     * @param moreData
     */
    public void s_methodResult_ind(byte[] payload, String contentType, boolean moreData) 
    {
		System.out.println("Get Response!!!");
		System.out.println("Content Type: "+ contentType);
		System.out.println("More Data: "+ moreData);
		System.out.println("Payload length: "+ payload.length + " bytes");
		
		saveToFile("mms/"+ System.currentTimeMillis() +".mms", payload);
		decode(payload);
		
		// if we do not need any more invocations of a GET/POST methode, we will
		// disconnect from WAP gateway
		System.out.println("\n\nDisconnecting from WAP-Gateway");
		if (!moreData)
		    session.s_disconnect();
	}

    public void decode(byte[] buf)
    {
		MMDecoder mmDecoder = new MMDecoder();
		MMMessage mm = new MMMessage();

		try {
			mm = mmDecoder.decode(buf);
		}
		catch (Exception e)
		{
		    System.err.println(e.getMessage());
		    e.printStackTrace();
		}
		// read the message type
		if (mm.getMessageType()==MMConstants.MESSAGE_TYPE_M_SEND_REQ) 
		{
		    System.out.println(" Message Type: m-send-req");
		} 
		if (mm.getMessageType()==MMConstants.MESSAGE_TYPE_M_RETRIEVE_CONF) 
		{
		    System.out.println(" Message Type: m-retrieve-conf");
		} 
		// read the transaction ID
		System.out.println(" Transaction Id: "+mm.getTransactionId());
	
		// read the version
		System.out.println(" Version: "+mm.getVersionStr());
	
		// read the recipients of the Multimedia Message
		if (mm.isToAvailable()) 
		{
		    ArrayList list=mm.getTo();
		    for (int n=0; n<list.size(); n++) 
		    {		
		    	System.out.println(" To : "+list.get(n));
		    }
		} else {
		    System.out.println("To no one!");
		}
		// read the date of the Multimedia Message
		if (mm.isDateAvailable()) 
		{
		    System.out.println(" Date: "+mm.getDate());
		}	
		// read the sender of the Multimedia Message
		if (mm.isFromAvailable()) 
		{
		   String from = mm.getFrom();
		   System.out.println(" From: "+from);
		}	
		// read the subject of the Multimedia Message
		if (mm.isSubjectAvailable()) 
		{
		    System.out.println(" Subject: "+mm.getSubject());
		}	
		// read the content type of the Multimedia Message//Multipart.Related or Mixed
		if (mm.isContentTypeAvailable())
		{
		   System.out.println(" Content Type: "+mm.getContentType());
		}	
		// read the kind of the presentation type
		if (mm.isMessageTypeAvailable())
		{
		    System.out.println(" Message Type: "+mm.getMessageTypeStr());//.getMessageType());
		    System.out.println(" Number of contents: "+mm.getPartCount());//.getMessageType());
		}
		
		//read the Multimedia Message entries and store them in files		
		MMS mms = new MMS();
		try {
			for (int j=0; j <mm.getPartCount(); j++)
			{			
				System.out.println("mmsid: " + this.mmsidforget);				
				
				MMS Info = new MMS();
				Info=mms.getMMSById(this.mmsidforget);
				
				mms.setid(this.mmsidforget);
				mms.setsender(mm.getFrom().substring(0,mm.getFrom().indexOf("/")).replace("+", ""));
				
	    		mms.setmsg(Info.getmsg());
	    		mms.setimage(Info.getimage());
	    		mms.setreplymsg(Info.getreplymsg());			        			    		
	    		mms.setexpertid(Info.getexpertid());			        			    		
	    		mms.setmajoridByMachine(Info.getmajoridByMachine());
	    		mms.setmajoridByHuman(Info.getmajoridByHuman());			        			    		
	    		mms.setanswered(Info.getanswered());
	    		mms.setclassifiedByHuman(Info.getclassifiedByHuman());
	    		mms.setclassifiedByMachine(Info.getclassifiedByMachine());
	    		mms.setencoded(Info.getencoded());
	    		mms.setreceivedMms(Info.getreceivedMms());
	    		mms.setused(Info.getused());	    					        			    		
	    		mms.setsentexpert(Info.getsentexpert());
	    		mms.setsentfarmer(Info.getsentfarmer());
	    		mms.setgatewayid(Info.getgatewayid());
	    		mms.setansweredDate(Info.getreceivedDate());
	    		mms.setreceivedDate(Info.getreceivedDate());
	    		//DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	    		//Date date = new Date();
	    		//mms.setreceivedDate(dateFormat.format(date));

				System.out.println(  " Content header value --> "+mm.getPart(j).headerValues);
				System.out.println(  " Content part length  --> "+mm.getPart(j).getContent().length);			
				
			    if (mm.getPart(j).getContentType().contains("text/plain"))
			    {
			    	System.out.println("Content part type text   --> "+mm.getPart(j).getContentType());	    	
			    	String s = new String(mm.getPart(j).getContent());//Chuyen tu kieu byte[] sang String
			    	mms.setmsg(s);
			    	mms.setreceivedMms(true);
			    }
			    else if (mm.getPart(j).getContentType().contains("image/jpeg")) 
			    {
					System.out.println("Content part type jpeg   --> "+mm.getPart(j).getContentType());
					String filename = System.currentTimeMillis() +".jpg";
					saveToFile("../RSSWEB/WebContent/images/rices/"+filename,mm.getPart(j).getContent());
					mms.setimage(filename);
					mms.setreceivedMms(true);
				}
			    else if (mm.getPart(j).getContentType().contains("image/gif")) 
			    {
					System.out.println("Content part type gif   --> "+mm.getPart(j).getContentType());
					String filename = System.currentTimeMillis() +".gif";
					saveToFile("../RSSWEB/WebContent/images/rices/"+filename,mm.getPart(j).getContent());
					mms.setimage(filename);
					mms.setreceivedMms(true);
				}
				else if (mm.getPart(j).getContentType().contains("application/smil"))
				{
					System.out.println("Content part type smil   --> "+mm.getPart(j).getContentType());
					saveToFile("mms/smil_file.smil",mm.getPart(j).getContent());
				}
				else if (mm.getPart(j).getContentType().contains("audio/wav"))
				{
					System.out.println("Content part type audio   --> "+mm.getPart(j).getContentType());
					saveToFile("../RSSWEB/WebContent/images/rices/audio.wav",mm.getPart(j).getContent());
				}
				else
				{
					System.out.println("Content part type else    --> "+mm.getPart(j).getContentType());
					String filename = "mms/" + System.currentTimeMillis() +".mms";
					saveToFile(filename,mm.getPart(j).getContent());
					mms.setimage(filename);
					mms.setreceivedMms(true);
				}
			    mms.setreceivedMms(true);
			    
				if(mms.updateMMSById(mms)!=0)
				{
					System.out.println("Get MMS Successful!");
				}
				else
				{
					System.out.println("Get MMS Unsuccessful!");
				}
			}
		}
		catch(Exception e) {e.printStackTrace();}
    }  

    /**
     * will be incoked by jwap stack, when we are disconnected from the WAP gateway
     * @param reason
     */
    public void s_disconnect_ind(short reason)
    {
    	System.out.println("Disconnected.");
    }

    /**
     * will be invoked by jwap stack, when we are disconnected by the WAP gateway
     * because it is redirected.
     *
     * @param redirectInfo
     */
    public void s_disconnect_ind(InetAddress[] redirectInfo) 
    {
    	System.out.println("WTP session  disc_ind");
    }

    /**
     * invoked ba the jwap stack to show, that the session is suspended
     * @param reason
     */
    public void s_suspend_ind(short reason) 
    {
    	System.out.println("WTP session susp_ind");
    }

    /**
     * invoked, when a suspended session will be resumed
     */
    public void s_resume_cnf() 
    {
    	System.out.println("WTP session resume_cnf");
    }
    
    /*
    public static void main(String[] args) {
    	try{	    
		    InetAddress gateway = InetAddress.getByName("YOUR_MMS_GATEWAY_HOST");
		    String addr="http://YOUR_MMS_GATEWAY_HOST:8080/xcDDy0";
		    if (args.length>0) addr = args[0];
		    MmsGetter getter = new MmsGetter(gateway, 9201, addr);
		} catch (UnknownHostException e){	    
		    e.printStackTrace();// Host address not ok
		}
    }
    */
}
