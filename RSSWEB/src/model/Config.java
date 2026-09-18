package model;

import java.sql.ResultSet;
import java.util.ArrayList;

import dao.DBAction;

public class Config {
	private int id;
	private String code;
	private int valueint;
	private String name;
	private String valuechar;	
	private boolean valuebool;	
	
	
	public Config(){
		this.id = 0;
		this.valueint = 0;
		this.name = "";
		this.code = "";
		this.valuechar = "";		
		this.valuebool = false;		
	}	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public int getvalueint() {
		return valueint;
	}
	public void setvalueint(int valueint) {
		this.valueint = valueint;
	}
	
	public String getcode() {
		return code;
	}
	public void setcode(String code) {
		this.code = code;
	}
	
	public String getname() {
		return name;
	}
	public void setname(String name) {
		this.name = name;
	}
	
	public String getvaluechar() {
		return valuechar;
	}
	public void setvaluechar(String valuechar) {
		this.valuechar = valuechar;
	}	
	
	public boolean getvaluebool() {
		return valuebool;
	}
	public void setvaluebool(boolean valuebool) {
		this.valuebool = valuebool;
	}	
	
	public Config getConfigById(int id){
		Config ConfigInfo = new Config();	
		try {
			DBAction db = new DBAction();
			ResultSet rs = db.executeSelectStatments("" + 
	        		"SELECT * " + 
	        		"FROM tbl_config " +
	        		"WHERE id = " + id);
	    	while(rs.next()){
	            ConfigInfo.id = rs.getInt("id");
	            ConfigInfo.code = rs.getString("code");
	            ConfigInfo.valueint = rs.getInt("valueint");
	            ConfigInfo.name = rs.getString("name");
	            ConfigInfo.valuechar = rs.getString("valuechar");
	            ConfigInfo.valuebool= rs.getBoolean("valuebool");
	        }
		} catch (Exception e) {

		}	
		return ConfigInfo;
	}
	
	public Config getConfigBycode(String codestr){
		Config ConfigInfo = new Config();	
		try {
			DBAction db = new DBAction();
			ResultSet rs = db.executeSelectStatments("" + 
	        		"SELECT * " + 
	        		"FROM tbl_config " +
	        		"WHERE code = '" + codestr + "'");
	    	while(rs.next()){
	            ConfigInfo.id = rs.getInt("id");
	            ConfigInfo.code = rs.getString("code");
	            ConfigInfo.valueint = rs.getInt("valueint");
	            ConfigInfo.name = rs.getString("name");
	            ConfigInfo.valuechar = rs.getString("valuechar");
	            ConfigInfo.valuebool= rs.getBoolean("valuebool");
	        }
		} catch (Exception e) {

		}	
		return ConfigInfo;
	}	
	
	public ArrayList<Config> getAllConfig(){
		ArrayList<Config> ConfigList = new ArrayList<Config>();	
		try {
			DBAction db = new DBAction();
			ResultSet rs = db.executeSelectStatments("" + 
	        		"SELECT * " + 
	        		"FROM tbl_config");
	    	while(rs.next()){
	    		Config ConfigInfo = new Config();
	            ConfigInfo.id = rs.getInt("id");
	            ConfigInfo.code = rs.getString("code");
	            ConfigInfo.valueint = rs.getInt("valueint");
	            ConfigInfo.name = rs.getString("name");
	            ConfigInfo.valuechar = rs.getString("valuechar");	            
	            ConfigInfo.valuebool= rs.getBoolean("valuebool");	 
	            ConfigList.add(ConfigInfo);
	        }
		} catch (Exception e) {

		}	
		return ConfigList;
	}
	
	/*public int updateConfigById(Config ConfigInfo){	
		try {
			DBAction db = new DBAction();
			int rs = db.executeUpdateStatments("update tbl_config set code = '"+ ConfigInfo.code +"',name = '"+ ConfigInfo.name +"',valuechar = '"+ ConfigInfo.valuechar +"',valuebool = '"+ ConfigInfo.valuebool +"',valueint = "+ ConfigInfo.valueint +" where id = "+ ConfigInfo.id);	        		
				return rs;
			} 
			catch (Exception e) 
			{
				return 0;
			}		
	}*/
	
	public int updateConfigById(Config ConfigInfo){	
		try {
			DBAction db = new DBAction();
			int rs = db.executeUpdateStatments("update tbl_config set valuechar = '"+ ConfigInfo.valuechar +"', valuebool = '"+ ConfigInfo.valuebool +"', valueint = '"+ ConfigInfo.valueint +"' where id = "+ ConfigInfo.id);	        		
				return rs;
			} 
			catch (Exception e) 
			{
				return 0;
			}		
	}
	
	public int updateConfigByCode(Config ConfigInfo){	
		try {
			DBAction db = new DBAction();
			int rs = db.executeUpdateStatments("update tbl_config set valuechar = '"+ ConfigInfo.valuechar +"', valuebool = "+ ConfigInfo.valuebool +", valueint = "+ ConfigInfo.valueint +" where code = '"+ ConfigInfo.code + "'");	        		
				return rs;
			} 
			catch (Exception e) 
			{
				return 0;
			}		
	}
	
	/*public int deleteConfigById(int id){			
		try {
			DBAction db = new DBAction();
			int rs = db.executeDeleteStatments("" + 
	        		"delete " + 
	        		"FROM tbl_config " +
	        		"where id=" + id);	 
			return rs;
			} 
			catch (Exception e) 
			{
				return 0;
			}		
	}*/
	
	/*public int addConfig(Config ConfigInfo){
		try {
			DBAction db = new DBAction();
			int rs = db.executeInsertStatments("" + 
	        		"insert into tbl_config(name,Config,valuebool,valueint) " + 
	        		"values('"+ ConfigInfo.name +"','"+ ConfigInfo.valuechar +"','"+ ConfigInfo.valuebool +"',valueint = "+ ConfigInfo.valueint +")");//"+ConfigInfo.expression+",	        		
			return rs;
			} 
			catch (Exception e) 
			{
				return 0;
			}		
	}*/
}
