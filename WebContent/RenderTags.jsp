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
List<TagStore> Tags = (List<TagStore>)request.getAttribute("Tags");

if (Tags==null){
 %>
	<h1>Posts</h1>
	<% 
}else{
%>
<h1>All Tags</h1>
<% 
Iterator<TagStore> iterator;


iterator = Tags.iterator();     
while (iterator.hasNext()){
	TagStore Tag = (TagStore)iterator.next();
	%>
	<a href="/jBloggyAppy/Tag/<%=Tag.gettag()%>"><%=Tag.gettag()%></a><br/>
<% }
}
%>
<p></p><a href="/jBloggyAppy/Tag">Return to Tags list</a></p>

</body>
</html>