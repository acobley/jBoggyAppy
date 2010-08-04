<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="uk.ac.dundee.computing.aec.jBloggyAppy.Stores.*" %>
    <%@ page import="java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<%
HashMap hm = (HashMap)request.getAttribute("Authors");
if (hm==null){
 %>
	<h1>Error no Hashmap</h1>
	<% 
}else{
%>
<h2><%=hm.get("Name") %></h2>
<p>Email:<%=hm.get("Email") %> </p>
<%

String Address=(String)hm.get("Address");
if (Address !=null){
	%>
	<h3>Address</h3>
	<p><%=Address %></p>
	<% 
}
}
%>
</body>
</html>