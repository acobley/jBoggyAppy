<%@ page import="uk.ac.dundee.computing.aec.jBloggyAppy.Stores.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<!DOCTYPE html >
<html lang="en">
<head>
<link rel="stylesheet" type="text/css" href="WebContent/WEB-INF/css/style.css"/>

<title>Mr Bloggy Article</title>
</head>
<body>
<header>
<%@include file="Header.jsp" %>
<% 
System.out.println("In RenderArticle.jsp");
ArticleStore Article = (ArticleStore)request.getAttribute("Article");
%>
<h1>An Article by <%=Article.getauthor() %></h1>
</header>
<article>
<% 
System.out.println("In RenderArticle.jsp");

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
</article>
<article>
<form action="/jBloggyAppy/Comment" method="POST">

Author:<input name="Author"></input><br/>

Body:<textarea name="Comment" rows="2" cols="80"></textarea><br/>

<input type="hidden" name="Title" value="<%=Article.gettitle() %>"></input>
<input type="submit"  value="Add Comment">
</form>
</article>
<nav>
<p><a href="/jBloggyAppy/Comment/<%=Article.gettitle()%>">Get Comments</a></p>
<p><a href="/jBloggyAppy/AddArticle.jsp">Add a new Article</a></p>
<p><a href="/jBloggyAppy/Author">Return to Authors list</a></p>
</nav>
</body>
</html>