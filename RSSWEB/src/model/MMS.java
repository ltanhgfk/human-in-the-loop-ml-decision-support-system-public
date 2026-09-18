package model;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;

import dao.DBAction;

public class MMS {
	 private int id;
	  private String sender;
	  private boolean receivedMms;
	  private String msg;
	  private String image;
	  private String replymsg;
	  private int expertid;
	  private int majoridByMachine;
	  private int majoridByHuman;
	  private boolean answered;
	  private boolean used;
	  private boolean classifiedByHuman;
	  private String encoded;
	  private boolean classifiedByMachine;
	  private String receivedDate;
	  private String answeredDate;
	  private String gatewayid;//modem1 , modem 2 ...
	  private boolean sentexpert;
	  private boolean sentfarmer;
	
	public String getsender() {
		return sender;
	}
	public void setsender(String sender) {
		this.sender = sender;
	}
	
	public boolean getreceivedMms() {
		return receivedMms;
	}
	public void setreceivedMms(boolean receivedMms) {
		this.receivedMms = receivedMms;
	}
	
	public String getmsg() {
		return msg;
	}
	public void setmsg(String msg) {
		this.msg = msg;
	}	

	public String getreplymsg() {
		return replymsg;
	}
	public void setreplymsg(String replymsg) {
		this.replymsg = replymsg;
	}
	
	public int getexpertid() {
		return expertid;
	}
	public void setexpertid(int expertid) {
		this.expertid = expertid;
	}
	
	public int getid() {
		return id;
	}
	public void setid(int id) {
		this.id = id;
	}
	
	public int getmajoridByMachine() {
		return majoridByMachine;
	}
	public void setmajoridByMachine(int majoridByMachine) {
		this.majoridByMachine = majoridByMachine;
	}	
	
	public int getmajoridByHuman() {
		return majoridByHuman;
	}
	public void setmajoridByHuman(int majoridByHuman) {
		this.majoridByHuman = majoridByHuman;
	}
	
	public boolean getanswered() {
		return answered;
	}
	public void setanswered(boolean answered) {
		this.answered = answered;
	}
	
	public boolean getused() {
		return used;
	}
	public void setused(boolean used) {
		this.used = used;
	}
	
	public boolean getclassifiedByHuman() {
		return classifiedByHuman;
	}
	public void setclassifiedByHuman(boolean classifiedByHuman) {
		this.classifiedByHuman = classifiedByHuman;
	}
	
	public String getencoded() {
		return encoded;
	}
	public void setencoded(String encoded) {
		this.encoded = encoded;
	}
	
	public String getimage() {
		return image;
	}
	public void setimage(String image) {
		this.image = image;
	}
	
	public boolean getclassifiedByMachine() {
		return classifiedByMachine;
	}
	public void setclassifiedByMachine(boolean classifiedByMachine) {
		this.classifiedByMachine = classifiedByMachine;
	}
	
	public String getreceivedDate() {
		return receivedDate;
	}
	public void setreceivedDate(String receivedDate) {
		this.receivedDate = receivedDate;
	}
	
	public String getansweredDate() {
		return receivedDate;
	}
	public void setansweredDate(String answeredDate) {
		this.answeredDate = answeredDate;
	}
	public String getgatewayid() {
		return gatewayid;
	}
	public void setgatewayid(String gatewayid) {
		this.gatewayid = gatewayid;
	}
	
	public boolean getsentexpert() {
		return sentexpert;
	}
	public void setsentexpert(boolean sentexpert) {
		this.sentexpert = sentexpert;
	}
	
	public boolean getsentfarmer() {
		return sentfarmer;
	}
	public void setsentfarmer(boolean sentfarmer) {
		this.sentfarmer = sentfarmer;
	}
	
	public MMS getMMSById(int id){
		MMS MMSInfo = new MMS();	
		try {
			DBAction db = new DBAction();
			ResultSet rs = db.executeSelectStatments("" + 
					"SELECT * " +
					"FROM tbl_mms " +
					"WHERE id = " + id);
	    	while(rs.next()){
	    		MMSInfo.id = rs.getInt("id");
	    		MMSInfo.sender = rs.getString("sender");
	    		MMSInfo.receivedMms = rs.getBoolean("receivedMms");
	    		MMSInfo.msg = rs.getString("msg");
	    		MMSInfo.image = rs.getString("image");
	    		MMSInfo.replymsg = rs.getString("replymsg");
	    		MMSInfo.expertid = rs.getInt("expertid");	    		
	    		MMSInfo.majoridByMachine = rs.getInt("majoridByMachine");
	    		MMSInfo.majoridByHuman = rs.getInt("majoridByHuman");
	    		MMSInfo.answered = rs.getBoolean("answered");
	    		MMSInfo.used = rs.getBoolean("used");
	    		MMSInfo.classifiedByHuman = rs.getBoolean("classifiedByHuman");
	    		MMSInfo.encoded = rs.getString("encoded");
	    		MMSInfo.classifiedByMachine = rs.getBoolean("classifiedByMachine");
	    		MMSInfo.receivedDate = rs.getString("receivedDate");
	    		MMSInfo.answeredDate = rs.getString("answeredDate");
	    		MMSInfo.gatewayid = rs.getString("gatewayid");
	    		MMSInfo.sentexpert = rs.getBoolean("sentexpert");
	    		MMSInfo.sentfarmer = rs.getBoolean("sentfarmer");
	        }
		} catch (Exception e) {

		}	
		return MMSInfo;
	}
	
