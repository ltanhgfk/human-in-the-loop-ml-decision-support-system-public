package controller;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.Connection;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.ChuyenGia;
import model.ChuyenGiaChuyenMon;

import dao.DBAction;

/**
 * Servlet implementation class Admin_CapNhatNguoiDungController
 */
//@WebServlet("/Admin_CapNhatNguoiDungController")
@WebServlet(name = "ChuyenGiaController", urlPatterns = { "/ChuyenGiaController" })
@MultipartConfig(maxFileSize = 16177215)	// upload file's size up to 16MB

public class ChuyenGiaController extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
	private static DBAction db;
	private Connection connection;
	String success = "Successful!";
	String unsuccess = "Unsuccessful!";
	String sameid = "Error, try again!";
	String notcheck = "Choose at least one record!";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChuyenGiaController() throws Exception 
    {
        super();
        db = new DBAction();
    }
      
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    { 	
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");        
        HttpSession session = request.getSession(true);   
        String url = "Admin_AcceptExpert.jsp";        
        String [] options = request.getParameterValues("option");//lay cac gia tri ma checkbox duoc check!
        
        boolean status;
        int expertid=0;
        ChuyenGia cm = new ChuyenGia();
        ChuyenGia cg = new ChuyenGia();        
        String majorid_str="";        
        int kq=0;
        try{
        	if(request.getParameter("expertRegistryInfo")!=null)
        	{	url = "Expert_RegistryInfo.jsp";
        		String [] majorOptions = request.getParameterValues("majorOption");
        		ChuyenGia expertInfo = new ChuyenGia();
        		
	    		//expertInfo.setid(request.getParameter());
	    		expertInfo.setcode(request.getParameter("code"));
	    		expertInfo.setname(request.getParameter("name"));
	    		expertInfo.settitle(request.getParameter("title"));
	    		expertInfo.setdegree(request.getParameter("degree"));
	    		expertInfo.setworkunit(request.getParameter("workunit"));
	    		expertInfo.setmobile(request.getParameter("mobile").replaceFirst("0", "84"));
	    		expertInfo.setgatewayid(Integer.parseInt(request.getParameter("gatewayid")));
	    		expertInfo.setemail(request.getParameter("email"));
	    		expertInfo.setaddress(request.getParameter("address"));
	    		expertInfo.setbirthdate(request.getParameter("birthdate"));
	    		//expertInfo.setbirthdate(request.getParameter("image"));
	    		expertInfo.setstatus(Boolean.parseBoolean(request.getParameter("status")));
	       		expertInfo.setsex(request.getParameter("sex"));
	       		
	    		expertInfo.setloginname(request.getParameter("loginname"));//username dang nhap=> session username
	    		expertInfo.setloginpassword(request.getParameter("loginpassword"));//lay session
	    		expertInfo.setusertype("chuyengia");
		
	    		
	    		DBAction db = new DBAction();
	    		ArrayList<ChuyenGia> listExpert = new ArrayList<ChuyenGia>();
        		ResultSet rs = db.executeSelectStatments("" + 
        				"SELECT count( * ) as KQ " +
        				"FROM tbl_expert " +
        				"WHERE code = '" + expertInfo.getcode() +"' or loginname= '"+ expertInfo.getloginname() +"'");
        		while(rs.next()){    	    		
        				kq = rs.getInt("KQ");
    	        	}
        	    	if(kq>=1)
        	    	{
        	    		session.setAttribute("themthanhcong", unsuccess + ", " + sameid);
        	    		url = "Expert_RegistryInfo.jsp";
        	    	}        	    	
        	    	else
        	    	{        	    		
        	    		if(cm.addExpert(expertInfo)!=0)
        	    		{        	    		
	        	    		cg = cm.getExpertByUserId(expertInfo.getloginname());
	        	    		if(majorOptions != null)
	                		{
	                			ChuyenGiaChuyenMon cgcm = new ChuyenGiaChuyenMon();	                			
	                			for (int i=0; i < majorOptions.length; i++)
	        					{
	                				cgcm.setExpertId(cg.getid());//coi lai cai nay nha.
	                				cgcm.setMajorId(Integer.parseInt(request.getParameter("id_cm:" + majorOptions[i])));                				                				
	                				cgcm.addMajor(cgcm);
	        					}
	                		}
	        	    		url = "Expert_ViewInfo.jsp";	                		
	        	    		session.setAttribute("themthanhcong", success);
        	    		}
        	    		else
        	    		{
        	    			url = "Expert_RegistryInfo.jsp";	                			
        	    			session.setAttribute("themthanhcong", unsuccess);
        	    		}
        	    	}
        	}    
        	
        	if(request.getParameter("expertUpdateInfo")!=null)
        	{	
        		url = "Expert_UpdateInfo.jsp";
        		String [] majorOptions = request.getParameterValues("majorOption");
        		ChuyenGia expertInfo = new ChuyenGia();
        		
	    		expertInfo.setid(Integer.parseInt(request.getParameter("hiddenExpertId")));
	    		expertInfo.setcode(request.getParameter("code"));
	    		expertInfo.setname(request.getParameter("name"));
	    		expertInfo.settitle(request.getParameter("title"));
	    		expertInfo.setdegree(request.getParameter("degree"));
	    		expertInfo.setworkunit(request.getParameter("workunit"));
	    		expertInfo.setmobile(request.getParameter("mobile").replaceFirst("0", "84"));
	    		expertInfo.setgatewayid(Integer.parseInt(request.getParameter("gatewayid")));
	    		expertInfo.setemail(request.getParameter("email"));
	    		expertInfo.setaddress(request.getParameter("address"));
	    		expertInfo.setbirthdate(request.getParameter("birthdate"));
	    		//expertInfo.setimage(request.getParameter("image"));	    		
	    		//expertInfo.setstatus(Boolean.parseBoolean(request.getParameter("status")));
	       		expertInfo.setsex(request.getParameter("sex"));
	       		expertInfo.setusertype("chuyengia");
	       		
      	    		
        	    int rs = cm.updateExpertById(expertInfo);

        	    String loginname = (String) session.getAttribute("username");
        	    cg = cm.getExpertByUserId(loginname);
        	    if(majorOptions != null)
                {
                	ChuyenGiaChuyenMon cgcm = new ChuyenGiaChuyenMon();
                	cgcm.deleteExpertMajorByExpertId(cg.getid());
                	for (int i=0; i < majorOptions.length; i++)
        			{
                		cgcm.setExpertId(cg.getid());//coi lai cai nay nha.
                		cgcm.setMajorId(Integer.parseInt(request.getParameter("id_cm:" + majorOptions[i])));                				                				
                		cgcm.addMajor(cgcm);
        			}        			
                }
        	    if(rs!=0)
        	    {
        	    	url = "Expert_ViewInfo.jsp";    					   		
        	    	session.setAttribute("themthanhcong", success);
        	    }
        	    else
        	    {
        	    	url = "Expert_UpdateInfo.jsp";        	    		
        	    	session.setAttribute("themthanhcong", unsuccess);        	    		
        	    }        	   
        	}    
        	
	        if(request.getParameter("adminAcceptExpert")!=null)
		    {
	        	if (options != null)
	        	{	
					for (int i=0; i < options.length; i++)
					{	
							expertid=Integer.parseInt(request.getParameter("id_khht:" + options[i]));
							status=Boolean.parseBoolean(request.getParameter("status:" + options[i]));
							majorid_str=majorid_str+ status;
							if(cm.acceptExpertById(status, expertid)!=0)
							{
								session.setAttribute("themthanhcong", success);	
							}
							else
							{
								session.setAttribute("themthanhcong", unsuccess);									
							}
					}
	        	}
	        	else 
		        {
	        		if(session.getAttribute("themthanhcong") != success)
	        			session.setAttribute("themthanhcong", notcheck);		        		
		        }
		    }
	        else if(request.getParameter("adminDeleteExpert")!=null)
		    {
	        	if (options != null)
	        	{		        	
		        	for (int j=0; j < options.length; j++)
		        	{	        		
		        		if(cm.deleteExpertById(Integer.parseInt(request.getParameter("id_khht:" + options[j])))!=0)
		        		{
		        			session.setAttribute("themthanhcong", success);
		        		}
		        		else
		        			session.setAttribute("themthanhcong", unsuccess);
		        	}	        	
	        	}	
		    }
        response.sendRedirect(url);
	    } 
		catch (Exception ex) 
		{
			session.setAttribute("themthanhcong", unsuccess);        	
        	response.sendRedirect(url);
        } 
    }        
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		processRequest(request, response);
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		processRequest(request, response);
	}
}