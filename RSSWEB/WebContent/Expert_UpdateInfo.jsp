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
    
<title>chuyên gia cập nhật thông tin</title>
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
			if(test_empty(document.frmUpdateExpertInfo.loginname.value)){
				alert("Bạn chưa nhập tên đăng nhập!");document.frmUpdateExpertInfo.loginname.focus();return false;
			}
			if(test_empty(document.frmUpdateExpertInfo.loginpassword.value)){
				alert("Bạn chưa nhập mật khẩu!");document.frmUpdateExpertInfo.loginpassword.focus();return false;
			}
			if(test_empty(document.frmUpdateExpertInfo.reloginpassword.value)){
				alert("Bạn chưa nhập lại mật khẩu!");document.frmUpdateExpertInfo.reloginpassword.focus();return false;
			}
			if(!test_confirm_pass(document.frmUpdateExpertInfo.loginpassword.value,document.frmUpdateExpertInfo.reloginpassword.value)){
				alert("Hai mật khẩu không khớp nhau!");
				document.frmUpdateExpertInfo.loginpassword.value = '';
				document.frmUpdateExpertInfo.reloginpassword.value = '';
				document.frmUpdateExpertInfo.loginpassword.focus();return false;
			}
			if(test_empty(document.frmUpdateExpertInfo.code.value)){
				alert("Bạn chưa nhập số CMND");document.frmUpdateExpertInfo.code.focus();return false;
			}
			if(test_integer(document.frmUpdateExpertInfo.code.value)){
				alert("Số CMND phải là kiểu số nguyên!");document.frmUpdateExpertInfo.code.focus();return false;
			}
			if(test_empty(document.frmUpdateExpertInfo.name.value)){
				alert("Bạn chưa nhập họ tên!");document.frmUpdateExpertInfo.name.focus();return false;
			}
			if(test_empty(document.frmUpdateExpertInfo.mobile.value)){
				alert("Bạn chưa nhập số di động của bạn!");document.frmUpdateExpertInfo.mobile.focus();return false;
			}			
			if(test_integer(document.frmUpdateExpertInfo.mobile.value)){
				alert("Số di động phải là kiểu số nguyên!");document.frmUpdateExpertInfo.mobile.focus();return false;
			}
			/*if(test_integer(document.frmUpdateExpertInfo.gatewayid.value)){
				alert("Bạn phải chọn nhà mạng cho số điện thoại của bạn!");document.frmUpdateExpertInfo.gatewayid.focus();return false;
			}*/
			if(!checkEmail(document.frmUpdateExpertInfo.email.value)){
				alert("Email không hợp lệ, mời bạn nhập lại!");document.frmUpdateExpertInfo.email.focus();return false;
			}
			
			return true;
		}
    </script>
    <script language="javascript" type="text/javascript">	
    function makeDisableOrEnable(id1){
		    var x=document.getElementById(id1);
		    /*var y=document.getElementById(id2);
		    var z=document.getElementById(id3);*/
		    if (x.disabled == true)// && y.disabled == true && z.disabled == true
		    {
		    	x.disabled = false;
		    	//y.disabled = false;
		    	//z.disabled = false;
		    }		    	
		    else{
		    	x.disabled = true;
		    	//y.disabled = true;
		    	//z.disabled = true;
		    } 	    	
		}
		function getValue(id){
		    var x = document.getElementById(id);
		    return x.value; 	    	
		}
	</script>
