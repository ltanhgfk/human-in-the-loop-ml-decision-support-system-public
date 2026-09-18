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

import model.Gateway;

import dao.DBAction;

/**
 * Servlet implementation class Admin_CapNhatNguoiDungController
 */
//@WebServlet("/Admin_CapNhatNguoiDungController")
@WebServlet(name = "GatewayController", urlPatterns = { "/GatewayController" })

public class GatewayController extends HttpServlet 
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
    public GatewayController() throws Exception 
    {
        super();
        db = new DBAction();
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    { 	
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(true);       
        String url = "Admin_UpdateGateway.jsp";
        String [] options = request.getParameterValues("option");
        Gateway cm = new Gateway();
        Gateway GatewayInfo = new Gateway();       
        
        try{
	        if(request.getParameter("adminUpdateGateway")!=null)
		    {
	        	if (options != null)
	        	{	
					for (int i=0; i < options.length; i++)
					{
							GatewayInfo.setId(Integer.parseInt(request.getParameter("id_khht:" + options[i])));
							GatewayInfo.setoperator(request.getParameter("operator" + options[i]));
							GatewayInfo.setgatewayip(request.getParameter("gatewayip" + options[i]));
							GatewayInfo.setport(Integer.parseInt(request.getParameter("port" + options[i])));
							GatewayInfo.setmmsc(request.getParameter("mmsc" + options[i]));
							GatewayInfo.setmmscnumber(request.getParameter("mmscnumber" + options[i]));
							if(cm.updateGatewayById(GatewayInfo)!=0)
							{
								session.setAttribute("themthanhcong", success);	
							}
							else session.setAttribute("themthanhcong", unsuccess);	
					}
	        	}
	        	else if(request.getParameter("operator").compareTo("")!=0 || request.getParameter("gatewayip").compareTo("")!=0)
	    		{        		
	        		GatewayInfo.setoperator(request.getParameter("operator"));
	    	        GatewayInfo.setgatewayip(request.getParameter("gatewayip"));		        
	    	        GatewayInfo.setport(Integer.parseInt(request.getParameter("port")));
	    	        GatewayInfo.setmmsc(request.getParameter("mmsc"));
	    	        GatewayInfo.setmmscnumber(request.getParameter("mmscnumber"));
	    			if(cm.addGateway(GatewayInfo)!=0)
	    			{
	    				session.setAttribute("themthanhcong", success);	
	    			}
	    			else session.setAttribute("themthanhcong", unsuccess);	
	    		}	        	
		    }
	        else if(request.getParameter("adminDeleteGateway")!=null)
		    {
	        	if (options != null)
	        	{		        	
		        	for (int j=0; j < options.length; j++)
		        	{	        		
		        		if(cm.deleteGatewayById(Integer.parseInt(request.getParameter("id_khht:" + options[j])))!=0)
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
			ex.printStackTrace();
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
