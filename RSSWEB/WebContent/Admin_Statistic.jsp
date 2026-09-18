<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="dao.*" %>
<%@ page import="global.*" %>
<%@ page import="model.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.sql.ResultSet" %>;
<%@ page import="java.io.*" %>
    
<title>Danh sách tin nhắn mới</title>
<head>
    <script language="javascript" type="text/javascript">
	    var xmlHttp;  
	    var xmlHttp;    
	    function CallAjax(url){
		      if (typeof XMLHttpRequest != "undefined"){
		      	xmlHttp= new XMLHttpRequest();
		      }
		      else if (window.ActiveXObject){
		      	xmlHttp= new ActiveXObject("Microsoft.XMLHTTP");
		      }  
		      if (xmlHttp==null){
		      	alert("Browser does not support XMLHTTP Request");
		      	return;
		      }
		      url += "?expertid=" + getValue('expertid') + "&majorid=" + getValue('majorid') + "&month=" + getValue('month') + "&year=" + getValue('year');
		      xmlHttp.onreadystatechange = stateChange;
		      xmlHttp.open("GET", url, true);
		      xmlHttp.send(null);
		}
		
		function stateChange(){   
		      if (xmlHttp.readyState==4 || xmlHttp.readyState=="complete"){   
		    	  document.getElementById("LietKe").innerHTML=xmlHttp.responseText;
		      }   
		}
		
		function makeDisableOrEnable(id1, id2){
		    var x=document.getElementById(id1);
		    var y=document.getElementById(id2);
		    if (x.disabled == true && y.disabled == true){
		    	x.disabled = false;
		    	y.disabled = false;
		    }		    	
		    else{
		    	x.disabled = true;
		    	y.disabled = true;
		    } 	    	
		}
		function getValue(id){
		    var x = document.getElementById(id);
		    return x.value; 	    	
		}
    </script>
</head>

