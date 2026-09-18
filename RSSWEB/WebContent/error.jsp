<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="dao.*" %>
<%@ page import="model.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.sql.ResultSet" %>
    
<title>Lỗi</title>
<jsp:include page="header.jsp"/>
<jsp:include page="menu.jsp"/>
<div style="width: 100%; text-align: center;">
	<h1>DANH SÁCH HỌC PHẦN</h1>
</div>
<br/>

<%
	Object objdn = session.getAttribute("username");
	Object objpq = session.getAttribute("phanquyen");
	Object objerr = session.getAttribute("error");
	if (objdn != null && objpq != null){
		try{
			String error = (String) objerr;			
			%>
			<div style="text-align: center; color: red;"><%= error %></div>
			<br/>
			<%
			session.removeAttribute("error");
			String back = request.getParameter("back");
			if (back != null)
			{
				if (back.equals("1")){
					%>					
					<div style="text-align: center; color: red;">
						<a class="nutsearch orange medium" href="sv_themhocphantrongkhungchuongtrinh.jsp">Trở lại</a>
					</div>
					<%
				}
				if (back.equals("2")){
					%>
					<div style="text-align: center; color: red;">
						<a class="nutsearch orange medium" href="sv_themhocphanngoaikhungchuongtrinh.jsp">Trở lại</a>
					</div>
					<%
				}
				if (back.equals("3")){
					%>
					<div style="text-align: center; color: red;">
						<a class="nutsearch orange medium" href="sv_capnhatnamhochocky.jsp">Trở lại</a>
					</div>
					<%
				}
				if (back.equals("4")){
					%>
					<div style="text-align: center; color: red;">
						<a class="nutsearch orange medium" href="sv_capnhathocphan.jsp">Trở lại</a>
					</div>
					<%
				}
				if (back.equals("5")){
					%>
					<div style="text-align: center; color: red;">
						<a class="nutsearch orange medium" href="cv_themhocphantrongkhungchuongtrinh.jsp">Trở lại</a>
					</div>
					<%
				}
				if (back.equals("6")){
					%>
					<div style="text-align: center; color: red;">
						<a class="nutsearch orange medium" href="cv_themhocphanngoaikhungchuongtrinh.jsp">Trở lại</a>
					</div>
					<%
				}
				if (back.equals("7")){
					%>
					<div style="text-align: center; color: red;">
						<a class="nutsearch orange medium" href="cv_xemkhht.jsp">Trở lại</a>
					</div>
					<%
				}
				if (back.equals("8")){
					%>
					<div style="text-align: center; color: red;">
						<a class="nutsearch orange medium" href="cv_capnhatnamhochocky.jsp">Trở lại</a>
					</div>
					<%
				}
			}
			
		}
		catch (Exception e){			
		}	
	}
	else{
		response.sendRedirect("dangnhap.jsp");
	}	
%>

<jsp:include page="footer.jsp"/>
