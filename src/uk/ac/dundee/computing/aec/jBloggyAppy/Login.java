package uk.ac.dundee.computing.aec.jBloggyAppy;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.RequestDispatcher;

import java.io.PrintWriter;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.expressme.openid.Association;
import org.expressme.openid.Authentication;
import org.expressme.openid.Endpoint;
import org.expressme.openid.OpenIdException;
import org.expressme.openid.OpenIdManager;

import uk.ac.dundee.computing.aec.jBloggyAppy.Connectors.AuthorConnector;
import uk.ac.dundee.computing.aec.jBloggyAppy.Connectors.CassandraHosts;
import uk.ac.dundee.computing.aec.jBloggyAppy.Stores.*;
import static uk.ac.dundee.computing.aec.jBloggyAppy.Connectors.CassandraHosts.*;

/**
 * Servlet implementation class Login
 */




/**
 * heavily based on sample code by Michael Liao at:
 * http://code.google.com/p/jopenid/
 * Sample servlet using JOpenID.
 * 
 * @author Andy Cobley ()
 */
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
	
	static final long ONE_HOUR = 3600000L;
    static final long TWO_HOUR = ONE_HOUR * 2L;
    static final String ATTR_MAC = "openid_mac";
    static final String ATTR_ALIAS = "openid_alias";

    private OpenIdManager manager;
	
    public Login() {
        super();
        // TODO Auto-generated constructor stub
        
        manager = new OpenIdManager();
        //manager.setRealm("http://134.36.37.221/");
        //manager.setReturnTo("http://134.36.37.221/jBloggyAppy/Login");
       
       
        
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//Added restful 
		//Possible Call Methods:
		
		// jBloggyAppy/Login/Yahoo
		// jBloggyAppy/Login/Google
		System.out.println("URL = "+request.getRequestURL());
		System.out.println("URI = "+request.getRequestURI());
		
		System.out.println("Server = "+request.getServerName());
		System.out.println("Protocol = "+request.getProtocol());
		System.out.println("Port = "+request.getServerPort());
		
		String sRealm="http://"+request.getServerName()+":"+request.getServerPort()+"/";
		System.out.println("srealm"+sRealm);
		manager.setRealm(sRealm);
        manager.setReturnTo(sRealm+"jBloggyAppy/Login");
		
		String args[]=SplitRequestPath(request);
		String op=null;
		//op = request.getParameter("op");
		switch (args.length){
			case 3: op=args[2];
					break;
		}
		
		
	        if (op==null) {
	            // check sign on result from Google or Yahoo:
	        	try{
	        		checkNonce(request.getParameter("openid.response_nonce"));
	        	}catch (Exception et){
	        		System.out.println("Check Nonce failed "+et);
	        		response.sendRedirect("/jBloggyAppy/Login.jsp");
	        		return;
	        	}
	            // get authentication:
	            byte[] mac_key = (byte[]) request.getSession().getAttribute(ATTR_MAC);
	            String alias = (String) request.getSession().getAttribute(ATTR_ALIAS);
	            Authentication authentication = manager.getAuthentication(request, mac_key, alias);
	            if (authentication==null) {
	            	response.sendRedirect("/jBloggyAppy/Login.jsp");
	            }
	            response.setContentType("text/html; charset=UTF-8");
	            showAuthentication(request,response, authentication);
	            return;
	        }
	        if (op.equals("Google") || op.equals("Yahoo")) {
	            // redirect to Google or Yahoo sign on page:
	            Endpoint endpoint = manager.lookupEndpoint(op);
	            Association association = manager.lookupAssociation(endpoint);
	            
	            request.getSession().setAttribute(ATTR_MAC, association.getRawMacKey());
	            request.getSession().setAttribute(ATTR_ALIAS, endpoint.getAlias());
	            String url = manager.getAuthenticationUrl(endpoint, association);
	            System.out.println(url);
	            response.sendRedirect(url);
	        }
	        else {
	            throw new ServletException("Unsupported OP: " + op);
	        }
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
	void showAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication auth) {
		
		HttpSession session=request.getSession();
		UserStore lc =(UserStore)session.getAttribute("User");
		if (lc==null){
			lc= new UserStore();	
			session.setAttribute("User", lc);
			//response.sendRedirect("/nosession.jsp?Page=ChecklDapLogin");
		}
		String Email=auth.getEmail();
		lc.setloggedIn("",Email);
		System.out.println("Login "+lc.getname());
		//Check to see if user is registered,  You can login but not be registered
		AuthorConnector au = new AuthorConnector();
		
		au.setHost(CassandraHosts.getHost());
		RequestDispatcher rd=null;
		AuthorStore ars=au.getAuthorFromEmail(Email);
		if (ars.getname()== null){
			System.out.println("Not Registered");
			System.out.flush();
			rd=request.getRequestDispatcher("RegisterUser.jsp");
			try{
				rd.forward(request,response);
			}catch(Exception et){
				System.out.println("Can't forward in login servlet"+et);
			}
		}
		//Now we are logged in and registered then we can set them both
		lc.setloggedIn(ars.getname(),Email);
		System.out.println("Login "+lc.getname());
		
		//System.out.println(request.getContextPath());
		
		ReturnStore Ret = (ReturnStore)session.getAttribute("ReturnPoint");
		
		
		
		if (Ret !=null){
			System.out.println("Return to "+Ret.getreturnTo());
			rd=request.getRequestDispatcher(Ret.getreturnTo());
		}else{
			rd=request.getRequestDispatcher("/Author");
		}
		
		try{
			rd.forward(request,response);
		}catch(Exception et){
			System.out.println("Can't forward in login servlet"+et);
		}
		
		/*
        pw.print("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /><title>Test JOpenID</title></head><body><h1>You have successfully signed on!</h1>");
        pw.print("<p>Identity: " + auth.getIdentity() + "</p>");
        pw.print("<p>Email: " + auth.getEmail() + "</p>");
        pw.print("<p>Full name: " + auth.getFullname() + "</p>");
        pw.print("<p>First name: " + auth.getFirstname() + "</p>");
        pw.print("<p>Last name: " + auth.getLastname() + "</p>");
        pw.print("<p>Gender: " + auth.getGender() + "</p>");
        pw.print("<p>Language: " + auth.getLanguage() + "</p>");
        pw.print("</body></html>");
        pw.flush();
        */
    }

    void checkNonce(String nonce) {
        // check response_nonce to prevent replay-attack:
        if (nonce==null || nonce.length()<20)
            throw new OpenIdException("Verify failed.");
        // make sure the time of server is correct:
        long nonceTime = getNonceTime(nonce);
        long diff = Math.abs(System.currentTimeMillis() - nonceTime);
        if (diff > ONE_HOUR)
            throw new OpenIdException("Bad nonce time.");
        if (isNonceExist(nonce))
            throw new OpenIdException("Verify nonce failed.");
        storeNonce(nonce, nonceTime + TWO_HOUR);
    }

    // simulate a database that store all nonce:
    private Set<String> nonceDb = new HashSet<String>();

    // check if nonce is exist in database:
    boolean isNonceExist(String nonce) {
        return nonceDb.contains(nonce);
    }

    // store nonce in database:
    void storeNonce(String nonce, long expires) {
        nonceDb.add(nonce);
    }

    long getNonceTime(String nonce) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .parse(nonce.substring(0, 19) + "+0000")
                    .getTime();
        }
        catch(ParseException e) {
            throw new OpenIdException("Bad nonce time.");
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
