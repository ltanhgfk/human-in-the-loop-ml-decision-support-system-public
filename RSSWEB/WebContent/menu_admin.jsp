<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%> 
<div id= "nav" >
<div id="nav_left">
	<div id="Menu-Top">
	<%
			Object objadmin = session.getAttribute("admin");			
			if (objadmin != null){
					%>
						<ul>
							<li><a href="Admin_UpdateMajor.jsp"> <span>CẬP NHẬT CHUYÊN NGÀNH</span></a> </li>
							<li><a href=""></a></li>
							<li><a href="Admin_ClassifyMMS.jsp"> <span>PHÂN LOẠI - CẬP NHẬT</span></a></li>
		                   	<li><a href=""> <span> </span></a></li>
		                    <li><a href="Admin_AcceptExpert.jsp"> <span>DUYỆT CHUYÊN GIA</span></a></li>
		                    <li><a href=""> <span></span></a></li>
		                    <li><a href="Admin_UpdateGateway.jsp"> <span>CẬP NHẬT GATEWAY</span></a> </li>
		                   	<li><a href=""> <span></span></a></li>
		                    <li><a href="Admin_UpdateConfig.jsp"> <span>QUẢN LÝ CẤU HÌNH</span></a> </li>
		                    <li><a href=""> <span></span></a></li>
		                    <li><a href="Admin_Statistic.jsp"> <span>THỐNG KÊ TIN NHẮN</span></a></li>
	                	</ul>
					<%
			}
			else{
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
</div>
<div id="wrapper-content">