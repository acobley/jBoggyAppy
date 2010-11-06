package uk.ac.dundee.computing.aec.jBloggyAppy.Connectors;

import uk.ac.dundee.computing.aec.utils.*;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.*;
import static me.prettyprint.hector.api.factory.HFactory.createRangeSlicesQuery;

import java.util.LinkedList;
import java.util.List;
import java.util.Date;
 
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
import me.prettyprint.cassandra.service.CassandraClient;
//import me.prettyprint.cassandra.service.Cluster;
import me.prettyprint.cassandra.service.*;
import me.prettyprint.hector.api.mutation.*;

 

import uk.ac.dundee.computing.aec.jBloggyAppy.Stores.ArticleStore;
import uk.ac.dundee.computing.aec.utils.Convertors;


public class ArticleConnector {
	
	
	
	public ArticleConnector(){
		
		
	}
	
	public ArticleStore getArticle(String title){
		ArticleStore Article= new ArticleStore();
		
		Cluster c; //V2
		try{
			
			c=CassandraHosts.getCluster();
			CassandraHosts.getHosts();
		}catch (Exception et){
			System.out.println("get Articles Posts Can't Connect"+et);
			return null;
		}
		ConsistencyLevelPolicy mcl = new MyConsistancyLevel();
		
		
		System.out.println("Posts for Article "+title);
		//For V2 API 0.7 build
		StringSerializer se = StringSerializer.get();
		try{
			OrderedRows<String, String, String> rows = null;
			ColumnSlice<String, String> slice=null;
			try{
			Keyspace ko = HFactory.createKeyspace("BloggyAppy", c);  //V2
			ko.setConsistencyLevelPolicy(mcl);
			//retrieve  data
			RangeSlicesQuery<String,String, String> s=createRangeSlicesQuery(ko,se, se, se);
			
			s.setColumnFamily("BlogEntries");
			//s.setColumnNames(columnNames)
			s.setKeys(title, ""); //Set the Key
			s.setRange("", "", false, 100); //Set the range of columns (we want them all) 
			QueryResult<OrderedRows<String,String, String>> r2 = s.execute();
			rows = r2.get();
			
			}catch(Exception et){
				System.out.println("Cant make Query on Article connector"+et);
				return null;
			}
		    for (Row<String,String, String> row2 : rows) {
		    	Article.settitle(row2.getKey());
		      System.out.println("key "+row2.getKey());
		      System.out.flush();
		      slice = row2.getColumnSlice();
		      
		      for (HColumn<String, String> column : slice.getColumns()) {
		        
		    	  	String Name=column.getName();
         		 	String Value=column.getValue();

         		 	if (Name.compareTo("Author")==0)
         		 		Article.setauthor(Value);
         		 	if (Name.compareTo("Body")==0)
      		 		Article.setbody(Value);
         		 	if (Name.compareTo("Tags")==0)
      		 		Article.settags(Value);
         		 	if (Name.compareTo("Slug")==0)
      		 		Article.setslug(Value);
         		 	if (Name.compareTo("pubDate")==0){
         		 		
         		 		byte[] bDate=se.toBytes(Value);
         		 	    long lDate=Convertors.byteArrayToLong(bDate);
         		 		Article.setpubDate(new Date(lDate));
         		 	}
         		 	
		          
		        
		      }
		    }
           
		}catch (Exception et){
			System.out.println("Can't get Article "+et);
			return null;
		}finally{
			/*
			try{
				
				CassandraHosts.releaseClient(client);
			}catch(Exception et){
				System.out.println("Pool can't be released");
				return null;
			}
			*/
		}    
		return Article;
	}
	
