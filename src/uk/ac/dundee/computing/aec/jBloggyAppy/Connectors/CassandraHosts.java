package uk.ac.dundee.computing.aec.jBloggyAppy.Connectors;

import uk.ac.dundee.computing.aec.utils.MyConsistancyLevel;

import java.util.Iterator;
import java.util.Set;

import me.prettyprint.cassandra.model.ConsistencyLevelPolicy;
import me.prettyprint.cassandra.model.HFactory;
import me.prettyprint.cassandra.model.KeyspaceOperator;
import me.prettyprint.cassandra.service.Cluster;
import me.prettyprint.cassandra.service.CassandraClient;

public  final class CassandraHosts {
	static Cluster c;
	
	public CassandraHosts(){
		
	}
	
	public static String getHost(){
		return ("134.36.36.150");
	}
	
	public static String[] getHosts(){
			c = HFactory.getOrCreateCluster("MyCluster", "134.36.36.152:9160");
		   System.out.println(c.describeClusterName());

		   Set <String>hosts= c.getClusterHosts(true);
		   String sHosts[] = new String[hosts.size()];
		   Iterator it =hosts.iterator();
		   int i=0;
		   while (it.hasNext()) {
		       sHosts[i]=(String)it.next();
		       System.out.println(it.next());
		   }
		   return sHosts;
	}
	public static Cluster getCluster(){
		c = HFactory.getOrCreateCluster("MyCluster", "134.36.36.152:9160");
		KeyspaceOperator ko = HFactory.createKeyspaceOperator("BloggyAppy", c);
		
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
