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


import uk.ac.dundee.computing.aec.jBloggyAppy.Connectors.TagPostConnector;
import uk.ac.dundee.computing.aec.jBloggyAppy.Stores.PostStore;
import uk.ac.dundee.computing.aec.jBloggyAppy.Stores.TagStore;



/**
 * Servlet implementation class Tag
 */
public class Tag extends HttpServlet {
	private static final long serialVersionUID = 1L;
	 private HashMap FormatsMap = new HashMap();
	 private HashMap ActionMap = new HashMap();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Tag() {
        super();
        // TODO Auto-generated constructor stub FormatsMap.put("Jsp", 0);
      	ActionMap.put("Tag",1);
      	ActionMap.put("Tags",2);
        
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
		// /jBloggyAppy/Tag List all posts for default Tag and redirect to jsp
		// /jBloggyAppy/Tags List all Tags and redirect to jsp
		// case 3
		// /jBloggyAppy/Tag/xml return posts for default Tag  as XML (not implemented)
		// /jBloggyAppy/Tag/rss return posts for default Tag as RSS (not implemented)
		// /jBloggyAppy/Tag/json return posts for default Tag  as JSON 
		// /jBloggyAppy/Tags/xml return all Tags  as XML (not implemented)
		// /jBloggyAppy/Tags/rss return all Tags  as RSS (not implemented)
		// /jBloggyAppy/Tags/json return all Tags   as JSON 
		// /jBloggyAppy/Tag/name return all posts with that tag and redirect to jsp
		// case 4
		// /jBloggyAppy/Tag/name/xml return all posts with that author and as xml (not implemented)
		// /jBloggyAppy/Tag/name/rss return all posts with that author and as rss (not implemented)
		// /jBloggyAppy/Tag/name/json return all posts with that author and as json 
		
		System.out.println("Tag doGet Path"+request.getRequestURI());
		System.out.println("Tag doGet uUrl"+request.getRequestURL());
		String args[]=SplitRequestPath(request);
		Integer iAction=0;
		switch (args.length){
			
			case 2: iAction=(Integer)ActionMap.get(args[1]);	
					switch((int)iAction.intValue()){
					case 1: ReturnAllTags(request, response,0,"_No-Tag_"); //Tag
						break;
					case 2:ReturnTagNames(request,response,0); 
						break; //Get all Tag Names
					}
			case 3: iAction=(Integer)ActionMap.get(args[1]);
					switch((int)iAction.intValue()){
					case 1:
							if (FormatsMap.containsKey(args[2])){ //all tags in a format
								Integer IFormat= (Integer)FormatsMap.get(args[2]);
								ReturnAllTags(request, response,(int)IFormat.intValue(),"_No-Tag_");
							}else {// must be single user
								System.out.println("Args 2 is"+args[2]);
								ReturnAllTags(request, response,0,args[2]);
							}
							break;
					case 2: //Get all tag names as JSON
						Integer IFormat= (Integer)FormatsMap.get(args[2]);
						ReturnTagNames(request,response,(int)IFormat.intValue() );
						break;
					}
					break;
			case 4: if (FormatsMap.containsKey(args[3])){ //all authors in a format
						Integer IFormat= (Integer)FormatsMap.get(args[3]);
						switch((int)IFormat.intValue()){
							case 3:ReturnAllTags(request, response,3,args[2]); //Only JSON implemented for now
							break;
							default:break;
						}
					}		
					break;
			default: System.out.println("Wrong number of arguements in doGet Tag "+request.getRequestURI()+" : "+args.length);
					break;
		}
	}

	/**
	 * @see HttpServlet#doTag(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
	public void ReturnAllTags(HttpServletRequest request, HttpServletResponse response,int Format,String Tag) throws ServletException, IOException{
		/*  Format is one of
		 *  0 jsp
		 *  1 xml
		 *  2 rss
		 *  3 json
		 * 
		 */
		TagPostConnector aup = new TagPostConnector();
		
		System.out.println("Return All Tags for"+Tag);
		List<PostStore> Posts = aup.getTagPosts(Tag);
		switch(Format){
			case 0: request.setAttribute("Posts", Posts);
					request.setAttribute("Tag",Tag);
					RequestDispatcher rd=null;
					try {
						rd=request.getRequestDispatcher("/RenderTaggedArticles.jsp");
					
						rd.forward(request,response);
					}catch(Exception et){
						System.out.println("Can't forward to "+ rd.toString());
					}
					break;
			case 3: request.setAttribute("Data", Posts);
					RequestDispatcher rdjson=request.getRequestDispatcher("/RenderJson");
					rdjson.forward(request,response);
					break;
			default: System.out.println("Invalid Format in ReturnAllTags ");
		}
	
	}
	
	public void ReturnTagNames(HttpServletRequest request, HttpServletResponse response,int Format) throws ServletException, IOException{
		/*  Format is one of
		 *  0 jsp
		 *  1 xml
		 *  2 rss
		 *  3 json
		 * 
		 */
		TagPostConnector aup = new TagPostConnector();
	
		
		System.out.println("Return All Tags ");
		List<TagStore> Tags = aup.getTagNames();
		switch(Format){
			case 0: request.setAttribute("Tags", Tags);
					
					RequestDispatcher rd=null;
					try {
						rd=request.getRequestDispatcher("/RenderTags.jsp");
					
						rd.forward(request,response);
					}catch(Exception et){
						System.out.println("Can't forward to "+ rd.toString());
					}
					break;
			case 3: request.setAttribute("Data", Tags);
					RequestDispatcher rdjson=request.getRequestDispatcher("/RenderJson");
					rdjson.forward(request,response);
					break;
			default: System.out.println("Invalid Format in ReturnAllTags ");
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
