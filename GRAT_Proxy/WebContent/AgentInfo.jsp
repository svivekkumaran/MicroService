<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Agent Information</title>
<link rel="stylesheet" type="text/css"
	href="resources/css/bootstrap/bootstrap.min.css">
<link rel="stylesheet" type="text/css"
	href="resources/css/datatable/dataTables.bootstrap4.min.css">
<script type="text/javascript" src="resources/js/jquery/jquery-3.3.1.js"></script>
<script type="text/javascript"
	src="resources/js/jquery/jquery.validate.min.js"></script>
<script type="text/javascript"
	src="resources/js/datatable/jquery.dataTables.min.js"></script>
<script type="text/javascript"
	src="resources/js/bootstrap/bootstrap.min.js"></script>
<script type="text/javascript"
	src="resources/js/datatable/dataTables.bootstrap4.min.js"></script>
<style type="text/css">
.container {
	padding-top: 50px;
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

.hide {
	display: none;
}

.container-fluid {
	padding: 40px;
}

.btn-submit {
	color: #fff;
	background-color: #17a2b8;
	border-color: #17a2b8;
}

.btn-submit:hover {
	color: #fff;
	background-color: #13889a;
	border-color: #13889a;
}

.btn-submit.focus, .btn-submit:focus {
	box-shadow: 0 0 0 0.2rem #17a2b86e;
}

.btn-submit:active {
	color: #fff;
	background-color: #13889a;
	border-color: #13889a;
}

.btn-submit:visited {
	color: #fff;
	background-color: #17a2b8;
	border-color: #17a2b8;
}

.btn-reset {
	color: #fff;
	background-color: #f35463;
	border-color: #f35463;
}

.btn-reset:hover {
	color: #fff;
	background-color: #f14253;
	border-color: #f14253;
}

.btn-reset.focus, .btn-reset:focus {
	box-shadow: 0 0 0 0.2rem #f3546373;
}

.btn-reset:active {
	color: #fff;
	background-color: #f14253;
	border-color: #f14253;
}

.btn-reset:visited {
	color: #fff;
	background-color: #f35463;
	border-color: #f35463;
}

td.nobefore:before {
	display: none;
}

.error {
	color: red;
}

.ajaxLoader {
	display: none;
	position: fixed;
	z-index: 1051;
	top: 0;
	left: 0;
	height: 100%;
	width: 100%;
	background: rgba(255, 255, 255, .8)
		url('resources/images/ajax/ajax-animation.gif') 50% 50% no-repeat;
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
	<div class="container-fluid">
		<div class="card">
			<div class="card-header">
				<ul class="nav nav-tabs card-header-tabs nav-fill">
					<li class="nav-item"><a class="nav-link active" id="bySkill"
						href="#">By Skill</a></li>
					<li class="nav-item"><a class="nav-link" id="byAgent" href="#">By
							AgentId</a></li>
				</ul>
			</div>
			<div class="card-body" id="skillBody">
				<h5 class="card-title">Agent Staffing Check</h5>
				<form name="form1" id="form1" action="#" method="post">
					<div class="row">
						<div class="col-md-4">
							<div class="form-group">
								<label for="skillName">Skill Name</label> <input id="skillName"
									name="skillName" placeholder="enter skill name..." type="text"
									required="required" class="form-control here">
							</div>
						</div>
						<div class="col-md-4">
							<div class="form-group">
								<label for="environment">Environment</label>
								<div>
									<select id="environment" name="environment" required="required"
										class="custom-select">
										<option value="">Choose</option>
										<option value="dev">Dev</option>
										<option value="stage">Stage</option>
										<option value="prod">Prod</option>
									</select>
								</div>
							</div>
						</div>
						<div class="col-md-4">
							<div class="form-group">
								<label for="skillLevel">Skill Level >=</label>
								<div>
									<select id="skillLevel" name="skillLevel" required="required"
										class="custom-select">
										<option value="">Choose</option>
										<option value="10">10</option>
										<option value="9">9</option>
										<option value="8">8</option>
										<option value="7">7</option>
										<option value="6">6</option>
										<option value="5">5</option>
										<option value="4">4</option>
										<option value="3">3</option>
										<option value="2">2</option>
										<option value="1">1</option>
										<option value="0">0</option>
									</select>
								</div>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-4">
							<div class="form-group">
								<button name="submit" type="submit" class="btn btn-primary">Submit</button>
							</div>
						</div>
					</div>
				</form>
			</div>

			<div class="card-body hide" id="idBody">
				<h5 class="card-title">Agent Info</h5>
				<form name="form2" action="#" method="post">
					<div class="row">
						<div class="col-md-6">
							<div class="form-group">
								<label for="agentId">Agent Id</label> <input id="agentId"
									name="agentId" placeholder="enter agentId..." type="text"
									required="required" class="form-control here">
							</div>
						</div>
						<div class="col-md-6">
							<div class="form-group">
								<label for="environment">Environment</label>
								<div>
									<select id="environment" name="environment" required="required"
										class="custom-select">
										<option value="">Choose</option>
										<option value="dev">Dev</option>
										<option value="stage">Stage</option>
										<option value="prod">Prod</option>
									</select>
								</div>
							</div>
						</div>

					</div>
					<div class="row">
						<div class="col-md-4">
							<div class="form-group">
								<button name="submit" type="submit" class="btn btn-primary">Submit</button>
							</div>
						</div>
					</div>
				</form>

			</div>
		</div>

	</div>

	<div class="container-fluid">
		<div class="card">
			<div class="card-header">
				<h5 class="card-title">Info</h5>
			</div>
			<div class="card-body">
				<div class="table-responsive">
					<table id="agentTable" class="table table-sm table-borderless">
						<thead>
							<tr>
								<th>SNo</th>
								<th>FirstName</th>
								<th>LastName</th>
								<th>UserName</th>
								<th>IsAgent</th>
								<th>State</th>
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
	<div id="DBIDTemplate" style="display: none;">
		<input type="hidden" name="DBID" id="DBID" value="{DBID}" />
	</div>
</body>
<script type="text/javascript">
	var agentDataTable ;
	$(document).ready(function(){
		
		agentDataTable = $('#agentTable').DataTable({
			aaSorting: [[1, 'asc']],
			"columnDefs": [
			    { "orderable": false, "targets": 0 },
			    { "searchable": false, "targets": 0 },
		    ]});
		
		$("#bySkill").click(function(){
			$('a.active').removeClass('active');
			$(this).addClass('active');
			$("#skillBody").removeClass("hide");
			$("#idBody").addClass('hide');
		});
		$("#byAgent").click(function(){
			$('a.active').removeClass('active');
			$(this).addClass('active');
			$("#skillBody").addClass("hide");
			$("#idBody").removeClass('hide');
		});
		
		$("form[name='form1']").validate({
			rules:{
				skillName:"required",
				environment:"required",
				skillLevel:"required"
			},
			messages:{
				skillName:"Required!",
				environment:"Required!",
				skillLevel:"Required!"
			},
			submitHandler : function(form) {
				agentDataTable.clear().draw();
				$(".dataTables_empty").closest("td").addClass("nobefore");
				getAgentInfo(form);
			}
		});
		$("form[name='form2']").validate({
			rules:{
				agentId:"required",
				environment:"required"
			},
			messages:{
				agentId:"Required!",
				environment:"Required!"
			},
			submitHandler : function(form) {
				agentDataTable.clear().draw();
				$(".dataTables_empty").closest("td").addClass("nobefore");
				getAgentInfobyId(form)
			}
		});
		$(".dataTables_empty").closest("td").addClass("nobefore");
		$(document).on({
			ajaxStart: function() { 
				$("body").addClass("loading");    
			},
			ajaxStop: function()  { 
				$("body").removeClass("loading"); 
			}    
		});
	});
	
	function getAgentInfo(form){
		$.ajax({
			url : "GetAgent",
			type : "POST",
			dataType : 'json',
			data:{"skillName":form.skillName.value,"skillLevel":form.skillLevel.value,"environment":form.environment.value},
			success : function(data) {
				console.log(data);
				var hiddenTemplate = $("#DBIDTemplate").html();
				var DBIDTemplate;
				var len = data.length;
				var allRow = [];
				var singleRow;
				for (var i = 0; i < len; i++) {
					if(!isEmpty(data[i])) {
						DBIDTemplate = hiddenTemplate;
						DBIDTemplate = DBIDTemplate.replace("{DBID}",data[i].DBID);
						singleRow = [];
						singleRow.push(DBIDTemplate);
						singleRow.push(data[i].FirstName);
						singleRow.push(data[i].LastName);
						singleRow.push(data[i].UserName);
						singleRow.push(data[i].IsAgent);
						singleRow.push(data[i].State);
						allRow.push(singleRow);
					}
				}
				if (allRow.length > 0) {
					agentDataTable.rows.add(allRow).draw();
				}else{
					console.log("No Records found!");
				}
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
	
	function getAgentInfobyId(form) {
		$.ajax({
			url : "GetAgentById",
			type : "POST",
			dataType : 'json',
			data : {
				"agentId" : form.agentId.value,"environment":form.environment.value
			},
			success : function(data) {
				if(!isEmpty(data)) {
				console.log(data);
				var hiddenTemplate = $("#DBIDTemplate").html();
				var DBIDTemplate;
				var len = data.length;
				DBIDTemplate = hiddenTemplate;
				DBIDTemplate = DBIDTemplate.replace("{DBID}", data.DBID);
				agentDataTable.row.add(
						[ DBIDTemplate, data.FirstName, data.LastName,
								data.UserName, data.IsAgent,
								data.State ]).draw();
				}
				
			},
			error : function(data) {
				console.log(data);
			}
		});
	}
	
	
</script>
</html>