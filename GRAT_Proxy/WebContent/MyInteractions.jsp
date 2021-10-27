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

<%
//allow access only if session exists
response.setHeader("Cache-Control","no-cache"); //Forces caches to obtain a new copy of the page from the origin server
response.setHeader("Cache-Control","no-store"); //Directs caches not to store the page under any circumstance
response.setDateHeader("Expires", 0); //Causes the proxy cache to see the page as "stale"
response.setHeader("Pragma","no-cache");
String userName = null;
String user = null;
if(session.getAttribute("user") == null){
	 request.setAttribute("Error", "Session has ended.  Please login.");
	   RequestDispatcher rd = request.getRequestDispatcher("Login.jsp");
	   rd.forward(request, response);
}else userName = (String) session.getAttribute("user");
%>
	<nav class="navbar navbar-expand-lg navbar-light" style="background-color: #e3f2fd;">
  <a class="navbar-brand" href="#">My Support</a>
  <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
    <span class="navbar-toggler-icon"></span>
  </button>

  <div class="collapse navbar-collapse" id="navbarSupportedContent">
    <ul class="navbar-nav mr-auto">
      
    </ul>
    <ul class="navbar-nav">
    <li class="nav-item dropdown">
        <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
          <%=userName %>
        </a>
        <div class="dropdown-menu dropdown-menu-right" aria-labelledby="navbarDropdown">
          <a class="dropdown-item" href="Logout">Logout</a>
     	</div>
      </li>
      </ul>
  </div>
  
</nav>
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