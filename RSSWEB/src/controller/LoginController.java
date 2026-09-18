package controller;

import dao.DBAction;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.*;

/**
 * Servlet implementation class DangNhapController
 */
@WebServlet("/LoginController")
public class LoginController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static DBAction db;
	String success = "Successful!";
	String unsuccess = "Unsuccessful!";
	String err = "Error, try again!";
	String notcheck = "Choose at least one record!";
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginController() throws Exception {
        super();
        db = new DBAction();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { 	
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");        
        HttpSession session = request.getSession(false);
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String url = "index.jsp";
        
        try 
        {	
        	if(request.getParameter("adminlogin")!=null)
        	{       
        		Admin ad = new Admin();
        		ad = ad.getAdminByUserName(username,password);
        		if(ad.getIdAdmin()>=1 && !ad.getUserName().equals("") && !ad.getPassword().equals(""))
        		{
        			session.setAttribute("admin", "TRUE");
    				url = "Admin.jsp";
    				session.setAttribute("tennguoidung", "Admin");    				
        		}     
        		url = "Admin.jsp";
        	}
        	if(request.getParameter("expertlogin")!=null)
        	{
	        	ChuyenGia cg = new ChuyenGia();
	        	cg = cg.getExpertByUserId(username);
	        	if(cg.getloginpassword().equals(password))
	        	{
	        		if(cg.getusertype().equals("chuyengia") || cg.getusertype().equals("dieuphoi"))
	        		{
	        			url = "Expert_ViewInfo.jsp";
	        			session.setAttribute("username", username);
	        			session.setAttribute("usertype", cg.getusertype());
	        			session.setAttribute("status",cg.getstatus());
	        			//session.setAttribute("themthanhcong", "Vao trang thong tin chuyen gia!" + cg.getloginpassword() + " " + password + " " + cg.getid() );
	        		}	        		
	        		if (!cg.getname().equals("")){
	            		session.setAttribute("tennguoidung", cg.getname());
	            	}
	            	else{
	                    session.removeAttribute("tennguoidung");
	            	}
	        	}
	        	else
	        	{
	        		session.setAttribute("themthanhcong", unsuccess);
	        	}
        	}
        	
        response.sendRedirect(url);
        
        } catch (Exception ex) {
        	session.setAttribute("themthanhcong", err);            
            response.sendRedirect(url);
        } 
    }
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

}