	//Title, Body and Author and are all needed
	public boolean AddArticle(ArticleStore Article){
		System.out.println("Add Article");
		if (Article.gettitle() == null){
			//If we don't have a name we can't add this post
			return false;
		}
		if (Article.getbody()==null){
			//Same with Email, all other fields are optional
			return false;
		}
		if (Article.getauthor()==null){
			//Same with Email, all other fields are optional
			return false;
		}
		System.out.println("Article conector addArticle "+Article);
		
		
		Cluster c; //V2
		try{
			
			c=CassandraHosts.getCluster();
		}catch (Exception et){
			System.out.println("get Articles Posts Can't Connect"+et);
			return false;
		}
		
		try{
			ConsistencyLevelPolicy mcl = new MyConsistancyLevel();
			Keyspace ko = HFactory.createKeyspace("BloggyAppy", c);  //V2
			ko.setConsistencyLevelPolicy(mcl);
			StringSerializer se = StringSerializer.get();
			LongSerializer le = LongSerializer.get();
			UUIDSerializer ue = UUIDSerializer.get();
			Mutator<String> m = HFactory.createMutator(ko,se);
			 String key = Article.gettitle();
	         String derivedSlug= key.replace(' ', '-');
			 
             String authorValue;
			 String columnName = "Author";
             String value = Article.getauthor();
             authorValue=value;
             m.insert(key, "BlogEntries",
            		    HFactory.createStringColumn(columnName, value));

             
             columnName = "Body";
             value = Article.getbody();
             m.insert(key, "BlogEntries",
         		    HFactory.createStringColumn(columnName, value));
             
             String Tag="_No_Tag_";
             if (Article.gettags()!=null){
            	 columnName = "Tags";
            	 value = Article.gettags(); 
            	 Tag=value;
            	 m.insert(key, "BlogEntries",
             		    HFactory.createStringColumn(columnName, value));

             }
             //The Slug is derived from the title
            	 columnName = "Slug";
            	 value = derivedSlug; 
            	 m.insert(key, "BlogEntries",
             		    HFactory.createStringColumn(columnName, value));

             //Pubdate is set here
             columnName = "pubDate";
   
             long now = System.currentTimeMillis();
	         Long lnow=new Long(now);
	         System.out.println("ArticleConnector addArticle lnow "+lnow);
	         m.insert(key, "BlogEntries",
	            		    HFactory.createColumn(columnName, now,se,le));
	             
	         // And increment the number of Posts in the Author
	         //Get the author Key and numPosts Value
	         long lValue=1;
	        
	         // Create a new V2 query
	         RangeSlicesQuery<String,String, Long> s=createRangeSlicesQuery(ko,se, se, le);
	         s.setColumnFamily("Authors");
	         s.setKeys(authorValue,authorValue); //Set the Key
	         s.setRange("numPosts", "numPosts", false, 1); //Set the range of columns (we want them all) 
	         QueryResult<OrderedRows<String,String, Long>> r2 = s.execute();
	         OrderedRows<String, String, Long> rows = r2.get();
	         ColumnSlice<String, Long> slice;
		    
	         for (Row<String,String, Long> row2 : rows) {
	        	 Article.settitle(row2.getKey());
	        	 System.out.println("key "+row2.getKey());
	        	 slice = row2.getColumnSlice();
	        	 for (HColumn<String, Long> column : slice.getColumns()) {
	        		 lValue=column.getValue().longValue();
	        		 lValue++;
	        		 System.out.println("\t" + column.getName() + "\t ==\t" +column.getValue());

	        	 }
	        }
	        //Its been incremented, put it back again now
	         columnName = "numPosts";
	         m.insert(key, "BlogEntries",
         		    HFactory.createColumn(columnName, now,se,le));
	       
        	 
        	 //Now we need to deal with the tags and authors indexes
        	 String[] Tags = Convertors.SplitTags(Tag);
        	 java.util.UUID timeUUID=Convertors.getTimeUUID();
             
             for (int i=0;i<Tags.length; i++){
             	
             	String tagKey=Tags[i];

             	m.insert(tagKey, "TaggedPosts",
             		    HFactory.createColumn(timeUUID, key,ue,se));
             	
             }
         
             m.insert(authorValue, "AuthorPosts",
          		    HFactory.createColumn(timeUUID, key,ue,se));

             
             //And do it for all others
             m.insert("_All-Authors_","AuthorPosts",
          		    HFactory.createColumn(timeUUID, key,ue,se));
 
             
             
		}catch (Exception et){
			System.out.println("Can't Create a new Article "+et);
			return false;
		}finally{
			try{
				/*
				CassandraHosts.releaseClient(client);
				*/
			}catch(Exception et){
				System.out.println("Pool acn't be released");
				return false;
			}
		}
		return true;
	}

}