	public ArrayList<MMS> getMMSBySentExpertMachine(boolean sentExpert, boolean classifiedByMachine){
		ArrayList<MMS> listMMS = new ArrayList<MMS>();
		try {
			DBAction db = new DBAction();
			ResultSet rs = db.executeSelectStatments("" + 
					"SELECT * " +
					"FROM tbl_mms " +
					"WHERE sentexpert is " + sentExpert +" and classifiedByMachine is " + classifiedByMachine);
	    	while(rs.next()){
	    		MMS MMSInfo = new MMS();
	    		MMSInfo.id = rs.getInt("id");
	    		MMSInfo.sender = rs.getString("sender");
	    		MMSInfo.receivedMms = rs.getBoolean("receivedMms");
	    		MMSInfo.msg = rs.getString("msg");
	    		MMSInfo.image = rs.getString("image");
	    		MMSInfo.replymsg = rs.getString("replymsg");
	    		MMSInfo.expertid = rs.getInt("expertid");	    		
	    		MMSInfo.majoridByMachine = rs.getInt("majoridByMachine");
	    		MMSInfo.majoridByHuman = rs.getInt("majoridByHuman");
	    		MMSInfo.answered = rs.getBoolean("answered");
	    		MMSInfo.used = rs.getBoolean("used");
	    		MMSInfo.classifiedByHuman = rs.getBoolean("classifiedByHuman");
	    		MMSInfo.encoded = rs.getString("encoded");
	    		MMSInfo.classifiedByMachine = rs.getBoolean("classifiedByMachine");
	    		MMSInfo.receivedDate = rs.getString("receivedDate");
	    		MMSInfo.answeredDate = rs.getString("answeredDate");
	    		MMSInfo.gatewayid = rs.getString("gatewayid");
	    		MMSInfo.sentexpert = rs.getBoolean("sentexpert");
	    		MMSInfo.sentfarmer = rs.getBoolean("sentfarmer");
	    		listMMS.add(MMSInfo);
	        }
		} catch (Exception e) {

		}	
		return  listMMS;
	}	
	
	public ArrayList<MMS> getMMSBySentExpertHuman(boolean sentExpert,boolean classifiedByHuman){
		ArrayList<MMS> listMMS = new ArrayList<MMS>();
		try {
			DBAction db = new DBAction();
			ResultSet rs = db.executeSelectStatments("" + 
					"SELECT * " +
					"FROM tbl_mms " +
					"WHERE sentexpert is " + sentExpert +" and classifiedByHuman is " + classifiedByHuman);
	    	while(rs.next()){
	    		MMS MMSInfo = new MMS();
	    		MMSInfo.id = rs.getInt("id");
	    		MMSInfo.sender = rs.getString("sender");
	    		MMSInfo.receivedMms = rs.getBoolean("receivedMms");
	    		MMSInfo.msg = rs.getString("msg");
	    		MMSInfo.image = rs.getString("image");
	    		MMSInfo.replymsg = rs.getString("replymsg");
	    		MMSInfo.expertid = rs.getInt("expertid");	    		
	    		MMSInfo.majoridByMachine = rs.getInt("majoridByMachine");
	    		MMSInfo.majoridByHuman = rs.getInt("majoridByHuman");
	    		MMSInfo.answered = rs.getBoolean("answered");
	    		MMSInfo.used = rs.getBoolean("used");
	    		MMSInfo.classifiedByHuman = rs.getBoolean("classifiedByHuman");
	    		MMSInfo.encoded = rs.getString("encoded");
	    		MMSInfo.classifiedByMachine = rs.getBoolean("classifiedByMachine");
	    		MMSInfo.receivedDate = rs.getString("receivedDate");
	    		MMSInfo.answeredDate = rs.getString("answeredDate");
	    		MMSInfo.gatewayid = rs.getString("gatewayid");
	    		MMSInfo.sentexpert = rs.getBoolean("sentexpert");
	    		MMSInfo.sentfarmer = rs.getBoolean("sentfarmer");
	    		listMMS.add(MMSInfo);
	        }
		} catch (Exception e) {

		}	
		return  listMMS;
	}	
	
	public ArrayList<MMS> getMMSBySentFarmer(boolean sentFarmer, boolean answered){
		ArrayList<MMS> listMMS = new ArrayList<MMS>();
		try {
			DBAction db = new DBAction();
			ResultSet rs = db.executeSelectStatments("" + 
					"SELECT * " +
					"FROM tbl_mms " +
					"WHERE sentfarmer is " + sentFarmer + " and answered is " + answered);
	    	while(rs.next()){
	    		MMS MMSInfo = new MMS();
	    		MMSInfo.id = rs.getInt("id");
	    		MMSInfo.sender = rs.getString("sender");
	    		MMSInfo.receivedMms = rs.getBoolean("receivedMms");
	    		MMSInfo.msg = rs.getString("msg");
	    		MMSInfo.image = rs.getString("image");
	    		MMSInfo.replymsg = rs.getString("replymsg");
	    		MMSInfo.expertid = rs.getInt("expertid");	    		
	    		MMSInfo.majoridByMachine = rs.getInt("majoridByMachine");
	    		MMSInfo.majoridByHuman = rs.getInt("majoridByHuman");
	    		MMSInfo.answered = rs.getBoolean("answered");
	    		MMSInfo.used = rs.getBoolean("used");
	    		MMSInfo.classifiedByHuman = rs.getBoolean("classifiedByHuman");
	    		MMSInfo.encoded = rs.getString("encoded");
	    		MMSInfo.classifiedByMachine = rs.getBoolean("classifiedByMachine");
	    		MMSInfo.receivedDate = rs.getString("receivedDate");
	    		MMSInfo.answeredDate = rs.getString("answeredDate");
	    		MMSInfo.gatewayid = rs.getString("gatewayid");
	    		MMSInfo.sentexpert = rs.getBoolean("sentexpert");
	    		MMSInfo.sentfarmer = rs.getBoolean("sentfarmer");
	    		listMMS.add(MMSInfo);
	        }
		} catch (Exception e) {

		}	
		return  listMMS;
	}
	
