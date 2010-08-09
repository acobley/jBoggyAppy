<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="uk.ac.dundee.computing.aec.jBloggyAppy.Stores.*" %>
    <%@ page import="java.util.*" %>
    <%@ page import="java.text.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Comments</title>
</head>
<body>
<% 
List<CommentStore> Comments = (List<CommentStore>)request.getAttribute("Comments");
if (Comments==null){
 %>
	<h1>Error no Comments</h1>
	<% 
}else{
%>
<h1>Comments</h1>

<% 
Iterator<CommentStore> iterator;


iterator = Comments.iterator();     
while (iterator.hasNext()){
	CommentStore comm = (CommentStore)iterator.next();
	
	%>
	<%=comm.getauthor() %><br/>
	 <%=comm.getbody() %>  <br/>
	 Date:<%
Date pubDate=comm.getpubDate();
System.out.println("RebderArticle "+pubDate);
SimpleDateFormat df = new SimpleDateFormat(); 

%>
<%=df.format(pubDate) %><br/>
 
<%
}
%>
<hr/>
<% 
}
%>
</body>
</html>