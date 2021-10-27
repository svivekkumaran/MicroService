<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>App Servers</title>
<link rel="stylesheet" type="text/css" href="resources/css/bootstrap/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="resources/css/dashboard.css">
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

	<nav class="navbar navbar-dark sticky-top bg-dark flex-md-nowrap p-0">
      <a class="navbar-brand col-sm-3 col-md-2 mr-0" href="#">Support Automation</a>
      <ul class="navbar-nav px-3">
        <li class="nav-item text-nowrap">
          <!-- <a class="nav-link" href="#">John</a> -->
        </li>
      </ul>
    </nav>
	<div class="container-fluid">
		<div class="row">
			<nav class="col-md-2 d-none d-md-block bg-light sidebar">
				<div class="sidebar-sticky">
					<ul class="nav flex-column">
						<li class="nav-item">
							<a class="nav-link" href="BusinessUnit.jsp"><span data-feather="home"></span> Business Unit</a>
						</li>
						<li class="nav-item">
							<a class="nav-link" href="MediaType.jsp"><span data-feather="file"></span>Media Type</a>
						</li>
						<li class="nav-item">
							<a class="nav-link" href="Credentials.jsp"><span data-feather="file"></span>Credentials</a>
						</li>
						<li class="nav-item">
							<a class="nav-link" href="AppGroup.jsp"><span data-feather="file"></span>Application Group</a>
						</li>
						<li class="nav-item">
							<a class="nav-link active" href="AppTable.jsp"><span data-feather="file"></span>Application Servers</a>
						</li>
					</ul>
				</div>
			</nav>
		</div>
	</div>
	<main role="main" class="col-md-9 ml-sm-auto col-lg-10 pt-3 px-4">
		<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pb-2 mb-3 border-bottom">
            <h1 class="h2">Application Servers</h1>
            <div class="btn-toolbar mb-2 mb-md-0">
              <button class="btn btn-sm btn-outline-secondary" onclick="getAppGroup()">
                <span><i class="fa fa-plus" aria-hidden="true"></i></span>
                New
              </button>
            </div>
	    </div>
	
		<div class="card">
			<div class="card-body">
				<div class="table-responsive">
					<table id="appGroupTable" class="table table-sm table-striped">
						<thead>
							<tr>
								<th>App Group</th>
								<th>MediaType</th>
								<th>Severs</th>
								<th>Option</th>
							</tr>
						</thead>
						<tbody>
	
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</main>
	
	 <!-- The Modal -->
	  <div class="modal fade" id="appServerModal">
	    <div class="modal-dialog modal-lg">
	      <div class="modal-content">
	      
	        <!-- Modal Header -->
	        <div class="modal-header">
	          <h4 class="modal-title">Configure Application Servers</h4>
	          <button type="button" class="close" data-dismiss="modal">&times;</button>
	        </div>
	        
	        <!-- Modal body -->
				<div class="modal-body">
					<div class="card">
						<div class="card-body">
							<form id ="appForm" name="appForm" action="#" method="post">
														  <div class="form-group">
							    <label for="appGroup">AppGroup</label> 
							    <div>
							      <select id="appGroup" name="appGroup" required="required" class="custom-select">
							        <option value="">Choose</option>
							      </select>
							    </div>
							  </div>
							  <div class="form-group">
							    <label for="serverType">Server Type</label> 
							    <div>
							      <select id="serverType" name="serverType" required="required" class="custom-select">
							        <option value="">Choose</option>
							        <option value="MS">MS</option>
							        <option value="UNIX">UNIX</option>
							      </select>
							    </div>
							  </div> 
							</form>
							<div class="table-responsive">
								<table id="example" class="table table-sm table-bordered">
									<thead>
										<tr>
											<th><input type="checkbox" id="selectAll"></th>
											<th>Name</th>
											<th>Type</th>
											<th>HostName</th>
											<th>Port</th>
											<th>LogPath</th>
										</tr>
									</thead>
									<tbody>
									</tbody>
								</table>
							</div>
						</div>
					</div>
				</div>

				<!-- Modal footer -->
	        <div class="modal-footer">
	          <div class="form-group">
				 <button name="submit" type="submit" id="submit" class="btn btn-sm btn-outline-primary">Submit</button>
				 <button type="button"  class="btn btn-sm btn-outline-danger" data-dismiss="modal">Close</button>
			 </div>
	        </div>
	        
	      </div>
	    </div>
	  </div>
	  
	  <!-- The Modal -->
	  <div class="modal fade" id="serverListModal">
	    <div class="modal-dialog modal-lg">
	      <div class="modal-content">
	      
	        <!-- Modal Header -->
	        <div class="modal-header">
	          <h4 class="modal-title">Servers Info</h4>
	          <button type="button" class="close" data-dismiss="modal">&times;</button>
	        </div>
	        
	        <!-- Modal body -->
				<div class="modal-body">
					<div class="card">
						<div class="card-body">
							<div class="table-responsive">
								<table id="serverListTable" class="table table-sm table-bordered">
									<thead>
										<tr>
											<th>HostName</th>
											<th>Port</th>
											<th>LogPath</th>
										</tr>
									</thead>
									<tbody>
									</tbody>
								</table>
							</div>
						</div>
					</div>
				</div>

				<!-- Modal footer -->
	        <div class="modal-footer">
	          <div class="form-group">
				 <button type="button"  class="btn btn-sm btn-outline-danger" data-dismiss="modal">Close</button>
			 </div>
	        </div>
	        
	      </div>
	    </div>
	  </div>
	  
	
	<div id="template" style="display:none;">
			<input type="checkbox" class="checkbox" >	
	</div>
	
  
	  <div id="optionTemplate" style="display:none;">
		<div class="btn-group mr-2">
			<button class="btn btn-sm btn-outline-secondary delete-item"><span><i class="fa fa-trash" aria-hidden="true"></i></span></button>
		</div>
	  </div>
	<div class="ajaxLoader"></div>
