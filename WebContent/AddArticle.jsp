<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Add A New Article</title>
</head>
<body>
<%@include file="LogCheck.jsp" %>
<h2>Add Article for <%=User.getname() %>(<%=User.getemail() %>)</h2>
<form action="/jBloggyAppy/Article" method="POST">


Title:<input name="Title"></input><br/>
Body:<textarea name="Body" rows="2" cols="80"></textarea><br/>
Tags:<input name="Tags"></input><br/>
 
<input type="submit"  value="Add Article">
</form>
</body>
</html>