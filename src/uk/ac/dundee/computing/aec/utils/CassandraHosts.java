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
	
	public static String[] getHosts(){
		  if (c==null){
			  System.out.println("Creating cluster connection");
			c = HFactory.getOrCreateCluster("CassandraStarbase", Host+":9160");
		  }
			System.out.println(c.describeClusterName());
		
		   //Set <String>hosts= c.getClusterHosts(true);
			Set <CassandraHost>hosts= c.getKnownPoolHosts(false);
			
		   String sHosts[] = new String[hosts.size()];
		   Iterator<CassandraHost> it =hosts.iterator();
		   int i=0;
		   while (it.hasNext()) {
			   CassandraHost ch=it.next();
			   
		       sHosts[i]=(String)ch.getHost();
		       System.out.println("Hosts"+sHosts[i]);
		       i++;
		   }
		  
		   return sHosts;
	}
	public static Cluster getCluster(){
		System.out.println("getCluster");
		cluster = Cluster.builder()
		         .addContactPoint(Host).build();
			getHosts();
			Keyspaces.SetUpKeySpaces(cluster);
		
		
		
		return cluster;
		
	}	
	
	
}
