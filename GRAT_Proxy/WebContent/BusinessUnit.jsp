<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Business Unit</title>
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
	
	#buadd {
		float: right;
	}
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
							<a class="nav-link active" href="BusinessUnit.jsp"><span data-feather="home"></span> Business Unit</a>
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
							<a class="nav-link" href="AppTable.jsp"><span data-feather="file"></span>Application Servers</a>
						</li>
					</ul>
				</div>
			</nav>
		</div>
	</div>
	<main role="main" class="col-md-9 ml-sm-auto col-lg-10 pt-3 px-4">
		<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pb-2 mb-3 border-bottom">
            <h1 class="h2">BusinessUnit</h1>
            <div class="btn-toolbar mb-2 mb-md-0">
              <button class="btn btn-sm btn-outline-secondary" data-toggle="modal" data-target="#buModal">
                <span><i class="fa fa-plus" aria-hidden="true"></i></span>
                New
              </button>
            </div>
	    </div>
	
		<div class="card">
			<div class="card-body">
				<div class="table-responsive">
					<table id="buTable" class="table table-sm table-striped">
						<thead>
							<tr>
								<th>S.No</th>
								<th>Business Unit</th>
								<th>Description</th>
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
	  <div class="modal fade" id="buModal">
	    <div class="modal-dialog modal-lg">
	      <div class="modal-content">
	      
	        <!-- Modal Header -->
	        <div class="modal-header">
	          <h4 class="modal-title">Create Business Unit</h4>
	          <button type="button" class="close" data-dismiss="modal">&times;</button>
	        </div>
	        
	        <!-- Modal body -->
	        <div class="modal-body">
		          <form name="buform" id="buform">
					  <div class="form-group row">
					    <label class="col-3 col-form-label" for="buname">Business Unit</label> 
					    <div class="col-9">
					      <input id="buname" name="buname" placeholder="Enter the business unit name" type="text" required="required" class="form-control here">
					    </div>
					  </div>
					  <div class="form-group row">
					    <label for="budesc" class="col-3 col-form-label">Description</label> 
					    <div class="col-9">
					      <textarea id="budesc" name="budesc" cols="40" rows="3" class="form-control" required="required"></textarea>
					    </div>
					  </div> 
				</form>
	        </div>
	        
	        <!-- Modal footer -->
	        <div class="modal-footer">
	          <button name="submit" id="busubmit" type="submit" class="btn btn-sm btn-outline-secondary">Submit</button>
	          <button type="button"  class="btn btn-sm btn-outline-secondary" data-dismiss="modal">Close</button>
	        </div>
	        
	      </div>
	    </div>
	  </div>
	  
  
	  <div id="buIdtemplate" style="display: none;">
	  	<input type="hidden" name="buId" id="buId" value="{buId}"/>
	  </div>
  
	  <div id="optionTemplate" style="display:none;">
		<div class="btn-group mr-2">
			<button class="btn btn-sm btn-outline-secondary edit-item"><span><i class="fa fa-edit" aria-hidden="true"></i></span></button>
			<button class="btn btn-sm btn-outline-secondary delete-item"><span><i class="fa fa-trash" aria-hidden="true"></i></span></button>
		</div>
	  </div>
