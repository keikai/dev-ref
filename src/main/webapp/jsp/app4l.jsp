<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="kkjsp" uri="http://www.keikai.io/jsp/kk"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
		<title>Application for Leave</title>
		<kkjsp:head/>
	</head>
<%
	//prevent page cache in browser side
	response.setHeader("Pragma", "no-cache"); 
	response.setHeader("Cache-Control", "no-store, no-cache"); 
%>
<body>
	<div>
		<kkjsp:spreadsheet id="myzss"
			bookProvider="io.keikai.devref.jsp.DemoBookProvider"
			width="700px" height="500px"
			maxVisibleRows="13" maxVisibleColumns="8"/>
	</div>
	<button id="resetBtn">Reset</button>
	<button id="checkBtn">Submit</button>
	<script type="text/javascript">
	//jq is jquery name in zk
	jq(document).ready(function(){
		//register client event on button by jquery api 
		jq("#checkBtn").click(function(){
			postAjax("check");
		});
		jq("#resetBtn").click(function(){
			postAjax("reset");
		});
	});
	
	function postAjax(action){
		//get the necessary zk ids form zssjsp[component_id]
		//'myzss' is the sparedhseet id that you gaved in sparedsheet tag 
		var desktopId = zssjsp['myzss'].desktopId;
		var zssUuid = zssjsp['myzss'].uuid;
		
		
		//use jquery api to post ajax to your servlet (in this demo, it is AjaxBookServlet),
		//provide desktop id and spreadsheet uuid to access zk component data in your servlet 
		jq.ajax({url:"app4l",//the servlet url to handle ajax request 
			data:{desktopId:desktopId,zssUuid:zssUuid,action:action},
			type:'POST',dataType:'json'}).done(handleAjaxResult);
	}
	
	//the method to handle ajax result from your servlet 
	function handleAjaxResult(result){
		//process the json result that contains zk client update information 
		zssjsp.processJson(result);
		
		//use your way to hanlde you ajax message or error 
		if(result.message){
			alert(result.message);
		};
		
		//use your way handle your ajax action result 
		if(result.action == "check" && result.valid){
			if(result.form){
				//create a form dynamically to submit the form data 
				var field,form = jq("<form action='submitted.jsp' method='post'/>").appendTo('body');
				for(var nm in result.form){
					field = jq("<input type='hidden' name='"+nm+"' />").appendTo(form);
					field.val(result.form[nm]);
				}
				form.submit();
			}
		};
	}
	</script>	
</body>
</html>
