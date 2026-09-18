package model;

import java.sql.ResultSet;
import java.util.ArrayList;

import dao.DBAction;

public class ChuyenMon {
	private int id;
	private String code;
	private String name;	
	private String note;	
	
	
	public ChuyenMon(){
		this.id = 0;
		this.code = "";
		this.name = "";		
		this.note = "";		
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	
	
	public ChuyenMon getMajorById(int id){
		ChuyenMon majorInfo = new ChuyenMon();	
		try {
			DBAction db = new DBAction();
			ResultSet rs = db.executeSelectStatments("" + 
	        		"SELECT * " + 
	        		"FROM tbl_major " +
	        		"WHERE id = " + id);
	    	while(rs.next()){
	            majorInfo.id = rs.getInt("id");
	            majorInfo.code = rs.getString("code");
	            majorInfo.name = rs.getString("name");	            
	            majorInfo.note= rs.getString("note");	           
	        }
		} catch (Exception e) {

		}	
		return majorInfo;
	}
	
	public ChuyenMon getMajorByClass(String classid){
		ChuyenMon majorInfo = new ChuyenMon();	
		try {
			DBAction db = new DBAction();
			ResultSet rs = db.executeSelectStatments("" + 
	        		"SELECT * " + 
	        		"FROM tbl_major " +
	        		"WHERE code = '" + classid + "'");
	    	while(rs.next()){
	            majorInfo.id = rs.getInt("id");
	            majorInfo.code = rs.getString("code");
	            majorInfo.name = rs.getString("name");	            
	            majorInfo.note= rs.getString("note");	           
	        }
		} catch (Exception e) {

		}	
		return majorInfo;
	}
	
	public ArrayList<ChuyenMon> getAllMajor(){
		ArrayList<ChuyenMon> majorList = new ArrayList<ChuyenMon>();	
		try {
			DBAction db = new DBAction();
			ResultSet rs = db.executeSelectStatments("" + 
	        		"SELECT * " + 
	        		"FROM tbl_major");
	    	while(rs.next()){
	    		ChuyenMon majorInfo = new ChuyenMon();
	            majorInfo.id = rs.getInt("id");
	            majorInfo.code = rs.getString("code");
	            majorInfo.name = rs.getString("name");	            
	            majorInfo.note= rs.getString("note");	 
	            majorList.add(majorInfo);
	        }
		} catch (Exception e) {

		}	
		return majorList;
	}
	
public int updateMajorById(ChuyenMon majorInfo){	
		try {
			DBAction db = new DBAction();
			int rs = db.executeUpdateStatments("update tbl_major set code = '"+ majorInfo.code +"',name = '"+ majorInfo.name +"',note = '"+ majorInfo.note +"' where id = "+ majorInfo.id);	        		
				return rs;
			} 
			catch (Exception e) 
			{
				return 0;
			}		
	}
	
	public int deleteMajorById(int id){			
		try {
			DBAction db = new DBAction();
			int rs = db.executeDeleteStatments("" + 
	        		"delete " + 
	        		"FROM tbl_major " +
	        		"where id=" + id);	 
			return rs;
			} 
			catch (Exception e) 
			{
				return 0;
			}		
	}
	
	public int addMajor(ChuyenMon majorInfo){
		try {
			DBAction db = new DBAction();
			int rs = db.executeInsertStatments("" + 
	        		"insert into tbl_major(code,name,note) " + 
	        		"values('"+ majorInfo.code +"','"+ majorInfo.name +"','"+ majorInfo.note +"')");//"+majorInfo.expression+",	        		
			return rs;
			} 
			catch (Exception e) 
			{
				return 0;
			}		
	}	
}
