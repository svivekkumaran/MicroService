<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>App Group</title>
<link rel="stylesheet" type="text/css" href="resources/css/bootstrap/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="resources/css/dashboard.css">
<link rel="stylesheet" type="text/css" href="resources/css/datatable/dataTables.bootstrap4.min.css">
<link rel="stylesheet" type="text/css" href="resources/fonts/font-awesome-4.6.3/css/font-awesome.min.css">
<link rel="stylesheet" type="text/css" href="resources/css/alertify/alertify.css">
<script type="text/javascript" src="resources/js/jquery/jquery-3.3.1.js"></script>
<script type="text/javascript" src="resources/js/jquery/jquery.validate.min.js"></script>
<script type="text/javascript" src="resources/js/datatable/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="resources/js/bootstrap/bootstrap.min.js"></script>
<script type="text/javascript" src="resources/js/datatable/dataTables.bootstrap4.min.js"></script>
<script type="text/javascript" src="resources/js/alertify/alertify.js"></script>
	<style type="text/css">
	
	.verror{
		color:red;
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
	td.nobefore:before {
  		display:none;
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
							<a class="nav-link active" href="AppGroup.jsp"><span data-feather="file"></span>Application Group</a>
						</li>
						<li class="nav-item">
							<a class="nav-link" href="AppTable.jsp"><span data-feather="file"></span>Application Servers</a>
						</li>
					</ul>
				</div>
			</nav>
		</div>
	</div>
	<main role="main" class="col-md-9 ml-sm-auto col-lg-10 pt-3 px-4">
		<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pb-2 mb-3 border-bottom">
            <h1 class="h2">Application Group</h1>
            <div class="btn-toolbar mb-2 mb-md-0">
              <button class="btn btn-sm btn-outline-secondary" data-toggle="modal" data-target="#appGroupModal">
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
								<th>S.No</th>
								<th>App Group</th>
								<th>MediaType</th>
								<th>Comments</th>
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
	
	<div class="ajaxLoader"></div>
	 <!-- The Modal -->
	  <div class="modal fade" id="appGroupModal">
	    <div class="modal-dialog modal-lg">
	      <div class="modal-content">
	      
	        <!-- Modal Header -->
	        <div class="modal-header">
	          <h4 class="modal-title">New App Group</h4>
	          <button type="button" class="close" data-dismiss="modal">&times;</button>
	        </div>
	        
	        <!-- Modal body -->
				<div class="modal-body">
					<form name="agform" id="agform">
						<div class="form-group">
							<label for="appGroupName">AppGroupName</label> <input
								id="appGroupName" name="appGroupName"
								placeholder="Enter the name" type="text" required="required"
								class="form-control here">
						</div>
						<div class="form-group">
							<label for="mediaType">MediaType</label>
							<div>
								<select id="mediaType" name="mediaType" required="required"
									class="custom-select">
									<option value="">Choose</option>
								</select>
							</div>
						</div>
						<div class="form-group">
							<label for="comments">Comments</label> <input id="comments"
								name="comments" placeholder="Enter your comments" type="text"
								class="form-control here">
						</div>
					</form>
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
	  
  
	  <div id="appGroupIdTemplate" style="display: none;">
	  	<input type="hidden" name="appGroupId" id="appGroupId" value="{appGroupId}"/>
	  </div>
  
	  <div id="optionTemplate" style="display:none;">
		<div class="btn-group mr-2">
			<button class="btn btn-sm btn-outline-secondary delete-item"><span><i class="fa fa-trash" aria-hidden="true"></i></span></button>
		</div>
	  </div>
</body>
<script type="text/javascript">
var appGroupTable;
$(document).ready(function() {
	appGroupTable = $("#appGroupTable").DataTable({
	 	 aaSorting: [[1, 'asc']],
		"columnDefs": [
		    { "orderable": false, "targets": 0 },
		    { "searchable": false, "targets": 0 },
		    { "orderable": false, "targets": 4 },
		    { "searchable": false, "targets": 4 }
	    ]
});
	getInitData();
	getAppGroup();
	$(document).on({
		ajaxStart: function() { 
			$("body").addClass("loading");    
		},
		ajaxStop: function()  { 
			$("body").removeClass("loading"); 
		}    
	});
	$(".dataTables_empty").closest("td").addClass("nobefore");
	
	$("#appGroupModal #submit").click(function() {
		$("#agform").submit();
	});
	$("form[name='agform']").validate({
		errorClass : "verror",
		rules : {
			appGroupName : {
				required: {
			        depends:function(){
			            $(this).val($.trim($(this).val()));
			            return true;
			        }
			    },
				checkIfExist: true
			},
			mediaType : "required"
		},
		messages : {
			appGroupName :{
				required:"Required !",
				checkIfExist:"AppGroup Name Already Exist!"
			},
			mediaType : "Required!"
		},
		submitHandler : function(form) {
			saveAppGroup(form);
		}
	});
	
	$.validator.addMethod("checkIfExist", function(value,element) {
		var flag =true;
		var length = appGroupTable.rows().data().length;
		for(var i=0;i<length;i++){
			data = appGroupTable.row(i).data();
			if(data[1] !=""){
				if(data[1].toLowerCase() == value.toLowerCase()){
					flag=false;
					break;
				}
			}
		} 
		return flag;
	});
	
	$("#appGroupTable").on("click",".delete-item",function() {
		var currentRow = $(this);
		alertify.reset().confirm("Do you want to remove this item?", function () {
			removeItem(currentRow);
		},function() {
			logInfo("Operation cancelled!");
		});
 	});
	
});
function getInitData() {
	$.ajax({
		url : "getMediaType",
		type : "POST",
		dataType : 'json',
		data:{isAppGroup:"true"},
		success : function(data) {
			if(!isEmpty(data)){
				var media = $("#mediaType");
				media.find('option').not(':first').remove();
				var len = data.length;
				for(var i=0;i<len;i++){
					media.append($('<option>',
						     {
				        value: data[i].mediaId,
				        text : data[i].mediaName 
				    }))
				}
			}
		},
		error : function(data) {
			logError("Error Occured while getting media type !" + JSON.stringify(data));
		}
	});
}
function getAppGroup() {
	$.ajax({
		url : "GetAppGroup",
		type : "POST",
		dataType : 'json',
		success : function(data) {
			fillDataTable(data)
		},
		error : function(data) {
			console.log(data);
		}
	});
}


function saveAppGroup(form) {
	$.ajax({
		
		url : "SaveAppGroup",
		method : "POST",
		data : { "appGroupName":form.appGroupName.value,"mediaType":form.mediaType.value,"comments":form.comments.value },
		success : function(response){
			if(0!=parseInt(response)){
				updateDataTable(form.appGroupName.value,$("#"+form.mediaType.id+" :selected").text(),form.comments.value,parseInt(response));
				getInitData();
			}else{
				logError("Insert error !");
			}
		},error : function(error){
			logError(error);
		}
	});
	
}

function removeItem(currentRow){
	var dbId = currentRow.parents('tr').find('td:eq(0)').find("input").val();
	$.ajax({
		
		url : "RemoveAppGroup",
		method : "POST",
		data : {"appGroupId":dbId},
		success : function(response){
			if("true"==response){
				logSuccess("Success!");
				appGroupTable.row( currentRow.parents('tr') )
		        .remove()
		        .draw();
				var length = appGroupTable.rows().data().length;
				if(length==0)
				$(".dataTables_empty").closest("td").addClass("nobefore");
				getInitData();
			}else{
				logError("Operation failed!");
			}
		},error : function(response){
			logError(response);
		}
	}); 
}

function updateDataTable(appGroupName,mediaType,comments,id) {
	var template = ($("#appGroupIdTemplate").clone()).html();
	var optionTemplate = $("#optionTemplate").html();
	var appGroupIdTemplate = template;
	appGroupIdTemplate = appGroupIdTemplate.replace("{appGroupId}",id);		
	appGroupTable.row.add([
		appGroupIdTemplate,
		appGroupName,
		mediaType,
		comments,
		optionTemplate
	]).draw();
	$("#appGroupModal").modal('toggle');
}

function fillDataTable(data) {
	var singleRow;
	var allRow = [];
	var len = data.length;
	var optionTemplate="";
	var template = $("#appGroupIdTemplate").html();
	var appGroupIdTemplate;
	var optionTemplate = $("#optionTemplate").html();
	for (var i = 0; i < len; i++) {
		if(!isEmpty(data[i])) {
			
			singleRow = [];
			appGroupIdTemplate = template;
			appGroupIdTemplate = appGroupIdTemplate.replace("{appGroupId}",data[i].appGroupId);
			singleRow.push(appGroupIdTemplate);
			singleRow.push(data[i].appGroupName);
			singleRow.push(data[i].mediaName);
			if (data[i].comments != undefined) {
				singleRow.push(data[i].comments);
			} else {
				singleRow.push("");
			}
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