	//Get classified by human mms by expert to answer question
	public ArrayList<MMS> getMMSClassifiedByHuman(int MajorIdByHuman, boolean classifiedByHuman, boolean answered){
		ArrayList<MMS> listMMS = new ArrayList<MMS>();
		try {
			DBAction db = new DBAction();
			ResultSet rs = db.executeSelectStatments("" + 
					"SELECT * " +
					"FROM tbl_mms " +
					"WHERE majoridByHuman = " + MajorIdByHuman + " and answered is " + answered + " and classifiedByHuman is " + classifiedByHuman);
	    	while(rs.next()){
	    		MMS MMSInfo = new MMS();
	    		MMSInfo.id = rs.getInt("id");
	    		MMSInfo.sender = rs.getString("sender");
	    		MMSInfo.receivedMms = rs.getBoolean("receivedMms");
	    		MMSInfo.msg = rs.getString("msg");
	    		MMSInfo.image = rs.getString("image");
	    		MMSInfo.replymsg = rs.getString("replymsg");
	    		MMSInfo.expertid = rs.getInt("expertid");	    		
	    		MMSInfo.majoridByMachine = rs.getInt("majoridByMachine");
	    		MMSInfo.majoridByHuman = rs.getInt("majoridByHuman");
	    		MMSInfo.answered = rs.getBoolean("answered");
	    		MMSInfo.used = rs.getBoolean("used");
	    		MMSInfo.classifiedByHuman = rs.getBoolean("classifiedByHuman");
	    		MMSInfo.encoded = rs.getString("encoded");
	    		MMSInfo.classifiedByMachine = rs.getBoolean("classifiedByMachine");
	    		MMSInfo.receivedDate = rs.getString("receivedDate");
	    		MMSInfo.answeredDate = rs.getString("answeredDate");
	    		MMSInfo.gatewayid = rs.getString("gatewayid");
	    		MMSInfo.sentexpert = rs.getBoolean("sentexpert");
	    		MMSInfo.sentfarmer = rs.getBoolean("sentfarmer");
	    		listMMS.add(MMSInfo);
	        }
		} catch (Exception e) {

		}	
		return  listMMS;
	}
	
	//Get classified by machine mms by expert to answer question
	public ArrayList<MMS> getMMSClassifiedByMachine(int MajorIdByMachine, boolean classifiedByMachine, boolean answered){
		ArrayList<MMS> listMMS = new ArrayList<MMS>();
		try {
			DBAction db = new DBAction();
			ResultSet rs = db.executeSelectStatments("" + 
					"SELECT * " +
					"FROM tbl_mms " +
					"WHERE majoridByMachine = " + MajorIdByMachine + " and answered is " + answered + " and classifiedByMachine is " + classifiedByMachine);
	    	while(rs.next()){
	    		MMS MMSInfo = new MMS();
	    		MMSInfo.id = rs.getInt("id");
	    		MMSInfo.sender = rs.getString("sender");
	    		MMSInfo.receivedMms = rs.getBoolean("receivedMms");
	    		MMSInfo.msg = rs.getString("msg");
	    		MMSInfo.image = rs.getString("image");
	    		MMSInfo.replymsg = rs.getString("replymsg");
	    		MMSInfo.expertid = rs.getInt("expertid");	    		
	    		MMSInfo.majoridByMachine = rs.getInt("majoridByMachine");
	    		MMSInfo.majoridByHuman = rs.getInt("majoridByHuman");
	    		MMSInfo.answered = rs.getBoolean("answered");
	    		MMSInfo.used = rs.getBoolean("used");
	    		MMSInfo.classifiedByHuman = rs.getBoolean("classifiedByHuman");
	    		MMSInfo.encoded = rs.getString("encoded");
	    		MMSInfo.classifiedByMachine = rs.getBoolean("classifiedByMachine");
	    		MMSInfo.receivedDate = rs.getString("receivedDate");
	    		MMSInfo.answeredDate = rs.getString("answeredDate");
	    		MMSInfo.gatewayid = rs.getString("gatewayid");
	    		MMSInfo.sentexpert = rs.getBoolean("sentexpert");
	    		MMSInfo.sentfarmer = rs.getBoolean("sentfarmer");
	    		
	    		listMMS.add(MMSInfo);
	        }
		} catch (Exception e) {

		}	
		return  listMMS;
	}
	
