package controller;

import java.io.IOException;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.ChuyenGiaChuyenMon;
import model.Config;
import model.Gateway;
import model.MMS;
import model.ChuyenGia;
import dao.DBAction;

/**
 * Servlet implementation class MMSController
 */
//@WebServlet("/MMSController")
@WebServlet(name = "MMSController", urlPatterns = { "/MMSController" })

public class MMSController extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
	private static DBAction db;
	String success = "Successful!";
	String unsuccess = "Unsuccessful!";
	String err = "Error!!!";
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MMSController() throws Exception 
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
        String url = "";//"Admin_ClassifyMMS.jsp";
        String [] options = request.getParameterValues("option");//lay cac gia tri ma checkbox duoc check!
        //boolean status;
        int MMSid=0;
        String msg="";
        String replymsg="";
        int majorId=0;
        MMS cm = new MMS();
        MMS MMSInfo = new MMS();  
        MMS Info = new MMS();
        ChuyenGia cg = new ChuyenGia();
        Config cf = new Config();
        
        try{
        	
	        if(request.getParameter("expertReplyMMS")!=null)
	        {//truong hop chuyen gia cap nhat noi dung tra loi hoac phan loai lai
	        	
	        	for (int i=0; i < options.length; i++)
	        	{
					MMSid=Integer.parseInt(request.getParameter("id_khht:" + options[i]));					
					replymsg=request.getParameter("replymsg" + options[i]);	
					majorId=Integer.parseInt(request.getParameter("classify:" + options[i]));
					
					
					if(replymsg.compareTo("null")!=0 && replymsg.compareTo("")!=0 && replymsg.length()>=10)
					{//truong hop chuyen gia tra loi
						
						Info=cm.getMMSById(MMSid);
						cg = cg.getExpertByUserId(username);//session username
						
						if(cg.getstatus())//kiem tra chuyen gia da duoc chap nhan de tra loi chua
						{						
							MMSInfo.setid(MMSid);
				    		MMSInfo.setsender(Info.getsender());
				    		MMSInfo.setmsg(Info.getmsg());
				    		MMSInfo.setimage(Info.getimage());
				    		MMSInfo.setreplymsg(replymsg);				    		
				    		
				    		MMSInfo.setexpertid(cg.getid());
				    		
				    		MMSInfo.setmajoridByMachine(Info.getmajoridByMachine());
				    		
				    		MMSInfo.setmajoridByHuman(majorId);
				    		
				    		MMSInfo.setanswered(true);
				    		MMSInfo.setclassifiedByHuman(Info.getclassifiedByHuman());
				    		MMSInfo.setclassifiedByMachine(Info.getclassifiedByMachine());
				    		MMSInfo.setencoded(Info.getencoded());
				    		MMSInfo.setreceivedMms(Info.getreceivedMms());
				    		MMSInfo.setused(Info.getused());
				    		MMSInfo.setreceivedDate(Info.getreceivedDate());
				    		//DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
				    		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				    		Date date = new Date();				    		
				    		MMSInfo.setansweredDate(dateFormat.format(date));	
				    		MMSInfo.setgatewayid(Info.getgatewayid());
				    		MMSInfo.setsentexpert(Info.getsentexpert());
				    		MMSInfo.setsentfarmer(Info.getsentfarmer());
				    		
							if(cm.expertReply(MMSInfo)!=0)
							{//neu cap nhat cau tra loi cua chuyen gia vao bang tbl-mms thanh cong thi ghi cau tra loi vao bang tbl_sms_out de gui cho nha nong.								
								session.setAttribute("themthanhcong", success);
								url = "Expert_ReplyMMS.jsp";
							}
							else
							{//neu cap nhat cau tra loi cua chuyen gia vao bang tbl-mms khong thanh cong								
								session.setAttribute("themthanhcong", unsuccess);			
								url = "Expert_ReplyMMS.jsp";
							}	
						}
					}
					else
					{//truong hop chuyen gia khong tra loi
						
						if(majorId>=1)
						{//truong hop chuyen gia phan loai lai cho
							
							Info=cm.getMMSById(MMSid);
							
							MMSInfo.setid(MMSid);
				    		MMSInfo.setsender(Info.getsender());
				    		MMSInfo.setmsg(Info.getmsg());
				    		MMSInfo.setimage(Info.getimage());
				    		MMSInfo.setreplymsg(Info.getreplymsg());
				    		MMSInfo.setexpertid(Info.getexpertid());
				    		MMSInfo.setmajoridByMachine(Info.getmajoridByMachine());
				    		
				    		MMSInfo.setmajoridByHuman(majorId);
				    		
				    		MMSInfo.setanswered(Info.getanswered());
				    		
				    		MMSInfo.setclassifiedByHuman(true);
				    		
				    		MMSInfo.setclassifiedByMachine(Info.getclassifiedByMachine());
				    		MMSInfo.setencoded(Info.getencoded());
				    		MMSInfo.setreceivedMms(Info.getreceivedMms());
				    		MMSInfo.setused(Info.getused());
				    		MMSInfo.setreceivedDate(Info.getreceivedDate());
				    		MMSInfo.setansweredDate(Info.getansweredDate());
				    		MMSInfo.setgatewayid(Info.getgatewayid());
				    		MMSInfo.setsentexpert(Info.getsentexpert());
				    		MMSInfo.setsentfarmer(Info.getsentfarmer());
				    		
							if(cm.updateMMSById(MMSInfo)!=0)
							{
								session.setAttribute("themthanhcong", success);								
							}
							else session.setAttribute("themthanhcong", unsuccess);
							url = "Expert_ReplyMMS.jsp";
						}
						else
						{//truong hop chuyen gia chon lai tin nhan la chua phan loai
							
							Info=cm.getMMSById(MMSid);
							
							MMSInfo.setid(MMSid);
				    		MMSInfo.setsender(Info.getsender());
				    		MMSInfo.setmsg(Info.getmsg());
				    		MMSInfo.setimage(Info.getimage());
				    		MMSInfo.setreplymsg(Info.getreplymsg());
				    		MMSInfo.setexpertid(Info.getexpertid());
				    		MMSInfo.setmajoridByMachine(Info.getmajoridByMachine());
				    		
				    		MMSInfo.setmajoridByHuman(majorId);
				    		
				    		MMSInfo.setanswered(Info.getanswered());
				    		
				    		MMSInfo.setclassifiedByHuman(false);
				    		
				    		MMSInfo.setclassifiedByMachine(Info.getclassifiedByMachine());
				    		MMSInfo.setencoded(Info.getencoded());
				    		MMSInfo.setreceivedMms(Info.getreceivedMms());
				    		MMSInfo.setused(Info.getused());
				    		MMSInfo.setreceivedDate(Info.getreceivedDate());
				    		MMSInfo.setansweredDate(Info.getansweredDate());
				    		MMSInfo.setgatewayid(Info.getgatewayid());
				    		MMSInfo.setsentexpert(Info.getsentexpert());
				    		MMSInfo.setsentfarmer(Info.getsentfarmer());
				    		
							if(cm.updateMMSById(MMSInfo)!=0)
							{
								session.setAttribute("themthanhcong", success);								
							}
							else session.setAttribute("themthanhcong", unsuccess);
							url = "Expert_ReplyMMS.jsp";
						}
					}
				}
	        }
	        if(request.getParameter("adminClassifyMMS") != null)
	        {//truong hop Admin phan loai tin nhan      	
	        	
	        	if (options != null)
	        	{	        		
					for (int i=0; i < options.length; i++)
					{
						MMSid=Integer.parseInt(request.getParameter("id_khht:" + options[i]));
						msg=request.getParameter("msg" + options[i]);
						majorId=Integer.parseInt(request.getParameter("classify:" + options[i]));
						
						if(majorId>=1)
						{//Neu Admin co chon chuyen nganh sau khi check vao checkbox
							
							Info=cm.getMMSById(MMSid);	
							
							MMSInfo.setid(MMSid);
				    		MMSInfo.setsender(Info.getsender());			    		
				    		
				    		//cai nay de xac dinh xem co cap nhat du lieu tu textbox msg hay kg, neu he thong ban tu dong thi co txtmsg, con tu dong thi kg//
				    		//vi Admin co the chinh sua lai tin nhan truoc khi phan loai cho chuyen gia
				    		cf = cf.getConfigBycode("systemconfig");			        		
			        		if( cf.getvaluechar().compareTo("semi-auto") == 0 )
			        		{
			        			MMSInfo.setmsg(msg);
			        		}
			        		else if( cf.getvaluechar().compareTo("auto") == 0 )
			        		{
			        			MMSInfo.setmsg(Info.getmsg());
			        		}
				    		
				    		MMSInfo.setimage(Info.getimage());
				    		MMSInfo.setreplymsg(Info.getreplymsg());
				    		MMSInfo.setexpertid(Info.getexpertid());
				    		MMSInfo.setmajoridByMachine(Info.getmajoridByMachine());
				    		MMSInfo.setmajoridByHuman(majorId);
				    		MMSInfo.setanswered(Info.getanswered());
				    		MMSInfo.setclassifiedByHuman(true);
				    		MMSInfo.setclassifiedByMachine(Info.getclassifiedByMachine());
				    		MMSInfo.setencoded(Info.getencoded());
				    		MMSInfo.setreceivedMms(Info.getreceivedMms());
				    		MMSInfo.setused(Info.getused());
				    		MMSInfo.setreceivedDate(Info.getreceivedDate());
				    		MMSInfo.setansweredDate(Info.getansweredDate());
				    		MMSInfo.setgatewayid(Info.getgatewayid());
				    		MMSInfo.setsentexpert(Info.getsentexpert());
				    		MMSInfo.setsentfarmer(Info.getsentfarmer());
				    		
				    		cm.updateMMSById(MMSInfo);
												
							url = "Admin_ClassifyMMS.jsp";							
						}//end of if(majorId>=1)
						else
						{//truong hop chuyen gia co check vao checkbox nhung khong thuc hien phan loai/chon nganh
							session.setAttribute("themthanhcong", err);
							url = "Admin_ClassifyMMS.jsp";		
						}
					}//end of for
	        	}//end options
	        	else 
		        {
	        		if(session.getAttribute("themthanhcong") != success)
	        			session.setAttribute("themthanhcong", "Choose at least one record!");		
	        		url = "Admin_ClassifyMMS.jsp";
		        }
		    }//end of adminClassifyMMS
	        else if(request.getParameter("adminDeleteMMS")!=null)
		    {
	        	if (options != null)
	        	{
		        	for (int j=0; j < options.length; j++)
		        	{
		        		if(cm.deleteMMSById(Integer.parseInt(request.getParameter("id_khht:" + options[j])))!=0)
		        		{
		        			session.setAttribute("themthanhcong", success);
		        		}
		        		else session.setAttribute("themthanhcong", unsuccess);
		        		url = "Admin_ClassifyMMS.jsp";		
		        	}
	        	}
		    }
        response.sendRedirect(url);
	    } 
		catch (Exception ex) 
		{
			session.setAttribute("themthanhcong", err);
			url = "Admin_ClassifyMMS.jsp";		
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
/*
Simple as that:

DROP TRIGGER IF EXISTS trg_emp_tools;
CREATE TRIGGER trg_emp_tools AFTER INSERT ON employee
FOR EACH ROW 
BEGIN
INSERT INTO employee_tools (Id, Tool, Status)
SELECT NEW.Id, tools.Tool_Name, 'Working'
FROM
tools
WHERE Division = NEW.Division;
END;

share|improve this answer


DELIMITER ;;

CREATE TRIGGER insert_tbl_mms AFTER INSERT ON tbl_sms_in
FOR EACH ROW 
BEGIN
INSERT INTO tbl_mms (sender, msg, encoded) values (new.originator, new.text, new.encoding);
END ;;
DELIMITER ;

 * 
 */
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/* 
You need to change the delimiter - MySQL is seeing the first ";" as the end of the CREATE TRIGGER statement.
Try this:

Change the delimiter so we can use ";" within the CREATE TRIGGER
DELIMITER $$

CREATE TRIGGER add_bcc
BEFORE INSERT ON MailQueue
FOR EACH ROW BEGIN
 IF (NEW.sHeaders LIKE "%support@mydomain.com%") THEN
   SET NEW.sHeaders = NEW.sHeaders + "BCC:internal@mydomain.com";
 END IF;
END$$

DELIMITER ;

*/
