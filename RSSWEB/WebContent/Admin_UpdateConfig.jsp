<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="dao.*" %>
<%@ page import="global.*" %>
<%@ page import="model.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.sql.ResultSet" %>
    
<title>cấu hình hệ thống</title>
<head>

	<script src="js/javascript.lib.js"></script>
	<script src="js/global_func.js"></script>
    <script language="javascript" type="text/javascript">	    
		
		function makeDisableOrEnable(id){
		    var x=document.getElementById(id);
		    
		    if (x.disabled == true)
		    {
		    	x.disabled = false;		    	
		    }		    	
		    else{
		    	x.disabled = true;		    	
		    } 	    	
		}
		
		function btnRegistry_onclick()
		{			
			//alert("Bạn không được để trống các thông số, hãy xem lại các thông số!");
			if(test_empty(document.frmConfig.number_sms_retrain.value)||test_empty(document.frmConfig.sleep_time_get_sms.value)||test_empty(document.frmConfig.sleep_time_retrain.value)||test_empty(document.frmConfig.sleep_time_classify.value))
			{
				alert("Bạn không được để trống các thông số, hãy xem lại các thông số!");document.frmConfig.number_sms_need.focus();return false;
			}			
			if(test_integer(document.frmConfig.number_sms_retrain.value)||test_integer(document.frmConfig.sleep_time_get_sms.value)||test_integer(document.frmConfig.sleep_time_retrain.value)||test_integer(document.frmConfig.sleep_time_classify.value))
			{
				alert("Các thông số phải là kiểu số nguyên, hãy xem lại các thông số!");document.frmConfig.number_sms_retrain.focus();return false;
			}
			
			if((document.frmConfig.number_sms_retrain.value < 0 ||document.frmConfig.number_sms_retrain.value > 1000000))
			{
				alert("Giá trị các thông số số lượng sms nằm ngoài khoảng cho phép, xin nhập lại!");document.frmConfig.number_sms_retrain.focus();return false;
			}
			if((document.frmConfig.sleep_time_get_sms.value < 0||document.frmConfig.sleep_time_get_sms.value > 32000)||(document.frmConfig.sleep_time_classify.value<=0||document.frmConfig.sleep_time_classify.value >32000)||(document.frmConfig.sleep_time_retrain.value < 0||document.frmConfig.sleep_time_retrain.value > 200))
			{
				alert("Giá trị các thông số thời gian lặp lại nằm ngoài khoảng cho phép, xin nhập lại!");document.frmConfig.number_sms_retrain.focus();return false;
			}
			
			return true;
		}
		
    </script>
</head>
<jsp:include page="header.jsp"/>
<jsp:include page="menu_admin.jsp"/>
<div style="text-align: center;">
	<h1>CẤU HÌNH HỆ THỐNG</h1>
