package uk.ac.dundee.computing.aec.jBloggyAppy;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.List;

import uk.ac.dundee.computing.aec.jBloggyAppy.Stores.*;
import uk.ac.dundee.computing.aec.utils.*;
import uk.ac.dundee.computing.aec.jBloggyAppy.Connectors.*;
/**
 * Servlet implementation class Subscribe
 */
public class Subscribe extends HttpServlet {
	private static final long serialVersionUID = 1L;
	 private HashMap FormatsMap = new HashMap();   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Subscribe() {
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
		// /jBloggyAppy/Subscription/Author return subscriptions for Author 
		//case 4
		// /jBloggyAppy/Subscription/Author/rss returns subscriptions for Author  as RSS (not implemented)
		// /jBloggyAppy/Subscription/Author/json returns subscriptions for Author  as JSON 
		// /jBloggyAppy/Subscription/Author/xml return subscriptions for Author  as XML (not implemented)
		String args[]=Convertors.SplitRequestPath(request);
		switch (args.length){
			
			
			case 3: 
					System.out.println("Args 2 is"+args[2]);
					ReturnSubscriptions(request, response,0,args[2]);
					
					break;
			case 4: if (FormatsMap.containsKey(args[3])){ //all authors in a format
						Integer IFormat= (Integer)FormatsMap.get(args[3]);
						switch((int)IFormat.intValue()){
							case 3:ReturnSubscriptions(request, response,3,args[2]); //Only JSON implemented for now
							break;
							default:break;
						}
					}
					break;
			default: System.out.println("Wrong number of arguements in doGet Article "+request.getRequestURI()+" : "+args.length);
					break;
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		RequestDispatcher rd;
		String tag= request.getParameter(org.apache.commons.lang.StringEscapeUtils.escapeHtml("Tag"));
		System.out.println("Subscribe, doPost "+tag);
		
		HttpSession session=request.getSession();
		UserStore lc =(UserStore)session.getAttribute("User");
		if (lc==null){
			rd=request.getRequestDispatcher("RegisterUser.jsp");
			rd.forward(request,response);
		}
		System.out.println("User"+lc.getname());
		SubscriptionStore ss= new SubscriptionStore();
		ss.setauthor(lc.getname());
		ss.settag(tag);
		SubscriptionConnector sc = new SubscriptionConnector();		
		sc.addSubsciption(ss);
	}
	
	public void ReturnSubscriptions(HttpServletRequest request, HttpServletResponse response,int Format,String Author) throws ServletException, IOException{
		/*  Format is one of
		 *  0 jsp
		 *  1 xml
		 *  2 rss
		 *  3 json
		 * 
		 */
		SubscriptionConnector sc = new SubscriptionConnector();
		
		System.out.println("Return  Subscriptions for"+Author);
		List<SubscriptionStore> Subscriptions = sc.getSusbscriptions(Author);
		switch(Format){
			case 0: request.setAttribute("Subscriptions", Subscriptions);
					
					RequestDispatcher rd=null;
					try {
						
						rd=request.getRequestDispatcher("/RenderSubscriptions.jsp");
					
						rd.forward(request,response);
					}catch(Exception et){
						System.out.println("Can't forward to "+ rd.toString());
					}
					break;
			case 3: request.setAttribute("Data", Subscriptions);
					RequestDispatcher rdjson=request.getRequestDispatcher("/RenderJson");
					rdjson.forward(request,response);
					break;
			default: System.out.println("Invalid Format in ReturnSubscriptions ");
		}
	
	}

}
