<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Insert title here</title>
</head>
<body>
<form action="SignServlet" method="post">
<label for="ssn">ssn</label>
<input name="ssn" type="text" value="${param.ssn}"/><br/>
<label for="documentArtifact">documentArtifact</label>
<input name="documentArtifact" type="text" value="${param.documentArtifact}"/><br/>
<label for="target">target</label>
<input name="target" type="text" value="${param.target}"/><br/>
<input type="submit"/>
</form>
</body>
</html>