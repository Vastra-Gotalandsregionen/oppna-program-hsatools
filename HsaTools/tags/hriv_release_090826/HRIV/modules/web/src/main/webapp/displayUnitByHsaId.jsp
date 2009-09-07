<%--

    /* Copyright 2009 Västa Götalandsregionen

      This library is free software; you can redistribute it and/or modify
      it under the terms of version 2.1 of the GNU Lesser General Public
      License as published by the Free Software Foundation.

      This library is distributed in the hope that it will be useful,
      but WITHOUT ANY WARRANTY; without even the implied warranty of
      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
      GNU Lesser General Public License for more details.

      You should have received a copy of the GNU Lesser General Public
      License along with this library; if not, write to the
      Free Software Foundation, Inc., 59 Temple Place, Suite 330,
      Boston, MA 02111-1307  USA
     */

--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<% String hsaIdentity = request.getParameter("hsaidentity");%>
<jsp:forward page="displayUnitDetails.jsf?_flowId=HRIV.Search.searchunit-flow">
   <jsp:param name="hsaidentity" value="<%=hsaIdentity %>" />
</jsp:forward>
</head>
<body>
<a href="displayUnitDetails.jsf?_flowId=HRIV.Search.searchunit-flow&hsaidentity=<%= request.getParameter("hsaidentity") %>">Get card</a>
</body>
</html>