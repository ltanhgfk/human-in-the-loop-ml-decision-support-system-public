<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@page import="java.sql.ResultSet"%>

<jsp:include page="header.jsp"/>
<jsp:include page="menu_admin.jsp"/>
<div class="box-center-right">
   <div class="box-center-left">
<%
	Object objdn = session.getAttribute("admin");
	if (objdn != null){
		response.sendRedirect("Admin_UpdateMajor.jsp");
	}
	else{
		%>
		<form action="LoginController" class="login-form" method="POST" id="frmDangNhapAdmin">
			<div style="width: 100%; text-align: left;">
				<div style="width: 50px; float: left;">
					<img src="images/system/icon_admin.gif" alt="Big Boat">
				</div>
				<h3>QUẢN TRỊ</h3>
			</div>
			<ul>
				<li><input class="required" id="username" name="username"
					type="text" class="text" tabindex="1" placeholder="Tên nguoi dùng" />
				</li>
				<li><input class="required" id="password" name="password"
					type="password" class="text" tabindex="2" placeholder="Mật khẩu" /></li>
				<li style="text-align: center;"><input class="btTxt submit" type="submit" name='adminlogin' value="Đăng nhập" />
				</li>
			</ul>
			<div id="thong_bao_loi"
				style="text-align: center; color: red; text-shadow: black;">
				<%
		        	Object objttc = session.getAttribute("themthanhcong");
		        	if (objttc != null){
		        		String ttc = (String) objttc;
		        		out.print(ttc);
		        		session.removeAttribute("themthanhcong");
		        	}
		        %>
			</div>
		</form>
		<%
	}
%>
</div> <!-- end class="box-center-left" -->
</div> <!-- end class="box-center-right"  -->
<div style = 'clear:both;'> </div>
<jsp:include page="footer.jsp"/>
