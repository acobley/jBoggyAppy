<jsp:useBean id="User"
class="uk.ac.dundee.computing.aec.jBloggyAppy.Stores.UserStore"
scope="session"
></jsp:useBean>

<jsp:useBean id="ReturnPoint"
class="uk.ac.dundee.computing.aec.jBloggyAppy.Stores.ReturnStore"
scope="session"
></jsp:useBean>
<%
System.out.println("Called From "+request.getRequestURI());
System.out.println("logcheck "+User.isloggedIn());
ReturnPoint.setReturnTo(request.getRequestURI());
%>

<%
if (User.isloggedIn()==false){
System.out.println("Log check redirect to login");
response.sendRedirect("/jBloggyAppy/Login.jsp");
}
%>