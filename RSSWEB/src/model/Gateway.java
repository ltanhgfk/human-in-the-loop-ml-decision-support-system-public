package model;

import java.sql.ResultSet;
import java.util.ArrayList;

import dao.DBAction;

public class Gateway {
	private int id;
	private int port;
	private String operator;
	private String gatewayip;	
	private String mmsc;
	private String mmscnumber;
	
	
	public Gateway(){
		this.id = 0;
		this.port = 0;
		this.operator = "";
		this.gatewayip = "";		
		this.mmsc = "";		
		this.mmscnumber = "";
	}	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public int getport() {
		return port;
	}
	public void setport(int port) {
		this.port = port;
	}
	
	public String getoperator() {
		return operator;
	}
	public void setoperator(String operator) {
		this.operator = operator;
	}
	
	public String getgatewayip() {
		return gatewayip;
	}
	public void setgatewayip(String gatewayip) {
		this.gatewayip = gatewayip;
	}	
	
	public String getmmsc() {
		return mmsc;
	}
	public void setmmsc(String mmsc) {
		this.mmsc = mmsc;
	}	
	
	public String getmmscnumber() {
		return mmscnumber;
	}
	public void setmmscnumber(String mmscnumber) {
		this.mmscnumber = mmscnumber;
	}	
	
	public Gateway getGatewayById(int id){
		Gateway GatewayInfo = new Gateway();	
		try {
			DBAction db = new DBAction();
			ResultSet rs = db.executeSelectStatments("" + 
	        		"SELECT * " + 
	        		"FROM tbl_gateway " +
	        		"WHERE id = " + id);
	    	while(rs.next()){
	            GatewayInfo.id = rs.getInt("id");
	            GatewayInfo.port = rs.getInt("port");
	            GatewayInfo.operator = rs.getString("operator");
	            GatewayInfo.gatewayip = rs.getString("gatewayip");	            
	            GatewayInfo.mmsc= rs.getString("mmsc");	    
	            GatewayInfo.mmscnumber= rs.getString("mmscnumber");
	        }
		} catch (Exception e) {

		}	
		return GatewayInfo;
	}
	
	public Gateway getGatewayByMmscnumber(String mmscnumber){
		Gateway GatewayInfo = new Gateway();	
		try {
			DBAction db = new DBAction();
			ResultSet rs = db.executeSelectStatments("" + 
	        		"SELECT * " + 
	        		"FROM tbl_gateway " +
	        		"WHERE mmscnumber = '" + mmscnumber + "'");
	    	while(rs.next()){
	            GatewayInfo.id = rs.getInt("id");
	            GatewayInfo.port = rs.getInt("port");
	            GatewayInfo.operator = rs.getString("operator");
	            GatewayInfo.gatewayip = rs.getString("gatewayip");	            
	            GatewayInfo.mmsc= rs.getString("mmsc");	 
	            GatewayInfo.mmscnumber= rs.getString("mmscnumber");	
	        }
		} catch (Exception e) {

		}	
		return GatewayInfo;
	}	
	
	public ArrayList<Gateway> getAllGateway(){
		ArrayList<Gateway> GatewayList = new ArrayList<Gateway>();	
		try {
			DBAction db = new DBAction();
			ResultSet rs = db.executeSelectStatments("" + 
	        		"SELECT * " + 
	        		"FROM tbl_gateway");
	    	while(rs.next()){
	    		Gateway GatewayInfo = new Gateway();
	            GatewayInfo.id = rs.getInt("id");
	            GatewayInfo.port = rs.getInt("port");
	            GatewayInfo.operator = rs.getString("operator");
	            GatewayInfo.gatewayip = rs.getString("gatewayip");	            
	            GatewayInfo.mmsc= rs.getString("mmsc");	 
	            GatewayInfo.mmscnumber= rs.getString("mmscnumber");	
	            GatewayList.add(GatewayInfo);
	        }
		} catch (Exception e) {

		}	
		return GatewayList;
	}
	
	public int updateGatewayById(Gateway GatewayInfo){
		try {
			DBAction db = new DBAction();
			int rs = db.executeUpdateStatments("update tbl_gateway set operator = '"+ GatewayInfo.operator +"',gatewayip = '"+ GatewayInfo.gatewayip +"',mmsc = '"+ GatewayInfo.mmsc +"',port = "+ GatewayInfo.port +",mmscnumber = '"+ GatewayInfo.mmscnumber +"' where id = "+ GatewayInfo.id);	        		
				return rs;
			} 
			catch (Exception e) 
			{
				return 0;
			}		
	}
	
	public int deleteGatewayById(int id){			
		try {
			DBAction db = new DBAction();
			int rs = db.executeDeleteStatments("" + 
	        		"delete " + 
	        		"FROM tbl_gateway " +
	        		"where id=" + id);	 
			return rs;
			} 
			catch (Exception e) 
			{
				return 0;
			}		
	}
	
	public int addGateway(Gateway GatewayInfo){
		try {
			DBAction db = new DBAction();
			int rs = db.executeInsertStatments("" + 
	        		"insert into tbl_gateway(operator,gatewayip,port,mmsc,mmscnumber) " + 
	        		"values('"+ GatewayInfo.operator +"','"+ GatewayInfo.gatewayip +"',"+ GatewayInfo.port +",'"+ GatewayInfo.mmsc +"','"+ GatewayInfo.mmscnumber +"')");      		
			return rs;
			} 
			catch (Exception e) 
			{
				return 0;
			}		
	}	
}
