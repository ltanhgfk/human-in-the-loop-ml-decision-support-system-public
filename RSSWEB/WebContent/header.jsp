<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>HỆ THỐNG HÕ TRỢ KHUYẾN NÔNG</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link href="css/default.css" rel="stylesheet" type="text/css" />
<link href="css/Hmenu.css" rel="stylesheet" type="text/css" />
<link href="css/table.css" rel="stylesheet" type="text/css" />
<link href="css/button-css3.css" rel="stylesheet" type="text/css" />
<link href="css/dangnhap.css" rel="stylesheet" type="text/css" />

<script type="text/javascript" src="js/jquery-1.9.1.min.js"></script>
<script type="text/javascript" src="js/ajax.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="localization/messages_vi.js"></script> 
<script type="text/javascript">
		$(document).ready(function(){
			$("#frmDangNhap").validate({
				errorElement: "span", //Thành phần HTML hiện thông báo lỗi
				//Sử dụng tùy chọn rules cho những validate không hỗ trợ bởi class name
				rules: {
					cpassword: {
						equalTo: "#password" //So sánh với trường cpassword với thành trường có id là password
					},
					min_field: { min: 5 }, //Giá trị tối thiểu
					max_field: { max : 10 }, //Giá trị tối đa
					range_field: { range: [4,10] }, //Giá trị trong khoảng từ 4 - 10
					rangelength_field: { rangelength: [4,10] } //Chiều dài chuỗi trong khoảng từ 4 - 10 ký tự
				}
			});
		});
</script>

	
<title></title>
</head>

<body>
	
	<div id="wrapper-container">
    <div id="wrapper">
	<div id="header" >	
	<div id="header-left">	                   
    </div>
    <div id="header-right">
                    
           <%
			Object objdn = session.getAttribute("username");
			Object objten = session.getAttribute("tennguoidung");			
			Object objpq = session.getAttribute("usertype");
			Object objadmin = session.getAttribute("admin");
			
			if (objdn != null && objpq != null){
				String username = (String) objdn;
				String ten = (String) objten;
				out.println("<br/><br/><br/>");
				out.println("<div><a href=\"huongdan.jsp\" class='textheader' >Hướng dẫn</a> | <a class='textheader' href=\"LogoutController\">Thoát</a></div>");

				out.println("<div class='div-header'> Chào " + ten + "</div>");

				out.println("<div class='div-header'>(" + username + ")</div>");
			}
			else if (objadmin != null){// && objdn == null && objpq == null
				out.println("<br/><br/><br/>");
				out.println("<div class='div-header'><a class='textheader' href=\"huongdan.jsp\">Hướng dẫn</a> | <a class='textheader' href=\"LogoutController\">Thoát</a></div>");

				out.println("<div class='div-header'>Chào Admin </div>");

			}
			else{
				out.println("<br/><br/><br/>");
				out.println("<div class='div-header'><a class='textheader' href=\"huongdan.jsp\">Hướng dẫn</a></div>");
			}
		%>
	</div>
	</div>
	</div>

