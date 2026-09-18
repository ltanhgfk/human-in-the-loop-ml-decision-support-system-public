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

import model.Config;

import dao.DBAction;

/**
 * Servlet implementation class Admin_CapNhatNguoiDungController
 */
//@WebServlet("/Admin_CapNhatNguoiDungController")
@WebServlet(name = "ConfigController", urlPatterns = { "/ConfigController" })

public class ConfigController extends HttpServlet 
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
    public ConfigController() throws Exception 
    {
        super();
        db = new DBAction();
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    { 	
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(true);
        String url = "Admin_UpdateConfig.jsp";
        Config cf = new Config();
        Config ConfigInfo = new Config();
        Config ConfigInfo1= new Config();
        Config Info = new Config();
        
        try
        {
	        if(request.getParameter("adminUpdateConfig")!=null)
		    {
	        	if( request.getParameter("systemconfig")!=null)
	        	{
	        	String systemconfig = request.getParameter("systemconfig");
	        	ConfigInfo = cf.getConfigBycode("systemconfig");
	        	Info.setcode(ConfigInfo.getcode());
	        	Info.setvaluebool(ConfigInfo.getvaluebool());
	        	Info.setvaluechar(systemconfig);
	        	Info.setvalueint(ConfigInfo.getvalueint());
	        	if(cf.updateConfigByCode(Info)!=0)
				{
					session.setAttribute("themthanhcong", success);	
				}
				else session.setAttribute("themthanhcong", unsuccess);	
	        	}
	        	
	        	//-----------------------------------------------------------------//	        	
	        	if(request.getParameter("autokeyword")!=null)
	        	{
	        	boolean autokeyword = Boolean.parseBoolean(request.getParameter("autokeyword"));
	        	ConfigInfo = cf.getConfigBycode("autokeyword");
	        	Info.setcode(ConfigInfo.getcode());
	        	Info.setvaluebool(autokeyword);
	        	Info.setvaluechar(ConfigInfo.getvaluechar());
	        	Info.setvalueint(ConfigInfo.getvalueint());
	        	if(cf.updateConfigByCode(Info)!=0)
				{
					session.setAttribute("themthanhcong", success);	
				}
				else session.setAttribute("themthanhcong", unsuccess);	
	        	}
	        	
				/*/-----------------------------------------------------------------//	        	
	        	if(request.getParameter("send_sms_expert")!=null)
	        	{
	        	boolean send_sms_expert = Boolean.parseBoolean(request.getParameter("send_sms_expert"));
	        	ConfigInfo = cf.getConfigBycode("send_sms_expert");
	        	Info.setcode(ConfigInfo.getcode());
	        	Info.setvaluebool(send_sms_expert);
	        	Info.setvaluechar(ConfigInfo.getvaluechar());
	        	Info.setvalueint(ConfigInfo.getvalueint());
	        	if(cf.updateConfigByCode(Info)!=0)
				{
					session.setAttribute("themthanhcong", success);	
				}
				else session.setAttribute("themthanhcong", unsuccess);	
	        	}
	        	
	        	//-----------------------------------------------------------------/*/	   
	        	if(request.getParameter("send_notification_farmer")!=null)
	        	{
	        	boolean send_notification_farmer = Boolean.parseBoolean(request.getParameter("send_notification_farmer"));
	        	ConfigInfo = cf.getConfigBycode("send_notification_farmer");
	        	Info.setcode(ConfigInfo.getcode());
	        	Info.setvaluebool(send_notification_farmer);
	        	Info.setvaluechar(ConfigInfo.getvaluechar());
	        	Info.setvalueint(ConfigInfo.getvalueint());
	        	if(cf.updateConfigByCode(Info)!=0)
				{
					session.setAttribute("themthanhcong", success);	
				}
				else session.setAttribute("themthanhcong", unsuccess);
	        	}
	        	
	        	/*/-----------------------------------------------------------------//	 
	        	
	        	if(request.getParameter("auto_send_farmer")!=null)
	        	{
	        	boolean auto_send_farmer = Boolean.parseBoolean(request.getParameter("auto_send_farmer"));
	        	ConfigInfo = cf.getConfigBycode("auto_send_farmer");
	        	Info.setcode(ConfigInfo.getcode());
	        	Info.setvaluebool(auto_send_farmer);
	        	Info.setvaluechar(ConfigInfo.getvaluechar());
	        	Info.setvalueint(ConfigInfo.getvalueint());
	        	if(cf.updateConfigByCode(Info)!=0)
				{
					session.setAttribute("themthanhcong", success);	
				}
				else session.setAttribute("themthanhcong", unsuccess);
	        	}
	        	
	        	//-----------------------------------------------------------------/*/	
	        	if(request.getParameter("number_sms_need")!=null)
	        	{
	        	//int number_sms_need = Integer.parseInt(request.getParameter("number_sms_need"));
	        	boolean number_sms_need = Boolean.parseBoolean(request.getParameter("number_sms_need"));
	        	ConfigInfo = cf.getConfigBycode("autoretrain");
	        	Info.setcode(ConfigInfo.getcode());
	        	Info.setvaluebool(number_sms_need);
	        	Info.setvaluechar(ConfigInfo.getvaluechar());
	        	Info.setvalueint(ConfigInfo.getvalueint());
	        	if(cf.updateConfigByCode(Info)!=0)
				{
					session.setAttribute("themthanhcong", success);	
				}
				else session.setAttribute("themthanhcong", unsuccess);	
	        	}
	        	//-----------------------------------------------------------------//	
	        	if(request.getParameter("number_sms_retrain")!=null)
	        	{
	        	int number_sms_retrain = Integer.parseInt(request.getParameter("number_sms_retrain"));
	        	ConfigInfo = cf.getConfigBycode("number_sms_retrain");
	        	Info.setcode(ConfigInfo.getcode());
	        	Info.setvaluebool(ConfigInfo.getvaluebool());
	        	Info.setvaluechar(ConfigInfo.getvaluechar());
	        	Info.setvalueint(number_sms_retrain);
	        	if(cf.updateConfigByCode(Info)!=0)
				{
					session.setAttribute("themthanhcong", success);	
				}
				else session.setAttribute("themthanhcong", unsuccess);	
	        	}
	        	
	        	//-----------------------------------------------------------------//	
	        	if(request.getParameter("sleep_time_get_sms")!=null)
	        	{
	        	int sleep_time_get_sms = Integer.parseInt(request.getParameter("sleep_time_get_sms"));	        	
	        	ConfigInfo = cf.getConfigBycode("sleep_time_get_sms");
	        	Info.setcode(ConfigInfo.getcode());
	        	Info.setvaluebool(ConfigInfo.getvaluebool());
	        	Info.setvaluechar(ConfigInfo.getvaluechar());
	        	Info.setvalueint(sleep_time_get_sms);
	        	if(cf.updateConfigByCode(Info)!=0)
				{
					session.setAttribute("themthanhcong", success);	
				}
				else session.setAttribute("themthanhcong", unsuccess);	
	        	}
	        	
	        	//-----------------------------------------------------------------//	
	        	if(request.getParameter("sleep_time_retrain")!=null)
	        	{
	        	int sleep_time_retrain = Integer.parseInt(request.getParameter("sleep_time_retrain"));	        	
	        	ConfigInfo = cf.getConfigBycode("sleep_time_retrain");
	        	Info.setcode(ConfigInfo.getcode());
	        	Info.setvaluebool(ConfigInfo.getvaluebool());
	        	Info.setvaluechar(ConfigInfo.getvaluechar());
	        	Info.setvalueint(sleep_time_retrain);
	        	if(cf.updateConfigByCode(Info)!=0)
				{
					session.setAttribute("themthanhcong", success);	
				}
				else session.setAttribute("themthanhcong", unsuccess);	
	        	}
	        	
	        	//-----------------------------------------------------------------//	
	        	if(request.getParameter("sleep_time_classify")!=null)
	        	{
	        	int sleep_time_classify = Integer.parseInt(request.getParameter("sleep_time_classify"));	        	
	        	ConfigInfo = cf.getConfigBycode("sleep_time_classify");
	        	Info.setcode(ConfigInfo.getcode());
	        	Info.setvaluebool(ConfigInfo.getvaluebool());
	        	Info.setvaluechar(ConfigInfo.getvaluechar());
	        	Info.setvalueint(sleep_time_classify);
	        	if(cf.updateConfigByCode(Info)!=0)
				{
					session.setAttribute("themthanhcong", success);	
				}
				else session.setAttribute("themthanhcong", unsuccess);	
	        	}
	        	
	        	//-----------------------------------------------------------------//	   
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