	//Get unclassified by human mms by Adapter to classify
	public ArrayList<MMS> getMMSByClassifyHuman(boolean classifiedByHuman, boolean answered, boolean used){
		ArrayList<MMS> listMMS = new ArrayList<MMS>();				
		try {
			DBAction db = new DBAction();
			ResultSet rs = db.executeSelectStatments(""+
					"SELECT * " +
					"FROM tbl_mms " +
					"WHERE classifiedByHuman is " + classifiedByHuman + " and answered is " + answered + " and used is " + used);
	    	
	    	while(rs.next()){
	    		MMS MMSInfo = new MMS();
	    		MMSInfo.id = rs.getInt("id");
	    		MMSInfo.sender = rs.getString("sender");	
	    		MMSInfo.receivedMms = rs.getBoolean("receivedMms");
	    		MMSInfo.msg = rs.getString("msg");
	    		MMSInfo.image = rs.getString("image");
	    		MMSInfo.replymsg = rs.getString("replymsg");
	    		MMSInfo.expertid = rs.getInt("expertid");	    		
	    		MMSInfo.majoridByMachine = rs.getInt("majoridByMachine");
	    		MMSInfo.majoridByHuman = rs.getInt("majoridByHuman");
	    		MMSInfo.answered = rs.getBoolean("answered");
	    		MMSInfo.used = rs.getBoolean("used");
	    		MMSInfo.classifiedByHuman = rs.getBoolean("classifiedByHuman");
	    		MMSInfo.encoded = rs.getString("encoded");
	    		MMSInfo.classifiedByMachine = rs.getBoolean("classifiedByMachine");
	    		MMSInfo.receivedDate = rs.getString("receivedDate");
	    		MMSInfo.answeredDate = rs.getString("answeredDate");
	    		MMSInfo.gatewayid = rs.getString("gatewayid");
	    		MMSInfo.sentexpert = rs.getBoolean("sentexpert");
	    		MMSInfo.sentfarmer = rs.getBoolean("sentfarmer");
	    		
	    		listMMS.add(MMSInfo);
	        }
		} catch (Exception e) {

		}	
		return  listMMS;
	}

	public ArrayList<MMS> getAllMMSForTrainByHuman(boolean classifiedByHuman, boolean answered){
		ArrayList<MMS> listMMS = new ArrayList<MMS>();				
		try {
			DBAction db = new DBAction();
			ResultSet rs = db.executeSelectStatments(""+
					"SELECT * " +
					"FROM tbl_mms " +
					"WHERE classifiedByHuman is " + classifiedByHuman + " and answered is " + answered);
	    	
	    	while(rs.next()){
	    		MMS MMSInfo = new MMS();
	    		MMSInfo.id = rs.getInt("id");
	    		MMSInfo.sender = rs.getString("sender");	
	    		MMSInfo.receivedMms = rs.getBoolean("receivedMms");
	    		MMSInfo.msg = rs.getString("msg");
	    		MMSInfo.image = rs.getString("image");
	    		MMSInfo.replymsg = rs.getString("replymsg");
	    		MMSInfo.expertid = rs.getInt("expertid");	    		
	    		MMSInfo.majoridByMachine = rs.getInt("majoridByMachine");
	    		MMSInfo.majoridByHuman = rs.getInt("majoridByHuman");
	    		MMSInfo.answered = rs.getBoolean("answered");
	    		MMSInfo.used = rs.getBoolean("used");
	    		MMSInfo.classifiedByHuman = rs.getBoolean("classifiedByHuman");
	    		MMSInfo.encoded = rs.getString("encoded");
	    		MMSInfo.classifiedByMachine = rs.getBoolean("classifiedByMachine");
	    		MMSInfo.receivedDate = rs.getString("receivedDate");
	    		MMSInfo.answeredDate = rs.getString("answeredDate");
	    		MMSInfo.gatewayid = rs.getString("gatewayid");
	    		MMSInfo.sentexpert = rs.getBoolean("sentexpert");
	    		MMSInfo.sentfarmer = rs.getBoolean("sentfarmer");
	    		
	    		listMMS.add(MMSInfo);
	        }
		} catch (Exception e) {

		}	
		return  listMMS;
	}
	
	//Get unclassified by machine mms by Adapter to classify
		public ArrayList<MMS> getMMSByClassifyMachine(boolean classifiedByMachine, boolean answered, boolean used){
			ArrayList<MMS> listMMS = new ArrayList<MMS>();	
			
			try {
					DBAction db = new DBAction();
					ResultSet rs = db.executeSelectStatments("" +
											"SELECT * " +
											"FROM tbl_mms " +
											"WHERE classifiedByMachine is " + classifiedByMachine + " and answered is " + answered + " and used is " + used);
					while(rs.next()){
			    		MMS MMSInfo = new MMS();
			    		MMSInfo.id = rs.getInt("id");
			    		MMSInfo.sender = rs.getString("sender");
			    		MMSInfo.receivedMms = rs.getBoolean("receivedMms");
			    		MMSInfo.msg = rs.getString("msg");
			    		MMSInfo.image = rs.getString("image");
			    		MMSInfo.replymsg = rs.getString("replymsg");
			    		MMSInfo.expertid = rs.getInt("expertid");	    		
			    		MMSInfo.majoridByMachine = rs.getInt("majoridByMachine");
			    		MMSInfo.majoridByHuman = rs.getInt("majoridByHuman");
			    		MMSInfo.answered = rs.getBoolean("answered");
			    		MMSInfo.used = rs.getBoolean("used");
			    		MMSInfo.classifiedByHuman = rs.getBoolean("classifiedByHuman");
			    		MMSInfo.encoded = rs.getString("encoded");
			    		MMSInfo.classifiedByMachine = rs.getBoolean("classifiedByMachine");
			    		MMSInfo.receivedDate = rs.getString("receivedDate");
			    		MMSInfo.answeredDate = rs.getString("answeredDate");
			    		MMSInfo.gatewayid = rs.getString("gatewayid");
			    		MMSInfo.sentexpert = rs.getBoolean("sentexpert");
			    		MMSInfo.sentfarmer = rs.getBoolean("sentfarmer");
			    		
			    		listMMS.add(MMSInfo);
			        }
				}catch (Exception e) 
					{
					}	
			return  listMMS;
		}	
	
