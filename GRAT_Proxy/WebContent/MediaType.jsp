<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Media Type</title>
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
	
	#buadd{
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
							<a class="nav-link" href="BusinessUnit.jsp"><span data-feather="home"></span>Business Unit</a>
						</li>
						<li class="nav-item">
							<a class="nav-link active" href="MediaType.jsp"><span data-feather="file"></span>Media Type</a>
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
            <h1 class="h2">MediaType</h1>
            <div class="btn-toolbar mb-2 mb-md-0">
              <button class="btn btn-sm btn-outline-secondary" data-toggle="modal" data-target="#mtModel">
                <span><i class="fa fa-plus" aria-hidden="true"></i></span>
                New
              </button>
            </div>
	    </div>
	
		<div class="card">
			<div class="card-body">
				<div class="table-responsive">
					<table id="mtTable" class="table table-sm table-striped">
						<thead>
							<tr>
								<th>S.No</th>
								<th>Media Type</th>
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
	  <div class="modal fade" id="mtModel">
	    <div class="modal-dialog modal-lg">
	      <div class="modal-content">
	      
	        <!-- Modal Header -->
	        <div class="modal-header">
	          <h4 class="modal-title">Create Media Type</h4>
	          <button type="button" class="close" data-dismiss="modal">&times;</button>
	        </div>
	        
	        <!-- Modal body -->
	        <div class="modal-body">
		          <form name="mtform" id="mtform">
					  <div class="form-group row">
					    <label class="col-3 col-form-label" for="MtName">Media Type</label> 
					    <div class="col-9">
					      <input id="MtName" name="MtName" placeholder="Enter the Media Type name" type="text" required="required" class="form-control here">
					    </div>
					  </div>
				</form>
	        </div>
	        
	        <!-- Modal footer -->
	        <div class="modal-footer">
	          <button name="submit" id="mtsubmit" type="submit" class="btn btn-sm btn-outline-secondary">Submit</button>
	          <button type="button"  class="btn btn-sm btn-outline-secondary" data-dismiss="modal">Close</button>
	        </div>
	        
	      </div>
	    </div>
	  </div>
  
	  <div id="mtIdtemplate" style="display: none;">
	  	<span class="number"></span>
	  	<input type="hidden" name="MtId" id="MtId" value="{MtId}"/>
	  </div>
  
	  <div id="optionTemplate" style="display:none;">
		<div class="btn-group mr-2">
			<button class="btn btn-sm btn-outline-secondary delete-item"><span><i class="fa fa-trash" aria-hidden="true"></i></span></button>
		</div>
	  </div>
</body>
<script type="text/javascript">
	var dataTableObject;
	$(document).ready(function() {
		$(document).on({
			ajaxStart: function() { 
				$("body").addClass("loading");    
			},
			ajaxStop: function()  { 
				$("body").removeClass("loading"); 
			}    
		});
		
		dataTableObject = $("#mtTable").DataTable({
			 	 aaSorting: [[1, 'asc']],
				"columnDefs": [
				    { "orderable": false, "targets": 0 },
				    { "orderable": false, "targets": 2 },
				    { "searchable": false, "targets": 0 },
				    { "searchable": false, "targets": 2 }
			    ]
		});
		getInitData();
		$("#mtModel #mtsubmit").click(function() {
			$("#mtform").submit();
		});
		$("form[name='mtform']").validate({
			errorClass : "verror",
			rules : {
				MtName : {
					required: {
				        depends:function(){
				            $(this).val($.trim($(this).val()));
				            return true;
				        }
				    },
					checkIfExist: true
				}
			},
			messages : {
				MtName :{
					required:"Required !",
					checkIfExist:"Media Type Name Already Exist!"
				}
			},
			submitHandler : function(form) {
				saveMediaType(form);
			}
		});
		
		$.validator.addMethod("checkIfExist", function(value,element) {
			var flag =true;
			var length = dataTableObject.rows().data().length;
			for(var i=0;i<length;i++){
				data = dataTableObject.row(i).data();
				if(data[1] !=""){
					if(data[1].toLowerCase() == value.toLowerCase()){
						flag=false;
						break;
					}
				}
			} 
			return flag;
		});
		
		 $("#mtTable").on("click",".delete-item",function() {
				var currentRow = $(this);
				alertify.reset().confirm("Do you want to remove this item?", function () {
					removeItem(currentRow);
				},function() {
					logInfo("Operation cancelled!");
				});
		 });
		
		$('#mtModel').on("hidden.bs.modal",function(){
			$("#mtModel input").val("");
		});
		$(".dataTables_empty").closest("td").addClass("nobefore");
	});
	
	function saveMediaType(form) {
		$.ajax({
			
			url : "SaveMediaType",
			method : "POST",
			data : {"MtName":form.MtName.value},
			success : function(response){
				if(0!=parseInt(response)){
					updateDataTable(form.MtName.value,parseInt(response));
				}else{
					logError("Insert error !");
				}
			},error : function(){
				
			}
		});
	}
	
	function removeItem(currentRow){
		var dbId = currentRow.parents('tr').find('td:eq(0)').find("input").val();
		$.ajax({
			
			url : "RemoveMediaType",
			method : "POST",
			data : {"MtId":dbId},
			success : function(response){
				console.log("Remove:"+response);
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
	
	function updateDataTable(MtName,id) {
		var template = ($("#mtIdtemplate").clone()).html();
		var optionTemplate = $("#optionTemplate").html();
		var mtIdtemplate = template;
		mtIdtemplate = mtIdtemplate.replace("{MtId}",id);		
		dataTableObject.row.add([
			mtIdtemplate,
			MtName,
			optionTemplate
		]).draw();
		$("#mtModel").modal('toggle');
	}
	
	function getInitData() {
		$.ajax({
			url : "getMediaType",
			type : "POST",
			dataType : 'json',
			success : function(data) {
				fillDataTable(data);
			},
			error : function(data) {
				logError("Error Occured while getting media type !" + JSON.stringify(data));
			}
		});
	}

	function fillDataTable(data) {
		var singleRow;
		var allRow = [];
		var len = data.length;
		var optionTemplate="";
		var template = $("#mtIdtemplate").html();
		var mtIdtemplate;
		var optionTemplate = $("#optionTemplate").html();
		for (var i = 0; i < len; i++) {
			if(!isEmpty(data[i])) {
				singleRow = [];
				mtIdtemplate = template;
				mtIdtemplate = mtIdtemplate.replace("{MtId}",data[i].mediaId);
				singleRow.push(mtIdtemplate);
				singleRow.push(data[i].mediaName);
				singleRow.push(optionTemplate);
				allRow.push(singleRow);
			}
		}
		if (allRow.length > 0) {
			dataTableObject.rows.add(allRow).draw();
		}else{
			logInfo("No MediaType Records found !");
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