</head>
<jsp:include page="header.jsp"/>
<jsp:include page="menu.jsp"/>
<div style="text-align: center;">
	<h1>CẬP NHẬT THÔNG TIN CHUYÊN GIA</h1>
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
%>				<div style="text-align: center; color: red;">				
		        	<%
		        	Object objttc = session.getAttribute("themthanhcong");
		        	if (objttc != null){
		        		String ttc = (String) objttc;
		        		out.print(ttc);
		        		session.removeAttribute("themthanhcong");
		        	}
		        	%>
		        </div>					
		        </br>
		        <div>
				<!-- <img id="captcha" src="<c:url value="simpleCaptcha.jpg"/>" width="150"> -->
				<form name='frmUpdateExpertInfo' action='ChuyenGiaController' method='POST' >				
					<div id='LietKe' text-align: center;">
					<div class="box-center-right">
					<div class="box-center-left">
					<table class="gridtable-xemkhht">
					<%
					int expertId = Integer.parseInt(request.getParameter("expertId_var"));	
					if(expertId <= 0)
					{
						String expertid_Str = (String) session.getAttribute("expertid");
						if(expertid_Str!="")
						{
							expertId = Integer.parseInt(expertid_Str);
						}
					}
	        		ChuyenGia mh = new ChuyenGia();
	        		ChuyenGia ds = new ChuyenGia();
	        		ds = mh.getExpertById(expertId);
	        		%>	        		     	     		
	        		<tr>		        		
		        		<td>Số CMND</td>
		        		<td class="tenmon">
		        			<input type='text' id='code' name='code' value='<%= ds.getcode() %>'>*
		        			<input type='hidden' name='hiddenExpertId' value='<%= expertId %>'>
		        		</td>
		        	</tr>
		        	<tr>		        		
		        		<td>Họ và tên</td>
		        		<td class="tenmon">
		        			<input type='text' id='name' name='name' value='<%= ds.getname() %>'>*
		        		</td>
		        	</tr>	        		
		        	<tr>		        		
		        		<td>Chức danh</td>
		        		<td>
		        		<%		        		
		        		String nonehh = ds.gettitle().compareTo("Không có")==0 ? "selected" : "";
		        		String gs = ds.gettitle().compareTo("Giáo sư")==0 ? "selected" : "";
		        		String pgs = ds.gettitle().compareTo("Phó giáo sư")==0 ? "selected" : "";
		        		String gvc = ds.gettitle().compareTo("Giảng viên chính")==0 ? "selected" : "";
		        		String otherhh = ds.gettitle().compareTo("Khác")==0 ? "selected" : "";
		        		%>
		        		<select id="title" name="title" size="1">
								<option value="Không có" <%=nonehh%>>[ Chọn Học hàm ]</option>
								<option value="Giáo sư" <%=gs%>>Giáo sư</option>
								<option value="Phó giáo sư" <%=pgs%>>Phó giáo sư</option>								
								<option value="Giảng viên chính" <%=gvc%>>Giảng viên chính</option>
								<option value="Khác" <%=otherhh%>>Khác</option>
						</select>
						</td>
		        	</tr>
		        	<tr>		        		
		        		<td>Học vị</td>
		        		<td>
		        		<%
		        		String nonehv = ds.getdegree().compareTo("Không có")==0 ? "selected" : "";
		        		String ts = ds.getdegree().compareTo("Tiến sĩ")==0 ? "selected" : "";
		        		String ths = ds.getdegree().compareTo("Thạc sĩ")==0 ? "selected" : "";
		        		String ks = ds.getdegree().compareTo("Kỹ sư")==0 ? "selected" : "";
		        		String cn = ds.getdegree().compareTo("Cử nhân")==0 ? "selected" : "";
		        		String otherhv = ds.getdegree().compareTo("Khác")==0 ? "selected" : "";
		        		%>
		        		<select id="degree" name="degree" size="1">
								<option value="Không có" <%=nonehv%>>[ Chọn Học vị ]</option>
								<option value="Tiến sĩ" <%=ts%>>Tiến sĩ</option>
								<option value="Thạc sĩ" <%=ths%>>Thạc sĩ</option>								
								<option value="Kỹ sư" <%=ks%>>Kỹ sư</option>
								<option value="Cử nhân" <%=cn%>>Cử nhân</option>
								<option value="Khác" <%=otherhv%>>Khác</option>
						</select>
						</td>
		        	</tr>
		        	<tr>
		        		<td>Cơ quan công tác</td>
		        		<td class="tenmon">
		        			<input type='text' id='workunit' name='workunit' value='<%= ds.getworkunit() %>'>
		        		</td>
		        	</tr>
		        	<tr>
		        		<td>Di động</td>
		        		<td class="tenmon">
		        			<input type='text' id='mobile' name='mobile' value='<%= ds.getmobile() %>'>*Ví dụ: 0901234567
		        		</td>
		        	</tr>
		        	<tr>		        		
		        		<td>Thuộc nhà mạng</td>		        		
		        		<td>
															<select id="gatewayid" name="gatewayid" size="1">																										
																<%
																Gateway gw = new Gateway();
																String selected="";
													    		ArrayList<Gateway> dsgw = new ArrayList<Gateway>();
													    		dsgw = gw.getAllGateway();
													    		for(Gateway record : dsgw)
													    		{
													    			if(ds.getgatewayid()==record.getId())
													    			{
													    				selected="selected";
													    			}
													    			else selected="";
													    			%>
													    			<option value="<%= record.getId() %>" <%=selected %>><%= record.getoperator() %></option>
													    			<%
													    		}
													    		%>
													    	</select>*
													</td>
		        	</tr>
		        	<tr>		        		
		        		<td>Email</td>
		        		<td class="tenmon">
		        			<input type='text' id='email' name='email' value='<%= ds.getemail() %>'>*
		        		</td>
		        	</tr>
		        	<tr>		        		
		        		<td>Địa chỉ </td>
		        		<td class="tenmon">
		        			<input type='text' id='address' name='address' value='<%= ds.getaddress() %>'>
		        		</td>
		        	</tr>
		        	<tr>		        		
		        		<td>Ngày sinh </td>
		        		<td class="tenmon">
		        			<input type='text' id='birthdate' name='birthdate' value='<%= ds.getbirthdate()%>'/>
		        			</br>
		        			Ví dụ: 20/10/1980
		        		</td>		        		
		        	</tr>
		        	<!-- 
		        	<tr>		        		
		        		<td>Ảnh 4x6 </td>
		        		<td class="tenmon">
		        			<img src='<%= ds.getimage() %>' width='40' height='60' >
		        			<input type="file" name="image" id="image" size="50"/>			        				
		        		</td>		        		
		        	</tr>
		        	-->
		        	<tr>		        		
		        		<td>Giới tính </td>
		        		<td>
		        		<%
		        		String nonesex = ds.getsex().compareTo("Không xác định")==0 ? "selected" : "";
		        		String nam = ds.getsex().compareTo("Nam")==0 ? "selected" : "";
		        		String nu = ds.getsex().compareTo("Nữ")==0 ? "selected" : "";
		        		String othersex = ds.getsex().compareTo("Khác")==0 ? "selected" : "";
		        		%>
		        		<select id="sex" name="sex" size="1">
								<option value="Không xác định" <%=nonesex%>>[ Chọn giới tính ]</option>
								<option value="Nam" <%=nam%>>Nam</option>
								<option value="Nữ" <%=nu%>>Nữ</option>								
								<option value="Khác" <%=othersex%>>Khác</option>
						</select>
						</td>
		        	</tr>
		        	<!-- 
		        	<tr>		        		
		        		<td>Đăng ký làm</td>
		        		<td>
		        		<%
		        		String chuyengia = ds.getusertype().compareTo("chuyengia")==0 ? "selected" : "";
		        		String dieuphoi = ds.getusertype().compareTo("dieuphoi")==0 ? "selected" : "";
		        		%>
		        		<select id="usertype" name="usertype" size="1">
								<option value="chuyengia" <%=chuyengia%>>[ Chuyên Gia ]</option>
								<option value="dieuphoi" <%=dieuphoi%>>[ Điều phối viên ]</option>								
						</select>
						</td>
		        	</tr>
		        	-->
		        	<tr>		        		
		        		<td>Chuyên môn </td>
		        		<td>
		        		<table class="gridtable-xemkhht">			        		
				        <%
							int stt = 0;				        	
			        		ChuyenMon cm = new ChuyenMon();
			        		ArrayList<ChuyenMon> dscm = new ArrayList<ChuyenMon>();
			        		dscm = cm.getAllMajor();
			        		
			        		for(ChuyenMon r : dscm)
			        		{
			        			stt += 1;		
			        			String checked="";
			        			ChuyenGiaChuyenMon cgcm = new ChuyenGiaChuyenMon();
				        		ArrayList<ChuyenGiaChuyenMon> dscgcm = new ArrayList<ChuyenGiaChuyenMon>();
				        		dscgcm = cgcm.getExpertMajorByExpertId(ds.getid());
				        		for(ChuyenGiaChuyenMon row : dscgcm)
				        		{
				        			if(row.getMajorId() == r.getId())
				        			{
				        				checked = "checked = 'checked'";
				        			}
				        		}
			        	%>
			        			<tr>					        		
					        		<td class="tenmon">
					        			<%=r.getName()%>
					        		</td>					        			        		
					        		<td>
					        			<input type='checkbox' id='option_<%= stt %>' name="majorOption" value='<%= stt %>' <%=checked%> >
					        			<input type='hidden' name='id_cm:<%= stt %>' value='<%= r.getId() %>'>
					        		</td>
					        	</tr>   
							     <%
							}
					     %>
		        		</table>
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
		        		<input type='submit' value='Cập nhật' name='expertUpdateInfo' onclick="return btnRegistry_onclick()" class="nutsearch blue small">
		        		<input type='reset' value='Làm lại' name='reset' class="nutsearch blue small">
		        	</div>
				</div> <!-- end class="box-center-left" -->
				</div> <!-- end class="box-center-right"  -->
				<div style = "clear:both;"> </div>
	    		</form>
	    		</div>
	    		<br/>  
		        <%			
		}
		catch (Exception e){			
		}	
	}
	else{
		response.sendRedirect("index.jsp");
	}
%>

<jsp:include page="footer.jsp"/>