<jsp:include page="header.jsp"/>
<jsp:include page="menu_admin.jsp"/>
<div style="text-align: center;">	
</div>
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
				
				DBManager db = new DBManager();				
				String pageindex = "";
				String pagee = request.getParameter("pagee");
				int p = 0;
				int pag = 0;
				if (pagee != null) {
					p = Integer.parseInt(pagee);
					pag = Integer.parseInt(pagee);
				}
				int MAXPAGE = 20;				
				
				MMS mh_m = new MMS();
				ArrayList<MMS> ds_m = new ArrayList<MMS>();				
				ds_m = mh_m.getMMSByAnswerNoLimit(true, -1, -1,-1,-1);
				int count = 0;
				count = ds_m.size();
				
				%>
		        </div>		        
				<%
					MMS mms = new MMS();
					ArrayList<MMS> ds_sms = mms.getAllMMS();
					int totalsms = ds_sms.size();
					
					ArrayList<MMS> ds_ans = new ArrayList<MMS>();				
					ds_ans = mh_m.getMMSByAnswer(true);
					int countans = ds_ans.size();
				%>
	        	<div style='text-align: left;'>							
						   Tổng số tin nhắn đã trả lời: <b><%=countans%></b>
		        </div>
		        <div style='text-align: left;'>		        					
						   Tổng số tin nhắn hệ thống nhận được: <b><%=totalsms%></b>
		        </div>
		         <div style='text-align: left;'>							
						   <!-- Độ chính xác của mô hình phân loại:<b></b> -->
		        </div>				
				<div style = "clear:both;"> </div>
				<div style = "clear:both;"> </div>
				<br>				
				
				<div class="box-center-right">
				<div class="box-center-left">
		        <div id="chonloc" >
		        <div><h1>THỐNG KÊ TIN NHẮN</h1></div>
				<br>
				<table class="gridtable-blank">
					<tr>						
						<td><select id="expertid" name="expertid" size="1">
								<option value="-1">[Chuyên gia]</option>
								<option value="-1">Tất cả</option>
								<%
													ChuyenGia cg = new ChuyenGia();
										    		ArrayList<ChuyenGia> dscg = new ArrayList<ChuyenGia>();
										    		dscg = cg.getAllExpert();	        		
										    		for(ChuyenGia r : dscg)
										    		{
								%>
													<option value="<%= r.getid()%>"><%= r.getname() %></option>
								<%
										    		}
								%>
						</select></td>						
						<td ><select id="majorid" name="majorid" size="1">
								<option value="-1">[Chuyên ngành]</option>
								<option value="-1">Tất cả</option>
								<%
								ChuyenMon cma = new ChuyenMon();
								ArrayList<ChuyenMon> dscm = new ArrayList<ChuyenMon>();
								dscm = cma.getAllMajor();
								for(ChuyenMon r : dscm)
					    		{
			%>
								<option value="<%= r.getId()%>"><%= r.getName() %></option>
			<%
					    		}
			%>							
						</select></td>						
						<td ><select id="month" name="month" size="1">
								<option value="-1">[Tháng nhận]</option>
								<option value="-1">Tất cả</option>
								<%								
								for(int i=1;i<=12;i++)
					    		{
			%>
								<option value="<%=i %>"><%=i %></option>
			<%
					    		}
			%>							
						</select></td>						
						<td ><select id="year" name="year" size="1">
								<option value="-1">[Năm nhận]</option>
								<option value="-1">Tất cả</option>
								<%
								int year=2012;
								for(int j=1;j<=20;j++)
					    		{
			%>
								<option value="<%=year+j %>"><%=year+j %></option>
			<%
					    		}
			%>							
						</select></td>
						<td>							
								<input class="nutsearch blue small" type="submit" value="Liệt kê" name="submit" onclick="CallAjax('Ajax_Statistic.jsp')">							
						</td>
					</tr>
				</table>
				</div>    		
				</div> <!-- end class="box-center-left" -->
				</div> <!-- end class="box-center-right"  -->
				
				<div style = "clear:both; height:10px;"> </div>
				<!--<div><h1>DANH SÁCH TIN NHẮN</h1></div>-->
				<div style = "clear:both; height:10px;"> </div>				
				<form name='frmMMS' action='MMSController' method='POST'>
				<input type="hidden" name="pagee" value="<%= pag %>">
				<%	
					pageindex = db.createPage(count,"Admin_Statistic.jsp?pagee=",MAXPAGE,pag);
				%>
				<div id='LietKe' text-align: center;">
					<div class="box-center-right">
					<div class="box-center-left">
					<div class="pop-up">
					<img id="popupImage" src="" style="display: none" width="300" height="300"/> 
					</div> 
					<table class="gridtable-xemkhht">
	        		
		        	<%					    	
		        	/*
		        	Config cf = new Config();
	        		cf = cf.getConfigBycode("systemconfig");
	        		MMS ms = new MMS();
	        		ArrayList<MMS> ds = new ArrayList<MMS>();
	        		
	        		if( cf.getvaluechar().compareTo("semi-auto") == 0 )
	        		{
	        			ds = ms.getMMSByAnswer(true, true, MAXPAGE, p, -1, -1);
	        		}
	        		else if( cf.getvaluechar().compareTo("auto") == 0 )
	        		{	        			
	        			ds = ms.getMMSByAnswer(true, true, MAXPAGE, p, -1, -1);     			
	        		}
	        		*/
	        		
	        		int stt = 0;
	        		MMS ms = new MMS();
	        		ArrayList<MMS> ds = new ArrayList<MMS>();
	        		//co limit de phan trang
	        		ds = ms.getMMSByAnswer(false, MAXPAGE, p, -1, -1);
	        		
	        		if (ds.size() == 0)	        		
	        		{
	        		%>	
	        			<br/><span style='color:red; font-weight:bold;'>Hiện không có tin nhắn chưa trả lời!</span><br/><br/>
	        		<%
	        		}
	        		else
	        		{
			        		%>
			        		
			        		<tr>
			        		<th>STT</th>
			        		<th>Ảnh</th>
			        		<th>Câu hỏi</th>
			        		<th>Thời gian nhận</th>
			        		<th>Trả lời</th>			        		
			        		<th>Thời gian trả lời</th>
			        		<!-- 
			        		<th>Máy phân loại</th>
			        		<th>Người phân loại</th>
			        		 -->
			        		</tr>
			        		<%
			        		for(MMS r : ds){
			        			stt += 1;			        			
			        			%>
			        			<tr>
					        		<td style="text-align: center; background-color: #dedede"><%= stt %></td>
					        		<td style="text-align: center;">					        		
					        		
					        		<%
									if( r.getimage().contains(".jpg") || r.getimage().contains(".jpeg") || r.getimage().contains(".png") || r.getimage().contains(".gif") )
									{											        			
									%>  
										<img src="images/rices/<%=r.getimage()%>" border="0" width="100" height="100" >													
									<%
									}
									else
									out.print("Không có");
									%>
					        		</td>
					        		<td class="tenmon">
					        		<%=r.getmsg() %>
					        		</td>
					        		<td>
					        		<%=r.getreceivedDate()%>
					        		</td>
					        		<td class="tenmon">
					        		<%=r.getreplymsg()%>
					        		</td>
					        		<td>
					        		<%=r.getansweredDate() %>
					        		</td>
					        		<!-- 
					        		<td>
					        		<%/*
					        		ChuyenMon cm = new ChuyenMon();
					        		cm = cm.getMajorById(r.getmajoridByMachine());
					        		out.print(cm.getName());*/
					        		%>	
					        		</td>
					        		<td>									
					        		<%/*					        		
					        		cm = cm.getMajorById(r.getmajoridByHuman());
					        		out.print(cm.getName());*/
					        		%>
									</td>
									 -->
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
	        		<!-- <div style='text-align: left;'> -->							
						   <!-- Số tin nhắn theo tiêu chí tìm kiếm: <b><%=stt%></b>-->
		        	<!-- </div>-->
	        		<br/>	        		
	        		<div class="paging" style='text-align: right;'><%= pageindex %></div>
	        		
				</div> <!-- end class="box-center-left" -->
				</div> <!-- end class="box-center-right"  -->
				<div style = "clear:both;"> </div>
				</div>
				
				<div class="box-center-right">
				<div class="box-center-left">
		        <%
	        		}
				%>
				</div> <!-- end class="box-center-left" -->
				</div> <!-- end class="box-center-right" -->				
				
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
