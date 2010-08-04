package uk.ac.dundee.computing.aec.jBloggyAppy;

import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;


import uk.ac.dundee.computing.aec.jBloggyAppy.Connectors.*;
/**
 * Servlet implementation class Author
 */
public class Author extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public Author() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String args[]=SplitRequestPath(request);
		//Lets just get all Authors for now
		AuthorConnector au = new AuthorConnector();
		au.setHost("134.36.36.151");
		HashMap hm = au.getAuthors();
		request.setAttribute("Authors", hm);
		RequestDispatcher rd=request.getRequestDispatcher("RenderAuthors.jsp");
		rd.forward(request,response);
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
	
	private String[] SplitRequestPath(HttpServletRequest request){
		String args[]= new String[10];
		
		StringTokenizer st = SplitString(request.getRequestURI());
		//Lets assume the number is the last argument
		
		int argv=0;
		while (st.hasMoreTokens ()) {
			args[argv]=new String();
			args[argv]=st.nextToken ();
			argv++;
			} 

	//so now they'll be in the args array.  
	// argv[0] should be the user directory
	// argv[1] should be the action, either fault or data  if fault return JASON
	// argv[2] should be a value or TYPE
		return args;
		}
		
	  private StringTokenizer SplitString(String str){
	  		return new StringTokenizer (str,"/");

	  }

}
