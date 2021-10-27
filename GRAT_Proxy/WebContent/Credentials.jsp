<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html >
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Credentials</title>
<link rel="stylesheet" type="text/css"
	href="resources/css/bootstrap/bootstrap.min.css">
<link rel="stylesheet" type="text/css"
	href="resources/css/dashboard.css">
<link rel="stylesheet" type="text/css"
	href="resources/fonts/font-awesome-4.6.3/css/font-awesome.min.css">
<script type="text/javascript" src="resources/js/jquery/jquery-3.3.1.js"></script>
<script type="text/javascript"
	src="resources/js/jquery/jquery.validate.min.js"></script>
<script type="text/javascript"
	src="resources/js/bootstrap/bootstrap.min.js"></script>
	<style type="text/css">
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
	.verror{
		color:red;
	}
	</style>
</head>
<body>
	<nav class="navbar navbar-dark sticky-top bg-dark flex-md-nowrap p-0">
		<a class="navbar-brand col-sm-3 col-md-2 mr-0" href="#">Support
			Automation</a>
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
						<li class="nav-item"><a class="nav-link"
							href="BusinessUnit.jsp"><span data-feather="home"></span>Business
								Unit</a></li>
						<li class="nav-item"><a class="nav-link" href="MediaType.jsp"><span
								data-feather="file"></span>Media Type</a></li>
						<li class="nav-item"><a class="nav-link active"
							href="Credentials.jsp"><span data-feather="file"></span>Credentials</a>
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
	<div
		class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pb-2 mb-3 border-bottom">
		<h1 class="h2">Credentials</h1>
		<div class="btn-toolbar mb-2 mb-md-0">
			<button class="btn btn-sm btn-outline-secondary" id="edit">
				<span><i class="fa fa-pencil" aria-hidden="true"></i></span> Edit
			</button>
		</div>
	</div>

	<div class="card">
		<div class="card-body">
			<form name="credentials" id="credentials" action="#" method="post">
				<div class="form-group">
					<label for="msname">MS Username</label> <input id="msname"
						name="msname" placeholder="Please enter your MS Username"
						type="text" required="required" class="form-control here">
				</div>
				<div class="form-group">
					<label for="mspass">MS Password</label> <input id="mspass"
						name="mspass" placeholder="Please enter MS Password" type="password"
						class="form-control here" required="required">
				</div>
				<div class="form-group">
					<label for="uuname">Unix Username</label> <input id="uuname"
						name="uuname" placeholder="Please enter Unix Username"
						type="text" class="form-control here">
				</div>
				<div class="form-group">
					<label for="upass">Unix Password</label> <input id="upass"
						name="upass" placeholder="Please enter Unix  Password" type="password"
						class="form-control here">
				</div>
				<div class="form-group">
					<label for="snowname">ServiceNow Username</label> <input
						id="snowname" name="snowname"
						placeholder="Please enter the Username" type="text"
						class="form-control here" required="required">
				</div>
				<div class="form-group">
					<label for="snowpass">ServiceNow Password</label> <input
						id="snowpass" name="snowpass"
						placeholder="Please enter the Password" type="password"
						class="form-control here" required="required">
				</div>
				<div class="form-group">
					<button name="submit" type="submit" class="btn btn-primary">Save</button>
				</div>
			</form>
		</div>
	</div>
	</main>
</body>
<script type="text/javascript">
$(document).ready(function() {
	 $("#credentials :input").prop("disabled", true);
	$(document).on({
		ajaxStart: function() { 
			$("body").addClass("loading");    
		},
		ajaxStop: function()  { 
			$("body").removeClass("loading"); 
		}    
	});
	
	$("form[name='credentials']").validate({
		errorClass : "verror",
		rules : {
			msname : {
				required: {
			        depends:function() {
			            $(this).val($.trim($(this).val()));
			            return true;
			        }
			    }
			},
			mspass : {
				required: {
			        depends:function(){
			            $(this).val($.trim($(this).val()));
			            return true;
			        }
			    }
			},
			uuname : {
				required: {
			        depends:function(){
			            $(this).val($.trim($(this).val()));
			            return true;
			        }
			    }
			},
			upass : {
				required: {
			        depends:function(){
			            $(this).val($.trim($(this).val()));
			            return true;
			        }
			    }
			},
			snowname : {
				required: {
			        depends:function(){
			            $(this).val($.trim($(this).val()));
			            return true;
			        }
			    }
			},
			snowpass : {
				required: {
			        depends:function(){
			            $(this).val($.trim($(this).val()));
			            return true;
			        }
			    }
			}
			
			
		},
		messages : {
			
		},
		submitHandler : function(form) {
			
			$.ajax({
				url : "SaveCredentials",
				method : "POST",
				data : { "msusername":form.msname.value,"mspassword":form.mspass.value,"unixusername":form.uuname.value,"unixpassword":form.upass.value,"snowusername":form.snowname.value,"snowpassword":form.snowpass.value },
				success : function(response){
					if(response=="true"){
						$("#credentials :input").prop("disabled", true);
						console.log("Success!");
					}else{
						alert("Error!");
					}
				},error : function(error){
					console.log(error);
				}
			});
		}
	});
	$("#edit").click(function(){
		$("#credentials :input").prop("disabled", false);
	});
	getData();
});
function getData(){
	$.ajax({
		url : "GetCredentials",
		method : "POST",
		dataType : 'json',
		success : function(response){
			if(!isEmpty(response)) {
				$("#msname").val(response.msusername);
				$("#mspass").val(response.mspassword);
				$("#uuname").val(response.unixusername);
				$("#upass").val(response.unixpassword);
				$("#snowname").val(response.snowusername);
				$("#snowpass").val(response.snowpassword);
			}else{
				$("#credentials :input").prop("disabled", false);
			}
		},error : function(error){
			console.log(error);
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
</script>
</html>