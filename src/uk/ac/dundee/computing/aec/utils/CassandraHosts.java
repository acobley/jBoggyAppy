package uk.ac.dundee.computing.aec.utils;

import uk.ac.dundee.computing.aec.utils.MyConsistancyLevel;

import java.util.Iterator;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

import me.prettyprint.hector.api.ConsistencyLevelPolicy;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.*;
//import me.prettyprint.cassandra.service.Cluster;
import me.prettyprint.cassandra.service.*;
import me.prettyprint.cassandra.model.BasicKeyspaceDefinition;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
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
	static Cluster c=null;
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
		
			c = HFactory.getOrCreateCluster("CassandraStarbase", Host+":9160");
			getHosts();
			Keyspaces.SetUpKeySpaces(c);
		
		
		
		return c;
		
	}	
	
	
}
