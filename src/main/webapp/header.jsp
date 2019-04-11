<%@ page import="io.keikai.devref.Configuration" %>
<%@ page import="io.keikai.client.*" %>
<div class="header">
    ${Version.UID}
    <span>${empty param.server? Configuration.DEFAULT_KEIKAI_SERVER : 'http://'.concat(param.server)}</span>
</div>
