package model;

import java.sql.ResultSet;
import java.util.ArrayList;

import dao.DBAction;

public class ChuyenGiaChuyenMon {
	private int expertid;
	private int majorid;
	
	
	public ChuyenGiaChuyenMon(){
		this.expertid = 0;
		this.expertid = 0;		
	}	
	
	public int getExpertId() {
		return expertid;
	}
	public void setExpertId(int expertid) {
		this.expertid = expertid;
	}
	
	public int getMajorId() {
		return majorid;
	}
	public void setMajorId(int majorid) {
		this.majorid = majorid;
	}	
	
	public ArrayList<ChuyenGiaChuyenMon> getExpertMajorByExpertId(int id){
		ArrayList<ChuyenGiaChuyenMon> dscm = new ArrayList<ChuyenGiaChuyenMon>();		
		try {
			DBAction db = new DBAction();
			ResultSet rs = db.executeSelectStatments("" + 
	        		"SELECT * " + 
	        		"FROM tbl_expertmajor " +
	        		"WHERE expertid = " + id);
	    	while(rs.next()){
	    		ChuyenGiaChuyenMon Info = new ChuyenGiaChuyenMon();	
	            Info.expertid = rs.getInt("expertid");
	            Info.majorid = rs.getInt("majorid");
	            dscm.add(Info);
	        }
		} catch (Exception e) {

		}	
		return dscm;
	}
	
	public ArrayList<ChuyenGiaChuyenMon> getExpertMajorByMajorId(int id){
		ArrayList<ChuyenGiaChuyenMon> dscm = new ArrayList<ChuyenGiaChuyenMon>();		
		try {
			DBAction db = new DBAction();
			ResultSet rs = db.executeSelectStatments("" + 
	        		"SELECT * " + 
	        		"FROM tbl_expertmajor " +
	        		"WHERE majorid = " + id);
	    	while(rs.next()){
	    		ChuyenGiaChuyenMon Info = new ChuyenGiaChuyenMon();	
	            Info.expertid = rs.getInt("expertid");
	            Info.majorid = rs.getInt("majorid");
	            dscm.add(Info);
	        }
		} catch (Exception e) {

		}	
		return dscm;
	}
	
	public int deleteExpertMajorByMajorId(int id){			
		try {
			DBAction db = new DBAction();
			int rs = db.executeDeleteStatments("" + 
	        		"delete " + 
	        		"FROM tbl_expertmajor " +
	        		"where majorid=" + id);	 
			return rs;
			} 
			catch (Exception e) 
			{
				return 0;
			}		
	}
	
	public int deleteExpertMajorByExpertId(int id){			
		try {
			DBAction db = new DBAction();
			int rs = db.executeDeleteStatments("" + 
	        		"delete " + 
	        		"FROM tbl_expertmajor " +
	        		"where expertid=" + id);	 
			return rs;
			} 
			catch (Exception e) 
			{
				return 0;
			}		
	}
	
	public int addMajor(ChuyenGiaChuyenMon Info){		
		try {
			DBAction db = new DBAction();
			int rs = db.executeInsertStatments("" + 
	        		"insert into tbl_expertmajor(expertid,majorid) " + 
	        		"values("+ Info.expertid +","+ Info.majorid +")");
			return rs;
			} 
			catch (Exception e) 
			{
				return 0;
			}		
	}	
}
