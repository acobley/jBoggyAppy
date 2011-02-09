<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
     <%@ page import="uk.ac.dundee.computing.aec.jBloggyAppy.Stores.*" %>
    <%@ page import="java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Subscriptions</title>
</head>
<body>

<%
List<SubscriptionStore> Subscriptions = (List<SubscriptionStore>)request.getAttribute("Subscriptions");

if (Subscriptions==null){
 %>
	<h1>Subscriptions for </h1>
	<% 
}else{
%>
<h1>All Subscriptions</h1>
<% 
Iterator<SubscriptionStore> iterator;


iterator = Subscriptions.iterator();     
while (iterator.hasNext()){
	SubscriptionStore Subscription = (SubscriptionStore)iterator.next();
	%>
	<a href="/jBloggyAppy/Tag/<%=Subscription.gettag()%>"><%=Subscription.gettag()%></a><br/>
<% }
}
%>
<p></p><a href="/jBloggyAppy/Subscription">Return to Subscriptions list</a></p>

</body>
</html>