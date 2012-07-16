<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<form:form commandName="person" method="post" action="/SpringMVC/person">
<form:hidden path="id" id="id" />
<table>
	<tr>
		<td><label>Name</label></td>
		<td><form:input path="name" id="name"/></td>
	</tr>
	<tr>
		<td><label>City</label></td>
		<td><form:input path="city" id="city"/></td>
	</tr>
	<tr>
		<td><input type="submit" name="save" value="save"/></td>
	</tr>
</table>	
</form:form>
</body>
	
</html>