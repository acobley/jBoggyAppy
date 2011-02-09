<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html >
<html lang=en>
<jsp:useBean id="User"
class="uk.ac.dundee.computing.aec.jBloggyAppy.Stores.UserStore"
scope="session"
></jsp:useBean>

<jsp:useBean id="ReturnPoint"
class="uk.ac.dundee.computing.aec.jBloggyAppy.Stores.ReturnStore"
scope="session"
></jsp:useBean>
<head>

<script lang="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
	
<LINK REL=StyleSheet HREF="/jBloggyAppy/css/style.css" TYPE="text/css" MEDIA=screen>
<script type="text/javascript" src="/jBloggyAppy/Scripts/GetAuthors.js"></script>

<title>Gossip</title>
</head>
<body>
<header>
<h1 id="title">Gossip</h1>
<article >
<header id="Subscriptions"></header>
<nav id="SubscribedTags"></nav>
<nav id="SubscribedArticles"></nav>
</article>
</header>
<nav>
<h2>Authors</h2>
<div id="navloading"></div>
<div id="nav"></div>
</nav>
<article id="main">
<div id="loading"></div>
<div id="content"></div>
</article>
<nav id="Blogs">
<div id="Blogsloading"></div>
<div id="BlogsHeading"></div>


</nav>
<footer>

<%
if (User.isloggedIn()==false){
	ReturnPoint.setReturnTo(request.getRequestURI());
	%><a href="/jBloggyAppy/Login.jsp">Login or Register to add a article</a>
	
<% }else{%>
	<h2>Add Article for <%=User.getname() %>(<%=User.getemail() %>)</h2>
	<form action="/jBloggyAppy/Article" method="POST">


	Title:<input name="Title"></input><br/>
	Body:<textarea name="Body" rows="2" cols="80"></textarea><br/>
	Tags:<input name="Tags"></input><br/>
	 
	<input type="submit"  value="Add Article">
	</form>
	<% 
}%>


</footer>
</body>
</html>