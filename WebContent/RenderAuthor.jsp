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
<p>Should put the output here</p>

	<%=Author.getname() %><br/>
	<%=Author.getemailName() %>  <br/>
	<%=Author.gettwitterName() %>  <br/>
	<%=Author.getbio() %>  <br/>
	<%=Author.getaddress() %>  <br/>
	<%=Author.gettel() %>  <br/>
	<% 
}
%>

</body>
</html>