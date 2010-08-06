<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="uk.ac.dundee.computing.aec.jBloggyAppy.Stores.*" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Display Single Author</title>
</head>
<body>

<% 
System.out.println("In RenderAuthor.jsp");
AuthorStore Author = (AuthorStore)request.getAttribute("Author");
if (Author==null){
 %>
	<h1>Error no Author returned</h1>
	<% 
}else{
%>
<h1>Author</h1>
<p><a href="/jBloggyAppy/Post/<%=Author.getname()%>">All Posts by <%=Author.getname() %></a><br/>
Name:	<%=Author.getname() %><br/>
Email:	<%=Author.getemailName() %>  <br/>
Twitter:	<%=Author.gettwitterName() %>  <br/>
Bio:	<%=Author.getbio() %>  <br/>
Address:	<%=Author.getaddress() %>  <br/>
Tel:	<%=Author.gettel() %>  <br/>
NumPosts: <%=Author.getnumPosts() %>
	<% 
}
%>
<p><a href="/jBloggyAppy/Author">Return to Authors list</a></p>
</body>
</html>