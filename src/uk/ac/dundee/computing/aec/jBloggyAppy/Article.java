package uk.ac.dundee.computing.aec.jBloggyAppy;
import java.net.URLDecoder;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import uk.ac.dundee.computing.aec.jBloggyAppy.Connectors.ArticleConnector;
import uk.ac.dundee.computing.aec.jBloggyAppy.Connectors.AuthorConnector;
import uk.ac.dundee.computing.aec.jBloggyAppy.Stores.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Servlet implementation class Article
 */
public class Article extends HttpServlet {
	private static final long serialVersionUID = 1L;
	 private HashMap FormatsMap = new HashMap();
     
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Article() {
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
		
		// case 3
		// /jBloggyAppy/Article/title return article 
		//case 4
		// /jBloggyAppy/Article/title/rss returns article as RSS (not implemented)
		// /jBloggyAppy/Article/title/json returns article as JSON 
		// /jBloggyAppy/Article/title/xml return arti le as XML (not implemented)

		String args[]=SplitRequestPath(request);
		
		switch (args.length){
			
			
			case 3: 
					System.out.println("Args 2 is"+args[2]);
					ReturnArticle(request, response,0,args[2]);
					
					break;
			case 4: if (FormatsMap.containsKey(args[3])){ //all authors in a format
						Integer IFormat= (Integer)FormatsMap.get(args[3]);
						switch((int)IFormat.intValue()){
							case 3:ReturnArticle(request, response,3,args[2]); //Only JSON implemented for now
							break;
							default:break;
						}
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
		// TODO Auto-generated method stub
		RequestDispatcher rd;
		ArticleStore Article =new ArticleStore();
		//Article.setauthor(request.getParameter(org.apache.commons.lang.StringEscapeUtils.escapeHtml("Author")));
		HttpSession session=request.getSession();
		UserStore lc =(UserStore)session.getAttribute("User");
		if (lc==null){
			rd=request.getRequestDispatcher("RegisterUser.jsp");
			rd.forward(request,response);
		}
		Article.setauthor(lc.getname());
		Article.settitle(request.getParameter(org.apache.commons.lang.StringEscapeUtils.escapeHtml("Title")));
		Article.setbody(request.getParameter(org.apache.commons.lang.StringEscapeUtils.escapeHtml("Body")));
		Article.settags(request.getParameter(org.apache.commons.lang.StringEscapeUtils.escapeHtml("Tags")));
		
		ArticleConnector au = new ArticleConnector();
		au.setHost("134.36.36.151");
		
		
		if (au.AddArticle(Article)== true){
			ReturnArticle(request,response,0,Article.gettitle());  //Return as Jsp only
		}else{
			rd=request.getRequestDispatcher("RegisterUser.jsp");
			rd.forward(request,response);
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
	
	public void ReturnArticle(HttpServletRequest request, HttpServletResponse response,int Format,String Title) throws ServletException, IOException{
		/*  Format is one of
		 *  0 jsp
		 *  1 xml
		 *  2 rss
		 *  3 json
		 * 
		 */
		ArticleConnector atc = new ArticleConnector();
		atc.setHost("134.36.36.150");
		System.out.println("Return  Post for"+Title);
		ArticleStore Article = atc.getArticle(Title);
		switch(Format){
			case 0: request.setAttribute("Article", Article);
					
					RequestDispatcher rd=null;
					try {
						
						rd=request.getRequestDispatcher("/RenderArticle.jsp");
					
						rd.forward(request,response);
					}catch(Exception et){
						System.out.println("Can't forward to "+ rd.toString());
					}
					break;
			case 3: request.setAttribute("Data", Article);
					RequestDispatcher rdjson=request.getRequestDispatcher("/RenderJson");
					rdjson.forward(request,response);
					break;
			default: System.out.println("Invalid Format in ReturnArticle ");
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
			try{
				System.out.println("String was "+URLDecoder.decode(args[argv],"UTF-8"));
				args[argv]=URLDecoder.decode(args[argv],"UTF-8");
				
			}catch(Exception et){
				System.out.println("Bad URL Encoding"+args[argv]);
			}
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
