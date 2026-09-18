package model;

import java.sql.ResultSet;
import java.util.ArrayList;

import dao.DBAction;

public class ContentMMS {
	private int content_id;
	private int mms_id;
	
	
	public ContentMMS(){
		this.content_id = 0;
		this.mms_id = 0;		
	}	
	
	public int getcontent_id() {
		return content_id;
	}
	public void setcontent_id(int content_id) {
		this.content_id = content_id;
	}
	
	public int getmms_id() {
		return mms_id;
	}
	public void setmms_id(int mms_id) {
		this.mms_id = mms_id;
	}	
	
	public ArrayList<ContentMMS> getByContent_id(int id){
		ArrayList<ContentMMS> dscm = new ArrayList<ContentMMS>();		
		try {
			DBAction db = new DBAction();
			ResultSet rs = db.executeSelectStatments("" + 
	        		"SELECT * " + 
	        		"FROM tbl_content_mms " +
	        		"WHERE content_id = " + id);
	    	while(rs.next()){
	    		ContentMMS Info = new ContentMMS();	
	            Info.content_id = rs.getInt("content_id");
	            Info.mms_id = rs.getInt("mms_id");
	            dscm.add(Info);
	        }
		} catch (Exception e) {

		}	
		return dscm;
	}
	
	public ArrayList<ContentMMS> getByMms_id(int id){
		ArrayList<ContentMMS> dscm = new ArrayList<ContentMMS>();		
		try {
			DBAction db = new DBAction();
			ResultSet rs = db.executeSelectStatments("" + 
	        		"SELECT * " + 
	        		"FROM tbl_content_mms " +
	        		"WHERE mms_id = " + id);
	    	while(rs.next()){
	    		ContentMMS Info = new ContentMMS();	
	            Info.content_id = rs.getInt("content_id");
	            Info.mms_id = rs.getInt("mms_id");
	            dscm.add(Info);
	        }
		} catch (Exception e) {

		}	
		return dscm;
	}
	
	public int deleteByMms_id(int id){			
		try {
			DBAction db = new DBAction();
			int rs = db.executeDeleteStatments("" + 
	        		"delete " + 
	        		"FROM tbl_content_mms " +
	        		"where mms_id=" + id);	 
			return rs;
			} 
			catch (Exception e) 
			{
				return 0;
			}		
	}
	
	public int deleteByContent_id(int id){			
		try {
			DBAction db = new DBAction();
			int rs = db.executeDeleteStatments("" + 
	        		"delete " + 
	        		"FROM tbl_content_mms " +
	        		"where content_id=" + id);	 
			return rs;
			} 
			catch (Exception e) 
			{
				return 0;
			}		
	}
	
	public int addContentMMS(ContentMMS Info){		
		try {
			DBAction db = new DBAction();
			int rs = db.executeInsertStatments("" + 
	        		"insert into tbl_content_mms(content_id,mms_id) " + 
	        		"values("+ Info.content_id +","+ Info.mms_id +")");
			return rs;
			} 
			catch (Exception e) 
			{
				return 0;
			}		
	}	
}
