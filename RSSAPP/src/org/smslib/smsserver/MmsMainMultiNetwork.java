package org.smslib.smsserver;

import java.io.*;  
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.Normalizer;
import java.util.ArrayList;

import mmslib.mms.MMSSender;

import dao.DBAction;

import libsvm.classify.ClassifySms;
import libsvm.classify.Vectorize;
import mmslib.mms.GatewayConfig;
import mmslib.mms.HexStringConverter;
import mmslib.mms.MmsGetter;
import model.*;


public class MmsMainMultiNetwork implements Runnable
{
    //declaring reader to read the keyboard and flag to notify main thread to stop when set
    //public static int mmsid = -111;
	static BufferedReader in;
    static boolean quit=false;
    
    public static void main(String args[]) throws UnknownHostException
    {
        in=new BufferedReader(new InputStreamReader(System.in));  
        Thread t1=new Thread(new MmsMainMultiNetwork());  	// creating a new thread to handle the input
        t1.start();          
        System.out.println("Press q then Enter to terminate!");         
        //int i=0;        
        DBAction db;    
        ChuyenGia cg = new ChuyenGia();
        
        while(true)
        {          	
        	//i++;
            try{
            	
	            MMS mms = new MMS();
	            ArrayList<MMS> mmslist = new ArrayList<MMS>();
	            mmslist = mms.getMMSByClassifyMachine(false, false, false);//lay danh sach tin nhan chua duoc phan loai boi may tinh va chua duoc tra loi, chua su dung
	           
	            if(mmslist.size()>=1)
	            {
	            	//System.out.println("size: " + mmslist.size()); 
		            for(MMS row : mmslist)
		        	{
		            	MMS MMSInfo = new MMS();
		            	
		            	boolean containblank = row.getmsg().contains(" ");		            	
		            	
		            	if(row.getencoded().compareTo("8")==0 && row.getreceivedMms()==false && containblank==false && row.getmsg().length()>=150)//kiem tra tin nhan co phai la MMS khong
		            	{//neu la tin nhan MMS thi lay thong tin gateway de Get tin nhan ve
		            		
		            		System.out.println("MMS!"); 
		            		
		            		String mmsc = getMMSCAddress(row.getmsg());
		            		String gatewayip = getGateway(row.getsender());
		            		InetAddress gateway = InetAddress.getByName(gatewayip);		            		
		            		int port = getPort(row.getsender());
		            		String operator = getOperator(row.getsender());
		            		//System.out.println("All infomation out: " + mmsc +" port: " + port + " gateway: "+ gatewayip);
		            		
		            		if(mmsc.contains("http://") && port >=0 && gateway != null)
		            		{//neu tin nhan co chua dia chi MMS tren MMSC thi get MMS ve
		            			
		            			//Set Metric lower cho mang dang dung de ket noi GPRS
		            			if(operator=="VNPT")
		            			{
		            				GatewayConfig gc = new GatewayConfig();
		            				if(gc.setMetric("Mobile Broadband Connection 2", 10, "Mobile Connect - 3G Network Card #2"))
		            				{
		            					new MmsGetter(gateway, port, mmsc, row.getid());
		            					gc.setMetric("Mobile Broadband Connection 2", 300, "Mobile Connect - 3G Network Card #2");
		            				}
		            			}
		            			else if(operator=="Viettel")
		            			{
		            				GatewayConfig gc = new GatewayConfig();
		            				if(gc.setMetric("Viettel Mobile d-com 3g", 10, "Mobile Connect - 3G Network Card #3"))
		            				{
		            					new MmsGetter(gateway, port, mmsc, row.getid());
		            					gc.setMetric("Viettel Mobile d-com 3g", 300, "Mobile Connect - 3G Network Card #3");
		            				}
		            			}
		            		}
		            		else//Neu khong co => tin nhan mms loi => xoa bo
		            		{
		            			if(mms.deleteMMSById(row.getid())!=0)
		            			{		            			
		            				System.out.println("MMS Error, MMS deleted!");
		            			}
		            			else System.out.println("MMS Error, MMS not deleted!");
		            		}
		            	}
		            	else
		            	{//neu khong phai tin nhan thong bao (MMS notification) thi thuc hien kiem tra the loai tin nhan (cua chuyen gia hay cua nha nong)
		            		
		            		System.out.println("SMS to be classified!");
		            		            		
		            		ArrayList<ChuyenGia> expertlist = new ArrayList<ChuyenGia>();	            		
		            		expertlist = cg.getAllExpert();
		            		int k=0;
            				int posbegin = row.getmsg().indexOf("-")+1;
            				int posend = row.getmsg().indexOf(":");
            				
            				if(row.getmsg().substring(0, 6).trim().compareTo("answer")==0 && posbegin >=1 && posend >=1)//Kiem tra la cau hoi cua nha nong hay cau tra loi cua chuyen gia
            				{//neu la cau tra loi cua chuyen gia thi duyet danh sach chuyen gia de kiem tra
            					
			            		for (int j=0;j<expertlist.size();j++)
			            		{
			            			if(row.getsender().replaceFirst("84", "0").compareTo(expertlist.get(j).getmobile())==0)
				            		{//kiem tra so dien thoai nguoi gui co phai cua chuyen gia khong
			            			
			            				System.out.print("Expert number!");
				            			k++;//neu la cua chuyen gia thi tang k len 1
				            		}
			            			System.out.print("Not expert numbe!");
			            		}
            				}
		            		if(k>=1)
		            		{//Neu day la tin nhan tra loi cua chuyen gia thi cap nhat tra loi vao csdl
		            				         				
		            				int id = Integer.parseInt(row.getmsg().substring(posbegin, posend).trim().replace(" ", ""));
		            				
		            				String msg = row.getmsg().substring(posend).trim();
		            				
		            				MMS Info = new MMS();
		            				Info=mms.getMMSById(id);//kiem tra xem tin nhan voi id nay da duoc tra loi chua
		            				ChuyenGia exm = new ChuyenGia();
		            				exm = cg.getExpertByMobile(row.getsender());//get chuyen gia boi so dien thoai cua chuyen gia
		            				
		            				if(msg.length()>=10 && id>=1 && Info.getanswered()==false && exm.getstatus() == true)
		            				{//neu chieu dai tin nhan tra loi >=10, chua duoc tra loi va chuyen gia co quyen tra loi thi cap nhat vao csdl, sau do xoa tin nhan nay di
		            					
		        						MMSInfo.setid(id);
		        			    		MMSInfo.setsender(Info.getsender());
		        			    		MMSInfo.setmsg(Info.getmsg());
		        			    		MMSInfo.setimage(Info.getimage());
		        			    		MMSInfo.setreplymsg(msg);
		        			    		
		        			    		MMSInfo.setexpertid(exm.getid());
		        			    		
		        			    		MMSInfo.setmajoridByMachine(Info.getmajoridByMachine());
		        			    		MMSInfo.setmajoridByHuman(Info.getmajoridByHuman());
		        			    		
		        			    		MMSInfo.setanswered(true);
		        			    		MMSInfo.setclassifiedByHuman(Info.getclassifiedByHuman());
		        			    		MMSInfo.setclassifiedByMachine(Info.getclassifiedByMachine());
		        			    		MMSInfo.setencoded(Info.getencoded());
		        			    		MMSInfo.setreceivedMms(Info.getreceivedMms());
		        			    		MMSInfo.setused(Info.getused());
		        			    		MMSInfo.setreceivedDate(Info.getreceivedDate());
		        			    		MMSInfo.setansweredDate(Info.getansweredDate());
		        			    		
		        						if(mms.updateMMSById(MMSInfo)!=0)
		        						{//neu cap nhat thanh cong thi ghi cau tra loi vao bang tbl_sms_out de gui cau tra loi cho nha nong
		        							
		        							db = new DBAction();
		        							int kq = 0;
		    								int flag = 1;
		    								while (kq == 0 && flag != 9)
		    								{	
		    									kq = db.executeInsertStatments("INSERT INTO tbl_sms_out(type,recipient,text,status_report,gateway_id) " +
		        									"VALUES('O','" + Info.getsender() + "','" + msg + "',0,'"+ Info.getgatewayid() +"')");
		    									flag++;
		    								}
		        							if(kq!=0)
		        							{//Neu gui cau tra loi cho nha nong thanh cong thi xoa tin nhan tra loi sau khi xu ly
		        								mms.deleteMMSById(row.getid());
		        							}
		        							
		        							//Chuyen tin nhan sms hop le thanh tin nhan sms khong dau tieng viet luu csdl for training
		        							String subjectString = Info.getmsg();
		        							subjectString = Normalizer.normalize(subjectString, Normalizer.Form.NFD);
		        							String resultString = subjectString.replaceAll("[^\\x00-\\x7F]", "");
		        							MMSInfo.setmsg(resultString);
		        							if(resultString.compareTo(Info.getmsg())!=0)
		        							{
		        								mms.addMMS(MMSInfo);
		        							}
		        							
		        						}//end if mms.updateMMSById
		        						else 
		        						{//neu cap nhat khong thanh cong thi lam lai them 1 lan nua, neu kg duoc ua thi thui
		        							
		        							if(mms.updateMMSById(MMSInfo)!=0)
			        						{//neu cap nhat thanh cong thi ghi cau tra loi vao bang tbl_sms_out de gui cau tra loi cho nha nong
			        							
			        							db = new DBAction();
			        							int kq = 0;
			    								int flag = 1;
			    								while (kq == 0 && flag != 9)
			    								{	
			    									kq = db.executeInsertStatments("INSERT INTO tbl_sms_out(type,recipient,text,status_report,gateway_id) " +
			        									"VALUES('O','" + Info.getsender() + "','" + msg + "',0,'"+ Info.getgatewayid() +"')");
			    									flag++;
			    								}
			        							if(kq!=0)
			        							{//Neu gui cau tra loi cho nha nong thanh cong thi xoa tin nhan tra loi sau khi xu ly
			        								mms.deleteMMSById(row.getid());
			        							}
			        							
			        							//Chuyen tin nhan sms hop le thanh tin nhan sms khong dau tieng viet luu csdl for training
			        							String subjectString = Info.getmsg().toLowerCase();			        							    							
			        							subjectString = subjectString.replaceAll("đ","d");			        							
			        							subjectString = Normalizer.normalize(subjectString, Normalizer.Form.NFD);
			        							String resultString = subjectString.replaceAll("[^\\x00-\\x7F]", "");
			        							MMSInfo.setmsg(resultString);
			        							
			        							if(resultString.compareTo(Info.getmsg())!=0)
			        							{
			        								mms.addMMS(MMSInfo);
			        							}			        							
			        						}//end if mms.updateMMSById cua else
		        						}
		            				}//end if(msg.length)
		            				else
		            				{//nguoc lai neu chieu dai tin nhan <=10, khong co so id, da duoc tra loi hoac chuyen gia nay khong co quyen tra loi thi xoa bo tin nhan nay		            					
		            					mms.deleteMMSById(row.getid());
		            				}
		            		}//end if(k>=1)
		            		else
		            		{//Neu day la tin nhan cau hoi cua nha nong thi Phan loai tin nhan bang svm
		            			
			            		Vectorize vt = new Vectorize();
		            			String vector = vt.CreateVector(row.getmsg());
		            			if(vector.compareTo("")!=0)
		            			{
		            				System.out.println("Not spam!");
			            			BufferedWriter vectorFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("./classifications/sms.vector"), "UTF8"));
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
						    		
						    		if(mms.updateMMSById(MMSInfo)!=0)
						    		{//Neu cap nhat thanh cong thi ghi vao bang tbl_sms_out de gui cau hoi sms cho chuyen gia
						    			
						    			System.out.println("Classify successfully!");
						    			
						    			Config cf = new Config();
						    			cf = cf.getConfigBycode("systemconfig");
						    			
						    			if(cf.getvaluechar().compareTo("auto") == 0)
				    	    			{//neu cau hinh la he thong tu dong thi se tu dong gui tin nhan cho chuyen gia
						    				
											ChuyenGiaChuyenMon em = new ChuyenGiaChuyenMon();
											ArrayList<ChuyenGiaChuyenMon> aem = new ArrayList<ChuyenGiaChuyenMon>();
											aem = em.getExpertMajorByMajorId(mj.getId());
											
												for(int j=0;j<aem.size();j++)
												{//duyet danh sach chuyen gia co chuyen nganh thuoc cau hoi
													
													ChuyenGia exp = new ChuyenGia();
													exp = cg.getExpertById(aem.get(j).getExpertId());
													Gateway gw = new Gateway();
													gw = gw.getGatewayById(exp.getgatewayid());
													if(exp.getstatus())//kiem tra xem chuyen gia da duoc chap nhan de tra loi cau hoi chua
													{	
														//Neu la tin nhan MMS co chua hinh anh thi dung MMSSender de gui tin cho chuyen gia
														if(row.getencoded().compareTo("8")==0 && row.getreceivedMms()==true && (row.getimage().contains(".jpg")||row.getimage().contains(".jpeg")||row.getimage().contains(".gif")||row.getimage().contains(".png")||row.getimage().contains(".bmp")))
														{												
																//Set Metric lower cho mang dang dung de ket noi GPRS
										            			if(gw.getoperator().compareTo("VNPT")==0)
										            			{
										            				GatewayConfig gc = new GatewayConfig();
										            				if(gc.setMetric("Mobile Broadband Connection 2", 10, "Mobile Connect - 3G Network Card #2"))
										            				{
										            					InetAddress gateway = InetAddress.getByName(gw.getgatewayip());
																	    mmslib.mms.MMSSender poster = new mmslib.mms.MMSSender(gateway,gw.getport(),gw.getmmsc(),null,"application/vnd.wap.mms-message",row.getid(),exp.getmobile() );	
										            					gc.setMetric("Mobile Broadband Connection 2", 300, "Mobile Connect - 3G Network Card #2");
										            				}										            				
										            			}
										            			else if(gw.getoperator().compareTo("Viettel")==0)
										            			{										            				
																    GatewayConfig gc = new GatewayConfig();
										            				if(gc.setMetric("Viettel Mobile d-com 3g", 10, "Mobile Connect - 3G Network Card #3"))
										            				{
										            					InetAddress gateway = InetAddress.getByName(gw.getgatewayip());
																	    mmslib.mms.MMSSender poster = new mmslib.mms.MMSSender(gateway,gw.getport(),gw.getmmsc(),null,"application/vnd.wap.mms-message",row.getid(),exp.getmobile() );
							            								
										            					gc.setMetric("Viettel Mobile d-com 3g", 300, "Mobile Connect - 3G Network Card #3");
										            				}										            				
										            			}
														}
														else
														{//neu la tin nhan SMS hoac tin MMS khong co hinh anh thi them vao bang tbl-sms_out de gui tin nhan cau hoi den chuyen gia														
															String msgStruct = Integer.toString(row.getid()) + "-" + row.getmsg();
															db = new DBAction();
															int kq = 0;
						    								int flag = 1;
						    								while (kq == 0 && flag != 9)
						    								{	
						    									kq = db.executeInsertStatments("INSERT INTO tbl_sms_out(type,recipient,text,status_report,gateway_id) " +
																	"VALUES('O','" + exp.getmobile() + "','" + msgStruct + "',0,'"+ row.getgatewayid() +"')");//modem1//*
						    									flag++;
						    								}
															if(kq!=0)
															{															
																System.out.println("Successful!");
															}
														}
													}//end of if(cg.getstatus())
												}//end for
											//}//end of else
				    	    			}//end of get auto
						    		}//end of if(mms.updateMMSById(MMSInfo)!=0)
		            			}//end of if(vector.compareTo("")!=0)
		            			else
		            			{//neu tin nha khong co tu khoa nao thi do coi la tin nhan rac class = 7
		            				System.out.println("Spam!");
		            						            			
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
            }
            catch(Exception e)
            {
            	e.printStackTrace();
            }
            try{
            	Config sleeptime = new Config();
	 			sleeptime = sleeptime.getConfigBycode("sleep_time_classify");
            	//Thread.sleep(sleeptime.getvalueint()*1000);	
            	Thread.sleep(1000);//main-thread is sleeping not the thread created above  
            }
            catch(InterruptedException e)
            {
            	e.printStackTrace();
            }
            // outputting data until the quit boolean flag is not set  
            if(quit==true) break;
        }        
    }

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

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


/*
private static String getMmsc(String sender)
{
	String gateway="";
	try{	    	
    	Gateway gw = new Gateway();
    	gw = gw.getGatewayByMmscnumber(sender);
    	gateway = gw.getmmsc();	    
	}
	catch(Exception e)
    {
    	e.printStackTrace();
    }
	return gateway;
}
*/

/*System.out.println("All infomation in: " + mmsc +" port: " + port + " gateway: "+ gatewayip);		            			
GatewayConfig gc = new GatewayConfig();
GatewayConfig pr = GatewayConfig.getInstance();
System.out.println("Gateway: "+ pr.getGateway());
System.out.println("IP: "+ pr.getLocalIPAddress());
*/
/*if(pr.getGateway().compareTo(gatewayip)==0)
{
	System.out.println("All infomation in 2: " + mmsc +" port: " + port + " gateway: "+ gatewayip +" defaultgateway:" +pr.getGateway());
	new MmsGetter(gateway, port, mmsc);
}		            			
else
{*/
	//co the phai delete default gw cu roi moi add cai moi vo
	//PHAI XOA HET TAT CAC CAC DEFAULT GATEWAY KHAC MOI KET NOI DUOC
/*		
 	* System.out.println("All infomation in 3: " + mmsc +" port: " + port + " gateway: "+ gatewayip +" defaultgateway:" +pr.getGateway());
	int time =0;
	//String commandAdd = "route add 0.0.0.0 mask 0.0.0.0 " + gatewayip;
	//String commanddel = "route delete 0.0.0.0 mask 0.0.0.0 " + pr.getGateway();
	
	String commanddel = "route delete 0.0.0.0 mask 0.0.0.0 "+pr.getGateway();
	String commandAdd = "route add 0.0.0.0 mask 0.0.0.0 192.168.1.1";
 	*/		
	//String commandAdd = "route add 0.0.0.0 mask 0.0.0.0 192.168.1.1";
	//System.out.println("command add route: " + commandAdd);
			            				
	/*	
	
	gc.execCommand(commandAdd);
	
	gc.execCommand(commanddel);
	
	System.out.println("Command OK!!!!");
	
	GatewayConfig pr1 = GatewayConfig.getInstance();
	System.out.println("Gateway111: "+ pr1.getGateway());
	System.out.println("IP111: "+ pr1.getLocalIPAddress());
	 */		


	/*while(gc.execCommand(commandAdd) == false && time<10)
	{
		System.out.println("command add route 2: " + commandAdd);
		gc.execCommand(commandAdd);
		time ++;
	}*/
	/*time=0;
	String commandDelete = "route delete 0.0.0.0 mask 0.0.0.0 " + defaultgateway[0];
	System.out.println("command delete route: " + commandDelete);
	while(gc.execCommand(commandDelete) == false && time<10)
	{
		System.out.println("command delete route 2: " + commandDelete);
		gc.execCommand(commandDelete);
		time++;
	}
	System.out.println("Delete ok: ");
	*/
	//MmsGetter getter = new MmsGetter(gateway, port, mmsc);
//}	