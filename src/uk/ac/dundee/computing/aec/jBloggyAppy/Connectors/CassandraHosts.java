package uk.ac.dundee.computing.aec.jBloggyAppy.Connectors;

import uk.ac.dundee.computing.aec.utils.MyConsistancyLevel;

import java.util.Iterator;
import java.util.Set;

import me.prettyprint.hector.api.ConsistencyLevelPolicy;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.*;
//import me.prettyprint.cassandra.service.Cluster;
import me.prettyprint.cassandra.service.*;
import me.prettyprint.cassandra.service.CassandraClient;

public  final class CassandraHosts {
	static Cluster c=null;
	static String Host ="134.36.36.208";
	public CassandraHosts(){
		
	}
	
	public static String getHost(){
		return (Host);
	}
	
	public static String[] getHosts(){
		  if (c==null){
			  System.out.println("Creating cluster connection");
			c = HFactory.getOrCreateCluster("MyCluster", Host+":9160");
		  }
			System.out.println(c.describeClusterName());

		   Set <String>hosts= c.getClusterHosts(true);
		   String sHosts[] = new String[hosts.size()];
		   Iterator it =hosts.iterator();
		   int i=0;
		   while (it.hasNext()) {
		       sHosts[i]=(String)it.next();
		       System.out.println(sHosts[i]);
		       i++;
		   }
		   return sHosts;
	}
	public static Cluster getCluster(){
		System.out.println("getCluster");
		c = HFactory.getOrCreateCluster("MyCluster", Host+":9160");
		Keyspace ko = HFactory.createKeyspace("Keyspace1", c);
		
		ConsistencyLevelPolicy mcl = new MyConsistancyLevel();
		  
		ko.setConsistencyLevelPolicy(mcl);
		return c;
		
	}
	
	public static CassandraClient getClient() throws IllegalStateException, Exception{
		if (c== null)
			getCluster();
		CassandraClient client = c.borrowClient();
        return client;
	}
	
	public static void releaseClient(CassandraClient client) throws IllegalStateException, Exception {
		c.releaseClient(client);
	}
	
}
