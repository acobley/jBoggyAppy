<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="uk.ac.dundee.computing.aec.jBloggyAppy.Stores.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Mr Bloggy Article</title>
</head>
<body>
<%@include file="Header.jsp" %>
<% 
System.out.println("In RenderArticle.jsp");
ArticleStore Article = (ArticleStore)request.getAttribute("Article");
if (Article==null){
 %>
	<h1>Error no Article returned</h1>
	<% 
}else{
%>
<h1><%=Article.gettitle() %></h1>

Author:	<%=Article.getauthor() %><br/>
Title:	<%=Article.gettitle() %>  <br/>

Body:	<%=Article.getbody() %>  <br/>
Tags:	<%=Article.gettags() %>  <br/>
Date:<%
Date pubDate=Article.getpubDate();
System.out.println("RebderArticle "+pubDate);
SimpleDateFormat df = new SimpleDateFormat(); 

%>
<%=df.format(pubDate) %><br/>

Slug: <%=Article.getslug() %>
	<% 
}
%>
<form action="/jBloggyAppy/Comment" method="POST">

Author:<input name="Author"></input><br/>

Body:<textarea name="Comment" rows="20" cols="80"></textarea><br/>

<input type="hidden" name="Title" value="<%=Article.gettitle() %>"></input>
<input type="submit"  value="Add Comment">
</form>
<p><a href="/jBloggyAppy/Comment/<%=Article.gettitle()%>">Get Comments</a></p>
<p><a href="/jBloggyAppy/AddArticle.jsp">Add a new Article</a></p>
<p><a href="/jBloggyAppy/Author">Return to Authors list</a></p>
</body>
</html>