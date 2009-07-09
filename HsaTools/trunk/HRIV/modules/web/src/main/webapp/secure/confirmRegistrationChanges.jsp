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

<%@page
	import="javax.crypto.Cipher,javax.crypto.SecretKey,javax.crypto.spec.SecretKeySpec,javax.crypto.spec.IvParameterSpec"%>
<%@page import="org.apache.commons.codec.binary.Base64"%>
<%@page import="java.net.URLEncoder"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<!-- TODO Needs to be configurable, eg via Spring configuration. See WebApplicationContextUtils.getWebApplicationContext(getServletContext()); -->
<%
	String ssnFromWebSeal = request.getHeader("iv-user");

	byte[] preSharedKey = "ACME1234ACME1234QWERT123".getBytes();
	SecretKey aesKey = new SecretKeySpec(preSharedKey, "DESede");
	Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
	String initialVector = "vardval0";
	IvParameterSpec ivs = new IvParameterSpec(initialVector.getBytes());
	cipher.init(Cipher.ENCRYPT_MODE, aesKey, ivs);
	byte[] cipherText = cipher.doFinal(ssnFromWebSeal.getBytes());

	String cipherTextStringBase64Encoded = new String(Base64.encodeBase64(cipherText));
	String cipherTextStringBase64EncodedURLEncoded = URLEncoder.encode(cipherTextStringBase64Encoded, "ISO-8859-1");
	String url = response.encodeRedirectURL("http://hriv.vgregion.se:8080/hriv/confirmRegistrationChanges.jsf?_flowId=HRIV.registrationOnUnit-flow&hsaidentity="
			+ request.getParameter("hsaidentity") + "&iv-user=" + cipherTextStringBase64EncodedURLEncoded);
	response.sendRedirect(url);
%>
</head>
<body>
</body>
</html>