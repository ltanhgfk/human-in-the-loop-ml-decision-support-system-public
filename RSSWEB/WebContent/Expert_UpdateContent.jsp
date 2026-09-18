<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="dao.*" %>
<%@ page import="global.*" %>
<%@ page import="model.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.sql.ResultSet" %>
    
<title>Cập nhật tri thức</title>
<head>
    <script language="javascript" type="text/javascript">	    
		
		function makeDisableOrEnable(id1, id2, id3, id4){
		    var x=document.getElementById(id1);
		    var y=document.getElementById(id2);
		    var z=document.getElementById(id3);
		    var t=document.getElementById(id4);
		    if (x.disabled == true && y.disabled == true && z.disabled == true && t.disabled == true)
		    {
		    	x.disabled = false;
		    	y.disabled = false;
		    	z.disabled = false;
		    	t.disabled = false;
		    }		    	
		    else{
		    	x.disabled = true;
		    	y.disabled = true;
		    	z.disabled = true;
		    	t.disabled = true;
		    } 	    	
		}
		
    </script>
</head>
<jsp:include page="header.jsp"/>
<jsp:include page="menu.jsp"/>
<div style="text-align: center;">
	<h1>DANH SÁCH</h1>
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
%>
<div>				
				<form name='frmMajor' action='ContentController' method='POST'>				
					<div id='LietKe' text-align: center;">
					<div class="box-center-right">
					<div class="box-center-left">
					<table class="gridtable-xemkhht">
	        		<tr>
		        		<th>STT</th>
		        		<th>Tên</th>
		        		<!--<th>Lớp</th>-->
		        		<th>Nội dung</th>
		        		<th>Từ khóa</th>
		        		<!-- <th>Lần cập nhật cuối</th> -->
		        		<th><img src="images/system/checkbox.gif" alt="Check"></th>
		        	</tr>
		        	<%
					int stt = 0;		        	
	        		Content mh = new Content();
	        		ArrayList<Content> ds = new ArrayList<Content>();
	        		ChuyenGia cg = new ChuyenGia();
	        		cg = cg.getExpertByUserId(username);
	        		ds = mh.getContentByExpertId(cg.getid());
	        		if (ds.size() == 0)
	        		{
	        		%>	<br/><span style='color:red; font-weight:bold;'>Chưa có dữ liệu!</span><br/><br/><%
	        		}
	        		for(Content r : ds){
	        			stt += 1;
	        			//tong_so_tc += r.getSo_tc();
	        			%>
	        			<tr>
			        		<td style="text-align: center; background-color: #dedede"><%= stt %></td>
			        		<td style="text-align: center;">
			        		<input type='text' size="15" id='name<%= stt %>' name='name<%= stt %>' value='<%= r.getname()%>' disabled="disabled">
			        		<input type='hidden' name='code:<%= stt %>' value='<%= r.getid() %>' >
			        		</td>
			        		<!-- <td>
			        		<input type='text' size="1" id='code<%= stt %>' name='code<%= stt %>' value='<%= r.getcode()%>' disabled="disabled">
			        		</td> -->
			        		<td><textarea cols="40" rows="3" id='content<%= stt %>' name='content<%= stt %>' disabled="disabled"><%=r.getcontent()%></textarea>
			        		</td>
			        		<td>
			        			<textarea cols="30" rows="3" id='keywords<%= stt %>' name='keywords<%= stt %>' disabled="disabled"><%=r.getkeywords()%></textarea>			        			
			        		</td>
			        		<!-- <td><%= r.getmodifieddate() %></td> -->
			        		<td>
			        			<input type='checkbox' id='option_<%= stt %>' name="option" value='<%= stt %>' onclick="makeDisableOrEnable('code<%=stt%>','name<%= stt %>','content<%= stt %>','keywords<%= stt %>')">
			        			<input type='hidden' name='id_khht:<%= stt %>' value='<%= r.getid() %>'>
			        		</td>			        		
			        	</tr>		        	
			        	<%
	        		}
	        		%>
	        			<tr>
			        		<td style="text-align: center; background-color: #dedede"><%= stt+1 %></td>
			        		<td style="text-align: center;">
			        		<input type='text' id='name' name='name' disabled="disabled" size="15">			        		
			        		</td>
			        		<!-- <td>
			        		<input type='text' size="1" id='code' name='code' disabled="disabled">
			        		</td> -->
			        		<td ><textarea cols="40" rows="3" id='content' name='content' disabled="disabled"></textarea>
			        		</td>
			        		<td>
			        		<textarea cols="30" rows="3" id='keywords' name='keywords' disabled="disabled"></textarea>			        		
			        		</td>
			        		<!-- <td></td> -->
			        		<td>
			        			<input type='checkbox' id='option_add' name="option_add" onclick="makeDisableOrEnable('code','name','content','keywords')">
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
						    Tổng số: <b><%= stt %></b>
		        	</div>
				</div> <!-- end class="box-center-left" -->
				</div> <!-- end class="box-center-right"  -->
				<div style = "clear:both;"> </div>
				</div>

				<div class="box-center-right">
				<div class="box-center-left">
					<div class="button-link" style="padding-top: 5px;">
		        		<!-- <input type='submit' value='Thêm chuyên ngành' name='addmajor'> -->
		        		<input type='submit' value='Cập nhật' name='UpdateKindOfRice'>
		        		<input type='submit' value='Xóa' name='DeleteContent' onClick="return confirm('Bạn có chắc chắn muốn xóa ?');">
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
		response.sendRedirect("index.jsp");
	}
%>

<jsp:include page="footer.jsp"/>
