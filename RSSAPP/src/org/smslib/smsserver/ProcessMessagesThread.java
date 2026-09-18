package org.smslib.smsserver;

import java.io.*;  
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.Normalizer;
import java.util.ArrayList;

import libsvm.classify.ClassifySms;
import libsvm.classify.Vectorize;
import mmslib.mms.GatewayConfig;
import mmslib.mms.HexStringConverter;
import mmslib.mms.MmsGetter;
import model.*;


public class ProcessMessagesThread extends Thread
{
    public int mmsid = -1;    
    private static boolean lopend = true;
    
    public void setloop(boolean loop)
    {
    	lopend = loop;
    }
    
    public boolean getloop()
    {
    	return lopend;
    }
    
    //@Override
    public void run()
    {
    	int i=0;
        while(lopend)
        {
        	i++;
            try{
                System.out.println("Rabbit running..... " + i);
                processSMS();
                
                sleep(2000);
            	Config sleeptime = new Config();
    			sleeptime = sleeptime.getConfigBycode("sleep_time_classify");                
    			//sleep(sleeptime.getvalueint()*60*1000);
            }
            catch(Exception e)
            {
                System.err.println(e.getMessage());
            }
        }
    }
    
    public void processSMS()
    {
    	System.out.println("Processing SMS....");
            try{
            	
	            MMS mms = new MMS();
	            ArrayList<MMS> mmslist = new ArrayList<MMS>();
	            mmslist = mms.getMMSByClassifyMachine(false, false, false);
	           
	            if(mmslist.size()>=1)
	            {
	            	System.out.println("size: " + mmslist.size()); 
		            for(MMS row : mmslist)
		        	{
		            	ProcessMessagesThread main = new ProcessMessagesThread();
		            	main.mmsid = row.getid();
		            	MMS MMSInfo = new MMS();		            	
		            	
		            	boolean containblank = row.getmsg().contains(" ");

		            	if(row.getencoded().compareTo("8")==0 && row.getreceivedMms()==false && containblank==false && row.getmsg().length()>=150)
		            	{	//vao day neu la tin nhan mms notification
		            		System.out.println("To be SMS notification!"); 
		            		String mmsc = getMMSCAddress(row.getmsg());
		            		
		            		String gatewayip = getGateway(row.getsender());
		            		InetAddress gateway = InetAddress.getByName(gatewayip);
		            		
		            		int port = getPort(row.getsender());
		            		
		            		if(mmsc.contains("http://") && port >=0 && gateway != null)
		            		{ 		            			
		            			GatewayConfig gc = new GatewayConfig();
		            			String[] defaultgateway = gc.getDefaultGateway("route print");
		            			if(defaultgateway[0] == gatewayip)
		            			{
		            				MmsGetter getter = new MmsGetter(gateway, port, mmsc);
		            			}
		            			else
		            			{
		            				//co the phai delete default gw cu roi moi add cai moi vo
		            				int time =0;
		            				String commandAdd = "route add 0.0.0.0 mask 0.0.0.0 " + gatewayip;
		            				while(gc.execCommand(commandAdd) == false && time<10)
		            				{
		            					gc.execCommand(commandAdd);
		            					time ++;
		            				}
		            				time=0;
		            				String commandDelete = "route delete 0.0.0.0 mask 0.0.0.0 " + defaultgateway[0];
		            				while(gc.execCommand(commandDelete) == false && time<10)
		            				{
		            					gc.execCommand(commandDelete);
		            					time++;
		            				}
		            				MmsGetter getter = new MmsGetter(gateway, port, mmsc);
		            			}
		            		}
		            	}
		            	else
		            	{
		            		System.out.println("To be SMS need to be classified!"); 
		            		Vectorize vt = new Vectorize();
	            			String vector = vt.CreateVector(row.getmsg());
	            			if(vector.compareTo("")!=0)
	            			{
	            				System.out.println("To be not Spam!");
		            			BufferedWriter vectorFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("./classifications/sms.vector"), "UTF-8"));
		            			vectorFile.write("-1 " + vector.trim());
		            			vectorFile.close();
		            			
		            			//phan loai tin nhan sms
		            			ClassifySms cs = new ClassifySms();
		            			String classid = cs.classify("./classifications/sms.vector");
		            			
		            			//luu vao co so du lieu
		            			MMSInfo.setid(row.getid());
					    		MMSInfo.setsender(row.getsender());
					    		MMSInfo.setmsg(row.getmsg());
					    		MMSInfo.setimage(row.getimage());
					    		MMSInfo.setreplymsg(row.getreplymsg());
					    		MMSInfo.setexpertid(row.getexpertid());

					    		ChuyenMon mj = new ChuyenMon();
					    		mj = mj.getMajorByClass(classid);
					    		MMSInfo.setmajoridByMachine(mj.getId());
					    		
					    		MMSInfo.setmajoridByHuman(row.getmajoridByHuman());
					    		MMSInfo.setanswered(row.getanswered());
					    		MMSInfo.setclassifiedByHuman(row.getclassifiedByHuman());
					    		MMSInfo.setclassifiedByMachine(true);
					    		MMSInfo.setencoded(row.getencoded());
					    		MMSInfo.setreceivedMms(row.getreceivedMms());
					    		MMSInfo.setused(row.getused());
					    		
					    		mms.updateMMSById(MMSInfo);
					    		
					    		/*/---------------------------------------//
					    		String subjectString = row.getmsg();
					    		subjectString = Normalizer.normalize(subjectString, Normalizer.Form.NFD);
								String resultString = subjectString.replaceAll("[^\\x00-\\x7F]", "");
								MMSInfo.setmsg(resultString);
								MMSInfo.setclassifiedByMachine(true);
								MMSInfo.setanswered(true);
								if(resultString.compareTo(row.getmsg())!=0)
								{
									mms.addMMS(MMSInfo);
								}
					    		//---------------------------------------/*/
	            			}
	            			else
	            			{	
	            				System.out.println("To be Spam!");
	            						            			
		            			MMSInfo.setid(row.getid());
					    		MMSInfo.setsender(row.getsender());
					    		MMSInfo.setmsg(row.getmsg());
					    		MMSInfo.setimage(row.getimage());
					    		MMSInfo.setreplymsg(row.getreplymsg());
					    		MMSInfo.setexpertid(row.getexpertid());

					    		ChuyenMon mj = new ChuyenMon();
					    		mj = mj.getMajorByClass("7");//coi lai cai nay
					    		MMSInfo.setmajoridByMachine(mj.getId());
					    		
					    		MMSInfo.setmajoridByHuman(row.getmajoridByHuman());
					    		MMSInfo.setanswered(row.getanswered());
					    		MMSInfo.setclassifiedByHuman(row.getclassifiedByHuman());
					    		MMSInfo.setclassifiedByMachine(true);
					    		MMSInfo.setencoded(row.getencoded());
					    		MMSInfo.setreceivedMms(row.getreceivedMms());
					    		MMSInfo.setused(row.getused());
					    		
					    		mms.updateMMSById(MMSInfo);
	            			}
		            	}
		        	}
	            }
            }
            catch(Exception e)
            {
            	e.printStackTrace();
            }            
        }   
    
    private static String getMMSCAddress(String msg)
    {
    	String MMSCAddress="";
    	try
    	{
    		String resultinString = HexStringConverter.getHexStringConverterInstance().hexToString(msg).trim();
    		int pos = resultinString.indexOf("http://");
    		boolean contain = resultinString.contains("http://");
    		if(pos>=0 && contain==true)
    		{
	    		MMSCAddress=resultinString.substring(pos).trim();
    		}
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	return MMSCAddress;
    }
    
    private static String getGateway(String sender)
    {
    	String gateway="";
    	try{	    	
	    	Gateway gw = new Gateway();
	    	gw = gw.getGatewayByMmscnumber(sender);
	    	gateway = gw.getgatewayip();	    
    	}
    	catch(Exception e)
        {
        	e.printStackTrace();
        }
    	return gateway;
    }
    
    private static String getOperator(String sender)
    {
    	String operator="";
    	try{	    	
	    	Gateway gw = new Gateway();
	    	gw = gw.getGatewayByMmscnumber(sender);
	    	operator = gw.getoperator();	    
    	}
    	catch(Exception e)
        {
        	e.printStackTrace();
        }
    	return operator;
    }

    private static int getPort(String sender)
    {
    	int port=0;
    	try{	    	
	    	Gateway gw = new Gateway();
	    	gw = gw.getGatewayByMmscnumber(sender);
	    	port = gw.getport();	    
    	}
    	catch(Exception e)
        {
        	e.printStackTrace();
        }
    	return port;
    }
}