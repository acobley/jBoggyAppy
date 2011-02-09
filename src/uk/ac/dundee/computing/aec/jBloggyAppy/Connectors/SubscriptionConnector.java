package uk.ac.dundee.computing.aec.jBloggyAppy.Connectors;



import java.util.List;
import java.util.LinkedList;
import java.util.UUID;

import uk.ac.dundee.computing.aec.utils.*;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.*;
import static me.prettyprint.hector.api.factory.HFactory.createRangeSlicesQuery;

import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
 
//import me.prettyprint.hector.api.beans.KeyspaceOperator;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.UUIDSerializer;

import me.prettyprint.hector.api.mutation.*;
import uk.ac.dundee.computing.aec.jBloggyAppy.Stores.PostStore;
import uk.ac.dundee.computing.aec.jBloggyAppy.Stores.SubscriptionStore;
import uk.ac.dundee.computing.aec.utils.Convertors;

public class SubscriptionConnector {
	public SubscriptionConnector(){
		
	}
	
	public boolean addSubsciption(SubscriptionStore subscription){
		Cluster c; //V2
		try{
			
			c=CassandraHosts.getCluster();
		}catch (Exception et){
			System.out.println("get Articles Posts Can't Connect"+et);
			return false;
		}
		String Author=subscription.getauthor();
		String Tag = subscription.gettag();
		StringSerializer se = StringSerializer.get();
		UUIDSerializer ue = UUIDSerializer.get();
		System.out.println("Add Subscription"+Tag);
		try{
			ConsistencyLevelPolicy mcl = new MyConsistancyLevel();
			
			Keyspace ko = HFactory.createKeyspace("BloggyAppy", c);  //V2
			Mutator<String> m = HFactory.createMutator(ko,se);
			java.util.UUID timeUUID=Convertors.getTimeUUID();
			m.insert(Author, "Subscriptions",
         		    HFactory.createColumn(timeUUID, Tag,ue,se));
			
		}finally{
			try{
				//pool.releaseClient(client);
			}catch(Exception et){
				System.out.println("Pool acn't be released");
				return false;
			}
		}
		return true;
	}
	
	public List<SubscriptionStore> getSusbscriptions(String Author){
		List<SubscriptionStore> ssl= new LinkedList<SubscriptionStore>();
		StringSerializer se = StringSerializer.get();
		UUIDSerializer ue = UUIDSerializer.get();
		Cluster c; //V2
		try{
			//retrieve sample data
			Keyspace ko=null;
			try{
				
				c=CassandraHosts.getCluster();
			}catch (Exception et){
				System.out.println("get Articles Posts Can't Connect"+et);
				return null;
			}
			try {
				ko = HFactory.createKeyspace("BloggyAppy", c);  //V2
				ConsistencyLevelPolicy mcl = new MyConsistancyLevel();
				
				ko.setConsistencyLevelPolicy(mcl);
			}catch(Exception et){
				System.out.println("AuthorPosts KeyspaceOperator");
				return null;
			}
			
			//retrieve  data
			RangeSlicesQuery<String,UUID, String> s=null;
			try{
				s=createRangeSlicesQuery(ko,se, ue, se);
				
				
				s.setColumnFamily("Subscriptions");
				s.setKeys(Author,Author); //Set the Key
				s.setRange(null, null, true, 100); //Set the range of columns (we want them all) 
			}
			catch(Exception et){
				System.out.println("TagedPosts RangeSlice Query"+et);
				return null;
			}
			QueryResult<OrderedRows<String,UUID, String>> r2=null;
			
			OrderedRows<String, UUID, String> rows=null;
			try{
				 if (s!=null){
					 r2 = s.execute();
					 
				 }else{
					 System.out.println("RangeSliceQuery is null");
					 return null;
				 }
				 if (r2!=null){
					 rows = r2.get();
				 }else{
					 System.out.println("Orderd Rows is null");
					 return null;
				 }
				 
			}catch(Exception et){
				System.out.println("AuthorPosts Result and Ordered Rows"+et);
				return null;
			}
			ColumnSlice<UUID, String> slice;
		
			 for (Row<String,UUID, String> row2 : rows) {
			     //Article.settitle(row2.getKey());
			      System.out.println("key "+row2.getKey());
			      try{
			    	  slice = row2.getColumnSlice();
			      }
			      catch(Exception et){
						System.out.println("Susbscriptions slice"+et);
						return null;
					}
			      try{
			      for (HColumn<UUID, String> column : slice.getColumns()) {
			        
			    	  	
	         		 	SubscriptionStore sStore =new SubscriptionStore();
	                    
	                	//java.util.UUID Name=toUUID(column.getName()) ;
	                	String Value=column.getValue();
	             
	                    System.out.println( "Subscription \t ==\t" + Value);
	                    sStore.settag(Value);
	                    sStore.setauthor(Author);
	                    ssl.add(sStore);
			      }
			      }
			      catch(Exception et){
						System.out.println("TagPosts slice getColumns"+et);
						return null;
					}
			 }
			
		return ssl;
	}
		finally{
			try{
				//pool.releaseClient(client);
			}catch(Exception et){
				System.out.println("Pool acn't be released");
				return null;
			}
		}
	
	}
}
