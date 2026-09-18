package controller;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.ChuyenGia;
import model.Content;
import model.Content;
import dao.DBAction;

/**
 * Servlet implementation class Admin_CapNhatNguoiDungController
 */
//@WebServlet("/Admin_CapNhatNguoiDungController")
@WebServlet(name = "ContentController", urlPatterns = { "/ContentController" })
@MultipartConfig(maxFileSize = 16177215)	// upload file's size up to 16MB

public class ContentController extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
	private static DBAction db;
	private Connection connection;
	String success = "Successful!";
	String unsuccess = "Unsuccessful!";
	String err = "Error, try again!";
	String notcheck = "Choose at least one record!";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ContentController() throws Exception 
    {
        super();
        db = new DBAction();
    }
      
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    { 	
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(true);  
        String username =  (String) session.getAttribute("username");
        String url = "Expert_UpdateContent.jsp";

        String [] options = request.getParameterValues("option");//lay cac gia tri ma checkbox duoc check!
        Content cm = new Content();
        Content contentInfo = new Content();   
		
        try{
	        if(request.getParameter("UpdateKindOfRice")!=null)
		    {	        	
	        	if (options != null)
	        	{	
					for (int i=0; i < options.length; i++)
					{
							contentInfo.setid(Integer.parseInt(request.getParameter("id_khht:" + options[i])));
							//contentInfo.setcode(request.getParameter("code" + options[i]));
							contentInfo.setname(request.getParameter("name" + options[i]));
							contentInfo.setcontent(request.getParameter("content" + options[i]));
							contentInfo.setkeywords(request.getParameter("keywords" + options[i]));
							
				    		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
				    		Date date = new Date();
				    		//contentInfo.setcreateddate(dateFormat.format(date));//cai nay thuc hien dung ne
				    		contentInfo.setmodifieddate(dateFormat.format(date));
				    		
				    		ChuyenGia cg = new ChuyenGia();
				    		cg = cg.getExpertByUserId(username);
				    		contentInfo.setexpertid(cg.getid());
							
							if(cm.updateContentById(contentInfo)!=0)
							{
								session.setAttribute("themthanhcong", success);	
							}
							else session.setAttribute("themthanhcong", unsuccess);	
					}
	        	}
	        	if(request.getParameter("name")!=null)
	    		{        		
	        		//contentInfo.setcode(request.getParameter("code"));
	        		contentInfo.setname(request.getParameter("name"));
					contentInfo.setcontent(request.getParameter("content"));
					contentInfo.setkeywords(request.getParameter("keywords"));
					
		    		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		    		Date date = new Date();				    		
		    		contentInfo.setcreateddate(dateFormat.format(date));//cai nay thuc hien dung ne
		    		contentInfo.setmodifieddate(dateFormat.format(date));
		    		
		    		ChuyenGia cg = new ChuyenGia();
		    		cg = cg.getExpertByUserId(username);
		    		contentInfo.setexpertid(cg.getid());
					
					if(cm.addContent(contentInfo)!=0)
					{
						session.setAttribute("themthanhcong", success);	
					}
					else session.setAttribute("themthanhcong", unsuccess);	
	    		}	        		
		    }
	        if(request.getParameter("UpdateContent")!=null)
		    {	        	
	        	if (options != null)
	        	{	
					for (int i=0; i < options.length; i++)
					{
							contentInfo.setid(Integer.parseInt(request.getParameter("id_khht:" + options[i])));
							//contentInfo.setcode(request.getParameter("code" + options[i]));
							contentInfo.setname(request.getParameter("name" + options[i]));
							contentInfo.setcontent(request.getParameter("content" + options[i]));
							contentInfo.setkeywords(request.getParameter("keywords" + options[i]));
							
				    		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
				    		Date date = new Date();
				    		//contentInfo.setcreateddate(dateFormat.format(date));//cai nay thuc hien dung ne
				    		contentInfo.setmodifieddate(dateFormat.format(date));
				    		
				    		ChuyenGia cg = new ChuyenGia();
				    		cg = cg.getExpertByUserId(username);
				    		contentInfo.setexpertid(cg.getid());
							
							if(cm.updateContentById(contentInfo)!=0)
							{
								session.setAttribute("themthanhcong", success);	
							}
							else session.setAttribute("themthanhcong", unsuccess);	
					}
	        	}
	        	if(request.getParameter("name")!=null)
	    		{        		
	        		//contentInfo.setcode(request.getParameter("code"));
	        		contentInfo.setname(request.getParameter("name"));
					contentInfo.setcontent(request.getParameter("content"));
					contentInfo.setkeywords(request.getParameter("keywords"));
					
		    		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		    		Date date = new Date();				    		
		    		contentInfo.setcreateddate(dateFormat.format(date));//cai nay thuc hien dung ne
		    		contentInfo.setmodifieddate(dateFormat.format(date));
		    		
		    		ChuyenGia cg = new ChuyenGia();
		    		cg = cg.getExpertByUserId(username);
		    		contentInfo.setexpertid(cg.getid());
					
					if(cm.addContent(contentInfo)!=0)
					{
						session.setAttribute("themthanhcong", success);	
					}
					else session.setAttribute("themthanhcong", unsuccess);	
	    		}	        		
		    }
	        else if(request.getParameter("DeleteContent")!=null)
		    {
	        	if (options != null)
	        	{		        	
		        	for (int j=0; j < options.length; j++)
		        	{	        		
		        		if(cm.deleteContentById(Integer.parseInt(request.getParameter("id_khht:" + options[j])))!=0)
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