</body>
<script type="text/javascript">
	var dataTableObject;
	var isUpdate = false;
	var currentItem;
	var currentIndex;
	var currentUpdationRow;
	$(document).ready(function() {
		$(document).on({
			ajaxStart: function() { 
				$("body").addClass("loading");    
			},
			ajaxStop: function()  { 
				$("body").removeClass("loading"); 
			}    
		});
		
		dataTableObject = $("#buTable").DataTable({
			 	 aaSorting: [[1, 'asc']],
				"columnDefs": [
				    { "orderable": false, "targets": 0 },
				    { "orderable": false, "targets": 3 },
				    { "searchable": false, "targets": 0 },
				    { "searchable": false, "targets": 3 }
			    ]
		});
		getInitData();
		$("#buModal #busubmit").click(function() {
			$("#buform").submit();
		});
		$("form[name='buform']").validate({
			errorClass : "verror",
			rules : {
				buname : {
					required: {
				        depends:function(){
				            $(this).val($.trim($(this).val()));
				            return true;
				        }
				    },
					checkIfExist: true
				},
				budesc : "required"
			},
			messages : {
				buname :{
					required:"Required !",
					checkIfExist:"Business Unit Name Already Exist!"
				},
				budesc : "Required!"
			},
			submitHandler : function(form) {
				saveBusinessUnit(form);
			}
		});
		
		$.validator.addMethod("checkIfExist", function(value,element) {
			var flag =true;
			var length = dataTableObject.rows().data().length;
			if(isUpdate==true){
				for(var i=0;i<length;i++){
					data = dataTableObject.row(i).data();
					if(data[1] !=""){
						if(data[1].toLowerCase() != currentItem.toLowerCase() && data[1].toLowerCase() == value.toLowerCase() ){
							flag=false;
							break;
						}
					}
				} 
			}else{
				for(var i=0;i<length;i++){
					data = dataTableObject.row(i).data();
					if(data[1] !=""){
						if(data[1].toLowerCase() == value.toLowerCase()){
							flag=false;
							break;
						}
					}
				} 
			}
			
			return flag;
		});
		
		 $("#buTable").on("click",".delete-item",function() {
				var currentRow = $(this);
				alertify.reset().confirm("Do you want to remove this item?", function () {
					removeItem(currentRow);
				},function() {
					logInfo("Operation cancelled!");
				});
		 });
		 
		 $("#buTable").on("click",".edit-item",function() {
			 	$("#buform").validate().resetForm();
				$("#buform input").removeClass("verror");
				$("#buform textarea").removeClass("verror");
				isUpdate = true;
				currentUpdationRow = $(this);
				currentIndex = currentUpdationRow.parents('tr').find('td:eq(0)').find('input')[0].value;
				currentItem = getTrimSlash(currentUpdationRow.parents('tr').find('td:eq(1)')[0].innerText);
				$("#buModal input").val(currentUpdationRow.parents('tr').find('td:eq(1)')[0].innerText);
				$("#buModal textarea").val(currentUpdationRow.parents('tr').find('td:eq(2)')[0].innerText);
				$("#buModal").modal("show");
		 });
		
		$('#buModal').on("hidden.bs.modal",function(){
			$("#buModal input").val("");
			$("#buModal textarea").val("");
			$("#buform").validate().resetForm();
			$("#buform input").removeClass("verror");
			$("#buform textarea").removeClass("verror");
		});
		$(".dataTables_empty").closest("td").addClass("nobefore");
	});
	
	function saveBusinessUnit(form) {
		if(isUpdate==true){
			$.ajax({
				
				url : "UpdateBusinessUnit",
				method : "POST",
				data : { "BuName":form.buname.value,"BuDesc":form.budesc.value,"BuId":currentIndex },
				success : function(response){
					if(response=="true"){
						currentUpdationRow.parents('tr').find('td:eq(1)')[0].innerText = form.buname.value;
						currentUpdationRow.parents('tr').find('td:eq(2)')[0].innerText = form.budesc.value;
						$("#buModal").modal('toggle');
						logSuccess("Success!");
						isUpdate = false;
					}else{
						logError("update error !");
					}
				},error : function(error){
					logError(error);
				}
			});
		}else{
			$.ajax({
				
				url : "SaveBusinessUnit",
				method : "POST",
				data : { "BuName":form.buname.value,"BuDesc":form.budesc.value },
				success : function(response){
					if(0!=parseInt(response)){
						updateDataTable(form.buname.value,form.budesc.value,parseInt(response));
					}else{
						logError("Insert error !");
					}
				},error : function(error){
					logError(error);
				}
			});
		}
		
	}
	
	function removeItem(currentRow){
		var dbId = currentRow.parents('tr').find('td:eq(0)').find("input").val();
		$.ajax({
			
			url : "RemoveBusinessUnit",
			method : "POST",
			data : {"BuId":dbId},
			success : function(response){
				if("true"==response){
					logSuccess("Success!");
					dataTableObject.row( currentRow.parents('tr') )
			        .remove()
			        .draw();
					var length = dataTableObject.rows().data().length;
					if(length==0)
					$(".dataTables_empty").closest("td").addClass("nobefore");
				}else{
					logError("Operation failed!");
				}
			},error : function(response){
				logError(response);
			}
		}); 
	}
	
	function updateDataTable(buname,budesc,id) {
		var template = ($("#buIdtemplate").clone()).html();
		var optionTemplate = $("#optionTemplate").html();
		var buIdTemplate = template;
		buIdTemplate = buIdTemplate.replace("{buId}",id);		
		dataTableObject.row.add([
			buIdTemplate,
			buname,
			budesc,
			optionTemplate
		]).draw();
		$("#buModal").modal('toggle');
	}
	
	function getInitData() {
		$.ajax({
			url : "getBusinessUnit",
			type : "POST",
			dataType : 'json',
			success : function(data) {
				fillDataTable(data);
			},
			error : function(data) {
				logError("Error Occured while getting businessunt !" + JSON.stringify(data));
			}
		});
	}

	function fillDataTable(data) {
		var singleRow;
		var allRow = [];
		var len = data.length;
		var template = $("#buIdtemplate").html();
		var buIdTemplate;
		var optionTemplate = $("#optionTemplate").html();
		for (var i = 0; i < len; i++) {
			if(!isEmpty(data[i])) {
				
				
				
				singleRow = [];
				buIdTemplate = template;
				buIdTemplate = buIdTemplate.replace("{buId}",data[i].buid);
				singleRow.push(buIdTemplate);
				singleRow.push(data[i].buname);
				if (data[i].budescription != undefined) {
					singleRow.push(data[i].budescription);
				} else {
					singleRow.push("");
				}
				singleRow.push(optionTemplate);
				allRow.push(singleRow);
			}
		}
		if (allRow.length > 0) {
			dataTableObject.rows.add(allRow).draw();
		}else{
			logInfo("No BusinessUnit Records found !");
		}
	}
	
	function isEmpty(obj) {
	    for(var key in obj) {
	        if(obj.hasOwnProperty(key))
	            return false;
	    }
	    return true;
	}
	
	function getTrimSlash(path){
		var lastChar = path.slice(-1);
		if(lastChar=="/"){
			return path.substring(0,path.length-1);
		}else{
			return path;
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
</script>
</html>