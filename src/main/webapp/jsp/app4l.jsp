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
	<button id="reset">Reset</button>
	<button id="submit">Submit</button>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/app4l.js"></script>
</body>
</html>
