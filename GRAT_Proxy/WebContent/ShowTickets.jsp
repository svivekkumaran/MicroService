<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html">
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>My Tickets</title>
<link rel="stylesheet" type="text/css" href="resources/css/bootstrap/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="resources/css/datatable/dataTables.bootstrap4.min.css">
<link rel="stylesheet" type="text/css" href="resources/fonts/font-awesome-4.6.3/css/font-awesome.min.css">
<link rel="stylesheet" type="text/css" href="resources/css/alertify/alertify.css">
<script type="text/javascript" src="resources/js/jquery/jquery-3.3.1.js"></script>
<script type="text/javascript" src="resources/js/jquery/jquery.validate.min.js"></script>
<script src="http://jqueryvalidation.org/files/dist/additional-methods.min.js"></script>
<script type="text/javascript" src="resources/js/datatable/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="resources/js/bootstrap/bootstrap.min.js"></script>
<script type="text/javascript" src="resources/js/datatable/dataTables.bootstrap4.min.js"></script>
<script type="text/javascript" src="resources/js/alertify/alertify.js"></script>
<style type="text/css">
	.main{
		padding:30px;
	}
	table {
   	  counter-reset: rowNumber-1;
	}
	table tr {
	    counter-increment: rowNumber;
	}
	table tr td:first-child::before {
	    content: counter(rowNumber);
	    min-width: 1em;
	    margin-right: 0.5em;
	}
	@font-face {
		font-family: 'Roboto';
		src: url('resources/fonts/roboto/Roboto-Regular-webfont.woff') format('woff')
		font-weight: normal;
		font-style: normal;
	}
	.dataTables_wrapper,#ticketTable{
		font-size: 13px;
    	font-family: Roboto, Tahoma, Verdana;
	}
	td.nobefore:before {
  		display:none;
	}
	.verror{
		color:red;
	}
	.ajaxLoader {
	    display:    none;
	    position:   fixed;
	    z-index:    1051;
	    top:        0;
	    left:       0;
	    height:     100%;
	    width:      100%;
	    background: rgba( 255, 255, 255, .8 ) 
	                url('resources/images/ajax/ajax-animation.gif') 
	                50% 50% 
	                no-repeat;
	}
	body.loading {
	    overflow: hidden;   
	}
	body.loading .ajaxLoader {
	    display: block;
	}

	
</style>
</head>
<body>
	<div class="main">
		<div class="card">
			<div class="card-body">
				<div class="table-responsive">
					<table id="ticketTable" class="table table-sm table-bordered">
						<thead>
							<tr>
								<th>S.No</th>
								<th>ENT_ContactFirstNm</th>
								<th>ENT_ContactLastNm</th>
								<th>Media</th>
								<th>BusinessUnit</th>
								<th>SubBusinessUnit</th>
								<th>InteractionUUID</th>
								<th>Incident</th>
								<th>IncidentTime</th>
								<th>IncidentStatus</th>
							</tr>
						</thead>
						<tbody>
		
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
	<div class="ajaxLoader"></div>
</body>
<script type="text/javascript">
		var interactionDataTable;
		var currentRow;
		var agentName= "<%=request.getParameter("username")%>";
		$(document).ready(function(){
			interactionDataTable = $("#ticketTable").DataTable({
				aaSorting: [[1, 'asc']],
				"columnDefs": [
				    { "orderable": false, "targets": 0 },
				    { "searchable": false, "targets": 0 }
			    ]
			});
			
			$(document).on({
				ajaxStart: function() { 
					$("body").addClass("loading");    
				},
				ajaxStop: function()  { 
					$("body").removeClass("loading"); 
				}    
			});
			
				
			if(agentName!="null"){
				getInitData();
				setInterval(function() {
					getInitData();
				}, 5000);
			}
			
			$(".dataTables_empty").closest("td").addClass("nobefore");
			 	
		});
		
		function getInitData() {
			if(interactionDataTable.rows().count()>0){
				interactionDataTable.clear().draw();
				$(".dataTables_empty").closest("td").addClass("nobefore");
			}
			$.ajax({
				url : "GetTickets",
				type : "POST",
				dataType : 'json',
				data:{"agentName":agentName},
				success : function(data) {
					if(data.records=="true"){
						fillDataTable(data.data);
						console.log(data);
					}else{
						console.log("no data found !");
					}
				},
				error : function(data) {
					logError("Error occured while fetcing data!" + JSON.stringify(data));
				}
			});
		}

		function fillDataTable(data) {
			
			var singleRow;
			var allRow = [];
			var len = data.length;
			for (var i = 0; i < len; i++) {
				if(!isEmpty(data[i])) {
					var url = "https://dev68143.service-now.com/nav_to.do?uri=/incident.do?sys_id="+data[i].sys_id;
					singleRow = [];
					singleRow.push("");
					singleRow.push(data[i].ENT_ContactFirstNm);
					singleRow.push(data[i].ENT_ContactLastNm);
					singleRow.push(data[i].media);
					singleRow.push(data[i].ENT_Segment);
					singleRow.push(data[i].ENT_Function);
					singleRow.push(data[i].interactionUUID);
					singleRow.push(" <a href="+url+" target='_blank'>"+data[i].tnumber+"</a>");
					singleRow.push(data[i].ticketTime);
					switch(data[i].tstate) {
						case 1:singleRow.push("New");
							break;
						case 2:singleRow.push("In Progress");
							break;
						case 3:singleRow.push("On Hold");
							break;
						case 6:singleRow.push("Resolved");
							break;
						case 7:singleRow.push("Closed");
							break;
						case 8:singleRow.push("Canceled");
							break;
						default:singleRow.push(data[i].tstate);
							break;
					}
					
					allRow.push(singleRow);
				}
			}
			if (allRow.length > 0) {
				interactionDataTable.rows.add(allRow).draw();
			}else{
				logInfo("No Ticket Records found !");
			}
		}
		
		function isEmpty(obj) {
		    for(var key in obj) {
		        if(obj.hasOwnProperty(key))
		            return false;
		    }
		    return true;
		}
		
		function logSuccess(message){
			alertify.reset();
			alertify.logPosition("bottom right");
			alertify.maxLogItems(1).success(message);
		}

		function logError(message){
			alertify.reset();
			alertify.logPosition("bottom right");
			alertify.maxLogItems(1).error(message);
		}
		function logInfo(message){
			alertify.reset();
			alertify.logPosition("bottom right");
			alertify.maxLogItems(1).log(message);
		}
</script>
</html>