<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
	<style>
	td{
		    background-color: #9ED8DA !important;
	}
	body{
		font-size:11px;
		font-family: Roboto, Tahoma, Verdana;
		padding:8px;
	}
	.doright{
		text-align:right;
	}
	.wrapper{
		border: 1px solid rgba(0, 0, 0, 0.13);
   		 height: -webkit-fill-available;
	}
	</style>
</head>
<body>
	<div class="wrapper">
		<table id="statsTable"
		class="table table-responsive table-bordered"
		style="width: 100%; border-top: 0px;">
		<thead align="right" style="background:#f3f4f6">
			<tr>
				<th>VQName</th>
				<th class="doright">ChatsInQueue</th>
				<th class="doright">LongestWaitTime</th>
			</tr>
		</thead>
		<tbody>
			<tr><td>-</td><td class='doright'>-</td><td class='doright'>-</td></tr>
		</tbody>
	</table>
	</div>

	
</body>
<script>
	
	
	/* $(function() {
		var response;

		setInterval(function() {
			$.ajax({
				type : "get",
				url : "StatsServlet",
				success : function(data) {
					updateTable(JSON.parse(data));
				},
				error : function(data) {
					console.log(data);
				}
			});
		}, 5000);

	}); */

	function updateTable(response) {
		$('#statsTable tbody').empty();
		var tr = "<tr><td>VQ_OPT_OptumRx_CustSrvc_Member</td><td class='doright'>"
				+ response.chatsInQueue + "</td><td class='doright'>"
				+ response.longestWaitTime + "</td></tr>";
				
		$('#statsTable tbody').append(tr);
	}
</script>
</html>