</div>
<BR/>
<%
Object objdn = session.getAttribute("admin");
if (objdn != null)
{	
		try
		{
%>
				<div>				
				<form name='frmConfig' action='ConfigController' method='POST'>				
					<div id='LietKe' text-align: center;">
					<div class="box-center-right">
					<div class="box-center-left">
					<table class="gridtable-xemkhht">
	        		
		        	<%
					int stt = 0;		        	
	        		Config mh = new Config();
	        		ArrayList<Config> ds = new ArrayList<Config>();
	        		ds = mh.getAllConfig();
	        		
	        		for(Config r : ds)
	        		{
	        			stt += 1;	        			
	        			%>
	        			<tr>
			        		<td style="text-align: center; background-color: #dedede"><%= stt %></td>			        		
			        		<td><%=r.getname()%>
			        		<input type='hidden' name='code:<%= stt %>' value='<%= r.getId() %>' >
			        		</td>
			        		<%
			        		if(r.getcode().compareTo("systemconfig")==0)
			        		{
			        			String semiauto = r.getvaluechar().compareTo("semi-auto")==0 ? "selected" : "";
				        		String auto = r.getvaluechar().compareTo("auto")==0 ? "selected" : "";
				        		String fullauto = r.getvaluechar().compareTo("fullauto")==0 ? "selected" : "";
			        		%>
			        		<td>
			        		<select id='systemconfig' name='systemconfig' size="1" disabled="disabled">
								<option value="semi-auto" <%=semiauto%>>Bán tự động</option>
								<option value="auto" <%=auto%>>Tự động phân loại</option>
								<!-- <option value="fullauto" <%=fullauto%>>Tự động hoàn toàn</option> -->
							</select>
			        		</td>
			        		<td>
			        			<input type='checkbox' id='option_add' name="option_add" onclick="makeDisableOrEnable('systemconfig')">
			        		</td>
			        		<%
			        		}
			        		if(r.getcode().compareTo("autokeyword")==0)
			        		{
				        		String dung = r.getvaluebool()==true ? "selected" : "";
				        		String sai = r.getvaluebool()== false ? "selected" : "";
				        	%>
				        	<td>
				        		<select id="autokeyword" name="autokeyword" size="1" disabled="disabled">
										<option value=true <%=dung%>>Tự động</option>
										<option value=false  <%=sai%>>Bán tự động</option>
								</select>
							</td>
							<td>
			        			<input type='checkbox' id='option_add' name="option_add" onclick="makeDisableOrEnable('autokeyword')">
			        		</td>							
			        		<%
			        		}
			        		if(r.getcode().compareTo("send_sms_expert")==0)
			        		{
				        		String dung = r.getvaluebool()==true ? "selected" : "";
				        		String sai = r.getvaluebool()== false ? "selected" : "";
				        	%>
				        	<td>
				        		<select id="send_sms_expert" name="send_sms_expert" size="1" disabled="disabled">
										<option value=true <%=dung%>>Gửi</option>
										<option value=false  <%=sai%>>Không gửi</option>
								</select>
							</td>
							<td>
			        			<input type='checkbox' id='option_add' name="option_add" onclick="makeDisableOrEnable('send_sms_expert')">
			        		</td>
							<%
			        		}
			        		if(r.getcode().compareTo("send_notification_farmer")==0)
			        		{
				        		String dung = r.getvaluebool()==true ? "selected" : "";
				        		String sai = r.getvaluebool()== false ? "selected" : "";
				        	%>
				        	<td>
				        		<select id="send_notification_farmer" name="send_notification_farmer" size="1" disabled="disabled">
										<option value=true <%=dung%>>Gửi</option>
										<option value=false  <%=sai%>>Không gửi</option>
								</select>
							</td>
							<td>
			        			<input type='checkbox' id='option_add' name="option_add" onclick="makeDisableOrEnable('send_notification_farmer')">
			        		</td>
							<%
			        		}
			        		
			        		if(r.getcode().compareTo("auto_send_farmer")==0)
			        		{
				        		String dung = r.getvaluebool()==true ? "selected" : "";
				        		String sai = r.getvaluebool()== false ? "selected" : "";
				        	%>
				        	<td>
				        		<select id="auto_send_farmer" name="auto_send_farmer" size="1" disabled="disabled">
										<option value=true <%=dung%>>Gửi</option>
										<option value=false  <%=sai%>>Không gửi</option>
								</select>
							</td>
							<td>
			        			<input type='checkbox' id='option_add' name="option_add" onclick="makeDisableOrEnable('auto_send_farmer')">
			        		</td>
							<%
			        		}
			        		if(r.getcode().compareTo("autoretrain")==0)
			        		{
			        			String dung = r.getvaluebool()==true ? "selected" : "";
				        		String sai = r.getvaluebool()== false ? "selected" : "";
			        		%>	
			        		<td>			        			
			        			<select id="number_sms_need" name="number_sms_need" size="1" disabled="disabled">
										<option value=true <%=dung%>>Huấn luyện lại</option>
										<option value=false  <%=sai%>>Không huấn luyện lại</option>
								</select>			        			
			        		</td>
			        		<td>
			        			<input type='checkbox' id='option_add' name="option_add" onclick="makeDisableOrEnable('number_sms_need')">
			        		</td>
			        		<%	
			        		}
			        		if(r.getcode().compareTo("number_sms_retrain")==0)
			        		{
			        		%>	
			        		<td>
			        			<input type="text" id='number_sms_retrain' name='number_sms_retrain' value='<%=r.getvalueint()%>' size="10" disabled="disabled">
			        			Số nguyên >=1 và <=1000000
			        		</td>
			        		<td>
			        			<input type='checkbox' id='option_add' name="option_add" onclick="makeDisableOrEnable('number_sms_retrain')">
			        		</td>
			        		<%
			        		}
			        		if(r.getcode().compareTo("sleep_time_get_sms")==0)
			        		{
			        		%>	
			        		<td>
			        			<input type="text" id='sleep_time_get_sms' name='sleep_time_get_sms' value='<%=r.getvalueint()%>' size="10" disabled="disabled">
			        			Nhập số giây >=1 và <=32000
			        		</td>
			        		<td>
			        			<input type='checkbox' id='option_add' name="option_add" onclick="makeDisableOrEnable('sleep_time_get_sms')">
			        		</td>
			        		<%
			        		}
			        		if(r.getcode().compareTo("sleep_time_retrain")==0)
			        		{
			        		%>	
			        		<td>
			        			<input type="text" id='sleep_time_retrain' name='sleep_time_retrain' value='<%=r.getvalueint()%>' size="10" disabled="disabled">
			        			Nhập số giờ >=1 và <=200
			        		</td>
			        		<td>
			        			<input type='checkbox' id='option_add' name="option_add" onclick="makeDisableOrEnable('sleep_time_retrain')">
			        		</td>
			        		<%
			        		}
			        		if(r.getcode().compareTo("sleep_time_classify")==0)
			        		{
			        		%>	
			        		<td>
			        			<input type="text" id='sleep_time_classify' name='sleep_time_classify' value='<%=r.getvalueint()%>' size="10" disabled="disabled">
			        			Nhập số giây >=1 và <=32000
			        		</td>
			        		<td>
			        			<input type='checkbox' id='option_add' name="option_add" onclick="makeDisableOrEnable('sleep_time_classify')">
			        		</td>
			        		<%
			        		}			        		
			        		%>		        	
			        	</tr>		        	
			        	<%
	        		}
	        		%>
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
		        		<input type='submit' value='Cập nhật' name='adminUpdateConfig' onclick="return btnRegistry_onclick()">		        		
		        	</div>
				</div> <!-- end class="box-center-left" -->
				</div> <!-- end class="box-center-right" -->
				<div style = "clear:both;"> </div>
	    		</form> 
	    		</div>
	    		<br/>  
<%
		}
		catch (Exception e)
		{			
		}	
	}
	else{
		response.sendRedirect("Admin.jsp");
	}
%>

<jsp:include page="footer.jsp"/>
