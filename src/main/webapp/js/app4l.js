//ZK has a bundled jQuery
$(document).ready(function(){
    //register client event on button by jquery api
    $("#checkBtn").click(function(){
        postAjax("check");
    });
    $("#resetBtn").click(function(){
        postAjax("reset");
    });
});

//kkjsp is created by keikai
function postAjax(action) {
	//use window.fetch() API
	fetch("app4l", {
		headers: {
			'Content-Type': 'application/json',
		},
		method: 'POST',
		body: JSON.stringify(kkjsp.prepare('myzss', {action: action})) // preparing Keikai's request data
	})
		.then(function (response) {
			return response.json();
		})
		.then(kkjsp.process) // processing Keikai's response
		.then(handleAjaxResult);
}

//the method to handle ajax result from your servlet
function handleAjaxResult(result){
	//process the json result that contains zk client update information
	kkjsp.processJson(result);

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