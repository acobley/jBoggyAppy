package uk.ac.dundee.computing.aec.utils;
import com.datastax.driver.core.*;

import java.util.Iterator;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

/**********************************************************
 * 
 * 
 * @author administrator
 *
 *Hosts are 
 * 134.36.36.83  Seed Windows 
 * 
 * 134.36.36.84 
 * 
 * 134.36.36.85  Windows 
 * 
 * 134.36.36.205 Seed Windows, no network
 *
 *
 */

public  final class CassandraHosts {
	private static Cluster cluster;
	static String Host ="134.36.36.84";
	public CassandraHosts(){
		
	}
	
	public static String getHost(){
		return (Host);
	}
	
	public static String[] getHosts(Cluster cluster){
		
		  if (cluster==null){
			  System.out.println("Creating cluster connection");
			  cluster = Cluster.builder()
				         .addContactPoint(Host).build();
		  }
			System.out.println("Cluster Name" + cluster.getClusterName());
		    Metadata mdata = cluster.getMetadata();
		    Set<Host> hosts =mdata.getAllHosts();
		    String sHosts[] = new String[hosts.size()];
			
		   Iterator<Host> it =hosts.iterator();
		   int i=0;
		   while (it.hasNext()) {
			   Host ch=it.next();
			   sHosts[i]=(String)ch.getAddress().toString();
		     
		       System.out.println("Hosts"+ch.getAddress().toString());
		       i++;
		   }
		  
		   return sHosts;
	}
	public static Cluster getCluster(){
		System.out.println("getCluster");
		cluster = Cluster.builder()
		         .addContactPoint(Host).build();
			getHosts(cluster);
			Keyspaces.SetUpKeySpaces(cluster);
		
		
		
		return cluster;
		
	}	
	
	
}
