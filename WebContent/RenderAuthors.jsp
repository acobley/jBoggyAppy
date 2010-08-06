<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="uk.ac.dundee.computing.aec.jBloggyAppy.Stores.*" %>
    <%@ page import="java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Display All Authors</title>
</head>
<body>
<%
List<AuthorStore> Authors = (List<AuthorStore>)request.getAttribute("Authors");
if (Authors==null){
 %>
	<h1>Error no Hashmap</h1>
	<% 
}else{
%>
<h1>Authors</h1>
<p><a href="/jBloggyAppy/Post">List all Posts</a></p>
<% 
Iterator<AuthorStore> iterator;


iterator = Authors.iterator();     
while (iterator.hasNext()){
	AuthorStore md = (AuthorStore)iterator.next();
	
	%>
	<a href="/jBloggyAppy/Author/<%=md.getname()%>"><%=md.getname() %></a> &nbsp; <%=md.getemailName() %>  <br/>
	<% 
 
}
%>
<% 
}
%>
</body>
</html>