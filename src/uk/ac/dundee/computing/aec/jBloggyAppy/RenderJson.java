package uk.ac.dundee.computing.aec.jBloggyAppy;
import org.json.*;
import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.lang.reflect.*;

/**
 * Servlet implementation class RenderJson
 */
public class RenderJson extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RenderJson() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Object temp=request.getAttribute("Data");
		Class c = temp.getClass();
		String className=c.getName();
		if (className.compareTo("java.util.LinkedList")==0){ //Deal with a linked list
			List Data = (List)request.getAttribute("Data");
			Iterator iterator;
			
			iterator = Data.iterator();     
			while (iterator.hasNext()){
				Object Value=iterator.next();
				ProcessObject(Value);
			}
			
		}else{
			Object Data=request.getAttribute("Data");
			ProcessObject(Data);
		}
	}
	
	private void ProcessObject(Object Value){
		
		
		try {
            Class c = Value.getClass();
            Method methlist[] = c.getDeclaredMethods();
            for (int i = 0; i < methlist.length; i++) {  
            	 Method m = methlist[i];
            	 //System.out.println(m.toString());
            	 String mName=m.getName();
            	
                 if (mName.startsWith("get")==true){
                	 //Class pvec[] = m.getParameterTypes(); //Get the Parameter types
	                 //for (int j = 0; j < pvec.length; j++)
	                 //   System.out.println("param #" + j + " " + pvec[j]);
	                 //System.out.println(mName+" return type = " +  m.getReturnType());
	                 Class partypes[] = new Class[0];
	                 Method meth = c.getMethod(mName, partypes);
	                
	                 Object rt= meth.invoke(Value);
	                 if (rt!=null){
	                	 System.out.println(mName+" Return "+ rt);
	                 }
                 }
            }
            
            
         }
         catch (Throwable e) {
            System.err.println(e);
         }
	}


}
