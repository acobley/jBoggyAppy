<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
         <%@ page import="uk.ac.dundee.computing.aec.jBloggyAppy.Stores.*" %>
    <%@ page import="java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Articles with a tag</title>
</head>
<body>


<%
List<PostStore> Posts = (List<PostStore>)request.getAttribute("Posts");
String Tag=(String)request.getAttribute("Tag");
if (Posts==null){
 %>
	<h1>Posts</h1>
	<% 
}else{
%>
<h1>Posts for <%=Tag %></h1>
<% 
Iterator<PostStore> iterator;


iterator = Posts.iterator();     
while (iterator.hasNext()){
	PostStore Post = (PostStore)iterator.next();
	%>
	<a href="/jBloggyAppy/Article/<%=Post.gettitle()%>"><%=Post.gettitle()%></a><br/>
<% }
}
%>
<p></p><a href="/jBloggyAppy/Tag">Return to Tags list</a></p>

</body>
</html>