<%@ page language='java' contentType='text/html; charset=UTF-8'
    pageEncoding='UTF-8'%>
<%@ page import='dao.*' %>
<%@ page import='global.*' %>
<%@ page import='model.*' %>
<%@ page import='java.util.*' %>
<%@ page import='java.sql.ResultSet' %>    

<%
Object objdn = session.getAttribute("admin");
String expertid=request.getParameter("expertid");
String majorid=request.getParameter("majorid");
String month=request.getParameter("month");
String year=request.getParameter("year");

//only for testing
//out.print("thang:" + month);
//out.print("nam:" + year);
//out.print("maorid:" + majorid);
//out.print("expertid:" + expertid);
if (objdn != null)
{		
	try
	{
				String buffer=""; 
			
				buffer += "<div style='text-align: center; color: red;'>";
				buffer += "</div>" +
				"<div style = 'clear:both; height:10px;'></div>"+
				"<form name='frmMMS' action='MMSController' method='POST'>";
				
				buffer += "<div id='LietKe'>"+
					"<div class='box-center-right'>"+
					"<div class='box-center-left'>"+
					"<div class='pop-up'>"+
					"<img id='popupImage' src='' style='display: none' width='300' height='300'/>"+
					"</div>"+
					"<table class='gridtable-xemkhht'>";
		        	
	        		int stt = 0;
	        		MMS ms = new MMS();
	        		ArrayList<MMS> ds = new ArrayList<MMS>();
	        		ds = ms.getMMSByAnswerNoLimit(true, Integer.parseInt(expertid),Integer.parseInt(majorid),Integer.parseInt(month),Integer.parseInt(year));
	        		
	        		if (ds.size() == 0)	        		
	        		{
	        			buffer += "<br/><span style='color:red; font-weight:bold;'>Không tìm thấy tin nhắn theo tiêu chí tìm kiếm!</span><br/><br/>";
	        		}
	        		else
	        		{
	        		buffer += "<tr>"+
			        		"<th>STT</th>"+
			        		"<th>Ảnh</th>"+
			        		"<th>Câu hỏi</th>"+
			        		"<th>Thời gian nhận</th>"+
			        		"<th>Trả lời</th>"+
			        		"<th>Thời gian trả lời</th>"+
			        		/*
			        		"<th>Máy phân loại</th>"+
			        		"<th>Người phân loại</th>"+*/
			        		"</tr>";

			        		for(MMS r : ds)
			        		{
			        			stt += 1;
			        			buffer += "<tr>"+
					        		"<td style='text-align: center; background-color: #dedede'>" + stt + "</td>"+
					        		"<td style='text-align: center;'>";
									if( r.getimage().contains(".jpg") || r.getimage().contains(".jpeg") || r.getimage().contains(".png") || r.getimage().contains(".gif") )
									{											        			
										buffer += "<img src='images/rices/"+r.getimage()+"' border='0' width='100' height='100'>";												
									}
									else
									{
										buffer += "Không có";
									}
									buffer += "</td>"+
					        		"<td class='tenmon'>"+
					        		r.getmsg() +
					        		"</td>"+
					        		"<td class='tenmon'>"+
					        		r.getreceivedDate() +
					        		"</td>"+
					        		"<td class='tenmon'>"+
					        		r.getreplymsg() +
					        		"</td>"+
									"<td class='tenmon'>"+
					        		r.getansweredDate() +
					        		"</td>"+
					        		/*
					        		"<td align='center'>";
					        		ChuyenMon cm = new ChuyenMon();
					        		cm = cm.getMajorById(r.getmajoridByMachine());
					        		buffer += cm.getName() +
					        		"</td>"+
					        		"<td align='center'>";
					        		cm = cm.getMajorById(r.getmajoridByHuman());
					        		buffer += cm.getName() +
					        		"</td>"+
					        				*/
					        		"</tr>";
			        		}
	        		}
	        		buffer += "</table>"+
	        		
	        		"<div style = 'clear:both; height:10px;'> </div>";
	        		if(stt>=1)
	        		{
	        		buffer += "<div style='text-align: left;'> Số tin nhắn nhận được ";
	        		if(Integer.parseInt(expertid)!=-1)
	        		{ 
	        			ChuyenGia cg = new ChuyenGia();
	        			cg = cg.getExpertById(Integer.parseInt(expertid));
		        		
	        			buffer +=" do chuyên gia <b>" + cg.getname() + "</b> trả lời, "; 
					}
	        		if(Integer.parseInt(majorid)!=-1)
	        		{
	        			ChuyenMon cm = new ChuyenMon();
		        		cm = cm.getMajorById(Integer.parseInt(majorid));
		        		
	        			buffer +=" thuộc chuyên môn <b>" + cm.getName() + "</b> ,";
	        		}
	        		if(Integer.parseInt(month)!=-1)
	        		{ 
	        			buffer +=" trong tháng <b>" + month + "</b>, "; 
	        		}
	        		if(Integer.parseInt(year)!=-1)
	        		{
	        			buffer +=" năm <b>" + year + "</b>"; 
	        		}
	        		buffer +=" là <b>"+ stt +"</b>" +
	        		
		        	"</div>"+
	        		"<br/>"+
		        	"</div>"+
					"</div>"+
					"</div>"+
					"<div style = 'clear:both;'> </div>"+
					"</div>"+
					"<div class='box-center-right'>"+
					"<div class='box-center-left'>"+
					"<div class='button-link' style='padding-top: 5px;'>"+
			        "</div>";
	        		}
				buffer += "</div>"+
				"</div>"+
				"<div style = 'clear:both;'> </div>"+
	    		"</form>"+ 
	    		"<br/>";
				response.getWriter().println(buffer);
		}
		catch (Exception e){		
			System.out.println(e);
		}	
	}
%>
