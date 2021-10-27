<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html">
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>DisplayInetractions</title>
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
	.dataTables_wrapper,#ixnTable{
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
					<table id="ixnTable" class="table table-sm table-bordered">
						<thead>
							<tr>
								<th>S.No</th>
								<th>ENT_ContactFirstNm</th>
								<th>ENT_ContactLastNm</th>
								<th>ENT_SubFunction</th>
								<th>Subject</th>
								<th>Media</th>
								<th>BusinessUnit</th>
								<th>SubBusinessUnit</th>
								<th>InteractionUUID</th>
								<th>InteractionTime</th>
								<th>Ticket</th>
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
	 <!-- The Modal -->
	  <div class="modal fade" id="ticketModal">
	    <div class="modal-dialog modal-lg">
	      <div class="modal-content">
	      
	        <!-- Modal Header -->
	        <div class="modal-header">
	          <h4 class="modal-title">New Ticket</h4>
	          <button type="button" class="close" data-dismiss="modal">&times;</button>
	        </div>
	        
	        <!-- Modal body -->
	        <div class="modal-body">
		          <form name="ticketForm" id="ticketForm">
					  <div class="form-group row">
				    <label for="sdesc" class="col-4 col-form-label">Short Description</label> 
				    <div class="col-8">
				      <input id="sdesc" name="sdesc" placeholder="please enter the description" type="text" class="form-control here">
				    </div>
				  </div>
				  <div class="form-group row">
				    <label for="ldesc" class="col-4 col-form-label">Long Description</label> 
				    <div class="col-8">
				      <textarea id="ldesc" name="ldesc" cols="40" rows="5" placeholder="please enter the description in detail" class="form-control"></textarea>
				    </div>
				  </div>
				  <div class="form-group row">
				    <label for="env" class="col-4 col-form-label">Environment</label> 
				    <div class="col-8">
				      <select id="env" name="env" class="custom-select">
				        <option value="Dev">Dev</option>
				        <option value="Stage">Stage</option>
				        <option value="Prod">Prod</option>
				      </select>
				    </div>
				  </div> 
				  <div class="form-group row">
				    <label for="sshots" class="col-4 col-form-label">Screen Shot</label> 
				    <div class="col-8">
				       <input type="file" name="sshots" id="sshots" class="form-control here">
				       <input type="hidden" name="imgString" id="imgString" class="form-control here">
				    </div>
				  </div>
				</form>
	        </div>
	        
	        <!-- Modal footer -->
	        <div class="modal-footer">
	          <button name="submit" id="ticketSubmit" type="submit" class="btn btn-sm btn-outline-primary">Create</button>
	          <button type="button"  class="btn btn-sm btn-outline-danger" data-dismiss="modal">Close</button>
	        </div>
	        
	      </div>
	    </div>
	  </div>
	
	
	
	<div id="ixnIdtemplate" style="display: none;">
	  	<input type="hidden" name="ixnId" id="ixnId" value="{ixnId}"/>
	 </div>
	<div id="template" style="display:none;">
		<button class="btn btn-secondary btn-sm newTicket"><span><i class="fa fa-edit" aria-hidden="true"></i></span></button>
	</div>
