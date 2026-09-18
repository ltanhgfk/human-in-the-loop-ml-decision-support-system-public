package model;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;

import dao.DBAction;

public class Content {
	private int id;
	private String name;
	private String code;
	private String content;
	private String createddate;
	private String modifieddate;
	private int expertid;
	private String keywords;
	//private String cate;
	
	//1
	public int getid() {
		return id;
	}
	public void setid(int id) {
		this.id = id;
	}
	//2
	public String getname() {
		return name;
	}
	public void setname(String name) {
		this.name = name;
	}
	//2
	public String getcode() {
		return code;
	}
	public void setcode(String code) {
		this.code = code;
	}
	//7//mo ta chi tiet ve loai nay
	public String getcontent() {
		return content;
	}
	public void setcontent(String content) {
		this.content = content;
	}	
	//11
	public String getcreateddate() {
		return createddate;
	}
	public void setcreateddate(String createddate) {
		this.createddate = createddate;
	}
	//12
	public String getmodifieddate() {
		return modifieddate;
	}
	public void setmodifieddate(String modifieddate) {
		this.modifieddate = modifieddate;
	}
	//13
	public int getexpertid() {
		return expertid;
	}
	public void setexpertid(int expertid) {
		this.expertid = expertid;
	}
	//
	public String getkeywords() {
		return keywords;
	}
	public void setkeywords(String keywords) {
		this.keywords = keywords;
	}
	
	public Content getContentById(int id){
		Content ContentInfo = new Content();	
		try {
			DBAction db = new DBAction();
			ResultSet rs = db.executeSelectStatments("" + 
	        		"SELECT * " + 
	        		"FROM tbl_content " +
	        		"WHERE id = " + id);
	    	while(rs.next()){
	    		ContentInfo.id = rs.getInt("id");
	            ContentInfo.code = rs.getString("code");
	            ContentInfo.name = rs.getString("name");	            
	            ContentInfo.content= rs.getString("content");
	            ContentInfo.createddate= rs.getString("createddate");
	            ContentInfo.modifieddate= rs.getString("modifieddate");
	            ContentInfo.expertid= rs.getInt("expertid");
	            ContentInfo.keywords= rs.getString("keywords");	            	           
	        }
		} catch (Exception e) {

		}	
		return ContentInfo;
	}
	
	public ArrayList<Content> getContentByExpertId(int expertid){
		ArrayList<Content> ContentList = new ArrayList<Content>();	
		try {
			DBAction db = new DBAction();
			ResultSet rs = db.executeSelectStatments("" + 
	        		"SELECT * " + 
	        		"FROM tbl_content " +
	        		"WHERE expertid = " + expertid);
	    	while(rs.next()){
	    		Content ContentInfo = new Content();
	    		ContentInfo.id = rs.getInt("id");
	            ContentInfo.code = rs.getString("code");
	            ContentInfo.name = rs.getString("name");	            
	            ContentInfo.content= rs.getString("content");
	            ContentInfo.createddate= rs.getString("createddate");
	            ContentInfo.modifieddate= rs.getString("modifieddate");
	            ContentInfo.expertid= rs.getInt("expertid");
	            ContentInfo.keywords= rs.getString("keywords");	   
	            ContentList.add(ContentInfo);
	        }
		} catch (Exception e) {

		}	
		return ContentList;
	}
	public Content getContentByClass(String classid){
		Content ContentInfo = new Content();	
		try {
			DBAction db = new DBAction();
			ResultSet rs = db.executeSelectStatments("" + 
	        		"SELECT * " + 
	        		"FROM tbl_content " +
	        		"WHERE code = '" + classid + "'");
	    	while(rs.next()){
	    		ContentInfo.id = rs.getInt("id");
	            ContentInfo.code = rs.getString("code");
	            ContentInfo.name = rs.getString("name");	            
	            ContentInfo.content= rs.getString("content");
	            ContentInfo.createddate= rs.getString("createddate");
	            ContentInfo.modifieddate= rs.getString("modifieddate");
	            ContentInfo.expertid= rs.getInt("expertid");
	            ContentInfo.keywords= rs.getString("keywords");	            	            
	        }
		} catch (Exception e) {

		}	
		return ContentInfo;
	}
	
	public ArrayList<Content> getAllContent(){
		ArrayList<Content> ContentList = new ArrayList<Content>();	
		try {
			DBAction db = new DBAction();
			ResultSet rs = db.executeSelectStatments("" + 
	        		"SELECT * " + 
	        		"FROM tbl_content");
	    	while(rs.next()){
	    		Content ContentInfo = new Content();
	    		ContentInfo.id = rs.getInt("id");
	            ContentInfo.code = rs.getString("code");
	            ContentInfo.name = rs.getString("name");	            
	            ContentInfo.content= rs.getString("content");
	            ContentInfo.createddate= rs.getString("createddate");
	            ContentInfo.modifieddate= rs.getString("modifieddate");
	            ContentInfo.expertid= rs.getInt("expertid");
	            ContentInfo.keywords= rs.getString("keywords");	            	           
	            
	            ContentList.add(ContentInfo);
	        }
		} catch (Exception e) {

		}	
		return ContentList;
	}
	
	public int updateContentById(Content ContentInfo){	
		try {
			DBAction db = new DBAction();
			//int rs = db.executeUpdateStatments("update tbl_content set code = '"+ ContentInfo.code +"',name = '"+ ContentInfo.name +"',content = '"+ ContentInfo.content +"',createddate = '"+ ContentInfo.createddate +"',modifieddate = '"+ ContentInfo.modifieddate +"',keywords = '"+ ContentInfo.keywords +"',expertid = "+ ContentInfo.expertid +" where id = "+ ContentInfo.id);
			int rs = db.executeUpdateStatments("update tbl_content set name = '"+ ContentInfo.name +"',content = '"+ ContentInfo.content +"',createddate = '"+ ContentInfo.createddate +"',modifieddate = '"+ ContentInfo.modifieddate +"',keywords = '"+ ContentInfo.keywords +"',expertid = "+ ContentInfo.expertid +" where id = "+ ContentInfo.id);
				return rs;
			} 
			catch (Exception e) 
			{
				return 0;
			}		
	}
	
	public int deleteContentById(int id){			
		try {
			DBAction db = new DBAction();
			int rs = db.executeDeleteStatments("" + 
	        		"delete " + 
	        		"FROM tbl_content " +
	        		"where id=" + id);	 
			return rs;
			} 
			catch (Exception e) 
			{
				return 0;
			}		
	}
	
	public int addContent(Content ContentInfo){
		try {
			DBAction db = new DBAction();
			int rs = db.executeInsertStatments("" + 
					"insert into tbl_content(name,content,createddate,modifieddate,expertid,keywords) " + 
	        		"values('"+ ContentInfo.name +"','"+ ContentInfo.content +"','"+ ContentInfo.createddate +"','"+ ContentInfo.modifieddate +"',"+ ContentInfo.expertid +",'"+ ContentInfo.keywords +"')");       		
	        		
	        		//"insert into tbl_content(code,name,content,createddate,modifieddate,expertid,keywords) " + 
	        		//"values('"+ ContentInfo.code +"','"+ ContentInfo.name +"','"+ ContentInfo.content +"','"+ ContentInfo.createddate +"','"+ ContentInfo.modifieddate +"',"+ ContentInfo.expertid +",'"+ ContentInfo.keywords +"')");       		
	        		//"values('2','tên','nội dung','04-02-2014 14:38:01','04-02-2014 14:38:01',1)");
			return rs;
			} 
			catch (Exception e) 
			{
				return 0;
			}		
	}	
	
}