		//Get unclassified by machine mms by Adapter to classify
				public ArrayList<MMS> getAllMMSForTrainByMachine(boolean classifiedByMachine, boolean answered){
					ArrayList<MMS> listMMS = new ArrayList<MMS>();	
					
					try {
							DBAction db = new DBAction();
							ResultSet rs = db.executeSelectStatments("" +
													"SELECT * " +
													"FROM tbl_mms " +
													"WHERE classifiedByMachine is " + classifiedByMachine + " and answered is " + answered);
							while(rs.next()){
					    		MMS MMSInfo = new MMS();
					    		MMSInfo.id = rs.getInt("id");
					    		MMSInfo.sender = rs.getString("sender");
					    		MMSInfo.receivedMms = rs.getBoolean("receivedMms");
					    		MMSInfo.msg = rs.getString("msg");
					    		MMSInfo.image = rs.getString("image");
					    		MMSInfo.replymsg = rs.getString("replymsg");
					    		MMSInfo.expertid = rs.getInt("expertid");	    		
					    		MMSInfo.majoridByMachine = rs.getInt("majoridByMachine");
					    		MMSInfo.majoridByHuman = rs.getInt("majoridByHuman");
					    		MMSInfo.answered = rs.getBoolean("answered");
					    		MMSInfo.used = rs.getBoolean("used");
					    		MMSInfo.classifiedByHuman = rs.getBoolean("classifiedByHuman");
					    		MMSInfo.encoded = rs.getString("encoded");
					    		MMSInfo.classifiedByMachine = rs.getBoolean("classifiedByMachine");
					    		MMSInfo.receivedDate = rs.getString("receivedDate");
					    		MMSInfo.answeredDate = rs.getString("answeredDate");
					    		MMSInfo.gatewayid = rs.getString("gatewayid");
					    		MMSInfo.sentexpert = rs.getBoolean("sentexpert");
					    		MMSInfo.sentfarmer = rs.getBoolean("sentfarmer");
					    		
					    		listMMS.add(MMSInfo);
					        }
						}catch (Exception e) 
							{
							}	
					return  listMMS;
				}
		
	//Get unclassified by human mms by Adapter for paging limit
		public ArrayList<MMS> getMMSByAnswer(boolean answered, int limit, int p, int expertid, int majorid){
			ArrayList<MMS> listMMS = new ArrayList<MMS>();
			try {
				DBAction db = new DBAction();
				
				if(expertid != -1 || majorid != -1)
				{
					String queryStr = "";
					queryStr = queryStr + "SELECT * FROM tbl_mms WHERE used is false and answered is " + answered;
					if(expertid>=1) queryStr += " and expertid = " + expertid;
					if(majorid>=1) queryStr += " and majoridByHuman = " + majorid;
					queryStr += " order by answeredDate desc limit " + p*limit + "," + limit;
					
					ResultSet rs = db.executeSelectStatments(queryStr);
	
			    	while(rs.next())
			    	{
			    		MMS MMSInfo = new MMS();
			    		MMSInfo.id = rs.getInt("id");
			    		MMSInfo.sender = rs.getString("sender");	
			    		MMSInfo.receivedMms = rs.getBoolean("receivedMms");
			    		MMSInfo.msg = rs.getString("msg");
			    		MMSInfo.image = rs.getString("image");
			    		MMSInfo.replymsg = rs.getString("replymsg");
			    		MMSInfo.expertid = rs.getInt("expertid");	    		
			    		MMSInfo.majoridByMachine = rs.getInt("majoridByMachine");
			    		MMSInfo.majoridByHuman = rs.getInt("majoridByHuman");
			    		MMSInfo.answered = rs.getBoolean("answered");
			    		MMSInfo.used = rs.getBoolean("used");
			    		MMSInfo.classifiedByHuman = rs.getBoolean("classifiedByHuman");
			    		MMSInfo.encoded = rs.getString("encoded");
			    		MMSInfo.classifiedByMachine = rs.getBoolean("classifiedByMachine");
			    		MMSInfo.receivedDate = rs.getString("receivedDate");
			    		MMSInfo.answeredDate = rs.getString("answeredDate");
			    		MMSInfo.gatewayid = rs.getString("gatewayid");
			    		MMSInfo.sentexpert = rs.getBoolean("sentexpert");
			    		MMSInfo.sentfarmer = rs.getBoolean("sentfarmer");
			    		
			    		listMMS.add(MMSInfo);
			        }
				}
				else //expertid == -1 || majorid = -1)
				{
					ResultSet rs = db.executeSelectStatments(""+
					"SELECT * " +
					"FROM tbl_mms " +
					"WHERE used is false and answered is " + answered + " order by answeredDate desc limit "+ p*limit +"," + limit);
	    	
			    	while(rs.next())
			    	{
			    		MMS MMSInfo = new MMS();
			    		MMSInfo.id = rs.getInt("id");
			    		MMSInfo.sender = rs.getString("sender");	
			    		MMSInfo.receivedMms = rs.getBoolean("receivedMms");
			    		MMSInfo.msg = rs.getString("msg");
			    		MMSInfo.image = rs.getString("image");
			    		MMSInfo.replymsg = rs.getString("replymsg");
			    		MMSInfo.expertid = rs.getInt("expertid");	    		
			    		MMSInfo.majoridByMachine = rs.getInt("majoridByMachine");
			    		MMSInfo.majoridByHuman = rs.getInt("majoridByHuman");
			    		MMSInfo.answered = rs.getBoolean("answered");
			    		MMSInfo.used = rs.getBoolean("used");
			    		MMSInfo.classifiedByHuman = rs.getBoolean("classifiedByHuman");
			    		MMSInfo.encoded = rs.getString("encoded");
			    		MMSInfo.classifiedByMachine = rs.getBoolean("classifiedByMachine");
			    		MMSInfo.receivedDate = rs.getString("receivedDate");
			    		MMSInfo.answeredDate = rs.getString("answeredDate");
			    		MMSInfo.gatewayid = rs.getString("gatewayid");
			    		MMSInfo.sentexpert = rs.getBoolean("sentexpert");
			    		MMSInfo.sentfarmer = rs.getBoolean("sentfarmer");
			    		
			    		listMMS.add(MMSInfo);
			        }
				}
			} catch (Exception e) {

			}	
			return  listMMS;
		}
		
