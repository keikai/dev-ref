<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="kkjsp" uri="http://www.keikai.io/jsp/kk"%>
<!DOCTYPE html>
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
			width="550px" height="400px" hiderowhead="true" hidecolumnhead="true"
			maxVisibleRows="13" maxVisibleColumns="8"/>
	</div>
	<div style="text-align: right; width: 550px">
        <button id="reset">Reset</button>
        <button id="submit">Submit</button>
	</div>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/app4l.js"></script>
</body>
</html>
