<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="dao.*" %>
<%@ page import="global.*" %>
<%@ page import="model.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.sql.ResultSet" %>
    
<title>Danh sách chuyên gia</title>
<head>
<style type="text/css">
body{
	 background:#202020;
	 font:bold 12px Cambria, Arial, Helvetica, sans-serif;
	 margin:0;
	 padding:0;
	 min-width:400px;
	 color:#bbbbbb; 
	 height:44px;
}

a { 
	text-decoration:none; 
	color:#00c6ff;
}

h1 {
	font: 4em normal Cambria, Arial, Helvetica, sans-serif;
	padding: 20px;	margin: 0;
	text-align:center;
}

h1 small{
	font: 0.2em normal Cambria, Arial, Helvetica, sans-serif;
	text-transform:uppercase; letter-spacing: 0.2em; line-height: 5em;
	display: block;
}

h2 {
    font-weight:700;
    color:#bbb;
    font-size:20px;
}

h2, p {
	margin-bottom:10px;
}

.container {width: 400px; margin: 0 auto; overflow: hidden; height:910px;}

.tooltip {
	display:none;
	position:absolute;
	border:1px solid #333;
	background-color:#161616;
	border-radius:5px;
	padding:10px;
	color:#fff;
	font-size:12px Arial;
}
</style>

<script src="js/jquery-1.9.1.js" language="JavaScript" type="text/javascript"></script>
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
		      url += "?mamh=" + getValue('ma_mon_hoc');
		      xmlHttp.onreadystatechange = stateChange;
		      xmlHttp.open("GET", url, true);
		      xmlHttp.send(null);
		}
		
		function stateChange(){   
		      if (xmlHttp.readyState==4 || xmlHttp.readyState=="complete"){   
		    	  document.getElementById("LietKe").innerHTML=xmlHttp.responseText;
		      }   
		}
		
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
		
<script type="text/javascript">
		$(document).ready(function() {
        // Tooltip only Text
        $('.masterTooltip').hover(function(){
                // Hover over code
                var title = $(this).attr('title');
                $(this).data('tipText', title).removeAttr('title');
                $('<p class="tooltip"></p>')
                .text(title)
                .appendTo('body')
                .fadeIn('slow');
        }, function() {
                // Hover out code
                $(this).attr('title', $(this).data('tipText'));
                $('.tooltip').remove();
        }).mousemove(function(e) {
                var mousex = e.pageX + 20; //Get X coordinates
                var mousey = e.pageY + 10; //Get Y coordinates
                $('.tooltip')
                .css({ top: mousey, left: mousex })
        });
});
</script>
<script>
/*$(document).ready(function(){
	  $("#hide").click(function(){
	    $("p").hide();
	  });
	  $("#show").click(function(){
	    $("p").show();
	  });
	});
$(document).ready(function(){
	  $("#toggle").click(function(){
	    $("pan").toggle();
	  });
	});*/
</script>
</head>

<jsp:include page="/header.jsp"/>
<jsp:include page="/menu_admin.jsp"/>
<div style="text-align: center;">
	<h1>DANH SÁCH CHUYÊN GIA</h1>
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
				<form name='frmExpert' action='ChuyenGiaController' method='POST'>				
				<div id='LietKe' text-align: center;">
					<div class="box-center-right">
					<div class="box-center-left">					
					<table class="gridtable-xemkhht">
	        		<tr>
		        		<th>STT</th>		        		
		        		<th>CMND</th>
		        		<th>Họ tên</th>
		        		<th>Di động</th>		        		
		        		<th>Duyệt</th>        		
		        		<th><img src="images/checkbox.gif" alt="Check"></th>
		        	</tr>
		        	<%
					int stt = 0;
		        	//int tong_so_tc = 0;
	        		ChuyenGia mh = new ChuyenGia();
	        		ArrayList<ChuyenGia> ds = new ArrayList<ChuyenGia>();
	        		ds = mh.getAllExpert();
	        		if (ds.size() == 0)	        		{
	        		%>	<br/><span style='color:red; font-weight:bold;'>Chưa có dữ liệu về chuyên gia!</span><br/><br/><%
	        		}
	        		for(ChuyenGia r : ds){
	        			stt += 1;
	        			String info = "Họ tên: " + r.getname() + ", Chức danh: " + r.gettitle() +", Học vị: " + r.getdegree() +", Cơ quan công tác: " + r.getworkunit() + ", Giới tính: " + r.getsex() + ", Email: " + r.getemail();
	        			
	        			%>
	        			<tr>
			        		<td style="text-align: center; background-color: #dedede"><%= stt %></td>			        				        		
			        		<td class="tenmon">
			        		<p class="masterTooltip" title="<%=info%>" >	
			        		<%=r.getcode() %>
			        		</p>		
			        		</td>			        		
			        		<td>			        		
			        		<%=r.getname() %>			        		
			        		</td>
			        		<td><%=r.getmobile() %></td>
			        		<td>
									<select id="status<%= stt %>" name="status:<%= stt %>" size="1" disabled="disabled">
										<%
										String selectrefuse = "";
										String selectaccept = "";
			        					if ( r.getstatus())
			        					{
			        						selectaccept="selected='selected'";
			        					}
			        					else{
			        						selectrefuse="selected='selected'";
			        					}										
								    		%>
			        						<option value=true <%=selectaccept%>>Chấp nhận</option>			        							
				        					<option value=false <%=selectrefuse%>>Từ chối</option>
				        					<%						        						    										    		%>
									</select>
								</td>			        					        		     		
			        		<td>
			        			<input type='checkbox' id='option_<%= stt %>' name="option" value='<%= stt %>' onclick="makeDisableOrEnable('status<%= stt %>')">
			        			<input type='hidden' name='id_khht:<%= stt %>' value='<%= r.getid() %>'>
			        		</td>
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
	        		<div style='text-align: left;'>							
						    Tổng số chuyên gia: <b><%= stt %></b>
		        	</div>
				</div> <!-- end class="box-center-left" -->
				</div> <!-- end class="box-center-right"  -->
				<div style = "clear:both;"> </div>
				</div>

				<div class="box-center-right">
				<div class="box-center-left">
					<div class="button-link" style="padding-top: 5px;">
		        		<!-- <input type='submit' value='Thêm chuyên ngành' name='addmajor'> -->
		        		<input type='submit' value='Cập nhật' name='adminAcceptExpert'>
		        		<input type='submit' value='Xóa' name='adminDeleteExpert' onClick="return confirm('Bạn có chắc chắn muốn xóa ?');">
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
		response.sendRedirect("Admin.jsp");
	}
%>

<jsp:include page="/footer.jsp"/>