		//Get unclassified by human mms by Adapter for paging
		public ArrayList<MMS> getMMSByAnswerNoLimit(boolean answered, int expertid, int majorid, int month, int year)
		{
					ArrayList<MMS> listMMS = new ArrayList<MMS>();				
					try {
						DBAction db = new DBAction();
						if(expertid != -1 || majorid != -1 || month != -1 || year != -1)
						{
							String queryStr = "";
							queryStr += "SELECT * FROM tbl_mms WHERE used is false and answered is " + answered;
							if(expertid>=0) queryStr += " and expertid = "+ expertid;
							if(majorid>=0) queryStr += " and majoridByHuman =" + majorid;	
							if(month>=0) queryStr += " and month(receivedDate) =" + month;	
							if(year>=0) queryStr += " and year(receivedDate) =" + year;
							queryStr+=" order by answeredDate desc";
							//queryStr += "SELECT * FROM tbl_mms WHERE answered is " + answered + " and year(receivedDate) = '2013'";
							
							ResultSet rs = db.executeSelectStatments(queryStr);
					    	while(rs.next())
					    	{
					    		MMS MMSInfo = new MMS();
					    		MMSInfo.id = rs.getInt("id");
					    		MMSInfo.sender = rs.getString("sender");	
					    		MMSInfo.receivedMms = rs.getBoolean("receivedMms");
					    		MMSInfo.msg = rs.getString("msg");
					    		MMSInfo.image = rs.getString("image");
					    		MMSInfo.replymsg = rs.getString("replymsg");
					    		MMSInfo.expertid = rs.getInt("expertid");	    		
					    		MMSInfo.majoridByMachine = rs.getInt("majoridByMachine");
					    		MMSInfo.majoridByHuman = rs.getInt("majoridByHuman");
					    		MMSInfo.answered = rs.getBoolean("answered");
					    		MMSInfo.used = rs.getBoolean("used");
					    		MMSInfo.classifiedByHuman = rs.getBoolean("classifiedByHuman");
					    		MMSInfo.encoded = rs.getString("encoded");
					    		MMSInfo.classifiedByMachine = rs.getBoolean("classifiedByMachine");
					    		MMSInfo.receivedDate = rs.getString("receivedDate");
					    		MMSInfo.answeredDate = rs.getString("answeredDate");
					    		MMSInfo.gatewayid = rs.getString("gatewayid");
					    		MMSInfo.sentexpert = rs.getBoolean("sentexpert");
					    		MMSInfo.sentfarmer = rs.getBoolean("sentfarmer");
					    		
					    		listMMS.add(MMSInfo);
					    	}
						}
						else
						{
							ResultSet rs = db.executeSelectStatments(""+
								"SELECT * " +
								"FROM tbl_mms " +
								"WHERE used is false and answered is " + answered  + " order by answeredDate desc");
						
					    	while(rs.next())
					    	{
					    		MMS MMSInfo = new MMS();
					    		MMSInfo.id = rs.getInt("id");
					    		MMSInfo.sender = rs.getString("sender");	
					    		MMSInfo.receivedMms = rs.getBoolean("receivedMms");
					    		MMSInfo.msg = rs.getString("msg");
					    		MMSInfo.image = rs.getString("image");
					    		MMSInfo.replymsg = rs.getString("replymsg");
					    		MMSInfo.expertid = rs.getInt("expertid");	    		
					    		MMSInfo.majoridByMachine = rs.getInt("majoridByMachine");
					    		MMSInfo.majoridByHuman = rs.getInt("majoridByHuman");
					    		MMSInfo.answered = rs.getBoolean("answered");
					    		MMSInfo.used = rs.getBoolean("used");
					    		MMSInfo.classifiedByHuman = rs.getBoolean("classifiedByHuman");
					    		MMSInfo.encoded = rs.getString("encoded");
					    		MMSInfo.classifiedByMachine = rs.getBoolean("classifiedByMachine");
					    		MMSInfo.receivedDate = rs.getString("receivedDate");
					    		MMSInfo.answeredDate = rs.getString("answeredDate");
					    		MMSInfo.gatewayid = rs.getString("gatewayid");
					    		MMSInfo.sentexpert = rs.getBoolean("sentexpert");
					    		MMSInfo.sentfarmer = rs.getBoolean("sentfarmer");
					    		
					    		listMMS.add(MMSInfo);				    		
					        }
						}
					} catch (Exception e) {

					}	
				return  listMMS;
		}
	
