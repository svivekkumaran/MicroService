<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>MySupport</title>
<link rel="stylesheet" type="text/css" href="resources/css/bootstrap/bootstrap.min.css">
<script type="text/javascript" src="resources/js/jquery/jquery-3.3.1.js"></script>
<script type="text/javascript" src="resources/js/bootstrap/bootstrap.min.js"></script>
<style type="text/css">
@font-face {
		font-family: 'Roboto';
		src: url('resources/fonts/roboto/Roboto-Regular-webfont.woff') format('woff')
		font-weight: normal;
		font-style: normal;
	}
	.container-fluid{
		font-size: 13px;
    	font-family: Roboto, Tahoma, Verdana;
	}
	.main{
		border: 1px solid #dee2e6;
	}
	.container-fluid {
		padding:20px;
	}
</style>
</head>
<body>
	<div class="container-fluid">
		<div class="main">
			<ul class="nav nav-tabs " style="background: #f3f4f6;">
		  <li class="nav-item">
		    <a class="nav-link active" id="myIxn" href="#">My Interactions</a>
		  </li>
		  <li class="nav-item">
		    <a class="nav-link" id="myTickets" href="#">My Tickets</a>
		  </li>
		</ul>
		<div class="embed-responsive embed-responsive-21by9">
		  <iframe class="embed-responsive-item" id="myFrame" src=""></iframe>
		</div>
		</div>
	</div>
</body>
<script type="text/javascript">
var agentName = "<%=request.getParameter("username")%>";
$(document).ready(function(){
	$('#myFrame').prop('src', "http://ve7d00009055:8080/GRAT_Proxy/DisplayInteraction.jsp?username="+agentName);
	$("#myIxn").click(function(e){
		e.preventDefault();
		$('a.active').removeClass('active');
		$(this).addClass('active');
		$('#myFrame').prop('src', "http://ve7d00009055:8080/GRAT_Proxy/DisplayInteraction.jsp?username="+agentName);
	});
	$("#myTickets").click(function(e){
		e.preventDefault();
		$('a.active').removeClass('active');
		$(this).addClass('active');
		$('#myFrame').prop('src', "http://ve7d00009055:8080//GRAT_Proxy/ShowTickets.jsp?username="+agentName);
	});
});
</script>
</html>