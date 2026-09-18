<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="dao.*" %>
<%@ page import="global.*" %>
<%@ page import="model.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.sql.ResultSet" %>
    
<title>Danh sách chuyên ngành</title>
<head>
    <script language="javascript" type="text/javascript">	    
		
		function makeDisableOrEnable(id1, id2, id3){
		    var x=document.getElementById(id1);
		    var y=document.getElementById(id2);
		    var z=document.getElementById(id3);
		    if (x.disabled == true && y.disabled == true && z.disabled == true)
		    {
		    	x.disabled = false;
		    	y.disabled = false;
		    	z.disabled = false;
		    }		    	
		    else{
		    	x.disabled = true;
		    	y.disabled = true;
		    	z.disabled = true;
		    } 	    	
		}
		
    </script>
</head>
<jsp:include page="header.jsp"/>
<jsp:include page="menu_admin.jsp"/>
<div style="text-align: center;">
	<h1>DANH SÁCH CHUYÊN NGÀNH</h1>
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
				<form name='frmMajor' action='ChuyenMonController' method='POST'>				
					<div id='LietKe' text-align: center;">
					<div class="box-center-right">
					<div class="box-center-left">
					<table class="gridtable-xemkhht">
	        		<tr>
		        		<th>STT</th>
		        		<th>Thuộc lớp</th>
		        		<th>Tên ngành</th>
		        		<th>Từ khóa</th>
		        		<!-- <th>Cập nhật</th> -->		        		
		        		<th><img src="images/system/checkbox.gif" alt="Check"></th>
		        	</tr>
		        	<%
					int stt = 0;
		        	//int tong_so_tc = 0;
	        		ChuyenMon mh = new ChuyenMon();
	        		ArrayList<ChuyenMon> ds = new ArrayList<ChuyenMon>();
	        		ds = mh.getAllMajor();
	        		if (ds.size() == 0)
	        		{
	        		%>	<br/><span style='color:red; font-weight:bold;'>Chưa có dữ liệu về chuyên ngành!</span><br/><br/><%
	        		}
	        		for(ChuyenMon r : ds){
	        			stt += 1;
	        			//tong_so_tc += r.getSo_tc();
	        			%>
	        			<tr>
			        		<td style="text-align: center; background-color: #dedede"><%= stt %></td>
			        		<td style="text-align: center;">
			        		<input type='text' size="1" id='majorcode<%= stt %>' name='majorcode<%= stt %>' value='<%= r.getCode()%>' disabled="disabled">
			        		<input type='hidden' name='code:<%= stt %>' value='<%= r.getId() %>' >
			        		</td>
			        		<td><input type='text' id='majorname<%= stt %>' name='majorname<%= stt %>' value='<%=r.getName()%>' disabled="disabled"></td>
			        		<td>
			        			<textarea cols="50" rows="3" id='majornote<%= stt %>' name='majornote<%= stt %>' disabled="disabled"><%=r.getNote()%></textarea> 
			        			<!-- <input type="text" id='majornote<%= stt %>' name='majornote<%= stt %>' value='<%=r.getNote()%>' disabled="disabled" size="50"> -->
			        		</td>
			        		<!-- <td style="text-align: center;"><a href="dpv_capnhatchuyenmon.jsp">cập nhật</a> </td> -->			        		
			        		<td>
			        			<input type='checkbox' id='option_<%= stt %>' name="option" value='<%= stt %>' onclick="makeDisableOrEnable('majorcode<%= stt %>','majorname<%= stt %>','majornote<%= stt %>')">
			        			<input type='hidden' name='id_khht:<%= stt %>' value='<%= r.getId() %>'>
			        		</td>
			        	</tr>		        	
			        	<%
	        		}
	        		%>
	        			<tr>
			        		<td style="text-align: center; background-color: #dedede"><%= stt+1 %></td>
			        		<td style="text-align: center;">
			        		<input type='text' id='majorcode' name='majorcode' disabled="disabled" size="1">			        		
			        		</td>
			        		<td ><input type='text' id='majorname' name='majorname' disabled="disabled"></td>
			        		<td>
			        		<!-- <input type="text" id='majornote' name='majornote' disabled="disabled" size="50"> -->
			        		<textarea cols="50" rows="3" id='majornote' name='majornote' disabled="disabled"></textarea>
			        		
			        		</td>
			        		<!-- <td style="text-align: center;"><a href="dpv_capnhatchuyenmon.jsp">cập nhật</a> </td>	 -->		        		
			        		<td>
			        			<input type='checkbox' id='option_add' name="option_add" onclick="makeDisableOrEnable('majorcode','majorname','majornote')">
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
						    Tổng số chuyên ngành: <b><%= stt %></b>
		        	</div>
				</div> <!-- end class="box-center-left" -->
				</div> <!-- end class="box-center-right"  -->
				<div style = "clear:both;"> </div>
				</div>

				<div class="box-center-right">
				<div class="box-center-left">
					<div class="button-link" style="padding-top: 5px;">
		        		<!-- <input type='submit' value='Thêm chuyên ngành' name='addmajor'> -->
		        		<input type='submit' value='Cập nhật' name='adminUpdateMajor'>
		        		<input type='submit' value='Xóa' name='adminDeleteMajor' onClick="return confirm('Bạn có chắc chắn muốn xóa ?');">
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
