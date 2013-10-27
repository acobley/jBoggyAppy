package uk.ac.dundee.computing.aec.jBloggyAppy;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.datastax.driver.core.Cluster;

import uk.ac.dundee.computing.aec.utils.CassandraHosts;
import uk.ac.dundee.computing.aec.utils.Convertors;

/**
 * Servlet implementation class info
 */
@WebServlet("/info")
public class info extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private HashMap FormatsMap = new HashMap();   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public info() {
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
		String args[]=Convertors.SplitRequestPath(request);
		
		switch (args.length){
		case 2: GetInfo();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
	private void GetInfo(){
		CassandraHosts hosts= new CassandraHosts();
		Cluster cluster= hosts.getCluster();
		hosts.getHosts(cluster);
	}

}