</body>
<script type="text/javascript">
		var interactionDataTable;
		var currentRow;
		var agentName = "<%=request.getParameter("username")%>";
		console.log("agentname ---"+agentName);
		$(document).ready(function(){
			interactionDataTable = $("#ixnTable").DataTable({
				aaSorting: [[1, 'asc']],
				"columnDefs": [
				    { "orderable": false, "targets": 0 },
				    { "orderable": false, "targets": 9 },
				    { "searchable": false, "targets": 0 },
				    { "searchable": false, "targets": 9 }
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
			
			 $("#ixnTable").on("click",".newTicket",function() {
				 currentRow = $(this);
				 $("#ticketModal").modal("show");
			});
			 
			 $("form[name='ticketForm']").validate({
					errorClass : "verror",
					rules : {
						sdesc : {
							required: {
						        depends:function(){
						            $(this).val($.trim($(this).val()));
						            return true;
						        }
						    }
						},
						ldesc : {
							required: {
						        depends:function(){
						            $(this).val($.trim($(this).val()));
						            return true;
						        }
						    }
						},
						sshots:{
							required:true,
							accept: "image/*"
						}
					},
					messages : {
						sdesc :{
							required:"Required !"
						},
						ldesc :{
							required:"Required !"
						},
						sshots:{
							required :"Required !",
							accept:"Only image files will be allowed!"
						}
					},
					submitHandler : function(form) {
						buildTicketParam(form);
					}
				});
			 
			 	$("#ticketModal #ticketSubmit").click(function() {
					$("#ticketForm").submit();
				});
			 	
			 	$('#ticketModal').on("hidden.bs.modal",function(){
					$("#ticketModal input").val("");
					$("#ticketModal textarea").val("");
					$("#ticketForm").validate().resetForm();
					$("#ticketForm input").removeClass("verror");
					$("#ticketForm textarea").removeClass("verror");
				});
			 	
			 	$("#sshots").change(function(e){
			 		var reader = new FileReader();
			 		reader. onload = function (event) {
			 		    document.getElementById("imgString").value = event.target.result.split(',')[1];
			 		};
			 		reader.readAsDataURL(document.getElementById("sshots").files[0]);
			 	});
			 	
		});
		
		function buildTicketParam(form){
			var media = currentRow.parents('tr').find('td:eq(5)')[0].innerText;
			var businessunit = currentRow.parents('tr').find('td:eq(6)')[0].innerText;
			var subBusinessUnit = currentRow.parents('tr').find('td:eq(7)')[0].innerText;
			var ixnId = currentRow.parents('tr').find('td:eq(8)')[0].innerText;
			var sdesc = form.sdesc.value;
			var ldesc = form.ldesc.value;
			var env = form.env.value;
			var sshots = form.imgString.value ;
			var ixnId = $.parseHTML(currentRow.parents('tr').find('td:eq(0)')[0].innerHTML);
			var interaction_id = currentRow.parents('tr').find('td:eq(8)')[0].innerText;
			var currentdate = new Date(); 
			var datetime = currentdate.getFullYear()+"-"+(currentdate.getMonth()+1)+"-"+currentdate.getDate()+" "+currentdate.getHours()+":"+currentdate.getMinutes()+":"+currentdate.getSeconds();
			var requestBody = {
					  "u_interaction_id":interaction_id ,
					  "u_agent_name": agentName,
					  "u_environment": env,
					  "u_businessunit": businessunit,
					  "u_subbusinessunit": subBusinessUnit,
					  "short_description": sdesc,
					  "description": ldesc,
					  "u_defect_category": "WWE",
					  "u_mediatype": media,
					  "state": "2",
					  "impact": "2",
					  "assignment_group": "WWE",
					  "u_screenshot": sshots,
					  "u_datetime":datetime,
					};
			console.log(requestBody);
			
			createTicket(requestBody,ixnId[1].value);
		}
		
		function getInitData() {
			if(interactionDataTable.rows().count()>0){
				interactionDataTable.clear().draw();
			}
			
			$.ajax({
				url : "GetInteractions",
				type : "POST",
				dataType : 'json',
				data:{"agentId": agentName},
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
			var template = $("#template").html();
			var hiddenTemplate = $("#ixnIdtemplate").html();
			var ixnIdTemplate;
			var allRow = [];
			var len = data.length;
			for (var i = 0; i < len; i++) {
				if(!isEmpty(data[i])) {
					ixnIdTemplate = hiddenTemplate;
					ixnIdTemplate = ixnIdTemplate.replace("{ixnId}",data[i].ixnId);
					singleRow = [];
					singleRow.push(ixnIdTemplate);
					singleRow.push(data[i].ENT_ContactFirstNm);
					singleRow.push(data[i].ENT_ContactLastNm);
					singleRow.push(data[i].ENT_SubFunction);
					singleRow.push(data[i].Subject);
					singleRow.push(data[i].media);
					singleRow.push(data[i].ENT_Segment);
					singleRow.push(data[i].ENT_Function);
					singleRow.push(data[i].interactionUUID);
					singleRow.push(data[i].IxnTime);
					singleRow.push(template);
					allRow.push(singleRow);
				}
			}
			if (allRow.length > 0) {
				interactionDataTable.rows.add(allRow).draw();
			}else{
				//logInfo("No interaction record found !");
			}
		}
		
		function updateDataTable(data) {
			var template = $("#template").html();
			var hiddenTemplate = ($("#ixnIdtemplate").clone()).html();
			hiddenTemplate = hiddenTemplate.replace("{ixnId}",data.ixnId);		
			interactionDataTable.row.add([
				hiddenTemplate,
				data.ENT_ContactFirstNm,
				data.ENT_ContactLastNm,
				data.ENT_SubFunction,
				data.Subject,
				data.media,
				data.interactionUUID,
				template
			]).draw();
		}
		
		function createTicket(requestBody,ixnId){
			$.ajax({
					url : "CreateIncident",
					type : "POST",
					data:{
						"incident":JSON.stringify(requestBody),
						"ixnId":ixnId,
						"mediaType":requestBody.u_mediatype,
						"interactionId":requestBody.u_interaction_id
					},
					dataType : 'json',
					success : function(data) {
						console.log(data);
						if(data.state=="success"){
							interactionDataTable.row( currentRow.parents('tr') )
					        .remove()
					        .draw();
							var length = interactionDataTable.rows().data().length;
							$(".dataTables_empty").closest("td").addClass("nobefore");
							$('#ticketModal').modal("toggle");
							logSuccess(data.message);
							moveLogFiles(requestBody.u_interaction_id,requestBody.u_mediatype);
						}else{
							logError(data.message);
						}
					},
					error : function(data) {
						logError(data);
					}
				});
		
		}
		
		function moveLogFiles(interactionId,mediaType){
			$.ajax({
				url : "MoveLogFiles",
				type : "POST",
				global: false,
				data:{
					"mediaType":mediaType,
					"interactionId":interactionId
				},
				success : function(data) {
					console.log(data);
				},
				error : function(data) {
					console.log(data);
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