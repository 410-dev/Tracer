<%@ page import="me.hysong.tracer.backend.Records" %><%--
  Created by IntelliJ IDEA.
  User: Hoyoun Song
  Date: 2023-11-01
  Time: 오후 9:22
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Records</title>
</head>
<body>
<%=Records.get().replace("\n", "<br>")%>
</body>
</html>
