<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="dao.*" %>
<%@ page import="global.*" %>
<%@ page import="model.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.sql.ResultSet" %>
    
<title>Danh sách Gateway</title>
<head>
    <script language="javascript" type="text/javascript">
	    
		function makeDisableOrEnable(id1, id2, id3, id4, id5){
		    var x=document.getElementById(id1);
		    var y=document.getElementById(id2);
		    var z=document.getElementById(id3);
		    var t=document.getElementById(id4);
		    var v=document.getElementById(id5);
		    if (x.disabled == true && y.disabled == true && z.disabled == true && t.disabled == true && v.disabled == true)
		    {
		    	x.disabled = false;
		    	y.disabled = false;
		    	z.disabled = false;
		    	t.disabled = false;
		    	v.disabled = false;
		    }		    	
		    else{
		    	x.disabled = true;
		    	y.disabled = true;
		    	z.disabled = true;
		    	t.disabled = true;
		    	v.disabled = true;
		    } 	    	
		}
		
    </script>
</head>
<jsp:include page="header.jsp"/>
<jsp:include page="menu_admin.jsp"/>
<div style="text-align: center;">
	<h1>DANH SÁCH GATEWAY</h1>
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
				<form name='frmGateway' action='GatewayController' method='POST'>				
					<div id='LietKe' text-align: center;">
					<div class="box-center-right">
					<div class="box-center-left">
					<table class="gridtable-xemkhht">
	        		<tr>
		        		<th>STT</th>
		        		<th>Nhà mạng</th>
		        		<th>Gateway</th>
		        		<th>Port</th>
		        		<th>Mmsc</th>
		        		<th>Mmsc Number</th>
		        		<th><img src="images/system/checkbox.gif" alt="Check"></th>
		        	</tr>
		        	<%
					int stt = 0;		        	
	        		Gateway mh = new Gateway();
	        		ArrayList<Gateway> ds = new ArrayList<Gateway>();
	        		ds = mh.getAllGateway();
	        		if (ds.size() == 0)
	        		{
	        		%>	<br/><span style='color:red; font-weight:bold;'>Chưa có dữ liệu về Gateway!</span><br/><br/><%
	        		}
	        		for(Gateway r : ds){
	        			stt += 1;	        			
	        			%>
	        			<tr>
			        		<td style="text-align: center; background-color: #dedede"><%= stt %></td>
			        		<td style="text-align: center;">
			        		<input type='text' size="8" id='operator<%= stt %>' name='operator<%= stt %>' value='<%= r.getoperator()%>' disabled="disabled">
			        		<input type='hidden' name='code:<%= stt %>' value='<%= r.getId() %>' >
			        		</td>
			        		<td ><input type='text' size="13" id='gatewayip<%= stt %>' name='gatewayip<%= stt %>' value='<%=r.getgatewayip()%>' disabled="disabled"></td>
			        		<td ><input type='text' size="5" id='port<%= stt %>' name='port<%= stt %>' value='<%=r.getport()%>' disabled="disabled"></td>
			        		<td ><input type='text' size="35" id='mmsc<%= stt %>' name='mmsc<%= stt %>' value='<%=r.getmmsc()%>' disabled="disabled"></td>
			        		<td ><input type='text' size="8" id='mmscnumber<%= stt %>' name='mmscnumber<%= stt %>' value='<%=r.getmmscnumber()%>' disabled="disabled"></td>
			        		<td>
			        			<input type='checkbox' id='option_<%= stt %>' name="option" value='<%= stt %>' onclick="makeDisableOrEnable('operator<%= stt %>','gatewayip<%= stt %>','port<%= stt %>','mmsc<%= stt %>','mmscnumber<%= stt %>')">
			        			<input type='hidden' name='id_khht:<%= stt %>' value='<%= r.getId() %>'>
			        		</td>
			        	</tr>		        	
			        	<%
	        		}
	        		%>
	        			<tr>
			        		<td style="text-align: center; background-color: #dedede"><%= stt+1 %></td>
			        		<td style="text-align: center;">
			        		<input type='text' size="8" id='operator' name='operator' disabled="disabled">
			        		</td>
			        		<td ><input type='text' size="13" id='gatewayip' name='gatewayip' disabled="disabled"></td>
			        		<td ><input type='text' size="5" id='port' name='port' disabled="disabled"></td>
			        		<td ><input type='text' size="35" id='mmsc' name='mmsc' disabled="disabled"></td>
			        		<td ><input type='text' size="8" id='mmscnumber' name='mmscnumber' disabled="disabled"></td>
			        		<td>
			        			<input type='checkbox' id='option_add' name="option_add" onclick="makeDisableOrEnable('operator','gatewayip','port','mmsc','mmscnumber')">
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
	        		<br/>	        		   
	        		<div style='text-align: left;'>							
						    Tổng số Gateway: <b><%= stt %></b>
		        	</div>
				</div> <!-- end class="box-center-left" -->
				</div> <!-- end class="box-center-right"  -->
				<div style = "clear:both;"> </div>
				</div>

				<div class="box-center-right">
				<div class="box-center-left">
					<div class="button-link" style="padding-top: 5px;">
		        		<!-- <input type='submit' value='Thêm chuyên ngành' name='addmajor'> -->
		        		<input type='submit' value='Cập nhật' name='adminUpdateGateway'>
		        		<input type='submit' value='Xóa' name='adminDeleteGateway' onClick="return confirm('Bạn có chắc chắn muốn xóa ?');">
		        	</div>
				</div> <!-- end class="box-center-left" -->
				</div> <!-- end class="box-center-right"  -->
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
