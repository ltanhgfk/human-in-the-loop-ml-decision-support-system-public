<%@page import="java.sql.ResultSet"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<head>
<script language="javascript" src="js/md5.js"></script>
<script language="javascript">
function btnlogin_onclick()
{
	document.frmDangNhap.password.value = hex_md5(document.frmDangNhap.password.value);
	//alert("Sau: " + document.frmDangNhap.password.value);	
	return true;
}
</script>
</head>
<jsp:include page="header.jsp"/>
<jsp:include page="menu.jsp"/>
<div id="thong_bao_loi" style="text-align: center; color: red; text-shadow: black;">
				<%				
		        	Object objttc = session.getAttribute("themthanhcong");
		        	if (objttc != null){
		        		String ttc = (String) objttc;
		        		out.print(ttc);
		        		session.removeAttribute("themthanhcong");
		        	}
		        %>
</div>
<div class="box-center-right">
   <div class="box-center-left">
		<form action="LoginController" class="login-form" method="POST" name="frmDangNhap">
			<div style="width: 100%; text-align: left;">
				<div style="width: 50px; float: left;">
					<img src="images/system/icon_sv.gif" alt="Big Boat">
				</div>
				<h3>ĐĂNG NHẬP</h3>
			</div>
			
			<ul>
				<li><input class="required" id="username" name="username"
					type="text" class="text" tabindex="1" placeholder="Tên đăng nhập" />
				</li>
				<li><input class="required" id="password" name="password"
					type="password" class="text" tabindex="2" placeholder="Mật khẩu" /></li>
				<li style="text-align: center;"><input class="btTxt submit" type="submit" name='expertlogin' value="Đăng nhập" onClick ="btnlogin_onclick()" />
				</li>
				<li style="text-align: center;">
				<a href="Expert_RegistryInfo.jsp"><img src="images/system/buttonregis.png"></a>				
				</li>				
				</br>
			</ul>			
		</form>
</div> <!-- end class="box-center-left" -->
</div> <!-- end class="box-center-right"  -->
<div style = 'clear:both;'> </div>
<jsp:include page="footer.jsp"/>
