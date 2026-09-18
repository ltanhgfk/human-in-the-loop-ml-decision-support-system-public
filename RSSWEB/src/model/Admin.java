package model;

import java.sql.ResultSet;
//import java.util.ArrayList;

import dao.DBAction;

public class Admin {
	private int ID_ADMIN;
	private String USERNAME;
	private String PASSWORD;
	
	public Admin(){
		this.ID_ADMIN = 0;
		this.USERNAME = "";
		this.PASSWORD = "";
	}
	
	
	public int getIdAdmin() {
		return ID_ADMIN;
	}
	public void setIdAdmin(int ID_ADMIN) {
		this.ID_ADMIN = ID_ADMIN;
	}
	
	public String getUserName() {
		return USERNAME;
	}
	public void setUserName(String UserName) {
		this.USERNAME = UserName;
	}
	
	public String getPassword() {
		return PASSWORD;
	}
	public void setPassword(String Password) {
		this.PASSWORD = Password;
	}
	
	
	public Admin getAdminByUserName(String username, String password){
		Admin Info = new Admin();	
		try {
			DBAction db = new DBAction();
			ResultSet rs = db.executeSelectStatments("" + 
	        		"SELECT * " + 
	        		"FROM admin " +
	        		"WHERE USERNAME = '" + username + "' and PASSWORD = MD5('"+ password +"') ");
	    	while(rs.next()){//khong the khong co "rs.next"
	            Info.ID_ADMIN = rs.getInt("ID_ADMIN");
	            Info.USERNAME = rs.getString("USERNAME");
	            Info.PASSWORD = rs.getString("PASSWORD");	            
	        }
		} catch (Exception e) {

		}	
		return Info;
	}
}
