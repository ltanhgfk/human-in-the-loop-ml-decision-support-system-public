<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="dao.*" %>
<%@ page import="global.*" %>
<%@ page import="model.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.sql.ResultSet" %>
    
<title>Thông tin cá nhân</title>
<head>  
</head>

<jsp:include page="header.jsp"/>
<jsp:include page="menu.jsp"/>
<div style="text-align: center;">
	<h1>THÔNG TIN CÁ NHÂN</h1>
</div>
<div style="text-align: center; color: red;">
		        	<%
		        	Object objttc = session.getAttribute("themthanhcong");
		        	if (objttc != null){
		        		String ttc = (String) objttc;
		        		out.print(ttc);
		        		session.removeAttribute("themthanhcong");
		        	}
		        	%>
</div>
<BR/>
<%
Object objdn = session.getAttribute("username");
String username = (String) session.getAttribute("username");
String usertype = (String) session.getAttribute("usertype");
	if (objdn != null && (usertype.equals("chuyengia") || usertype.equals("dieuphoi")))
	{		
		try
		{
%>				<div id='LietKe'>
				<div class="box-center-right">
				<div class="box-center-left">
				<table class="gridtable-xemkhht">
					<%
	        		ChuyenGia cg = new ChuyenGia();
	        		//ChuyenGia ds = new ChuyenGia();
	        		
	        		ChuyenGia expertId = cg.getExpertByUserId(username);	     
	        		cg = cg.getExpertById(expertId.getid());
	        		if(cg.getid()<=-1)
	        		{
	        			%>
	        			<br/><span style='color:red; font-weight:bold;'>Chưa có dữ liệu về chuyên gia!</span><br/><br/>
	        			<%
	        		}
	        		else
	        		{
	        		%>
	        		<tr>		        		
		        		<td>Tên đăng nhập</td>
		        		<td class="tenmon">
		        			<%= cg.getloginname() %>
		        		</td>
		        	</tr>       		        	      
	        		<tr>		        		
		        		<td>Số CMND</td>
		        		<td class="tenmon">
		        			<%= cg.getcode() %>
		        		</td>
		        	</tr>	
		        	<tr>		        		
		        		<td>Họ và tên</td>
		        		<td class="tenmon">
		        			<%= cg.getname() %>
		        		</td>
		        	</tr>
		        	<tr>
		        		<td>Di động</td>
		        		<td class="tenmon">
		        			<%= cg.getmobile() %>
		        		</td>
		        	</tr>
		        	<tr>		        		
		        		<td>Email</td>
		        		<td class="tenmon">
		        			<%= cg.getemail() %>
		        		</td>
		        	</tr>
		        	<!-- <tr>		        		
		        		<td>Thuộc nhà mạng</td>
		        		<td class="tenmon">
		        			<%= cg.getgatewayid() %>
		        		</td>
		        	</tr> -->		        	
		        	<tr>		        		
		        		<td>Địa chỉ </td>
		        		<td class="tenmon">
		        			<%= cg.getaddress() %>
		        		</td>
		        	</tr>
		        	<tr>
		        		<td>Cơ quan công tác</td>
		        		<td class="tenmon">
		        			<%= cg.getworkunit() %>
		        		</td>
		        	</tr>
		        	<tr>		        		
		        		<td>Ngày sinh </td>
		        		<td class="tenmon">
		        			<%= cg.getbirthdate() %>
		        		</td>
		        	</tr>
		        	<!-- 
		        	<tr>		        		
		        		<td>Ảnh 4x6 </td>
		        		<td class="tenmon">
		        			<img src="<%= cg.getimage() %>" width="" height="">		        			     			
		        		</td>
		        	</tr>
		        	-->
		        	<tr>		        		
		        		<td>Giới tính </td>
		        		<td>
		        		<%= cg.getsex() %>
						</td>
		        	</tr>
		        	<tr>		        		
		        		<td>Chức danh</td>
		        		<td>
		        		<%= cg.gettitle() %>
						</td>
		        	</tr>
		        	<tr>		        		
		        		<td>Học vị</td>
		        		<td>
		        		<%= cg.getdegree() %>
						</td>
		        	</tr>	
		        	<tr>		        		
		        		<td>Tình trạng</td>
		        		<td>
		        		<%
		        		if(cg.getstatus()==true)
		        		{
		        			out.print("Được chấp nhận");
		        		}
		        		else
		        		{
		        			out.print("Chưa được chấp nhận");
		        		}
						 %>
						</td>
		        	</tr>		        	
		        	<tr>		        		
		        		<td>Chuyên môn </td>
		        		<td>
		        		<!-- 
		        		<table class="gridtable-xemkhht">			        		
				        <%
						/*	int stt = 0;
				        	//int tong_so_tc = 0;
			        		ChuyenGiaChuyenMon cgcm = new ChuyenGiaChuyenMon();
			        		ArrayList<ChuyenGiaChuyenMon> dscgcm = new ArrayList<ChuyenGiaChuyenMon>();
			        		dscgcm = cgcm.getExpertMajorByExpertId(expertId.getid());
			        		
			        		for(ChuyenGiaChuyenMon r : dscgcm){
			        			stt += 1;
			        			ChuyenMon cm =  new ChuyenMon();
			        			ChuyenMon cminfo = new ChuyenMon();
			        			cminfo = cm.getMajorById(r.getMajorId());
			        	%>
			        			<tr>					        		
					        		<td class="tenmon">
					        			<%=cminfo.getName()%>
					        		</td>
					        	</tr>   
					     <%
					     }
			        	*/
					     %>   	     		
		        		</table>
		        		-->
		        		<%
							int stt = 0;
		        			String majorStr = "";
			        		ChuyenGiaChuyenMon cgcm = new ChuyenGiaChuyenMon();
			        		ArrayList<ChuyenGiaChuyenMon> dscgcm = new ArrayList<ChuyenGiaChuyenMon>();
			        		dscgcm = cgcm.getExpertMajorByExpertId(expertId.getid());
			        		
			        		for(ChuyenGiaChuyenMon r : dscgcm)
			        		{
			        			stt += 1;
			        			ChuyenMon cm =  new ChuyenMon();
			        			ChuyenMon cminfo = new ChuyenMon();
			        			cminfo = cm.getMajorById(r.getMajorId());
			        			majorStr =  majorStr + cminfo.getName() + ", ";			        	
					     	}
			        		out.print(majorStr);
					     %>
		        		</td>		        		
		        	</tr>		        				        		
	        		</table>
	        		<br/>        		
	        		
				</div> <!-- end class="box-center-left" -->
				</div> <!-- end class="box-center-right"  -->
				<div style = "clear:both;"> </div>
				</div>

				<div class="box-center-right">
				<div class="box-center-left">
					<div class="button-link" style="padding-top: 5px;">		        		
		        		<a href="Expert_UpdateInfo.jsp?expertId_var=<%=expertId.getid() %>" class="nutsearch blue small">Cập nhật thông tin</a>
		        	</div>
				</div> <!-- end class="box-center-left" -->
				</div> <!-- end class="box-center-right"  -->
				<div style = "clear:both;"></div>	    		
	    		<br/>
		        <%
				}
			}		
		catch (Exception e){			
		}	
	}	
	else
	{
		response.sendRedirect("index.jsp");
	}
%>

<jsp:include page="footer.jsp"/>
