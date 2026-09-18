var xmlHttp;  
var xmlHttp;

function ham(url, id){
	alert(url + " " + id);
}

function show_data_ajax(url, id){
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
		      
	xmlHttp.onreadystatechange = stateChange(id);
	xmlHttp.open("GET", url, true);
	xmlHttp.send(null);
}
		
function Change(id){   
	if (xmlHttp.readyState==4 || xmlHttp.readyState=="complete"){   
		document.getElementById("LietKe").innerHTML=xmlHttp.responseText; 
	}   
}