package uk.ac.dundee.computing.aec.jBloggyAppy.Connectors;


import static me.prettyprint.hector.api.factory.HFactory.createRangeSlicesQuery;
import uk.ac.dundee.computing.aec.utils.*;
import java.util.LinkedList;
import java.util.List;

import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.*;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
 
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.cassandra.serializers.StringSerializer;





import uk.ac.dundee.computing.aec.jBloggyAppy.Stores.PostStore;
import uk.ac.dundee.computing.aec.utils.MyConsistancyLevel;
public class AuthorPostsConnector {
	
	
	
	public AuthorPostsConnector(){
	
		
	}
	//Get a list of all posts by an Author
	// use _All-Authors_ for all Authors
	public List<PostStore> getAuthorPosts(String Author) 
	{
		List <PostStore> Posts =  new LinkedList<PostStore>();
		
		Cluster c; //V2
		try{
			
			c=CassandraHosts.getCluster();
			CassandraHosts.getHosts();
		}catch (Exception et){
			System.out.println("get Articles Posts Can't Connect"+et);
			return null;
		}
		System.out.println("Posts for Author "+Author);
		
		//For V2 API
		StringSerializer se = StringSerializer.get();
		
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
			RangeSlicesQuery<String,String, String> s=null;
			try{
				s=createRangeSlicesQuery(ko,se, se, se);
				
				
				s.setColumnFamily("AuthorPosts");
				s.setKeys(Author,Author); //Set the Key
				s.setRange("", "", true, 20); //Set the range of columns (we want them all) 
			}
			catch(Exception et){
				System.out.println("AuthorPosts RangeSlice Query"+et);
				return null;
			}
			QueryResult<OrderedRows<String,String, String>> r2=null;
			OrderedRows<String, String, String> rows=null;
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
			ColumnSlice<String, String> slice;
		
			 for (Row<String,String, String> row2 : rows) {
			     //Article.settitle(row2.getKey());
			      System.out.println("key "+row2.getKey());
			      try{
			    	  slice = row2.getColumnSlice();
			      }
			      catch(Exception et){
						System.out.println("AuthorPosts slice"+et);
						return null;
					}
			      try{
			      for (HColumn<String, String> column : slice.getColumns()) {
			        
			    	  	
	         		 	PostStore pStore =new PostStore();
	                    
	                	//java.util.UUID Name=toUUID(column.getName()) ;
	                	String Value=column.getValue();
	             
	                    System.out.println( "\t ==\t" + Value);
	                    pStore.settitle(Value);
	                    Posts.add(pStore);
			      }
			      }
			      catch(Exception et){
						System.out.println("AuthorPosts slice getColumns"+et);
						return null;
					}
			 }
          
		}catch (Exception et){
			System.out.println("Can't get posts "+et);
			return null;
		}finally{
		
		}

		return Posts;
	}
	


}
