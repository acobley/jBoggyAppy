package uk.ac.dundee.computing.aec.jBloggyAppy.Connectors;

import static me.prettyprint.cassandra.utils.StringUtils.string;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.*;
import static me.prettyprint.hector.api.factory.HFactory.createRangeSlicesQuery;
import static me.prettyprint.hector.api.factory.HFactory.createSliceQuery;

import java.util.UUID;


import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
 
//import me.prettyprint.hector.api.beans.KeyspaceOperator;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import me.prettyprint.hector.api.query.SliceQuery;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.serializers.UUIDSerializer;



import uk.ac.dundee.computing.aec.jBloggyAppy.Stores.PostStore;
import uk.ac.dundee.computing.aec.jBloggyAppy.Stores.TagStore;
import uk.ac.dundee.computing.aec.utils.MyConsistancyLevel;
import uk.ac.dundee.computing.aec.utils.*;
public class TagPostConnector {

	String Host=null;
	
	
	public TagPostConnector(){
	
		
	}
	//Get a list of all posts by an Author
	// use _All-Authors_ for all Authors
	public List<PostStore> getTagPosts(String Tag) 
	{
		List <PostStore> Posts =  new LinkedList<PostStore>();
		
		Cluster c; //V2
		try{
			
			c=CassandraHosts.getCluster();
			CassandraHosts.getHosts();
		}catch (Exception et){
			System.out.println("get Tag Posts Can't Connect"+et);
			return null;
		}
	
		
		//For V2 API
		StringSerializer se = StringSerializer.get();
		UUIDSerializer ue = UUIDSerializer.get();
		System.out.println("Posts for Tag "+Tag);
		try{
			//retrieve sample data
			Keyspace ko=null;
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
				
				
				s.setColumnFamily("TaggedPosts");
				s.setKeys(Tag,Tag); //Set the Key
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
						System.out.println("Tagosts slice"+et);
						return null;
					}
			      try{
			      for (HColumn<UUID, String> column : slice.getColumns()) {
			        
			    	  	
	         		 	PostStore pStore =new PostStore();
	                    
	                	//java.util.UUID Name=toUUID(column.getName()) ;
	                	String Value=column.getValue();
	             
	                    System.out.println( "\t ==\t" + Value);
	                    pStore.settitle(Value);
	                    Posts.add(pStore);
			      }
			      }
			      catch(Exception et){
						System.out.println("TagPosts slice getColumns"+et);
						return null;
					}
			 }
			
           
            
		}catch (Exception et){
			System.out.println("Can't get Authors "+et);
			return null;
		}finally{
			try{
				//pool.releaseClient(client);
			}catch(Exception et){
				System.out.println("Pool acn't be released");
				return null;
			}
		}
		return Posts;
	}
	
	
	
	public List<TagStore> getTagNames() 
	{
		
		String Tag="";
		List <TagStore> Tags =  new LinkedList<TagStore>();
		Cluster c; //V2
		try{
			
			c=CassandraHosts.getCluster();
			CassandraHosts.getHosts();
		}catch (Exception et){
			System.out.println("get Tag Posts Can't Connect"+et);
			return null;
		}
	
		
		//For V2 API
		StringSerializer se = StringSerializer.get();
		UUIDSerializer ue = UUIDSerializer.get();
		System.out.println("Posts for Tag "+Tag);
		try{
			//retrieve sample data
			Keyspace ko=null;
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
				s.setColumnFamily("TaggedPosts");
				s.setKeys(Tag,Tag); //Set the Key
				s.setRange(null, null, false, 1); //Set the range of columns (we want them all) 
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
			 for (Row<String,UUID, String> row2 : rows) {
			     
			      System.out.println("key "+row2.getKey());
			      String key=row2.getKey();
			      TagStore tStore =new TagStore();
                  
                  tStore.settag(key);
                  Tags.add(tStore);
			 }
		}catch (Exception et){
			System.out.println("Can't get tags"+et);
			return null;
		}finally{
			try{
				
			}catch(Exception et){
				System.out.println("Pool acn't be released");
				return null;
			}
		}
		return Tags;
	}
	
	
	
		
		public static java.util.UUID toUUID( byte[] uuid )
	    {
	    long msb = 0;
	    long lsb = 0;
	    assert uuid.length == 16;
	    for (int i=0; i<8; i++)
	        msb = (msb << 8) | (uuid[i] & 0xff);
	    for (int i=8; i<16; i++)
	        lsb = (lsb << 8) | (uuid[i] & 0xff);
	   

	    com.eaio.uuid.UUID u = new com.eaio.uuid.UUID(msb,lsb);
	    return java.util.UUID.fromString(u.toString());
	    }
	
	
}
