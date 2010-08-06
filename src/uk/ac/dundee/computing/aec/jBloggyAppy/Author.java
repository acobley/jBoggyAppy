package uk.ac.dundee.computing.aec.jBloggyAppy;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;


import uk.ac.dundee.computing.aec.jBloggyAppy.Connectors.*;
import uk.ac.dundee.computing.aec.jBloggyAppy.Stores.AuthorStore;
/**
 * Servlet implementation class Author
 */
public class Author extends HttpServlet {
	private static final long serialVersionUID = 1L;
	 private HashMap FormatsMap = new HashMap();
    /**
     * Default constructor. 
     */
    public Author() {
        // TODO Auto-generated constructor stub
    	 FormatsMap.put("Jsp", 0);
    	 FormatsMap.put("xml", 1);
    	 FormatsMap.put("rss", 2);
    	 FormatsMap.put("json",3);
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//Possible Call Methods:
		//case 2
		// /jBloggyAppy/Author List all Authors and redirect to jsp
		// case 3
		// /jBloggyAppy/Author/xml return all authors as XML (not implemented)
		// /jBloggyAppy/Author/rss return all authors as RSS (not implemented)
		// /jBloggyAppy/Author/json return all authors as JSON (not implemented)
		// /jBloggyAppy/Author/name return author and redirect to jsp
		//case 4
		// /jBloggyAppy/Author/name/xml return author and return xml (not implemented)
		// /jBloggyAppy/Author/name/rss return author and return rss (not implemented)
		// /jBloggyAppy/Author/name/json return author and return json (not implemented)
		
		String args[]=SplitRequestPath(request);
		
		switch (args.length){
			
			case 2:  ReturnAllAuthors(request, response,0);
					break;
			case 3: if (FormatsMap.containsKey(args[2])){ //all authors in a format
					}else{ //Must be a single Author request
						System.out.println("Call return Author");
						 ReturnAuthor(request, response,0,args[2]);
					}
					break;
			default: System.out.println("Wrong number of arguements in doGet Author "+request.getRequestURI()+" : "+args.length);
					break;
		}
		
		
		
		
	}
	
	public void ReturnAllAuthors(HttpServletRequest request, HttpServletResponse response,int Format) throws ServletException, IOException{
		/*  Format is one of
		 *  0 jsp
		 *  1 xml
		 *  2 rss
		 *  3 json
		 * 
		 */
		AuthorConnector au = new AuthorConnector();
		au.setHost("134.36.36.151");
		List<AuthorStore> Authors = au.getAuthors();
		switch(Format){
			case 0: request.setAttribute("Authors", Authors);
					RequestDispatcher rd=request.getRequestDispatcher("RenderAuthors.jsp");
					
					rd.forward(request,response);
					break;
			default: System.out.println("Invalid Format in ReturnAllAuthors ");
		}
	
	}
	
	public void ReturnAuthor(HttpServletRequest request, HttpServletResponse response,int Format, String AuthorName) throws ServletException, IOException{
		/*  Format is one of
		 *  0 jsp
		 *  1 xml
		 *  2 rss
		 *  3 json
		 * 
		 */
		AuthorConnector au = new AuthorConnector();
		au.setHost("134.36.36.151");
		AuthorStore Author = au.getAuthor(AuthorName);
		System.out.println("Got Author "+Author.getname()+" : "+Format);
		System.out.flush();
		switch(Format){
			case 0: request.setAttribute("Author", Author);
					RequestDispatcher rd=request.getRequestDispatcher("/RenderAuthor.jsp");
					//System.out.println("Added jsp to dispatcher");
					rd.forward(request,response);
					//System.out.println("We Shouldn't be here");
					break;
			default: System.out.println("Invalid Format in ReturnAllAuthors ");
		}
		
	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		AuthorStore Author =new AuthorStore();
		Author.setname(request.getParameter("Name"));
		Author.setemailName(request.getParameter("Email"));
		Author.setaddress(request.getParameter("Address"));
		Author.settwitterName(request.getParameter("Twitter"));
		Author.setbio(request.getParameter("Bio"));
		Author.settel(request.getParameter("Tel"));
		AuthorConnector au = new AuthorConnector();
		au.setHost("134.36.36.151");
		
		RequestDispatcher rd;
		if (au.AddAuthor(Author)== true){
			ReturnAllAuthors(request,response,0);  //Return as Jsp only
		}else{
			rd=request.getRequestDispatcher("RegisterUser.jsp");
		}
		
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
		String args[] = null;
		
		StringTokenizer st = SplitString(request.getRequestURI());
		args = new String[st.countTokens()];
		//Lets assume the number is the last argument
		
		int argv=0;
		while (st.hasMoreTokens ()) {;
			args[argv]=new String();
			args[argv]=st.nextToken();
			argv++;
			} 

	//so now they'll be in the args array.  
	// argv[0] should be the user directory
	
		return args;
		}
		
	  private StringTokenizer SplitString(String str){
	  		return new StringTokenizer (str,"/");

	  }

}
