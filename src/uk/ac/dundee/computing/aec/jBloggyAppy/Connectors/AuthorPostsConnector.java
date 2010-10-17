package uk.ac.dundee.computing.aec.jBloggyAppy.Connectors;
import static me.prettyprint.cassandra.model.HFactory.createRangeSlicesQuery;

import java.util.LinkedList;
import java.util.List;

import me.prettyprint.cassandra.model.ColumnSlice;
import me.prettyprint.cassandra.model.HColumn;
import me.prettyprint.cassandra.model.HFactory;
import me.prettyprint.cassandra.model.KeyspaceOperator;
import me.prettyprint.cassandra.model.OrderedRows;
import me.prettyprint.cassandra.model.RangeSlicesQuery;
import me.prettyprint.cassandra.model.Result;
import me.prettyprint.cassandra.model.Row;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.serializers.UUIDSerializer;
import me.prettyprint.cassandra.service.CassandraClient;
import me.prettyprint.cassandra.service.Cluster;



import uk.ac.dundee.computing.aec.jBloggyAppy.Stores.AuthorStore;
import uk.ac.dundee.computing.aec.jBloggyAppy.Stores.PostStore;
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
		}catch (Exception et){
			System.out.println("get Articles Posts Can't Connect"+et);
			return null;
		}
		System.out.println("Posts for Author "+Author);
		
		//For V2 API
		StringSerializer se = StringSerializer.get();
		UUIDSerializer ue = UUIDSerializer.get();
		try{
			
            //retrieve sample data
			KeyspaceOperator ko=null;
			try {
				ko = HFactory.createKeyspaceOperator("BloggyAppy", c);  //V2
				
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
				s.setRange("", "", false, 100); //Set the range of columns (we want them all) 
			}
			catch(Exception et){
				System.out.println("AuthorPosts RangeSlice Query"+et);
				return null;
			}
			Result<OrderedRows<String,String, String>> r2=null;
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
/*			try{
				pool.releaseClient(client);
			}catch(Exception et){
				System.out.println("Pool acn't be released");
				return null;
			}
			*/
		
		return Posts;
	}
	
	
	//This Connects to a named host.  
@Deprecated
	private CassandraClient Connect(String Host) throws IllegalStateException, Exception{
		return CassandraHosts.getClient();
        
	}
	
	
	private CassandraClient Connect() throws IllegalStateException, Exception{
		
		
        return CassandraHosts.getClient();
       
	}
	  


}
