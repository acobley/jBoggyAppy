<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html >
<html>
<head>
<meta charset=UTF-8>
<title>Gossip</title>
   <link rel="stylesheet" href="http://yui.yahooapis.com/2.8.0r4/build/reset-fonts-grids/reset-fonts-grids.css" type="text/css">

<script lang="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
<LINK REL=StyleSheet HREF="/jBloggyAppy/css/index.css" TYPE="text/css" MEDIA=screen>
<script type="text/javascript" src="/jBloggyAppy/Scripts/index.js"></script>
<jsp:useBean id="User"
class="uk.ac.dundee.computing.aec.jBloggyAppy.Stores.UserStore"
scope="session"
></jsp:useBean>
<jsp:useBean id="ReturnPoint"
class="uk.ac.dundee.computing.aec.jBloggyAppy.Stores.ReturnStore"
scope="session"
></jsp:useBean>
</head>
<body>
<div id="doc" class="yui-t2">
   <div id="hd" role="banner">
   <header id="Banner">
<hgroup>
<h1>Gossip</h1>
<h2>Where Friends Talk</h2>
<%
if (User.isloggedIn()==true){
	%>
<form  id="changeaAuthorListForm">
	</form>
	<% 
}
%>
</hgroup>

<nav id="LoginOptions">
<%
if (User.isloggedIn()==false){
	System.out.println("jsp return point"+request.getRequestURL());
	ReturnPoint.setReturnTo(request.getRequestURI());
	%>Login with:
<ul id="LoginOptions">
 <li><a href="Login/Google">Google</a></li>
  <li><a href="Login/Yahoo">Yahoo</a></li>
  
</ul>
	
<% } else { %>
<h3>Say Something</h3>
	<form  id="AddMessageForm">
	What:<input name="Title" cols="20" id="SayWhat"></input><br/>
	More:<textarea name="Body" rows="1" cols="20" id ="SayMore"></textarea><br/>
	About:<input name="Tags" cols="20" id ="SayTags"></input>
	
	</form>
	
	<form action="/jBloggyAppy/Logout" method="Get">
	<input type="submit" value="Logout"></input>
	</form>
<%} %>

</nav>


<article id="TagsListScroller">
<ul id="ticker01">

	<!-- eccetera -->
</ul>
</article>

</header>
   </div>
   <div id="bd" role="main">
	<div id="yui-main">
	<div class="yui-b"><div class="yui-g">
	<!-- YOUR DATA GOES HERE -->
	<section id="content">
<article id="articlecontent">
</article>

</section>
	</div>
</div>
	</div>
	<div class="yui-b"><!-- YOUR NAVIGATION GOES HERE -->
	
	
<article id="AuthorsMessages">
<header id="AuthorName">
<h1 id="AuthorName">
<% 
if (User.isloggedIn()==true){
	%><%=User.getname() %>
	
	<% 
}
%></h1>

</header>
<header id="ArticleHeader">
</header>
	
	
	<hr></hr>
	<article id="Tags">
<nav id="UserTags"></nav>

<nav id="SubscribedArticles"></nav>

</article>
	</div>
	
	</div>
   <div id="ft" role="contentinfo"><p>&copy; R2 Project 2010,2011</p>

</div>


</body>
</html>