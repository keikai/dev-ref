//ZK has a bundled jQuery
$(document).ready(function(){
    //register client event on button by jquery api
    $("#submit").click(function(){
        postAjax("submit");
    });
    $("#reset").click(function(){
        postAjax("reset");
    });
});

//kkjsp is created by keikai
function postAjax(action) {
	//use window.fetch() API
	//app4l is the servlet URL
	fetch("app4l", {
		headers: {
			'Content-Type': 'application/json',
		},
		method: 'POST',
		// 'myzss' is the id specified on kkjsp tag
		body: JSON.stringify(kkjsp.prepare('myzss', {action: action})) // preparing Keikai's request data
	})
    .then(function (response) {
        return response.json();
    })
    .then(kkjsp.process) // update Keikai widget upon the server's response
    .then(handleAjaxResult); //optional post-processing
}


function handleAjaxResult(result){
	//show input validation message
	if(result.message){
		alert(result.message);
	};

	//handle your ajax response in your way
	if(result.action == "submit" && result.valid){
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