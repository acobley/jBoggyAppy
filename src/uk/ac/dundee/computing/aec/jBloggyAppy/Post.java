package uk.ac.dundee.computing.aec.jBloggyAppy;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.dundee.computing.aec.jBloggyAppy.Connectors.AuthorPostsConnector;
import uk.ac.dundee.computing.aec.jBloggyAppy.Stores.AuthorStore;

/**
 * Servlet implementation class Post
 */
public class Post extends HttpServlet {
	private static final long serialVersionUID = 1L;
	 private HashMap FormatsMap = new HashMap();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Post() {
        super();
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
		// /jBloggyAppy/Post List all Posts and redirect to jsp
		// case 3
		// /jBloggyAppy/Post/xml return all posts as XML (not implemented)
		// /jBloggyAppy/Post/rss return all posts as RSS (not implemented)
		// /jBloggyAppy/Post/json return all posts as JSON (not implemented)
		// /jBloggyAppy/Post/name return author and redirect to jsp
		
		System.out.println("Post doGet Path"+request.getRequestURI());
		System.out.println("Post doGet uUrl"+request.getRequestURL());
		String args[]=SplitRequestPath(request);
		
		switch (args.length){
			
			case 2:  ReturnAllPosts(request, response,0,"_All-Authors_");
					break;
			case 3: if (FormatsMap.containsKey(args[2])){ //all posts in a format
						Integer IFormat= (Integer)FormatsMap.get(args[2]);
						ReturnAllPosts(request, response,(int)IFormat.intValue(),"_All-Authors_");
					}else {// must be single user
						System.out.println("Args 2 is"+args[2]);
						ReturnAllPosts(request, response,0,args[2]);
					}
					break;
			default: System.out.println("Wrong number of arguements in doGet Author "+request.getRequestURI()+" : "+args.length);
					break;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
	public void ReturnAllPosts(HttpServletRequest request, HttpServletResponse response,int Format,String Author) throws ServletException, IOException{
		/*  Format is one of
		 *  0 jsp
		 *  1 xml
		 *  2 rss
		 *  3 json
		 * 
		 */
		AuthorPostsConnector aup = new AuthorPostsConnector();
		aup.setHost("134.36.36.150");
		System.out.println("Return All Posts for"+Author);
		List<String> Posts = aup.getAuthorPosts(Author);
		switch(Format){
			case 0: request.setAttribute("Posts", Posts);
					request.setAttribute("Author",Author);
					RequestDispatcher rd=null;
					try {
						rd=request.getRequestDispatcher("/RenderPosts.jsp");
					
						rd.forward(request,response);
					}catch(Exception et){
						System.out.println("Can't forward to "+ rd.toString());
					}
					break;
			default: System.out.println("Invalid Format in ReturnAllPosts ");
		}
	
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
