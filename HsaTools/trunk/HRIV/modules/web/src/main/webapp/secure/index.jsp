<%@ page import="java.util.*" %>

<%@page import="java.security.Principal"%><html>
<head>
<title>Http Request Headers Example</title>
</head>
<body>
<h2>HTTP Request Headers Received</h2>
<table>
<%
Enumeration enumeration = request.getHeaderNames();
while (enumeration.hasMoreElements()) {
String name = (String) enumeration.nextElement();
String value = request.getHeader(name);
%>
<tr><td><%= name %></td><td><%= value %></td></tr>
<%
}
String httpRemoteUser = request.getHeader("HTTP_REMOTE_USER");
String httpRemoteUser2 = request.getRemoteUser();

Principal p = request.getUserPrincipal();
String pName = "";
if (p != null) {
	pName = p.getName();
}
%>
<tr><td>HTTP_REMOTE_USER</td><td><%= httpRemoteUser %></td></tr>
<tr><td>HTTP_REMOTE_USER via getRemoteUser</td><td><%= httpRemoteUser2 %></td></tr>
<tr><td>principal.getName()</td><td><%= pName %></td></tr>
</table>
</body>
</html>