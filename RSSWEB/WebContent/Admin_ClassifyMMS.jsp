<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="dao.*" %>
<%@ page import="global.*" %>
<%@ page import="model.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.sql.ResultSet" %>
    
<title>Danh sách tin nhắn</title>
<head>

<style media="screen" type="text/css">
 .pop-up {
  height : 0px;
  width : 0px;
  top : 0px;
  left : 0px;
  right : 800px;
  float : right;  
  position:fixed; /* positive, relative, none */
  z-index:0;
}

</style>

<script type="text/javascript">
    function ShowImage(src)
    {
        var img = document.getElementById('popupImage');
        img.src = src;
        img.style.display = "block";
    }
    function HideImage()
    {
        document.getElementById('popupImage').style.display = "none";
    }
</script>
    <script language="javascript" type="text/javascript">
		function makeDisableOrEnable(id1, id2){
		    var x=document.getElementById(id1);
		    var y=document.getElementById(id2);
		    /*var z=document.getElementById(id3);*/
		    if (x.disabled == true && y.disabled == true)// && z.disabled == true
		    {
		    	x.disabled = false;
		    	y.disabled = false;
		    	//z.disabled = false;
		    }		    	
		    else{
		    	x.disabled = true;
		    	y.disabled = true;
		    	//z.disabled = true;
		    } 	    	
		}		
    </script>
</head>

<jsp:include page="header.jsp"/>
<jsp:include page="menu_admin.jsp"/>
<div style="text-align: center;">
	<h1>DANH SÁCH TIN NHẮN</h1>
</div>

<BR/>
<%
Object objdn = session.getAttribute("admin");

	if (objdn != null)
	{		
		try
		{
%>				
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
		        
				<form name='frmMMS' action='MMSController' method='POST'>
				<div id='LietKe' text-align: center;">
					<div class="box-center-right">
					<div class="box-center-left">
					<div class="pop-up">
					<img id="popupImage" src="" style="display: none" width="300" height="300"/> 
					</div> 
					<table class="gridtable-xemkhht">
	        		
		        	<%
					int stt = 0;	
		        	Config cf = new Config();
	        		cf = cf.getConfigBycode("systemconfig");
	        		MMS ms = new MMS();
	        		ArrayList<MMS> ds = new ArrayList<MMS>();
	        		
	        		if( cf.getvaluechar().compareTo("semi-auto") == 0 )
	        		{
	        			ds = ms.getMMSByClassifyHuman(false, false, false);
	        		}
	        		/*else if( cf.getvaluechar().compareTo("auto") == 0 )
	        		{	        			
	        			ds = ms.getMMSByClassifyMachine(false, false, false);	        			
	        		}*/
	        		
	        		if (ds.size() == 0)
	        		{
	        		%>	
	        			<br/><span style='color:red; font-weight:bold;'>Chưa có tin nhắn mới hoặc hệ thống được cấu hình tự động!</span><br/><br/>
	        		<%
	        		}
	        		else
	        		{
			        		%>
			        		
			        		<tr>
			        		<th>STT</th>
			        		<th>Ảnh</th>
			        		<th>Nội dung</th>
			        		<th>Ngày nhận</th>
			        		<th>Phân loại tự động</th>
			        		<th>Phân loại bán tự động</th>
			        		<th><img src="images/system/checkbox.gif" alt="Check"></th>
			        		</tr>
			        		<%
			        		for(MMS r : ds){
			        			stt += 1;			        			
			        			%>
			        			<tr>
					        		<td style="text-align: center; background-color: #dedede"><%= stt %></td>
					        		<td style="text-align: center;">					        		
					        		
					        		<%
											        		if( r.getimage() != null && r.getimage().compareTo("null")!=0 && r.getimage().compareTo("")!=0)
											        		{											        			
											        		%>  
																<a href="" onmouseover="ShowImage('images/rices/<%=r.getimage()%>')" onmouseout="HideImage()">
																	<img src="images/rices/<%=r.getimage()%>" border="0" width="100" height="100" >
																</a>																
											        		<%
											        		}
											        		else
											        			out.print("Không có");
											        		%>			        		
					        		</td>
					        		<td class="tenmon">
					        		<textarea cols="50" rows="3" id='msg<%= stt %>' name='msg<%= stt %>' disabled="disabled"><%=r.getmsg() %></textarea>
					        		</td>
					        		<td class="tenmon">
					        		<%=r.getreceivedDate()%>
					        		</td>
					        		<td>
					        		<%
					        		if(r.getmajoridByMachine() >= 1)
					        		{
					        			ChuyenMon cma = new ChuyenMon();
					        			cma = cma.getMajorById(r.getmajoridByMachine());
					        			out.print(cma.getName());
					        			//out.print("Đã phân loại");
					        		}
					        		else out.print("chưa phân loại");					        		
					        		%>					        		
					        		</td>
					        		<td>
											<select id="classify<%= stt %>" name="classify:<%= stt %>" size="1" disabled="disabled" >
												<option value="-1">Chưa phân loại</option>										
												<%
												ChuyenMon cm = new ChuyenMon();
												String selected="";
									    		ArrayList<ChuyenMon> dscm = new ArrayList<ChuyenMon>();
									    		dscm = cm.getAllMajor();
									    		for(ChuyenMon row : dscm)
									    		{
									    			if(r.getmajoridByHuman()==row.getId())
									    			{
									    				selected="selected";	
									    			}
									    			else selected="";	
									    			%>
									    			<option value="<%= row.getId() %>" <%=selected %>><%= row.getName() %></option>
									    			<%							    			
									    		}
									    		%>
									    	</select>
									</td>			        					        		     		
					        		<td>
					        			<input type='checkbox' id='option_<%= stt %>' name="option" value='<%= stt %>' onclick="makeDisableOrEnable('classify<%= stt %>','msg<%= stt %>')">
					        			<input type='hidden' name='id_khht:<%= stt %>' value='<%= r.getid() %>'>
					        		</td>
					        	</tr>		        	
					        	<%
			        		}
	        		}
	        		%>
	        		</table>
	        		<%
	        		if(stt>=1)
	        		{
	        		%>
	        		<br/>	        		
	        		<div style='text-align: left;'>							
						    Tổng số tin nhắn: <b><%= stt %></b>
		        	</div>
				</div> <!-- end class="box-center-left" -->
				</div> <!-- end class="box-center-right"  -->
				<div style = "clear:both;"> </div>
				</div>

				<div class="box-center-right">
				<div class="box-center-left">
					<div class="button-link" style="padding-top: 5px;">
		        		<!-- <input type='submit' value='Thêm chuyên ngành' name='addmajor'> -->
		        		<input type='submit' value='Cập nhật' name='adminClassifyMMS'>
		        		<input type='submit' value='Xóa' name='adminDeleteMMS' onClick="return confirm('Bạn có chắc chắn muốn xóa ?');">
		        	</div>
		        <%
	        		}
				%>
				</div> <!-- end class="box-center-left" -->
				</div> <!-- end class="box-center-right"  -->
				
				<div style = "clear:both;"> </div>
	    		</form> 
	    		<br/>  
		        <%		
		}
		catch (Exception e){			
		}	
	}
	else{
		response.sendRedirect("Admin.jsp");
	}
%>

<jsp:include page="footer.jsp"/>
