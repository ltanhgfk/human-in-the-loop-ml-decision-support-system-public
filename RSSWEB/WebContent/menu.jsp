
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="dao.*" %>
<%@ page import="global.*" %>
<%@ page import="model.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.sql.ResultSet" %>

    <div id= "nav" >

	<div id="Menu-Top">
	<%
			Object objdn = session.getAttribute("username");
			String userid = (String) objdn;
			ChuyenGia cg = new ChuyenGia();
			cg = cg.getExpertByUserId(userid);
			
			if (objdn != null && cg.getstatus() == true)
			{
			%>				
						<ul>
		                    <li><a href="Expert_ViewInfo.jsp"> <span>THÔNG TIN CÁ NHÂN</span></a></li>		                    
		                    <li><a href="Expert_ReplyMMS.jsp"> <span>PHẢN HỒI NHÀ NÔNG</span></a></li>
		                    <!-- <li><a href="Expert_UpdateContent.jsp"> <span>BỔ SUNG DỮ LIỆU</span></a></li>
		                    <li><a href="Expert_UpdateContent.jsp"> <span>GIỐNG LÚA</span></a></li>
		                    <li><a href="Expert_ReplyMMS.jsp"> <span>QUY TRÌNH KỸ THUẬT</span></a></li>
		                    <li><a href="Expert_ReplyMMS.jsp"> <span>SÂU BỆNH CỎ HẠI</span></a></li>
		                    <li><a href="Expert_ReplyMMS.jsp"> <span>SAU THU HOẠCH</span></a></li>
		                    <li><a href="Expert_ReplyMMS.jsp"> <span>SẢN XUẤT/TIÊU THỤ</span></a></li>
		                     -->
		                </ul>
		    <%
			}
			else if (objdn != null && cg.getstatus() == false)
			{
				%>				
				<ul>
                    <li><a href="Expert_ViewInfo.jsp"> <span>THÔNG TIN CÁ NHÂN</span></a></li>                    
                </ul>
    			<%
			}
			else
			{
			%>
				<ul>
	         	  <li><a href="index.jsp" > <span>TRANG CHỦ</span></a> </li>	
	         	  <li><a href="Admin.jsp" > <span>QUẢN TRỊ</span></a> </li>	               
	            </ul>
				<%
			}
	%>
	         
    </div>
    <script type="text/javascript">
            function setActive() {
                aObj = document.getElementById('Menu-Top').getElementsByTagName('li');
                var found = false;
                for (var i = 0; i < aObj.length; i++) {
                    if (document.location.href.indexOf(aObj[i].getElementsByTagName('a')[0].href) >= 0) {
                        aObj[i].className = 'current';
                        found = true;
                    }
                }
                if (!found) aObj[0].className = 'current';
            }
            setActive();
        </script> 

</div>
<div id="wrapper-content">   