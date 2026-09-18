package model;

//import java.io.InputStream;
//import java.sql.PreparedStatement;
import java.io.InputStream;
import java.sql.ResultSet;
//import java.sql.SQLException;
import java.util.ArrayList;
//import java.sql.Connection;
//import javax.servlet.http.Part;

import dao.DBAction;

public class ChuyenGia {
	private int id;
	private String code;
	private String name;
	private String title;
	private String degree;
	private String workunit;
	private String mobile;
	private int gatewayid;
	private String email;
	private String address;
	private String birthdate;
	private String image;	
	private boolean status;
	private String sex;
	private String loginname;
	private String loginpassword;
	private String usertype;
	
	
	public String getloginname() {
		return loginname;
	}
	public void setloginname(String loginname) {
		this.loginname = loginname;
	}
	
	public String getloginpassword() {
		return loginpassword;
	}
	public void setloginpassword(String loginpassword) {
		this.loginpassword = loginpassword;
	}
	
	public String getusertype() {
		return usertype;
	}
	public void setusertype(String usertype) {
		this.usertype = usertype;
	}
	
	public String getname() {
		return name;
	}
	public void setname(String name) {
		this.name = name;
	}
	
	public String gettitle() {
		return title;
	}
	public void settitle(String title) {
		this.title = title;
	}
	
	public String getdegree() {
		return degree;
	}
	public void setdegree(String degree) {
		this.degree = degree;
	}
	
	public String getbirthdate() {
		return birthdate;
	}
	public void setbirthdate(String birthdate) {
		this.birthdate = birthdate;
	}
	
	public String getsex() {
		return sex;
	}
	public void setsex(String sex) {
		this.sex = sex;
	}
	
	public String getcode() {
		return code;
	}
	public void setcode(String code) {
		this.code = code;
	}
	
	public int getid() {
		return id;
	}
	public void setid(int id) {
		this.id = id;
	}
	
	public String getworkunit() {
		return workunit;
	}
	public void setworkunit(String workunit) {
		this.workunit = workunit;
	}	
	
	public String getmobile() {
		return mobile;
	}
	public void setmobile(String mobile) {
		this.mobile = mobile;
	}
	
	public int getgatewayid() {
		return gatewayid;
	}
	public void setgatewayid(int gatewayid) {
		this.gatewayid = gatewayid;
	}
	
	public String getemail() {
		return email;
	}
	public void setemail(String email) {
		this.email = email;
	}
	
	public String getaddress() {
		return address;
	}
	public void setaddress(String address) {
		this.address = address;
	}
	
	public String getimage() {
		return image;
	}
	public void setimage(String image) {
		this.image = image;
	}
	
	public boolean getstatus() {
		return status;
	}
	public void setstatus(boolean status) {
		this.status = status;
	}
	
	public ChuyenGia getExpertById(int id){
		ChuyenGia expertInfo = new ChuyenGia();	
		try {
			DBAction db = new DBAction();
			ResultSet rs = db.executeSelectStatments("" + 
					"SELECT * " +
					"FROM tbl_expert " +
					"WHERE id = " + id);
	    	while(rs.next()){
	    		expertInfo.id = rs.getInt("id");
	    		expertInfo.code = rs.getString("code");
	    		expertInfo.name = rs.getString("name");
	    		expertInfo.title = rs.getString("title");
	    		expertInfo.degree = rs.getString("degree");
	    		expertInfo.workunit = rs.getString("workunit");
	    		expertInfo.mobile = rs.getString("mobile");
	    		expertInfo.gatewayid = rs.getInt("gatewayid");
	    		expertInfo.email = rs.getString("email");
	    		expertInfo.address = rs.getString("address");
	    		expertInfo.birthdate = rs.getString("birthdate");
	    		expertInfo.image = rs.getString("image");
	    		expertInfo.status = rs.getBoolean("status");
	       		expertInfo.sex = rs.getString("sex");
	    		expertInfo.loginname = rs.getString("loginname");	 
	    		expertInfo.loginpassword = rs.getString("loginpassword");
	    		expertInfo.usertype = rs.getString("usertype");
	        }
		} catch (Exception e) {

		}
		return expertInfo;
	}	
	