		public ArrayList<MMS> getMMSByAnswer(boolean answered)
		{
					ArrayList<MMS> listMMS = new ArrayList<MMS>();				
					try {
						DBAction db = new DBAction();
						ResultSet rs = db.executeSelectStatments(""+
								"SELECT * " +
								"FROM tbl_mms " +
								"WHERE answered is " + answered + " order by answeredDate desc");
						
					    	while(rs.next())
					    	{
					    		MMS MMSInfo = new MMS();
					    		MMSInfo.id = rs.getInt("id");
					    		MMSInfo.sender = rs.getString("sender");	
					    		MMSInfo.receivedMms = rs.getBoolean("receivedMms");
					    		MMSInfo.msg = rs.getString("msg");
					    		MMSInfo.image = rs.getString("image");
					    		MMSInfo.replymsg = rs.getString("replymsg");
					    		MMSInfo.expertid = rs.getInt("expertid");	    		
					    		MMSInfo.majoridByMachine = rs.getInt("majoridByMachine");
					    		MMSInfo.majoridByHuman = rs.getInt("majoridByHuman");
					    		MMSInfo.answered = rs.getBoolean("answered");
					    		MMSInfo.used = rs.getBoolean("used");
					    		MMSInfo.classifiedByHuman = rs.getBoolean("classifiedByHuman");
					    		MMSInfo.encoded = rs.getString("encoded");
					    		MMSInfo.classifiedByMachine = rs.getBoolean("classifiedByMachine");
					    		MMSInfo.receivedDate = rs.getString("receivedDate");
					    		MMSInfo.answeredDate = rs.getString("answeredDate");
					    		MMSInfo.gatewayid = rs.getString("gatewayid");
					    		MMSInfo.sentexpert = rs.getBoolean("sentexpert");
					    		MMSInfo.sentfarmer = rs.getBoolean("sentfarmer");
					    		
					    		listMMS.add(MMSInfo);				    		
					        }
					} catch (Exception e) {

					}	
				return  listMMS;
		}
		
	//get All thi ket qua tra ve can 1 list hay array => coi lai sau	
	public ArrayList<MMS> getAllMMS(){
		ArrayList<MMS> listMMS = new ArrayList<MMS>();	
		try {
			DBAction db = new DBAction();
			ResultSet rs = db.executeSelectStatments("" + 
					"SELECT * " +
					"FROM tbl_mms");					
	    	while(rs.next()){
	    		MMS MMSInfo = new MMS();
	    		MMSInfo.id = rs.getInt("id");
	    		MMSInfo.sender = rs.getString("sender");
	    		MMSInfo.receivedMms = rs.getBoolean("receivedMms");
	    		MMSInfo.msg = rs.getString("msg");
	    		MMSInfo.image = rs.getString("image");
	    		MMSInfo.replymsg = rs.getString("replymsg");
	    		MMSInfo.expertid = rs.getInt("expertid");	    		
	    		MMSInfo.majoridByMachine = rs.getInt("majoridByMachine");
	    		MMSInfo.majoridByHuman = rs.getInt("majoridByHuman");
	    		MMSInfo.answered = rs.getBoolean("answered");
	    		MMSInfo.used = rs.getBoolean("used");
	    		MMSInfo.classifiedByHuman = rs.getBoolean("classifiedByHuman");
	    		MMSInfo.encoded = rs.getString("encoded");
	    		MMSInfo.classifiedByMachine = rs.getBoolean("classifiedByMachine");	 
	    		MMSInfo.receivedDate = rs.getString("receivedDate");
	    		MMSInfo.answeredDate = rs.getString("answeredDate");
	    		MMSInfo.gatewayid = rs.getString("gatewayid");
	    		MMSInfo.sentexpert = rs.getBoolean("sentexpert");
	    		MMSInfo.sentfarmer = rs.getBoolean("sentfarmer");
	    		
	    		listMMS.add(MMSInfo);
	        }
		} catch (Exception e) {

		}	
		return listMMS;
	}
	
	public int updateMMSById(MMS MMSInfo){
		try {
			DBAction db = new DBAction();
			int rs = db.executeUpdateStatments("" + 
					"update tbl_mms " +		
					"set sender='" + MMSInfo.sender + "',"+
					"receivedMms=" + MMSInfo.receivedMms + ","+
					"msg='" + MMSInfo.msg + "',"+
					"expertid=" + MMSInfo.expertid + ","+
					"majoridByMachine=" + MMSInfo.majoridByMachine + ","+
					"majoridByHuman=" + MMSInfo.majoridByHuman + ","+
					"answered=" + MMSInfo.answered + ","+
					"classifiedByHuman=" + MMSInfo.classifiedByHuman + ","+
					"encoded='" + MMSInfo.encoded + "',"+
					"image='" + MMSInfo.image + "',"+
					"classifiedByMachine=" + MMSInfo.classifiedByMachine + ","+
					"replymsg='" + MMSInfo.replymsg + "'," +	
					"receivedDate='" + MMSInfo.receivedDate + "'," +	
					"answeredDate='" + MMSInfo.answeredDate + "'," +	
					"used=" + MMSInfo.used + ", "+
					"gatewayid='" + MMSInfo.gatewayid + "',"+
					"sentexpert=" + MMSInfo.sentexpert + ", "+
					"sentfarmer=" + MMSInfo.sentfarmer + " "+
					"where id= " + MMSInfo.id);
			return rs;
	        }
		catch (Exception e) {
			return 0;
		}			
	}
	
