<%@include file="LogCheck.jsp" %>
<%
if (User.isloggedIn()==true){
%>
<h2>Logged in as <%=User.getname() %></h2>
<%
}
%>