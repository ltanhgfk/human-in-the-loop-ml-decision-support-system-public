package org.smslib.smsserver;

import java.io.*;  
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import dao.DBAction;
import libsvm.classify.ClassifySms;
import libsvm.classify.Vectorize;
import mmslib.mms.GatewayConfig;
import mmslib.mms.HexStringConverter;
import mmslib.mms.MmsGetter;
import model.*;
import utils.SimilarityUtil;


public class MmsMain implements Runnable
{
    //declaring reader to read the keyboard and flag to notify main thread to stop when set
	static BufferedReader in;
    static boolean quit=false;
    
    public static void main(String args[]) throws UnknownHostException
    {    	
        DBAction db;
        //MMS mms = new MMS();
        //MMS MMSInfo = new MMS();
        ChuyenGia cg = new ChuyenGia();
        in=new BufferedReader(new InputStreamReader(System.in));
        Thread t1=new Thread(new MmsMain());
        t1.start();          
        System.out.println("Press q then Enter to terminate!");
        
        while(true)
        {
            try{
            	
            	//-----------Het vong lap thu 1---------------------------------------------------//
            	MMS mms = new MMS();
            	ArrayList<MMS> mmslist = new ArrayList<MMS>();
	            mmslist = mms.getMMSByClassifyMachine(false, false, false);//Lay danh sach tin nhan chua duoc phan loai boi may tinh
	            
	            //Neu danh sach tin nhan co tu 1 tin tro len thi moi thuc hien
	            if(mmslist.size()>=1)
	            {
	    			BufferedReader phonenumberFile = new BufferedReader(new InputStreamReader(new FileInputStream("./classifications/stopwordlist.txt"), "UTF8"));
	    			Set<String> phonenumberSet = new LinkedHashSet<String>();
	    			for(String line;(line = phonenumberFile.readLine()) != null;)
	    			{
	    				phonenumberSet.add(line.trim().toLowerCase());
	    			}
	    			phonenumberFile.close();

	            	//---- Lay "kho cau hoi/tra loi" (cac tin nhan da duoc tra loi truoc do) de lam co so
	            	//---- tinh do tuong dong (cosine similarity) va tu dong tra loi neu co cau hoi giong nhau ----
	            	ArrayList<MMS> autoAnswerKnowledgeBase = mms.getMMSByAnswer(true);
	            	Config autoAnswerEnabledCfg = new Config();
	            	autoAnswerEnabledCfg = autoAnswerEnabledCfg.getConfigBycode("auto_answer_enabled");
	            	//Neu chua cau hinh trong bang tbl_config (id==0) thi mac dinh la BAT tinh nang tu dong tra loi
	            	boolean autoAnswerEnabled = (autoAnswerEnabledCfg.getId() == 0) ? true : autoAnswerEnabledCfg.getvaluebool();
	            	Config autoAnswerThresholdCfg = new Config();
	            	autoAnswerThresholdCfg = autoAnswerThresholdCfg.getConfigBycode("auto_answer_threshold");
	            	//Nguong (%) do tuong dong toi thieu de duoc tu dong tra loi, vd 75 nghia la 75%. Mac dinh 75%.
	            	double autoAnswerThreshold = (autoAnswerThresholdCfg.getId() == 0 || autoAnswerThresholdCfg.getvalueint() <= 0)
	            		? 0.75 : (autoAnswerThresholdCfg.getvalueint() / 100.0);
	            	
	            	for(MMS row : mmslist)//Cho vong lap chay tung tin nhan den het
		        	{
	            		//Neu so dien thoai nguoi gui thuoc danh sach nhung nguoi hay gui tin rac 
	            		//thi cap nhat cho tin nhan nay la tin rac va tat ca cac truong khac
	            		if(row.getsender().length()<10 && phonenumberSet.contains(row.getsender()) && row.getmsg().trim().contains(" ") == true && row.getencoded().compareTo("7")==0)
	            		{//MMS co 19 thuoc tinh (fields) tat ca
	            			MMS MMSInfo = new MMS();
	            			
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
				    		MMSInfo.setsentexpert(row.getsentexpert());
    			    		MMSInfo.setsentfarmer(row.getsentfarmer());
    			    		MMSInfo.setreceivedDate(row.getreceivedDate());	
    			    		MMSInfo.setansweredDate(row.getansweredDate());
    			    		MMSInfo.setgatewayid(row.getgatewayid());

				    		mms.updateMMSById(MMSInfo);
	            		}
	            		//Neu chieu dai cua tin nhan > 30 thi xu ly
	            		else if(row.getmsg().length()>1 || row.getimage().contains(".jpg")||row.getimage().contains(".jpeg")||row.getimage().contains(".gif")||row.getimage().contains(".png")||row.getimage().contains(".bmp"))
		            	{
	            			//Xac dinh tin nhan la SMS thuong hay SMS Notification
	            			boolean containblank = row.getmsg().contains(" ");
	            			if(row.getencoded().compareTo("8")==0 && row.getreceivedMms()==false && containblank==false && row.getmsg().length()>=150)
			            	{//neu la tin nhan SMS Notification MMS thi lay thong tin gateway de Get tin nhan ve			            		
			            		System.out.println("MMS Notification!");
			            		String mmsc = getMMSCAddress(row.getmsg());
			            		String gatewayip = getGateway(row.getsender());
			            		InetAddress gateway = InetAddress.getByName(gatewayip);
			            		String operator = getOperator(row.getsender());
			            		int port = getPort(row.getsender());
			            		
			            		if(mmsc.contains("http://") && port >=0 && gateway != null)
			            		{//neu tin nhan co chua dia chi MMS tren MMSC thi get MMS ve
			            			System.out.println("Vao http!");
			            			/*/voi multinetwork			            			
			            			if(operator.compareTo("VNPT")==0)
			            			{
			            				System.out.println("VNPT!");
			            				GatewayConfig gc = new GatewayConfig();
			            				if(gc.setMetric("Mobile Broadband Connection 2", 10, "Mobile Connect - 3G Network Card #2"))
			            				{
			            					System.out.println("Vao Mobile Broadband Connection 2!");
			            					new MmsGetter(gateway, port, mmsc, row.getid());
			            					System.out.println("Vao sau MmsGetter!");
			            					gc.setMetric("Mobile Broadband Connection 2", 300, "Mobile Connect - 3G Network Card #2");
			            					System.out.println("Vao sau Set Metric 300!");
			            				}
			            			}
			            			else if(operator.compareTo("Viettel")==0)
			            			{
			            				GatewayConfig gc = new GatewayConfig();
			            				if(gc.setMetric("Viettel Mobile d-com 3g", 10, "Mobile Connect - 3G Network Card #3"))
			            				{
			            					new MmsGetter(gateway, port, mmsc, row.getid());
			            					gc.setMetric("Viettel Mobile d-com 3g", 300, "Mobile Connect - 3G Network Card #3");
			            				}
			            			}*/
			            			new MmsGetter(gateway, port, mmsc, row.getid());
			            		}
			            		else
			            		{//Neu khong co => tin nhan mms loi => xoa bo
			            			mms.deleteMMSById(row.getid());
			            		}
			            	}//end of if(row.getencoded().compareTo("8")==0 ...
	            			else //111
	            			{//Nguoc lai neu khong phai tin MMS Notification hoac la da Get MMS ve rui thi xu ly tiep	            				
	            				
	            				int posbegin = row.getmsg().indexOf("-") + 1;
	            				int posend = row.getmsg().indexOf(":");	
	            				String answer = "";
	            				if(posbegin >=1 && posend >=1 && posend > posbegin)
	            				{
	            					answer = row.getmsg().substring(0, posbegin-1).trim().toLowerCase();
	            				}
	            				//Kiem tra tin nhan la cau hoi cua nha nong hay cau tra loi cua chuyen gia
			            		
	            				if(answer.compareTo("answer")==0 && posbegin >=1 && posend >=1)
	            				{//neu la cau tra loi cua chuyen gia thi duyet danh sach chuyen gia de kiem tra
			            			if(row.getmsg().length()>=30)
			            			{
			            				boolean status = false;
			            				boolean answered = false;
			            				int id = Integer.parseInt(row.getmsg().substring(posbegin, posend).trim().replace(" ", ""));
			            				String msg = row.getmsg().substring(posend+1).trim();
			            				MMS Info = new MMS();			            				
			            				Info=mms.getMMSById(id);//De kiem tra xem tin nhan voi id nay da duoc tra loi chua			            				
			            				ChuyenGia exm = new ChuyenGia();			            				
			            				exm = cg.getExpertByMobile(row.getsender());//De get chuyen gia boi so dien thoai cua chuyen gia(la so ma chuyen gia dung de tra loi tin nhan sender)			            					            				
			            				if(exm.getid()>=1)
			            				{
			            					status = exm.getstatus();
			            				}
			            				if(Info.getid()>=1)
			            				{
			            					answered = Info.getanswered();
			            				}
			            				
			            				if(id>=1 && answered == false && status == true)
			            				{//Neu tin nhan chua duoc tra loi va chuyen gia co quyen tra loi thi cap nhat vao csdl, sau do xoa tin nhan nay di
			            					MMS MMSInfo = new MMS();
			            					
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
			        			    		MMSInfo.setsentexpert(Info.getsentexpert());
			        			    		MMSInfo.setsentfarmer(Info.getsentfarmer());
			        			    		MMSInfo.setgatewayid(Info.getgatewayid());
			        			    		
			        			    		//DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			        			    		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			        			    		Date date = new Date();
			        			    		MMSInfo.setansweredDate(dateFormat.format(date));
			        			    		System.out.print("Vao if(id>=1 && answered == false && status == true)");
			        			    		
			        						if(mms.expertReply(MMSInfo)!=0)
			        						{//neu cap nhat thanh cong thi ghi cau tra loi vao bang tbl_sms_out de gui cau tra loi cho nha nong
			        							mms.deleteMMSById(row.getid());//Xoa tin nhan tra loi cua chuyen gia sau khi cap nhat thanh cong! 
			        						}//end if mms.updateMMSById			        						
			            				}//end if(id>=1 && answered == false && status == true)
			            				else
			            				{//Nguoc lai neu khong co so id, da duoc tra loi hoac chuyen gia nay khong co quyen tra loi thi xoa bo tin nhan nay		            					
			            					mms.deleteMMSById(row.getid());
			            					//System.out.println("xoa rui!!!");
			            				}			            				
			            			}//end of if row.getmsg.length()
			            			else
			            			{
			            				mms.deleteMMSById(row.getid());
			            			}
	            				}//end of if(row.getmsg().substring...
			            		else
			            		{//Neu la tin nhan cau hoi cua nha nong thi Phan loai tin nhan bang svm			            			
				            		Vectorize vt = new Vectorize();
			            			String vector = vt.CreateVector(row.getmsg());
			            			
			            			if(vector.compareTo("")!=0)
			            			{//Neu tin nhan co chua tu khoa
				            			BufferedWriter vectorFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("./classifications/sms.vector"), "UTF8"));
				            			vectorFile.write("-1 " + vector.trim());
				            			vectorFile.close();
				            			
				            			MMS MMSInfo = new MMS();
				            			//luu vao co so du lieu
				            			MMSInfo.setid(row.getid());
							    		MMSInfo.setsender(row.getsender());
							    		MMSInfo.setmsg(row.getmsg());
							    		MMSInfo.setimage(row.getimage());
							    		MMSInfo.setreplymsg(row.getreplymsg());
							    		MMSInfo.setexpertid(row.getexpertid());
		
				            			//phan loai tin nhan sms
				            			ClassifySms cs = new ClassifySms();
				            			String classid = cs.classify("./classifications/sms.vector");
							    		ChuyenMon mj = new ChuyenMon();
							    		mj = mj.getMajorByClass(classid);
							    		MMSInfo.setmajoridByMachine(mj.getId());

							    		//---- Tu dong tra loi dua vao do tuong dong Cosine voi cac cau hoi da tra loi truoc do ----
							    		boolean autoAnswered = false;
							    		String autoReplyMsg = "";
							    		if(autoAnswerEnabled)
							    		{
							    			SimilarityUtil.SimilarityResult simResult = SimilarityUtil.findMostSimilar(row.getmsg(), autoAnswerKnowledgeBase);
							    			if(simResult != null && simResult.getScore() >= autoAnswerThreshold)
							    			{
							    				autoAnswered = true;
							    				autoReplyMsg = simResult.getMatch().getreplymsg();
							    				System.out.println("Tu dong tra loi tin nhan id=" + row.getid() + " (cosine similarity = " + simResult.getScore() + ", khop voi cau hoi id=" + simResult.getMatch().getid() + ")");
							    			}
							    		}
							    		
							    		MMSInfo.setmajoridByHuman(row.getmajoridByHuman());
							    		if(autoAnswered)
							    		{
							    			MMSInfo.setanswered(true);
							    			MMSInfo.setreplymsg(autoReplyMsg);
							    			DateFormat autoAnswerDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							    			MMSInfo.setansweredDate(autoAnswerDateFormat.format(new Date()));
							    		}
							    		else
							    		{
							    			MMSInfo.setanswered(row.getanswered());
							    		}
							    		MMSInfo.setclassifiedByHuman(row.getclassifiedByHuman());
							    		MMSInfo.setclassifiedByMachine(true);
							    		MMSInfo.setencoded(row.getencoded());
							    		MMSInfo.setreceivedMms(row.getreceivedMms());
							    		MMSInfo.setused(row.getused());
							    		//Neu da tu dong tra loi duoc thi khong can gui cau hoi nay cho chuyen gia nua
							    		MMSInfo.setsentexpert(autoAnswered ? true : row.getsentexpert());
		        			    		MMSInfo.setsentfarmer(row.getsentfarmer());
		        			    		MMSInfo.setreceivedDate(row.getreceivedDate());	
		        			    		//Neu tu dong tra loi thi khong ghi de len ngay tra loi da duoc thiet lap o tren
		        			    		if(!autoAnswered)
		        			    		{
		        			    			MMSInfo.setansweredDate(row.getansweredDate());
		        			    		}
		        			    		MMSInfo.setgatewayid(row.getgatewayid());
		        			    		
		        			    		mms.updateMMSById(MMSInfo);
			            			}//end of if(vector.compareTo("")!=0)
			            			else
			            			{//neu tin nha khong co tu khoa nao thi do coi la tin nhan rac class = 7
			            				System.out.println("Spam!");
			            				
			            				MMS MMSInfo = new MMS();
			            				
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
							    		MMSInfo.setsentexpert(row.getsentexpert());
		        			    		MMSInfo.setsentfarmer(row.getsentfarmer());
		        			    		MMSInfo.setreceivedDate(row.getreceivedDate());	
		        			    		MMSInfo.setansweredDate(row.getansweredDate());
		        			    		MMSInfo.setgatewayid(row.getgatewayid());

							    		mms.updateMMSById(MMSInfo);
			            			}
				            	}//end of else Neu la tin nhan cau hoi cua nha nong thi Phan loai tin nhan bang svm
	            			}//end of else 111	            			
		            	}//end of if(row.getmsg().length()>30 ||...
	            		else
	            		{//Neu chieu dai <=30 thi xoa bo tin nhan
	            			mms.deleteMMSById(row.getid());
	            		}
		        	}//end of for(MMS row : mmslist)
	            }//end of if(mmslist.size()>=1)
	            //-----------Het vong lap thu 1--------------------------------------------------------------//
	            
	            //---------Vong lap thu 2 => Gui tin nhan cau hoi den cho chuyen gia ca MMS/SMS--------------//
	            Config cf = new Config();
    			cf = cf.getConfigBycode("systemconfig");
    			if(cf.getvaluechar().compareTo("auto") == 0)
    			{//Neu le he thong tu dong
    				System.out.println("Vao auto!");
    				ArrayList<MMS> dsSentExpert = new ArrayList<MMS>();
    				dsSentExpert = mms.getMMSBySentExpertMachine(false,true);//lay danh sach nhung tin nhan chua duoc gui cho chuyen gia
    				if(dsSentExpert.size()>=1)
		            {
    					System.out.println("Size of dsSentExpert: "+dsSentExpert.size());
						for(MMS record : dsSentExpert)
		            	{
							System.out.println("Vao lap for record: " + record.getid());
							ChuyenGiaChuyenMon em = new ChuyenGiaChuyenMon();
							ArrayList<ChuyenGiaChuyenMon> aem = new ArrayList<ChuyenGiaChuyenMon>();
							aem = em.getExpertMajorByMajorId(record.getmajoridByMachine());
							
							for(int j=0;j<aem.size();j++)
							{//duyet danh sach chuyen gia co chuyen nganh thuoc cau hoi														
								ChuyenGia exp = new ChuyenGia();
								exp = cg.getExpertById(aem.get(j).getExpertId());
								Gateway gw = new Gateway();
								gw = gw.getGatewayById(exp.getgatewayid());
								if(exp.getstatus())//kiem tra xem chuyen gia da duoc chap nhan de tra loi cau hoi chua
								{									
									//Neu la tin nhan MMS co chua hinh anh thi dung MMSSender de gui tin cho chuyen gia
									if(record.getencoded().compareTo("8")==0 && record.getreceivedMms()==true && (record.getimage().contains(".jpg")||record.getimage().contains(".jpeg")||record.getimage().contains(".gif")||record.getimage().contains(".png")||record.getimage().contains(".bmp")))
									{	
										InetAddress gateway = InetAddress.getByName(gw.getgatewayip());
										new mmslib.mms.MMSSender(gateway,gw.getport(),gw.getmmsc(),null,"application/vnd.wap.mms-message",record.getid(),exp.getmobile());																
									}
									else
									{//neu la tin nhan SMS hoac tin MMS khong co hinh anh thi them vao bang tbl-sms_out de gui tin nhan cau hoi den chuyen gia										
										int kq = 0;
	    								int flag = 1;
	    								db = new DBAction();
	    								String gateway ="*";
	    								if( record.getgatewayid()==null)
	    								{
	    									gateway = record.getgatewayid();
	    								}
	    								String msgStruct = Integer.toString(record.getid()) + "-" + record.getmsg();
	    								while (kq == 0 && flag != 3)
	    								{	
	    									kq = db.executeInsertStatments("INSERT INTO tbl_sms_out(type,recipient,text,status_report,gateway_id) " +
												"VALUES('O','" + exp.getmobile() + "','" + msgStruct + "',0,'"+ gateway +"')");//modem1//*
	    									flag++;
	    								}
									}
								}//end of if(cg.getstatus())
							}//end for (int j=0 ...
							
							//Cap nhat sentExpert = true vao co so du lieu
			    			MMS Info = new MMS();
			    			
			    			Info.setid(record.getid());
				    		Info.setsender(record.getsender());
				    		Info.setmsg(record.getmsg());
				    		Info.setimage(record.getimage());
				    		Info.setreplymsg(record.getreplymsg());
				    		Info.setexpertid(record.getexpertid());
				    		Info.setmajoridByMachine(record.getmajoridByMachine());
				    		Info.setmajoridByHuman(record.getmajoridByHuman());
				    		Info.setanswered(record.getanswered());
				    		Info.setclassifiedByHuman(record.getclassifiedByHuman());
				    		Info.setclassifiedByMachine(record.getclassifiedByMachine());
				    		Info.setencoded(record.getencoded());
				    		Info.setreceivedMms(record.getreceivedMms());
				    		Info.setused(record.getused());
				    		Info.setreceivedDate(record.getreceivedDate());	
    			    		Info.setansweredDate(record.getansweredDate());		
				    		Info.setsentexpert(true);
				    		Info.setsentfarmer(record.getsentfarmer());
				    		Info.setgatewayid(record.getgatewayid());
							
							mms.updateMMSById(Info);
							System.out.println("Thuc hien cap nhat sentexpert auto thanh cong!");		            		
		            	}
		            }	
    			}
    			else//Nguoc lai neu he thong la semi-auto
    			{
    				System.out.println("Vao semi-auto!");
    				ArrayList<MMS> dsSentExpert = new ArrayList<MMS>();
    				dsSentExpert = mms.getMMSBySentExpertHuman(false,true);//lay danh sach nhung tin nhan chua duoc gui cho chuyen gia
    				if(dsSentExpert.size()>=1)
    	            {
    					for(MMS record : dsSentExpert)
    	            	{
    						System.out.println("Vao lap for record: " + record.getid());
    						ChuyenGiaChuyenMon em = new ChuyenGiaChuyenMon();
							ArrayList<ChuyenGiaChuyenMon> aem = new ArrayList<ChuyenGiaChuyenMon>();
							aem = em.getExpertMajorByMajorId(record.getmajoridByHuman());								
							for(int j=0;j<aem.size();j++)
							{//duyet danh sach chuyen gia co chuyen nganh thuoc cau hoi														
								ChuyenGia exp = new ChuyenGia();
								exp = cg.getExpertById(aem.get(j).getExpertId());
								Gateway gw = new Gateway();
								gw = gw.getGatewayById(exp.getgatewayid());
								if(exp.getstatus())//kiem tra xem chuyen gia da duoc chap nhan de tra loi cau hoi chua
								{//Neu la tin nhan MMS co chua hinh anh thi dung MMSSender de gui tin cho chuyen gia
									if(record.getencoded().compareTo("8")==0 && record.getreceivedMms()==true && (record.getimage().contains(".jpg")||record.getimage().contains(".jpeg")||record.getimage().contains(".gif")||record.getimage().contains(".png")||record.getimage().contains(".bmp")))
									{	
										InetAddress gateway = InetAddress.getByName(gw.getgatewayip());
										new mmslib.mms.MMSSender(gateway,gw.getport(),gw.getmmsc(),null,"application/vnd.wap.mms-message",record.getid(),exp.getmobile() );																
									}
									else
									{//neu la tin nhan SMS hoac tin MMS khong co hinh anh thi them vao bang tbl-sms_out de gui tin nhan cau hoi den chuyen gia														
										int kq = 0;
	    								int flag = 1;
	    								db = new DBAction();
	    								String gateway ="*";
	    								if( record.getgatewayid()==null)
	    								{
	    									gateway = record.getgatewayid();
	    								}
	    								String msgStruct = Integer.toString(record.getid()) + "-" + record.getmsg();
	    								while (kq == 0 && flag != 3)
	    								{	
	    									kq = db.executeInsertStatments("INSERT INTO tbl_sms_out(type,recipient,text,status_report,gateway_id) " +
												"VALUES('O','" + exp.getmobile() + "','" + msgStruct + "',0,'"+ gateway +"')");
	    									flag++;
	    								}
									}
								}//end of if(cg.getstatus())
							}//end for
							//Cap nhat sentExpert = true vao co so du lieu
			    			MMS Info = new MMS();
			    			
			    			Info.setid(record.getid());
				    		Info.setsender(record.getsender());
				    		Info.setmsg(record.getmsg());
				    		Info.setimage(record.getimage());
				    		Info.setreplymsg(record.getreplymsg());
				    		Info.setexpertid(record.getexpertid());
				    		Info.setmajoridByMachine(record.getmajoridByMachine());
				    		Info.setmajoridByHuman(record.getmajoridByHuman());
				    		Info.setanswered(record.getanswered());
				    		Info.setclassifiedByHuman(record.getclassifiedByHuman());
				    		Info.setclassifiedByMachine(record.getclassifiedByMachine());
				    		Info.setencoded(record.getencoded());
				    		Info.setreceivedMms(record.getreceivedMms());
				    		Info.setused(record.getused());
				    		Info.setreceivedDate(record.getreceivedDate());	
    			    		Info.setansweredDate(record.getansweredDate());		
				    		Info.setsentexpert(true);
				    		Info.setsentfarmer(record.getsentfarmer());
				    		Info.setgatewayid(record.getgatewayid());
							
							mms.updateMMSById(Info);
							System.out.println("Thuc hien cap nhat sentexpert semi-auto thanh cong!");
    	            	}//end of for(MMS record : dsSentExpert)
	            	}//end of if(dsSentExpert.size()>=1)	
	            }//end of else semi-auto							
	            //-----------Het vong lap thu 2---------------------------------------------------------------------//
				
				//-----------Vong lap thu 3 => Gui tin nhan phan hoi cho nha nong chi SMS---------------------------//				
				ArrayList<MMS> dsSentFarmer = new ArrayList<MMS>();
		        dsSentFarmer = mms.getMMSBySentFarmer(false,true);//lay danh sach nhung tin nhan chua duoc gui cho nha nong
				if(dsSentFarmer.size()>=1)
	            {
					System.out.println("Vao vong lap 3!");
					for(MMS r: dsSentFarmer)
	            	{
						System.out.println("Vao vong lap for dsSentFarmer!");
						if(r.getanswered()==true)
	            		{//Neu da duoc tra loi rui thi tu dong gui lai cho nha nong							
							int kq = 0;
							int flag = 1;
							db = new DBAction();
							String gateway ="*";
							if( r.getgatewayid()==null)
							{
								gateway = r.getgatewayid();
							}
							while (kq == 0 && flag != 3)
							{	
								kq = db.executeInsertStatments("INSERT INTO tbl_sms_out(type,recipient,text,status_report,gateway_id) " +
									"VALUES('O','" + r.getsender() + "','" + r.getreplymsg() + "',0,'"+ gateway +"')");
								flag++;
							}
							if(kq!=0)
							{//Neu gui cau tra loi cho nha nong thanh cong thi cap nhat sentfarmer = true va xoa tin nhan tra loi sau khi xu ly								
								MMS Inf = new MMS();
				    			
				    			Inf.setid(r.getid());
					    		Inf.setsender(r.getsender());
					    		Inf.setmsg(r.getmsg());
					    		Inf.setimage(r.getimage());
					    		Inf.setreplymsg(r.getreplymsg());
					    		Inf.setexpertid(r.getexpertid());
					    		Inf.setmajoridByMachine(r.getmajoridByMachine());
					    		Inf.setmajoridByHuman(r.getmajoridByHuman());
					    		Inf.setanswered(r.getanswered());
					    		Inf.setclassifiedByHuman(r.getclassifiedByHuman());
					    		Inf.setclassifiedByMachine(r.getclassifiedByMachine());
					    		Inf.setencoded(r.getencoded());
					    		Inf.setreceivedMms(r.getreceivedMms());
					    		Inf.setused(r.getused());
					    		Inf.setreceivedDate(r.getreceivedDate());	
        			    		Inf.setansweredDate(r.getansweredDate());		
					    		Inf.setsentexpert(r.getsentexpert());
					    		Inf.setsentfarmer(true);	
					    		Inf.setgatewayid(r.getgatewayid());
								
								if(mms.updateMMSById(Inf)!=0)
								{
									//Chuyen tin nhan sms hop le thanh tin nhan sms khong dau tieng viet luu csdl for training
        							String subjectString = Inf.getmsg().toLowerCase();			        							    							
        							subjectString = subjectString.replaceAll("đ","d");			        							
        							subjectString = Normalizer.normalize(subjectString, Normalizer.Form.NFD);
        							String resultString = subjectString.replaceAll("[^\\x00-\\x7F]", "");
        							Inf.setmsg(resultString);
        							if(resultString.compareTo(Inf.getmsg().toLowerCase())!=0)
        							{
        								mms.addMMS(Inf);
        							}
        							System.out.print("Chuyen tin nhan sms hop le thanh tin nhan sms khong dau tieng viet luu csdl for training!");
								}
							}	
	            		}										
	            	}
	            }
    			//----------Het vong lap thu 3-----------------------------------------------------//
            }
            catch(Exception e)
            {
            	e.printStackTrace();
            }
            try{
            	Config sleeptime = new Config();
	 			sleeptime = sleeptime.getConfigBycode("sleep_time_classify");
            	Thread.sleep(sleeptime.getvalueint()*1000);
            	//Thread.sleep(1000);
            }
            catch(InterruptedException e)
            {
            	e.printStackTrace();
            }
            if(quit==true) break;
        }
    }
    
    public void run()
    {  
        String msg = null;        
        while(true)
        {
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