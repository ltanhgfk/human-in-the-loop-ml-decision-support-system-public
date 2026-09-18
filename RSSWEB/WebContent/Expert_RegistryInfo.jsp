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

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
    
<title>Đăng ký chuyên gia</title>
<head>
<!-- <link rel="stylesheet" href="css/demos.css"> -->
	<script type="text/javascript" src="js/jquery-1.9.1.min.js"></script> 
	<link rel="stylesheet" href="css/jquery.ui.all.css">	
	<script src="js/jquery.ui.core.js"></script>
	<script src="js/jquery.ui.widget.js"></script>
	<script src="js/jquery.ui.datepicker.js"></script>		
	<script src="js/javascript.lib.js"></script>
	<script src="js/global_func.js"></script>
	<script>
		$(function() {
			$( "#birthdate" ).datepicker({
				defaultDate: "+1w",
				changeMonth: true,
				numberOfMonths: 3				
			});	
		});
	</script>
	<script language="javascript" src="js/md5.js"></script>
    <script language="javascript" type="text/javascript">
		function btnRegistry_onclick(){			
			if(test_empty(document.frmRegistry.loginname.value)){
				alert("Bạn chưa nhập tên đăng nhập!");document.frmRegistry.loginname.focus();return false;
			}
			if(test_empty(document.frmRegistry.loginpassword.value)){
				alert("Bạn chưa nhập mật khẩu!");document.frmRegistry.loginpassword.focus();return false;
			}
			if(test_empty(document.frmRegistry.reloginpassword.value)){
				alert("Bạn chưa nhập lại mật khẩu!");document.frmRegistry.reloginpassword.focus();return false;
			}
			if(!test_confirm_pass(document.frmRegistry.loginpassword.value,document.frmRegistry.reloginpassword.value)){
				alert("Hai mật khẩu không khớp nhau!");
				document.frmRegistry.loginpassword.value = '';
				document.frmRegistry.reloginpassword.value = '';
				document.frmRegistry.loginpassword.focus();return false;
			}
			if(test_empty(document.frmRegistry.code.value)){
				alert("Bạn chưa nhập số CMND");document.frmRegistry.code.focus();return false;
			}
			if(test_integer(document.frmRegistry.code.value)){
				alert("Số CMND phải là kiểu số nguyên!");document.frmRegistry.code.focus();return false;
			}
			if(test_empty(document.frmRegistry.name.value)){
				alert("Bạn chưa nhập họ tên!");document.frmRegistry.name.focus();return false;
			}
			if(test_empty(document.frmRegistry.mobile.value)){
				alert("Bạn chưa nhập số di động của bạn!");document.frmRegistry.mobile.focus();return false;
			}			
			if(test_integer(document.frmRegistry.mobile.value)){
				alert("Số di động phải là kiểu số nguyên!");document.frmRegistry.mobile.focus();return false;
			}
			if(test_integer(document.frmRegistry.gatewayid.value)){
				alert("Bạn phải chọn nhà mạng cho số điện thoại của bạn!");document.frmRegistry.gatewayid.focus();return false;
			}
			if(!checkEmail(document.frmRegistry.email.value)){
				alert("Email không hợp lệ, mời bạn nhập lại!");document.frmRegistry.email.focus();return false;
			}
			
			document.frmRegistry.loginpassword.value = hex_md5(document.frmRegistry.loginpassword.value);
			document.frmRegistry.reloginpassword.value = hex_md5(document.frmRegistry.reloginpassword.value);			
			alert("Sau: " + document.frmDangNhap.password.value);
			
			return true;
		}
    </script>
</head>
<jsp:include page="header.jsp"/>
<jsp:include page="menu.jsp"/>
<div style="text-align: center;">
	<h1>ĐĂNG KÝ THÔNG TIN CHUYÊN GIA</h1>
