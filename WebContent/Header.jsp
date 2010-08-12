<jsp:useBean id="User"
class="uk.ac.dundee.computing.aec.jBloggyAppy.Stores.UserStore"
scope="session"
></jsp:useBean>
<%
if (User.isloggedIn()==true){
%>
<h2>Logged in as <%=User.getname() %></h2>
<%
}
%>