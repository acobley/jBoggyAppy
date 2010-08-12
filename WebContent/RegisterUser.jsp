<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<h2>Add a new User</h2>
<div>
<%@include file="LogCheck.jsp" %>
<P>Fill in the form to register <%= User.getname()%></P>
<form action="/jBloggyAppy/Author" method="POST">


Address:<input name="Address"></input><br/>
Tel:<input name="Tel"></input><br/>
Emai:<input name="Email"></input><br/>
Twitter:<input name="Twitter"></input><br/>
Bio:<input name="Bio"></input><br/>
<input type="submit"  value="Add Yourself">
</form>

</div>
</body>
</html>