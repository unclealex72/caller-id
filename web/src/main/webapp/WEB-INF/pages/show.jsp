<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<body>
	<h1>List Of People</h1>
	<table>
	<tr>
		<th>Name</th>
		<th>City</th>
	</tr>
	<c:forEach items="${persons}" var="person">
	<tr>
		<td>${person.name}</td>
		<td>${person.city}</td>
		<td>
			<a href="/SpringMVC/person/delete/${person.id}">DELETE</a>
		</td>
		<td>
			<a href="/SpringMVC/person/update/${person.id}">EDIT</a>
		</td>
	</tr>
	</c:forEach>
	</table>	
	<a href="/SpringMVC/person/newuser">Add New</a>
</body>
</html>