	//ham nay get expert info by mobile number
	public ChuyenGia getExpertByMobile(String mobile){
		ChuyenGia expertInfo = new ChuyenGia();
		try {			
			DBAction db = new DBAction();
			ResultSet rs = db.executeSelectStatments("" + 
					"SELECT * " +
					"FROM tbl_expert " +
					"WHERE mobile = '" + mobile + "'");
			while(rs.next()){
	    		expertInfo.id = rs.getInt("id");
	    		expertInfo.code = rs.getString("code");
	    		expertInfo.name = rs.getString("name");
	    		expertInfo.title = rs.getString("title");
	    		expertInfo.degree = rs.getString("degree");
	    		expertInfo.workunit = rs.getString("workunit");
	    		expertInfo.mobile = rs.getString("mobile");
	    		expertInfo.gatewayid = rs.getInt("gatewayid");
	    		expertInfo.email = rs.getString("email");
	    		expertInfo.address = rs.getString("address");
	    		expertInfo.birthdate = rs.getString("birthdate");
	    		expertInfo.image = rs.getString("image");
	    		expertInfo.status = rs.getBoolean("status");
	       		expertInfo.sex = rs.getString("sex");
	    		expertInfo.loginname = rs.getString("loginname");	 
	    		expertInfo.loginpassword = rs.getString("loginpassword");
	    		expertInfo.usertype = rs.getString("usertype");
	        }
		}catch (Exception e) {			
		}
		return expertInfo;
	}
	
	//ham nay co van de nha, coi lai// phai co rs.next moi lay duoc du lieu
		public int getExpertIdByUserId(String userid){
			int expertId=-1;
			try {			
				DBAction db = new DBAction();
				ResultSet rs = db.executeSelectStatments("" + 
						"SELECT count(*) as count, id " +
						"FROM tbl_expert " +
						"WHERE loginname = '" + userid + "'");			
				while(rs.next()){
			    	if(rs.getInt("count") >=1)
			    	{
			    		expertId = rs.getInt("id");
			    		return expertId;
			    	}
				}
			}catch (Exception e) {
				return expertId;
			}
			return expertId;
		}
	
	public ChuyenGia getExpertByUserId(String userid){
		ChuyenGia expertInfo = new ChuyenGia();	
		try {
			DBAction db = new DBAction();
			ResultSet rs = db.executeSelectStatments("" + 
					"SELECT * " +
					"FROM tbl_expert " +
					"WHERE loginname = '" + userid + "'");
	    	while(rs.next()){
	    		expertInfo.id = rs.getInt("id");
	    		expertInfo.code = rs.getString("code");
	    		expertInfo.name = rs.getString("name");
	    		expertInfo.title = rs.getString("title");
	    		expertInfo.degree = rs.getString("degree");
	    		expertInfo.workunit = rs.getString("workunit");
	    		expertInfo.mobile = rs.getString("mobile");
	    		expertInfo.gatewayid = rs.getInt("gatewayid");
	    		expertInfo.email = rs.getString("email");
	    		expertInfo.address = rs.getString("address");
	    		expertInfo.birthdate = rs.getString("birthdate");
	    		expertInfo.image = rs.getString("image");
	    		expertInfo.status = rs.getBoolean("status");
	       		expertInfo.sex = rs.getString("sex");
	    		expertInfo.loginname = rs.getString("loginname");
	    		expertInfo.loginpassword = rs.getString("loginpassword");
	    		expertInfo.usertype = rs.getString("usertype");
	        }
		}catch (Exception e) {

		}	
		return expertInfo;
	}	
	
	//get All thi ket qua tra ve can 1 list hay array => coi lai sau
	public ArrayList<ChuyenGia> getAllExpert(){
		ArrayList<ChuyenGia> listExpert = new ArrayList<ChuyenGia>();	
		try {
			DBAction db = new DBAction();
			ResultSet rs = db.executeSelectStatments("" + 
					"SELECT * " +
					"FROM tbl_expert");					
	    	while(rs.next()){
	    		ChuyenGia expertInfo = new ChuyenGia();
	    		expertInfo.id = rs.getInt("id");
	    		expertInfo.code = rs.getString("code");
	    		expertInfo.name = rs.getString("name");
	    		expertInfo.title = rs.getString("title");
	    		expertInfo.degree = rs.getString("degree");
	    		expertInfo.workunit = rs.getString("workunit");
	    		expertInfo.mobile = rs.getString("mobile");
	    		expertInfo.gatewayid = rs.getInt("gatewayid");
	    		expertInfo.email = rs.getString("email");
	    		expertInfo.address = rs.getString("address");
	    		expertInfo.birthdate = rs.getString("birthdate");
	    		expertInfo.image = rs.getString("image");
	    		expertInfo.status = rs.getBoolean("status");
	       		expertInfo.sex = rs.getString("sex");
	    		expertInfo.loginname = rs.getString("loginname");
	    		expertInfo.loginpassword = rs.getString("loginpassword");
	    		expertInfo.usertype = rs.getString("usertype");
	    		
	    		listExpert.add(expertInfo);
	        }
		} catch (Exception e) {

		}	
		return listExpert;
	}	
	
