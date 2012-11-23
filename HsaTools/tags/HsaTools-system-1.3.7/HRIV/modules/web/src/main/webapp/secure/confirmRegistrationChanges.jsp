<%--

    Copyright 2010 Västra Götalandsregionen

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


--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="se.vgregion.kivtools.search.util.EncryptionUtil"%>
<%@page import="se.vgregion.kivtools.util.StringUtil"%><html>
<%@page import="java.io.IOException"%>
<html>
<%@page import="java.util.Properties"%>
<html>
<%@page import="java.io.FileInputStream"%>
<html>






<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<!-- TODO Needs to be configurable, eg via Spring configuration. See WebApplicationContextUtils.getWebApplicationContext(getServletContext()); -->
<%
    String ssnFromWebSeal = request.getHeader("iv-user");

    String cipherTextStringBase64Encoded = EncryptionUtil.encrypt(ssnFromWebSeal);
    String cipherTextStringBase64EncodedURLEncoded =
            StringUtil.urlEncode(cipherTextStringBase64Encoded, "ISO-8859-1");
    String hrivHost = "hittavard.vgregion.se";

    String url =
            response.encodeRedirectURL("http://" + hrivHost
                    + "/hriv/HRIV.registrationOnUnit-flow.flow?hsaidentity="
                    + request.getParameter("hsaidentity") + "&iv-user="
                    + cipherTextStringBase64EncodedURLEncoded);
    response.sendRedirect(url);
%>
</head>
<body>
</body>
</html>