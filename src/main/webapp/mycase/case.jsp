<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no"/>
    <link rel='stylesheet' href='${pageContext.request.contextPath}/css/tutorial.css'/>
    <script async defer src="${keikaiJs}"></script>
    <title>Keikai Editor</title>
</head>
<body>
    <div class="myapp">
        <%@ include file="../header.jsp" %>
        <div id="spreadsheet" style="height: 90%" >
        </div>
    </div>
</body>
</html>