	public int addExpert(ChuyenGia expertInfo){	
		try {
			DBAction db = new DBAction();
			int rs = db.executeInsertStatments("" + 
					"insert into tbl_expert(code,name,title,degree,workunit,mobile,gatewayid,email,address,birthdate,image,status,sex,loginname,loginpassword,usertype) " +					
					"values('"+ expertInfo.code +"','"+ expertInfo.name +"','"+ expertInfo.title +"','"+ expertInfo.degree +"','"+ expertInfo.workunit +"','"+ expertInfo.mobile +"',"+ expertInfo.gatewayid +",'"+ expertInfo.email +"','"+ expertInfo.address +"','"+ expertInfo.birthdate +"','"+ expertInfo.image +"',"+ expertInfo.status +",'"+ expertInfo.sex +"','"+ expertInfo.loginname +"','"+ expertInfo.loginpassword +"','"+ expertInfo.usertype +"')");
					//"values("+expertInfo.code+","+mh.name+","+mh.expression+","+mh.note+")");
			return rs;
	        }		
		catch (Exception e) {
			return 0;
		}			
	}
	/*/
	public int addImage(InputStream inputStream, int id ){	
		try {
			DBAction db = new DBAction();
			int rs = db.InsertImage(inputStream, id);
			return rs;
	        }		
		catch (Exception e) {
			return 0;
		}			
	}	
	/*******/
	public int updateExpertById(ChuyenGia expertInfo){	
		try {
			DBAction db = new DBAction();
			int rs = db.executeUpdateStatments("" + 
					"update tbl_expert set code='"+ expertInfo.code +"',name='"+ expertInfo.name +"',title='"+ expertInfo.title +"',degree='"+ expertInfo.degree +"',workunit='"+ expertInfo.workunit +"',mobile='"+ expertInfo.mobile +"',gatewayid="+ expertInfo.gatewayid +",email='"+ expertInfo.email +"',address='"+ expertInfo.address +"',birthdate='"+ expertInfo.birthdate +"',sex='"+ expertInfo.sex +"' where id= "+ expertInfo.id );					
					//"update tbl_expert set code='"+ expertInfo.code +"',name='"+ expertInfo.name +"',title='"+ expertInfo.title +"',degree='"+ expertInfo.degree +"',workunit='"+ expertInfo.workunit +"',mobile='"+ expertInfo.mobile +"',gatewayid='"+ expertInfo.gatewayid +"',email='"+ expertInfo.email +"',address='"+ expertInfo.address +"',birthdate='"+ expertInfo.birthdate +"',image='"+ expertInfo.image +"',status="+ expertInfo.status +",sex='"+ expertInfo.sex +"',usertype='"+ expertInfo.usertype +"' where id= "+ expertInfo.id );
			return rs;
	        }		
		catch (Exception e) {
			return 0;
		}
	}
	
	public int acceptExpertById(boolean status, int id){		
		try {
			DBAction db = new DBAction();
			int rs = db.executeUpdateStatments("" + 
					"update tbl_expert set status= "+ status +" where id= " + id);					
			return rs;
	        }		
		catch (Exception e) {
			return 0;
		}			
	}
	
	public int deleteExpertById(int id){		
		try {
			DBAction db = new DBAction();
			int rs = db.executeDeleteStatments("" + 
					"delete " + 
	        		"FROM tbl_expert " +	        		
					"where id= " + id);					
			return rs;
	        }		
		catch (Exception e) {
			return 0;
		}			
	}	
}
