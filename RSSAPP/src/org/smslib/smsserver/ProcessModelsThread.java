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

public class ProcessModelsThread extends Thread
{	
	public static boolean lopend = true;
	
	public void setloop(boolean loop)
    {
    	lopend = loop;
    }
    
    public boolean getloop()
    {
    	return lopend;
    }
    
    public static void createAutoKeyWordList(String strword)
    {
    	try
		{
    		//Append vao keyfoundfile true
	    	BufferedWriter keyfoundFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("./classifications/autokeywordlistTemp.txt",true), "UTF8"));
			//ghi vao file nhung tu con lai
	    	keyfoundFile.append(strword);
			keyfoundFile.newLine();
			//System.out.println(keyArray.get(i));			
			keyfoundFile.close();
		}
		catch (Exception e) 
	       {
	         e.printStackTrace();
	       }
    }
    
    public static int countLines(File filename) throws IOException 
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
	}
    
    //@Override
    public void run()
    {
    	int i=0;    	
        while(lopend)
        {
        	i++;
            try{
                System.out.println("Cat running..... " + i);
                processModel();
                sleep(3000);
            	Config sleeptime = new Config();
    			sleeptime = sleeptime.getConfigBycode("sleep_time_retrain");                
    			//sleep(sleeptime.getvalueint()*60*1000);
            }
            catch(Exception e)
            {
                System.err.println(e.getMessage());
            }
        }
    }
    
    public void processModel()
    {
    	System.out.println("Processing Models....");
    	 		try{    	 				
    	            	MMS ms = new MMS();
    	            	MMS MMSInfo = new MMS();
    	            	Config cg = new Config();
    	            	Config total = new Config();
    	    			Config retrain = new Config();
    	    			total = cg.getConfigBycode("number_sms_need");
    	    			retrain = cg.getConfigBycode("number_sms_retrain");	
    	            	cg = cg.getConfigBycode("systemconfig");
    	            	ArrayList<MMS> mmslist =  new ArrayList<MMS>();
    	            	Config keyword = new Config();
	        			keyword = keyword.getConfigBycode("autokeyword");
	        			String stringfile = "";
	        			if(keyword.getvaluebool()==true)
	        			{ 
	        				stringfile = "./classifications/autosms.train";	        				
	        			}
	        			else
	        			{
	        				stringfile = "./classifications/sms.train";
	        			}
	        			File file =new File(stringfile);
	        			
    	    			if(cg.getvaluechar().compareTo("semi-auto") == 0)
    	    			{
    	    				//System.out.println("auto semi");
    	    				mmslist = ms.getMMSByClassifyHuman(true, true, false);//lay tin nhan chua duoc dung de huan luyen
    	    			}
    	    			else if(cg.getvaluechar().compareTo("auto") == 0)
    	    			{
    	    				//System.out.println("auto");
    	    				mmslist = ms.getMMSByClassifyMachine(true, true, false);//lay tin nhan chua duoc dung de huan luyen
    	    			}
    	    			
    	            	if(mmslist.size()>=retrain.getvalueint() && countLines(file)<total.getvalueint())
    	            	{    	            		        			
    	        			if(keyword.getvaluebool()==true)
    	        			{    	        				
    	        				/*/Tao file autokeywordlist.txt tu dong
    	        				StopWords sw =  new StopWords();
    	        				for (int i = 0; i < mmslist.size(); i++)
    	        	            {			
    	        					createAutoKeyWordList(sw.removeStopWord(mmslist.get(i).getmsg()));
    	        					
    	        					//------Cập nhật used vào csdl-------------------//    	            		
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
    	    			    		ms.updateUsedMMSById(MMSInfo);
    	    	            		//--ket thuc cap nhat-----------------------------//
    	        	            }
    	        				//xoa bo nhung tu lap lai trong file tu khoa autokeywordlist.txt
    	        				sw.removeDublicate();*/
    	        				CreateModel cm = new CreateModel();
        		                //cm.createTrainingFile();
        		                cm.trainLinearKernel();
    	        			}
    	        			else
    	        			{
    	        				for (int i = 0; i < mmslist.size(); i++)
    	        	            {
    	        					//------Cập nhật used vào csdl-------------------//    	            		
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
    	    			    		ms.updateMMSById(MMSInfo);
    	    	            		//--ket thuc cap nhat-----------------------------//    	    			    		
    	        	            }
    	        				CreateModel cm = new CreateModel();
        		                cm.createTrainingFile();
        		                cm.trainLinearKernel();
    	        			}
    	            	}
    	            }
    	            catch(Exception e)
    	            {
    	                System.err.println(e.getMessage());
    	            }
            }
}
