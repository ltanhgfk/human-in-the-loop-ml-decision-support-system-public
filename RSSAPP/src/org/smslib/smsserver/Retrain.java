package org.smslib.smsserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import vn.hus.nlp.tokenizer.VietTokenizer;

import libsvm.classify.CreateModel;
import libsvm.classify.StopWords;
import model.ChuyenMon;
import model.Config;
import model.MMS;

public class Retrain implements Runnable
{
	//declaring reader to read the keyboard and flag to notify main thread to stop when set  
    static BufferedReader in;    
    static boolean quit=false;  
      
    public void run()
    {  
        String msg = null;        
        while(true){// threading is waiting for the key Q to be pressed   
            try{  
            	msg=in.readLine();  
            }
            catch(IOException e)
            {  
            	e.printStackTrace();  
            }              
            if(msg.equals("q"))
            {
            	quit=true;
            	break;
            }
        }
    }
    
    public static void createAutoKeyWordList(String strword)
    {
    	try
		{
    		Config keyword = new Config();
    		keyword = keyword.getConfigBycode("autokeyword");
    		String fileString="";    		
    		if(keyword.getvaluebool()==true)
    		{
    			fileString = "./classifications/autokeywordlistTemp.txt";
    		}
    		else
    		{
    			fileString = "./classifications/keywordlistTemp.txt";
    		}
    		//Append vao keyfoundfile true
	    	BufferedWriter keyfoundFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileString,true), "UTF8"));	    	
			//ghi vao file nhung tu con lai
	    	keyfoundFile.append(strword);
			keyfoundFile.newLine();
			//System.out.println(keyArray.get(i));			
			keyfoundFile.close();
			StopWords st = new StopWords();
			st.removeDuplicatesLines(fileString);
		}
		catch (Exception e) 
	       {
	         e.printStackTrace();
	       }
    }
    
    public static void main(String args[]) throws UnknownHostException
    {
            in=new BufferedReader(new InputStreamReader(System.in));  
            Thread t1=new Thread(new Retrain());  	// creating a new thread to handle the input
            t1.start();
            System.out.println("Press q then Enter to terminate!");
            
            while(true)
            {            	
    	 		try{ 
    	 				System.out.println("Running!!!");
    	 				
    	            	MMS ms = new MMS();
    	            	MMS MMSInfo = new MMS();
    	            	Config cg = new Config();
    	            	Config total = new Config();
    	    			Config retrain = new Config();
    	    			total = cg.getConfigBycode("autoretrain");
    	    			retrain = cg.getConfigBycode("number_sms_retrain");	
    	            	cg = cg.getConfigBycode("systemconfig");
    	            	ArrayList<MMS> mmslist =  new ArrayList<MMS>();
    	            	Config keyword = new Config();
	        			keyword = keyword.getConfigBycode("autokeyword");
	        			
    	    			if(cg.getvaluechar().compareTo("semi-auto") == 0)
    	    			{
    	    				System.out.println("semi-auto config");    	    				
    	    				mmslist = ms.getMMSByClassifyHuman(true, true, false);//lay tin nhan chua duoc dung de huan luyen
    	    			}
    	    			else if(cg.getvaluechar().compareTo("auto") == 0)
    	    			{
    	    				System.out.println("auto config");  
    	    				mmslist = ms.getMMSByClassifyMachine(true, true, false);//lay tin nhan chua duoc dung de huan luyen
    	    			}

    	    			/**for test*****/
    	    			CreateModel cm = new CreateModel();
    	    			cm.trainLinearKernel();
    	    			/**for test*****/
    	    			/*/
    	    			if(mmslist.size()>=retrain.getvalueint() && total.getvaluebool()==true)
    	            	{
    	    				System.out.println("Size of new SMS: "+mmslist.size());
    	        			if(keyword.getvaluebool()==true)
    	        			{    	        				
    	        				//Tao file autokeywordlist.txt tu dong
    	        				StopWords sw =  new StopWords();
    	        				for (int i = 0; i < mmslist.size(); i++)
    	        	            {	
    	        					//System.out.print("");
    	        					createAutoKeyWordList(sw.removeStopWord(mmslist.get(i).getmsg()));
    	        					
    	        					//------Cap nhat used vao csdl-------------------//    	            		
    	    						MMSInfo.setid(mmslist.get(i).getid());
    	    			    		MMSInfo.setsender(mmslist.get(i).getsender());
    	    			    		MMSInfo.setmsg(mmslist.get(i).getmsg());
    	    			    		MMSInfo.setimage(mmslist.get(i).getimage());
    	    			    		MMSInfo.setreplymsg(mmslist.get(i).getreplymsg());
    	    			    		MMSInfo.setexpertid(mmslist.get(i).getexpertid());
    	    			    		MMSInfo.setmajoridByMachine(mmslist.get(i).getmajoridByMachine());
    	    			    		MMSInfo.setmajoridByHuman(mmslist.get(i).getmajoridByHuman());
    	    			    		MMSInfo.setanswered(mmslist.get(i).getanswered());
    	    			    		MMSInfo.setclassifiedByHuman(mmslist.get(i).getclassifiedByHuman());
    	    			    		MMSInfo.setclassifiedByMachine(mmslist.get(i).getclassifiedByMachine());
    	    			    		MMSInfo.setencoded(mmslist.get(i).getencoded());
    	    			    		MMSInfo.setreceivedMms(mmslist.get(i).getreceivedMms());
    	    			    		MMSInfo.setused(true);
    					    		MMSInfo.setsentexpert(mmslist.get(i).getsentexpert());
    	    			    		MMSInfo.setsentfarmer(mmslist.get(i).getsentfarmer());
    	    			    		MMSInfo.setreceivedDate(mmslist.get(i).getreceivedDate());	
    	    			    		MMSInfo.setansweredDate(mmslist.get(i).getansweredDate());
    	    			    		MMSInfo.setgatewayid(mmslist.get(i).getgatewayid());    	    			    		
    	    			    		
    	    			    		ms.updateUsedMMSById(MMSInfo);
    	    	            		//--ket thuc cap nhat-----------------------------//
    	        	            }
    	        				//xoa bo nhung tu trung lap trong file tu khoa autokeywordlist.txt
    	        				sw.removeDublicate();
    	        				CreateModel cm = new CreateModel();
        		                cm.createTrainingFile();
        		                cm.trainLinearKernel();
    	        			}
    	        			else
    	        			{
    	        				ChuyenMon mj = new ChuyenMon();
    	        				ArrayList<ChuyenMon> dscm = new ArrayList<ChuyenMon>();
    	        				dscm = mj.getAllMajor();
    	        				StopWords sw =  new StopWords();
    	        				for(int j= 0; j<dscm.size(); j++)
    	        				{
    	        					createAutoKeyWordList(sw.tokenString(dscm.get(j).getNote()));
    	        				}
    	        				sw.removeDublicate();
    	        				for (int i = 0; i < mmslist.size(); i++)
    	        	            {
    	        					//------Cap nhat used vao csdl-------------------//    	            		
    	    						MMSInfo.setid(mmslist.get(i).getid());
    	    			    		MMSInfo.setsender(mmslist.get(i).getsender());
    	    			    		MMSInfo.setmsg(mmslist.get(i).getmsg());
    	    			    		MMSInfo.setimage(mmslist.get(i).getimage());
    	    			    		MMSInfo.setreplymsg(mmslist.get(i).getreplymsg());
    	    			    		MMSInfo.setexpertid(mmslist.get(i).getexpertid());
    	    			    		MMSInfo.setmajoridByMachine(mmslist.get(i).getmajoridByMachine());
    	    			    		MMSInfo.setmajoridByHuman(mmslist.get(i).getmajoridByHuman());
    	    			    		MMSInfo.setanswered(mmslist.get(i).getanswered());
    	    			    		MMSInfo.setclassifiedByHuman(mmslist.get(i).getclassifiedByHuman());
    	    			    		MMSInfo.setclassifiedByMachine(mmslist.get(i).getclassifiedByMachine());
    	    			    		MMSInfo.setencoded(mmslist.get(i).getencoded());
    	    			    		MMSInfo.setreceivedMms(mmslist.get(i).getreceivedMms());
    	    			    		MMSInfo.setused(true);
    	    			    		MMSInfo.setsentexpert(mmslist.get(i).getsentexpert());
    	    			    		MMSInfo.setsentfarmer(mmslist.get(i).getsentfarmer());
    	    			    		MMSInfo.setreceivedDate(mmslist.get(i).getreceivedDate());	
    	    			    		MMSInfo.setansweredDate(mmslist.get(i).getansweredDate());
    	    			    		MMSInfo.setgatewayid(mmslist.get(i).getgatewayid());  
    	    			    		
    	    			    		ms.updateUsedMMSById(MMSInfo);
    	    	            		//--ket thuc cap nhat-----------------------------//    	    			    		
    	        	            }
    	        				CreateModel cm = new CreateModel();
        		                cm.createTrainingFile();
        		                cm.trainLinearKernel();
    	        			}
    	            	}/**/
    	            }
    	            catch(Exception e)
    	            {
    	                System.err.println(e.getMessage());
    	            }    	 		
    	 		
    	 		try{
    	 			Config sleeptime = new Config();
    	 			sleeptime = sleeptime.getConfigBycode("sleep_time_retrain");
    	 			//main-thread is sleeping
                	//Thread.sleep(sleeptime.getvalueint()*60*60*1000);	
    	 			Thread.sleep(5000);//gio = 60 * 60 giay
	                }
	                
    	 		catch(InterruptedException e)
	                {
	                	e.printStackTrace();
	                }
	                //outputting data until the quit boolean flag is not set  
    	 		if(quit==true) break;
            }
    }
}

/////////////////////////////////////////////////////////////////
/*public static int countLines(File filename) throws IOException 
{
    LineNumberReader reader = null;
    try {
        reader = new LineNumberReader(new FileReader(filename));
        while ((reader.readLine()) != null);
        return reader.getLineNumber();
    } catch (Exception ex) {
        return -1;
    } finally { 
        if(reader != null) 
            reader.close();
    }
}*/
