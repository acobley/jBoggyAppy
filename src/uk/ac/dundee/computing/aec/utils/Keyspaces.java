package uk.ac.dundee.computing.aec.utils;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ColumnType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.*;

import java.util.ArrayList;
import java.util.List;

import me.prettyprint.cassandra.model.BasicColumnFamilyDefinition;
import me.prettyprint.cassandra.service.*;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.*;

public final class Keyspaces {

	
	
	public Keyspaces(){
		
	}
	public static void AddColumnFamilies(){
		try{
			ColumnFamilyDefinition cfDef = HFactory.createColumnFamilyDefinition("BloggyAppy", "DynCf");
		}catch(Exception et){
			
			System.out.println("Can't create ColumnFamily "+et);
		}
	}
	public static void SetUpKeySpaces(Cluster c){
		try{
			try{
				KeyspaceDefinition kd =c.describeKeyspace("BloggyAppy");
				System.out.println("Keyspace: "+kd.getName());
				System.out.println("Replication: "+kd.getReplicationFactor());
				System.out.println("Strategy: "+kd.getStrategyClass());
			}catch(Exception et){
				System.out.println("Keyspace probably doesn't exist, tryping to create it"+et);
				List<ColumnFamilyDefinition> cfs = new ArrayList<ColumnFamilyDefinition>(); 
				BasicColumnFamilyDefinition cf = new BasicColumnFamilyDefinition(); 
				cf.setName("Authors");
				cf.setKeyspaceName("BloggyAppy");
				cf.setComparatorType(ComparatorType.BYTESTYPE);
				ColumnFamilyDefinition cfDef = new ThriftCfDef(cf); 
				cfs.add(cfDef);
				cf.setName("BlogEntries");
				
				cfDef = new ThriftCfDef(cf); 
				cfs.add(cfDef);
				cf.setComparatorType(ComparatorType.TIMEUUIDTYPE);
				cf.setName("TaggedPosts");
				cfDef = new ThriftCfDef(cf); 
				cfs.add(cfDef);
				cf.setName("AuthorPosts");
				cfDef = new ThriftCfDef(cf); 
				cfs.add(cfDef);
				
				cf.setName("Comments");
				cf.setColumnType(ColumnType.SUPER);
				cf.setSubComparatorType(ComparatorType.BYTESTYPE );
				cfDef = new ThriftCfDef(cf); 
				cfs.add(cfDef);
				
				KeyspaceDefinition ks=HFactory.createKeyspaceDefinition("BloggyAppy","org.apache.cassandra.locator.SimpleStrategy", 1, cfs);
				c.addKeyspace(ks);
			}
			
			
		}catch(Exception et){
			System.out.println("Other keyspace or coulm definition error" +et);
		}
		
	}
}
