<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="dao.*" %>
<%@ page import="model.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.sql.ResultSet" %>   

<jsp:include page="header.jsp"/>
<% 
	Object objpq = session.getAttribute("phanquyen");
	String pq = (String) objpq;
	if ((objpq != null)  && (pq.equals("1"))){
		%>
		<jsp:include page="menu.jsp"/>
		<%
	}
	
	else {
		%>
		<jsp:include page="menu.jsp"/>
		<%
	}
%>

<div style="width: 100%; text-align: center;">
	<h1>HƯỚNG DẪN SỬ DỤNG</h1>
</div>
<br/>
<div style="height: auto;">
Đọc kỹ hướng dẫn sử dụng trước khi dùng!
</div>

<jsp:include page="footer.jsp"/>
