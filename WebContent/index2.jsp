<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%-- 
   Document   : test
   Created on : 15-Aug-2010, 15:50:56
   Author     : phil
--%>

<html>
   <head>
       <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
       <title>JSP Page</title>
       <style type="text/css">
           #postscontainer
           {
               list-style: none;
           }
           #postscontainer li
           {
               border: 1px dashed #000;
               padding:10px;
               margin-top:10px;
               margin-bottom:10px;
               width:500px;
           }
           .postarticle {
               padding:10px;
               margin:10px;
               border:1px solid blue;
               display:none;
           }
       </style>
   </head>
   <body>
       <h1>All posts</h1>

       <ul id="postscontainer">
       </ul>
       <script src="http://code.jquery.com/jquery-1.4.2.min.js" type="text/javascript"></script>
       <script type="text/javascript">
           $(document).ready(function() {
               /* load in posts via JSON */
               $.getJSON('/jBloggyAppy/Post/json',function(data) {
                   $.each(data.Data,function(i,d) {
                       $('#postscontainer').append('<li><a href="#" rel="' + d.title + '">' + d.title + '</a></li>')
                   });
                   $('#postscontainer li a').click(function() {
                       jqobj = $('<div class="postarticle">post<br/>content<br/>goes<br/>here<br/></div>');
                       jqobj.insertAfter($(this));
                       jqobj.fadeIn();
                       $(this).unbind("click")
                       return false;
                   });
               });
           });
       </script>
   </body>
</html>