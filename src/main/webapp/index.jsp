<%@ page import="io.keikai.devref.Configuration" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no"/>
    <link href="${pageContext.request.contextPath}/resources/common.css" type="text/css" rel="stylesheet">
    <title>Main</title>
</head>
<body>
    Please visit the URL below:
    <ul>
<%
Iterator<String> pathIterator = Configuration.pathCaseMap.keySet().iterator();
while (pathIterator.hasNext()){
    String path = pathIterator.next();
%>
<li>
<a href="case/<%=path%>"><%=path%></a>
</li>
<%
}
%>
</ul>
</body>
</html>