</body>
	<script type="text/javascript">
	var appGroupTable;
	var serverListTable;

	$(document).ready(
			function() {
				
				appGroupTable = $("#appGroupTable").DataTable({
				 	 aaSorting: [[1, 'asc']],
					"columnDefs": [
					    { "orderable": false, "targets": 3 },
					    { "searchable": false, "targets": 3 }
				    ]
			});
				
				serverListTable =  $("#serverListTable").DataTable();

				appServerTable = $('#example').DataTable(
						{
							aaSorting: [[1, 'asc']],
							"columnDefs": [
							    { "orderable": false, "targets": 0 },
							    { "searchable": false, "targets": 0 },
						    ],
						    ajax : {
								"type" : "POST",
								"url" : 'GetAppInfo',
								"dataSrc" : function(json) {
									var return_data = new Array();
									var hiddenTemplate = $("#template").html();
									for (var i = 0; i < json.length; i++) {
										var singleRow = [ hiddenTemplate,
												json[i].AppName,
												json[i].AppType,
												json[i].AppHostName,
												json[i].AppPort,
												json[i].LogPath ];
										return_data.push(singleRow);
									}
									return return_data;
								}
							}
						});
				
				init();
				 $('#example').on('click', '#selectAll', function () {
						var allPages = appServerTable.rows({'search':'applied'}).nodes();
				        if ($(this). prop("checked") == false) {
				            $('input[type="checkbox"]',allPages).prop('checked', false); 
				        } else {
				            $('input[type="checkbox"]',allPages).prop('checked', true); 
				        }
				  });
				 
				 $("#appGroupTable").on("click",".delete-item",function() {
						var currentRow = $(this);
						alertify.reset().confirm("Do you want to remove this item?", function () {
							removeItem(currentRow);
						},function() {
							logInfo("Operation cancelled!");
						});
				 });

				
				$(document).on({
					ajaxStart: function() { 
						$("body").addClass("loading");    
					},
					ajaxStop: function()  { 
						$("body").removeClass("loading"); 
					}    
				});
				
				
				$("#appServerModal #submit").click(function() {
					$("#appForm").submit();
				});
				
				
				$("form[name='appForm']").validate({
					errorClass : "verror",
					rules : {
						appGroup : "required",
						serverType : "required"
					},
					messages : {
						appGroup:"Required !",
						serverType : "Required!"
					},
					submitHandler : function(form) {
						saveAppServers(form);
					}
				});

			});
	
	function  getCheckedRow(){
		 var allPages = appServerTable.rows({'search':'applied'}).nodes();
		 var app;
		 var selectedId = [];
			$('input[type="checkbox"]:checked',allPages).each(function() {
				app={};
				app.hostname=$(this).closest('tr').find('td:eq(3)')[0].innerText;
					app.port=$(this).closest('tr').find('td:eq(4)')[0].innerText;
						app.logpath=$(this).closest('tr').find('td:eq(5)')[0].innerText;
				selectedId.push(app);
			});
		return selectedId;
	}
	
	function saveAppServers(form){
		var apps = getCheckedRow();
		if(!apps.length>0){
			logError("please select the server from the table!");
		}else{
			$.ajax({
				
				url : "SaveApps",
				method : "POST",
				data : { servers:JSON.stringify(apps),appGroupId:form.appGroup.value,serverType:form.serverType.value },
				success : function(response){
					getAppGroup();
					$("#appServerModal").modal('toggle');
					console.log(response);
				},error : function(error){
					logError(error);
				}
			});
		}
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
	
	function getAppGroup() {
		$.ajax({
			url : "GetAppGroup",
			type : "POST",
			dataType : 'json',
			data:{forAppTable:"true"},
			success : function(data) {
				if(!isEmpty(data)){
					var appGroup = $("#appGroup");
					appGroup.find('option').not(':first').remove();
					var len = data.length;
					for(var i=0;i<len;i++){
						appGroup.append($('<option>',
							     {
					        value: data[i].appGroupId,
					        text : data[i].appGroupName 
					    }))
					}
				}
			},
			error : function(data) {
				console.log(data);
			}
		});
		 $("#appServerModal").modal("show");
	}
	
	function init() {
		$.ajax({
			url : "GetAppGroup",
			type : "POST",
			dataType : 'json',
			data:{registered:"true"},
			success : function(data) {
				fillDataTable(data)
			},
			error : function(data) {
				console.log(data);
			}
		});
	}
	
	function fillDataTable(data) {
		var singleRow;
		var allRow = [];
		var len = data.length;
		var optionTemplate="";
		var optionTemplate = $("#optionTemplate").html();
		var link = ""
		for (var i = 0; i < len; i++) {
			if(!isEmpty(data[i])) {
				singleRow = [];
				singleRow.push(data[i].appGroupName);
				singleRow.push(data[i].mediaName);
				singleRow.push("<a href='#' onclick='getServers("+data[i].appGroupId+")' data-value='"+data[i].appGroupId+"'>Click Here</a>")
				singleRow.push(optionTemplate);
				allRow.push(singleRow);
			}
		}
		if (allRow.length > 0) {
			appGroupTable.rows.add(allRow).draw();
		}else{
			console.log("No Records found !");
		}
	}
	
	function getServers(appGroupId){
		
		$.ajax({
			url : "GetServers",
			type : "POST",
			//dataType : 'json',
			data:{id:appGroupId},
			success : function(data) {
				if(data!="error"){
					serverListTable.clear().draw();
					serverListTable.rows.add(JSON.parse(data)).draw();
					 $("#serverListModal").modal("show");
				}else{
					doAlert("Erro occured!");
				}
				
			},
			error : function(data) {
				console.log(data);
			}
		});
	}
	
	function removeItem(currentRow){
		var dbId = currentRow.parents('tr').find('td:eq(2)').find("a").data("value");
		$.ajax({
			
			url : "RemoveServers",
			method : "POST",
			data : {"id":dbId},
			success : function(response){
				if("true"==response){
					logSuccess("Success!");
					appGroupTable.row( currentRow.parents('tr') )
			        .remove()
			        .draw();
				}else{
					logError("Operation failed!");
				}
			},error : function(response){
				logError(response);
			}
		}); 
	}
	
	function isEmpty(obj) {
	    for(var key in obj) {
	        if(obj.hasOwnProperty(key))
	            return false;
	    }
	    return true;
	}
	
	function doAlert(message){
		alertify.reset();
		alertify.alert(message);
		$(".alertify").css("z-index",1050);
	}
</script>
</html>