package controller;

import java.io.IOException;
//import java.io.PrintWriter;
//import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.ChuyenMon;

import dao.DBAction;

/**
 * Servlet implementation class Admin_CapNhatNguoiDungController
 */
//@WebServlet("/Admin_CapNhatNguoiDungController")
@WebServlet(name = "ChuyenMonController", urlPatterns = { "/ChuyenMonController" })

public class ChuyenMonController extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
	private static DBAction db;
	String success = "Successful!";
	String unsuccess = "Unsuccessful!";
	String err = "Error, try again!";
	String notcheck = "Choose at least one record!";
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChuyenMonController() throws Exception 
    {
        super();
        db = new DBAction();
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    { 	
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(true);       
        String url = "Admin_UpdateMajor.jsp";

        String [] options = request.getParameterValues("option");//lay cac gia tri ma checkbox duoc check!
        ChuyenMon cm = new ChuyenMon();
        ChuyenMon majorInfo = new ChuyenMon();       
        
        try{
	        if(request.getParameter("adminUpdateMajor")!=null)
		    {	        	
	        	if (options != null)
	        	{	
					for (int i=0; i < options.length; i++)
					{
							majorInfo.setId(Integer.parseInt(request.getParameter("id_khht:" + options[i])));
							majorInfo.setCode(request.getParameter("majorcode" + options[i]));
							majorInfo.setName(request.getParameter("majorname" + options[i]));
							majorInfo.setNote(request.getParameter("majornote" + options[i]));
							
							if(cm.updateMajorById(majorInfo)!=0)
							{
								session.setAttribute("themthanhcong", success);	
							}
							else session.setAttribute("themthanhcong", unsuccess);	
					}
	        	}
	        	if(request.getParameter("majorcode")!=null || request.getParameter("majorname")!=null)
	    		{        		
	        		majorInfo.setCode(request.getParameter("majorcode"));
	    	        majorInfo.setName(request.getParameter("majorname"));		        
	    	        majorInfo.setNote(request.getParameter("majornote"));
	    			        
	    			if(cm.addMajor(majorInfo)!=0)
	    			{
	    				session.setAttribute("themthanhcong", success);	
	    			}
	    			else session.setAttribute("themthanhcong", unsuccess);	
	    		}
	        	if(session.getAttribute("themthanhcong") != success)
	        			session.setAttribute("themthanhcong",err);		        		
		    }
	        else if(request.getParameter("adminDeleteMajor")!=null)
		    {
	        	if (options != null)
	        	{		        	
		        	for (int j=0; j < options.length; j++)
		        	{	        		
		        		if(cm.deleteMajorById(Integer.parseInt(request.getParameter("id_khht:" + options[j])))!=0)
		        		{		        		
		        			session.setAttribute("themthanhcong", success);
		        		}
		        		else session.setAttribute("themthanhcong", unsuccess);
		        	}	        	
	        	}	
		    }
        response.sendRedirect(url);
	    }
		catch (Exception ex) 
		{
			session.setAttribute("themthanhcong", err);        	
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