</div>
<BR/>
<div>
				<!-- <img id="captcha" src="<c:url value="simpleCaptcha.jpg"/>" width="150"> -->
				<form name='frmRegistry' action='ChuyenGiaController' method='POST'>				
					<div id='LietKe' text-align: center;">
					<div class="box-center-right">
					<div class="box-center-left">
					<table class="gridtable-xemkhht">
					
	        		<tr>		        		
		        		<td>Tên đăng nhập</td>
		        		<td class="tenmon">
		        			<input type='text' id='loginname' name='loginname' >*
		        		</td>
		        	</tr>
		        	<tr>		        		
		        		<td>Mật khẩu</td>
		        		<td class="tenmon">
		        			<input type='password' id='loginpassword' name='loginpassword' >*
		        		</td>
		        	</tr>   
		        	<tr>
		        		<td>Nhập lại mật khẩu</td>
		        		<td class="tenmon">
		        			<input type='password' id='reloginpassword' name='reloginpassword'>*
		        		</td>
		        	</tr>
		        	<!--<tr>
		        		<td>Nhập mã kiểm tra</td>
		        		<td class="tenmon">
		        			<input type='text' id='txtcaptcha' name='txtcaptcha'>
		        		</td>
		        	</tr>-->
					<tr>		        		
		        		<td>Số CMND</td>
		        		<td class="tenmon">
		        			<input type='text' id='code' name='code' >*
		        		</td>
		        	</tr>	
		        	<tr>		        		
		        		<td>Họ và tên</td>
		        		<td class="tenmon">
		        			<input type='text' id='name' name='name' >*
		        		</td>
		        	</tr>	        		
		        	<tr>		        		
		        		<td>Chức danh</td>
		        		<td>
		        		<select id="title" name="title" size="1">
								<option value="Không có">[ Chọn chức danh ]</option>
								<option value="Giáo sư">Giáo sư</option>
								<option value="Phó giáo sư">Phó giáo sư</option>								
								<option value="Giảng viên chính">Giảng viên chính</option>
								<option value="Khác">Khác</option>
						</select>
						</td>
		        	</tr>
		        	<tr>		        		
		        		<td>Học vị</td>
		        		<td>
		        		<select id="degree" name="degree" size="1">
								<option value="Không có">[ Chọn Học vị ]</option>
								<option value="Tiến sĩ">Tiến sĩ</option>
								<option value="Thạc sĩ">Thạc sĩ</option>								
								<option value="Kỹ sư">Kỹ sư</option>
								<option value="Cử nhân">Cử nhân</option>
								<option value="Khác">Khác</option>
						</select>
						</td>
		        	</tr>
		        	<tr>
		        		<td>Cơ quan công tác</td>
		        		<td class="tenmon">
		        			<input type='text' id='workunit' name='workunit' >
		        		</td>
		        	</tr>
		        	<tr>
		        		<td>Di động</td>
		        		<td class="tenmon">
		        			<input type='text' id='mobile' name='mobile'>*Ví dụ: 0901234567
		        		</td>
		        	</tr>
		        	<tr>		        		
		        		<td>Thuộc nhà mạng</td>		        		
		        		<td>
															<select id="gatewayid" name="gatewayid" size="1">
																<option value="n">[ Chọn nhà mạng ]</option>										
																<%
																Gateway gw = new Gateway();
																ArrayList<Gateway> dsgw = new ArrayList<Gateway>();
													    		dsgw = gw.getAllGateway();
													    		for(Gateway record : dsgw)
													    		{
													    			%>
													    			<option value="<%= record.getId() %>"><%= record.getoperator()%></option>
													    			<%
													    		}
													    		%>
													    	</select>*
													</td>
		        	</tr>
		        	<tr>		        		
		        		<td>Email</td>
		        		<td class="tenmon">
		        			<input type='text' id='email' name='email' >*
		        		</td>
		        	</tr>
		        	<tr>		        		
		        		<td>Địa chỉ </td>
		        		<td class="tenmon">
		        			<input type='text' id='address' name='address' >
		        		</td>
		        	</tr>
		        	<tr>		        		
		        		<td>Ngày sinh </td>
		        		<td class="tenmon">
		        			<input type='text' id='birthdate' name='birthdate'/>
		        			</br>
		        			Ví dụ: 20/10/1980
		        		</td>		        		
		        	</tr>
		        	<!-- 
		        	<tr>		        		
		        		<td>Ảnh 4x6 </td>
		        		<td class="tenmon">
		        			<input type="file" name="image" id="image" size="50"/>		        			
		        		</td>		        		
		        	-->
		        	<tr>		        		
		        		<td>Giới tính </td>
		        		<td>
		        		<select id="sex" name="sex" size="1">
								<option value="Không xác định">[ Chọn giới tính ]</option>
								<option value="Nam">Nam</option>
								<option value="Nữ">Nữ</option>								
								<option value="Khác">Khác</option>
						</select>
						</td>
		        	</tr>
		        	<!-- 
		        	<tr>		        		
		        		<td>Đăng ký làm</td>
		        		<td>
		        		<select id="usertype" name="usertype" size="1">
								<option value="chuyengia">[ Chuyên Gia ]</option>
								<option value="Dieuphoi">[ Điều phối viên ]</option>								
						</select>
						</td>
		        	</tr>
		        	-->
		        	<tr>		        		
		        		<td>Chuyên môn </td>
		        		<td>
		        		<table class="gridtable-xemkhht">			        		
				        <%
				        try
				        {
							int stt = 0;				        	
			        		ChuyenMon cm = new ChuyenMon();
			        		ArrayList<ChuyenMon> dscm = new ArrayList<ChuyenMon>();
			        		dscm = cm.getAllMajor();
			        		
			        		for(ChuyenMon r : dscm)
			        		{
			        			stt += 1;			        			
			        	%>
			        			<tr>					        		
					        		<td class="tenmon">
					        			<%=r.getName()%>
					        		</td>					        			        		
					        		<td>
					        			<input type='checkbox' id='option_<%= stt %>' name="majorOption" value='<%= stt %>'>
					        			<input type='hidden' name='id_cm:<%= stt %>' value='<%= r.getId() %>'>
					        		</td>
					        	</tr>   
					     <%
					     	}
			        	}
			    		catch (Exception e){			
			    		}
					     %>   	     		
		        		</table>
		        		</td>		        		
		        	</tr>
		        				        		
	        		</table>
	        		<br/>
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
	        		
				</div> <!-- end class="box-center-left" -->
				</div> <!-- end class="box-center-right"  -->
				<div style = "clear:both;"> </div>
				</div>

				<div class="box-center-right">
				<div class="box-center-left">
					<div class="button-link" style="padding-top: 5px;">
		        		<input type='submit' value='Đăng ký' name='expertRegistryInfo' onclick="return btnRegistry_onclick()" class="nutsearch blue small">
		        		<input type='reset' value='Làm lại' name='reset' class="nutsearch blue small">
		        	</div>
				</div> <!-- end class="box-center-left" -->
				</div> <!-- end class="box-center-right"  -->
				<div style = "clear:both;"> </div>
	    		</form>
</div>
<br/>
<jsp:include page="footer.jsp"/>