	public int expertReply(MMS MMSInfo){
		try {
			DBAction db = new DBAction();
			int rs = db.executeUpdateStatments("" + 
					"update tbl_mms " +		
					"set sender='" + MMSInfo.sender + "',"+
					"receivedMms=" + MMSInfo.receivedMms + ","+
					"msg='" + MMSInfo.msg + "',"+
					"expertid=" + MMSInfo.expertid + ","+
					"majoridByMachine=" + MMSInfo.majoridByMachine + ","+
					"majoridByHuman=" + MMSInfo.majoridByHuman + ","+
					"answered=" + MMSInfo.answered + ","+
					"classifiedByHuman=" + MMSInfo.classifiedByHuman + ","+
					"encoded='" + MMSInfo.encoded + "',"+
					"image='" + MMSInfo.image + "',"+
					"classifiedByMachine=" + MMSInfo.classifiedByMachine + ","+
					"replymsg='" + MMSInfo.replymsg + "'," +	
					"receivedDate='" + MMSInfo.receivedDate + "'," +	
					"answeredDate='" + MMSInfo.answeredDate + "'," +	
					"used=" + MMSInfo.used + ", "+
					"gatewayid='" + MMSInfo.gatewayid + "',"+
					"sentexpert=" + MMSInfo.sentexpert + ", "+
					"sentfarmer=" + MMSInfo.sentfarmer + " "+
					"where id= " + MMSInfo.id + " and answered is false");
			return rs;
	        }
		catch (Exception e) {
			return 0;
		}			
	}
	
	public int updateUsedMMSById(MMS MMSInfo){
		try {
			DBAction db = new DBAction();
			int rs = db.executeUpdateStatments("" + 
					"update tbl_mms " +		
					"set sender='" + MMSInfo.sender + "',"+
					"receivedMms=" + MMSInfo.receivedMms + ","+
					"msg='" + MMSInfo.msg + "',"+
					"expertid=" + MMSInfo.expertid + ","+
					"majoridByMachine=" + MMSInfo.majoridByMachine + ","+
					"majoridByHuman=" + MMSInfo.majoridByHuman + ","+
					"answered=" + MMSInfo.answered + ","+
					"classifiedByHuman=" + MMSInfo.classifiedByHuman + ","+
					"encoded='" + MMSInfo.encoded + "',"+
					"image='" + MMSInfo.image + "',"+
					"classifiedByMachine=" + MMSInfo.classifiedByMachine + ","+
					"replymsg='" + MMSInfo.replymsg + "'," +	
					"receivedDate='" + MMSInfo.receivedDate + "'," +	
					"answeredDate='" + MMSInfo.answeredDate + "'," +	
					"used=" + MMSInfo.used + ", "+
					"gatewayid='" + MMSInfo.gatewayid + "',"+
					"sentexpert=" + MMSInfo.sentexpert + ", "+
					"sentfarmer=" + MMSInfo.sentfarmer + " "+
					"where id= " + MMSInfo.id );
			return rs;
	        }
		catch (Exception e) {
			return 0;
		}			
	}
	
	public int deleteMMSById(int id){
		try {
			DBAction db = new DBAction();
			int rs = db.executeDeleteStatments("" + 
					"delete " + 
	        		"FROM tbl_mms " +	        		
					"where id= " + id);					
			return rs;
	        }		
		catch (Exception e) {
			return 0;
		}			
	}
	
	public int addMMS(MMS MMSInfo){		
		try {
			DBAction db = new DBAction();
			int rs = db.executeInsertStatments("" + 
					"insert into tbl_mms(sender,receivedMms,msg,expertid,majoridByMachine,majoridByHuman,answered,classifiedByMachine,classifiedByHuman,encoded,image,replymsg,used,receivedDate,answeredDate,sentexpert,sentfarmer,gatewayid) " +					
					"values('"+ MMSInfo.sender +"',"+ MMSInfo.receivedMms +",'"+ MMSInfo.msg +"',"+ MMSInfo.expertid +","+ MMSInfo.majoridByMachine +","+ MMSInfo.majoridByHuman +","+ MMSInfo.answered +","+ MMSInfo.classifiedByMachine +","+ MMSInfo.classifiedByHuman +",'"+ MMSInfo.encoded +"','"+ MMSInfo.image +"','"+ MMSInfo.replymsg +"',"+ MMSInfo.used +",'"+ MMSInfo.receivedDate +"','"+ MMSInfo.answeredDate +"',"+ MMSInfo.sentexpert +","+ MMSInfo.sentfarmer+",'"+ MMSInfo.gatewayid +"')");					
			return rs;
	        }		
		catch (Exception e) {
			return 0;
		}			
	}	
	
	/*public String getAccuracy()
	{		
		String accuracy ="";
		try {			
			   BufferedReader stopwordFile = new BufferedReader(new InputStreamReader(new FileInputStream("./files/accuracy.txt"), "UTF8"));
			   System.out.print("Line1: ");
			   for(String line;(line = stopwordFile.readLine()) != null;)
				{
					//if(line.compareTo("")!=0) accuracy = line.trim().toLowerCase();
				   	System.out.print("Line: " +line.trim().toLowerCase());
				  
				}
			   stopwordFile.close();
	        }		
		catch (Exception e) {			
		}
		return accuracy;
	}*/
}
