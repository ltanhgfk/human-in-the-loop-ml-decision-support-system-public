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
		
		function makeDisableOrEnable(id1,id2){
		    var x=document.getElementById(id1);
		    var y=document.getElementById(id2);
		    //var z=document.getElementById(id3);
		    if (x.disabled == true)// && y.disabled == true)
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
		function getValue(id){
		    var x = document.getElementById(id);
		    return x.value; 	    	
		}
    </script>
</head>

<jsp:include page="header.jsp"/>
<jsp:include page="menu.jsp"/>
<div style="text-align: center;">
	<h1>DANH SÁCH CÂU HỎI</h1>
</div>
<BR/>
<%
Object objdn = session.getAttribute("username");
String username = (String) session.getAttribute("username");
String usertype = (String) session.getAttribute("usertype");

String userid = (String) objdn;
ChuyenGia cgst = new ChuyenGia();
cgst = cgst.getExpertByUserId(userid);

	if (objdn!=null && cgst.getstatus() == true && (usertype.equals("chuyengia") || usertype.equals("dieuphoi")))
	{		
		try
		{
%>			<div style="text-align: center; color: red;">
		        	<%
		        	Object objttc = session.getAttribute("themthanhcong");
		        	if (objttc != null){
		        		String ttc = (String) objttc;
		        		out.print(ttc);
		        		session.removeAttribute("themthanhcong");
		        	}//MMSController
		        	%>
		    </div>
		    </br>
		    <div>	
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
		        	int expertId=0;
		        	int i=0;	
		        	int time=0;
		        	
		        	ChuyenGia cg = new ChuyenGia();
		        	expertId = cg.getExpertIdByUserId(username);
		        	ChuyenGiaChuyenMon cgcm=new ChuyenGiaChuyenMon();
		        	ArrayList<ChuyenGiaChuyenMon> cgcmList = new ArrayList<ChuyenGiaChuyenMon>();
		        	cgcmList = cgcm.getExpertMajorByExpertId(expertId);
		        	
		        	if(cgcmList.size()==0)
		        	{
		        		%>	<br/><span style='color:red; font-weight:bold;'>Chuyên gia chưa đăng ký chuyên môn, xin cập nhật thông tin!</span><br/><br/>
		        		<%
			        }
		        	else
		        	{
			        		// de tieu de cho nay neu het cach
			        		System.out.println("size: "+cgcmList.size());
				        	for(ChuyenGiaChuyenMon row : cgcmList)
				        	{
				        			time+=1;
					        		MMS ms = new MMS();
					        		ArrayList<MMS> ds = new ArrayList<MMS>();
					        		Config cf = new Config();
					        		cf = cf.getConfigBycode("systemconfig");
					        		if( cf.getvaluechar().compareTo("semi-auto") == 0 )
					        		{
					        			//System.out.println("semi: " +row.getMajorId());
					        			ds = ms.getMMSClassifiedByHuman(row.getMajorId(), true, false);
					        			if(ds.size()>=1) i+=1;
					        		}
					        		else if( cf.getvaluechar().compareTo("auto") == 0 )
					        		{
					        			//System.out.println("auto: " +row.getMajorId());
					        			ds = ms.getMMSClassifiedByMachine(row.getMajorId(), true, false);
					        			//ds = ms.getMMSClassifiedByHuman(row.getMajorId(), true, false);
					        			if(ds.size()>=1) i+=1;
					        			//System.out.println("size ds: " +ds.size());
					        		}

					        		if (ds.size() == 0)
					        		{
					        			//System.out.println("size ds2: " +ds.size());
					        			//System.out.println("i= " +i);
					        			if(time==cgcmList.size() && i<=0)
					        			{
					        				%><br/><span style='color:red; font-weight:bold;'>Chưa có câu hỏi mới!</span><br/><br/><%
					        			}
					        		}
					        		else
					        		{
					        			//System.out.println("size ds3: " +ds.size());
					        			//System.out.println("i2= " +i);
					        			if(i==1)
					        			{					        				
					        				%>
						        			<tr>
								        		<th>STT</th>
								        		<th>Ảnh</th>
								        		<th>Nội dung câu hỏi</th>
								        		<th>Ngày nhận</th>
								        		<th>Phân loại lại</th>
								        		<th>Nội dung trả lời</th>
								        		<th><img src="images/system/checkbox.gif" alt="Check"></th>
								        	</tr>
						        			<%
					        			}
							        		for(MMS r : ds)
							        		{
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
									        			<%=r.getmsg() %>
									        		</td>
									        		<td class="tenmon">
									        		<%=r.getreceivedDate()%>
									        		</td>
									        		<td>
															<select id="classify<%= stt %>" name="classify:<%= stt %>" size="1" disabled="disabled" >
																<option value="-1">Chưa phân loại</option>										
																<%
																ChuyenMon cm = new ChuyenMon();
																String selected="";
													    		ArrayList<ChuyenMon> dscm = new ArrayList<ChuyenMon>();
													    		dscm = cm.getAllMajor();
													    		for(ChuyenMon record : dscm)
													    		{
													    			if(r.getmajoridByHuman()==record.getId())
													    			{
													    				selected="selected";
													    			}
													    			else selected="";
													    			%>
													    			<option value="<%= record.getId() %>" <%=selected %>><%= record.getName() %></option>
													    			<%
													    		}
													    		%>
													    	</select>
													</td>	
									        		<td class="tenmon">			        		
									        			<textarea cols="40" rows="5" id='replymsg<%= stt %>' name='replymsg<%= stt %>' disabled="disabled"><%=r.getreplymsg() %></textarea>
									        		</td>	        					        		     		
									        		<td>
									        			<input type='checkbox' id='option_<%= stt %>' name="option" value='<%= stt %>' onclick="makeDisableOrEnable('replymsg<%= stt %>','classify<%= stt %>')">
									        			<input type='hidden' name='id_khht:<%= stt %>' value='<%= r.getid() %>'>
									        		</td>
									        	</tr>		        	
					<%
							        		}
							        		//i=i+1;
					        		}
				        	}
		        	}
	        		%>
	        		</table>
	        		<%
	        		if(stt>=1)
	        		{
	        		%>
	        		
	        		<br/>	        			
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
		        		<input type='submit' value='Cập nhật' name='expertReplyMMS'>
		        		<!-- <input type='submit' value='Xóa' name='deletemajor' onClick="return confirm('Bạn có chắc chắn muốn xóa ?');"> -->
		        		<input type='reset' value='Làm lại' name='reset' class="nutsearch blue small">
		        	</div>
		        <%
	        		}
				%>
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
