package uk.ac.dundee.computing.aec.jBloggyAppy.Connectors;
import static me.prettyprint.cassandra.model.HFactory.createRangeSlicesQuery;
import static me.prettyprint.cassandra.utils.StringUtils.string;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.StringTokenizer;

import me.prettyprint.cassandra.service.CassandraClient;
import me.prettyprint.cassandra.service.CassandraClientPool;
import me.prettyprint.cassandra.service.CassandraClientPoolFactory;
import me.prettyprint.cassandra.service.Keyspace;


import org.apache.cassandra.thrift.*;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import uk.ac.dundee.computing.aec.jBloggyAppy.Stores.*;
import uk.ac.dundee.computing.aec.utils.*;
import me.prettyprint.cassandra.extractors.StringExtractor;
import me.prettyprint.cassandra.model.ColumnQuery;
import me.prettyprint.cassandra.model.ColumnSlice;
import me.prettyprint.cassandra.model.HColumn;
import me.prettyprint.cassandra.model.HFactory;
import me.prettyprint.cassandra.model.KeyspaceOperator;
import me.prettyprint.cassandra.model.OrderedRows;
import me.prettyprint.cassandra.model.RangeSlicesQuery;
import me.prettyprint.cassandra.model.Result;
import me.prettyprint.cassandra.model.Row;
import me.prettyprint.cassandra.service.Cluster;
public class ArticleConnector {
	
	
	
	public ArticleConnector(){
		
		
	}
	
	public ArticleStore getArticle(String title){
		ArticleStore Article= new ArticleStore();
		
		Cluster c; //V2
		try{
			
			c=CassandraHosts.getCluster();
		}catch (Exception et){
			System.out.println("get Articles Posts Can't Connect"+et);
			return null;
		}
		System.out.println("Posts for Article "+title);
		//For V2 API
		StringExtractor se = StringExtractor.get();
		try{
			
			KeyspaceOperator ko = HFactory.createKeyspaceOperator("BloggyAppy", c);  //V2
			//retrieve  data
			RangeSlicesQuery<String, String> s=createRangeSlicesQuery(ko, se, se);
			s.setColumnFamily("BlogEntries");
			s.setKeys(title, ""); //Set the Key
			s.setRange("", "", false, 100); //Set the range of columns (we want them all) 
			Result<OrderedRows<String, String>> r2 = s.execute();
			OrderedRows<String, String> rows = r2.get();
			ColumnSlice<String, String> slice;
		    
		    for (Row<String, String> row2 : rows) {
		    	Article.settitle(row2.getKey());
		      System.out.println("key "+row2.getKey());
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
         		 		
         		 		byte[] bDate=column.getValueBytes();
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
		
		CassandraClient client=null;
		try{
			client=Connect();
		}catch (Exception et){
			System.out.println("Can't Connect"+et);
			return false;
		}
		
		try{
			 Keyspace ks = client.getKeyspace("BloggyAppy");
			 ColumnPath columnPath = new ColumnPath("BlogEntries");
			 ColumnPath tagsColumnPath = new ColumnPath("TaggedPosts");
             ColumnPath authorsColumnPath = new ColumnPath("AuthorPosts");
			 String key = Article.gettitle();
			 
             String derivedSlug= key.replace(' ', '-');
			 
             String authorValue;
			 String columnName = "Author";
             String value = Article.getauthor();
             authorValue=value;
             columnPath.setColumn(columnName.getBytes());
             ks.insert(key, columnPath, value.getBytes());
            
             
             columnName = "Body";
             value = Article.getbody();
             columnPath.setColumn(columnName.getBytes());
             ks.insert(key, columnPath, value.getBytes());
             String Tag="_No_Tag_";
             if (Article.gettags()!=null){
            	 columnName = "Tags";
            	 value = Article.gettags(); 
            	 Tag=value;
            	 columnPath.setColumn(columnName.getBytes());
            	 ks.insert(key, columnPath, value.getBytes());
             }
             //The Slug is derived from the title
            	 columnName = "Slug";
            	 value = derivedSlug; 
            	 columnPath.setColumn(columnName.getBytes());
            	 ks.insert(key, columnPath, value.getBytes());
             
             //Pubdate is set here
             columnName = "pubDate";
   
            	 long now = System.currentTimeMillis();
	             Long lnow=new Long(now);
	             System.out.println("ArticleConnector addArticle lnow "+lnow);
	             
            	
            	 columnPath.setColumn(columnName.getBytes());
            	 ks.insert(key, columnPath, Convertors.longToByteArray(now));
           
             // And increment the number of Posts in the Author
            //Get the author Key and numPosts Value
            long lValue=1;
            ColumnParent authorColumnParent = new ColumnParent("Authors");	 
            SliceRange columnRange = new SliceRange();
            columnRange.setCount(1);
            String start="numPosts";
            byte bStart[]=start.getBytes();
            columnRange.setStart(bStart);
            columnRange.setFinish(bStart);

           //We only want one key
            KeyRange keyRange = new KeyRange(1);
            keyRange.setStart_key(authorValue);
            keyRange.setEnd_key(authorValue);
            SlicePredicate slicePredicate = new SlicePredicate();
            slicePredicate.setSlice_range(columnRange);
            Map<String, List<Column>> map = ks.getRangeSlices(authorColumnParent, slicePredicate, keyRange);
            for (String authorKey : map.keySet()) {
                List<Column> columns = map.get(authorKey);
                System.out.println(authorKey);
                for (Column column : columns) {
  
                	String Name=string(column.getName());
                	lValue=Convertors.byteArrayToLong(column.getValue());
                	lValue++;
                	System.out.println("\t" + string(column.getName()) + "\t ==\t" + string(column.getValue()));
                }
            }
  
           	 ColumnPath authorColumnPath = new ColumnPath("Authors");
             
             columnName = "numPosts";
             byte[] bValue=Convertors.longToByteArray(lValue);
        	 
        	 authorColumnPath.setColumn(columnName.getBytes());
        	 ks.insert(authorValue, authorColumnPath, bValue);
        	 
        	 
        	 //Now we need to deal with the tags and authors indexes
        	 String[] Tags = Convertors.SplitTags(Tag);
        	 java.util.UUID timeUUID=Convertors.getTimeUUID();
             
             for (int i=0;i<Tags.length; i++){
             	
             	String tagKey=Tags[i];
             	tagsColumnPath.setColumn(Convertors.asByteArray(timeUUID)); //This is the name of the value pair.  a time;
             	
             	ks.insert(tagKey, tagsColumnPath, key.getBytes());
             	
             }
         
             
             //Now add this post to the Authors Posts column family
             authorsColumnPath.setColumn(Convertors.asByteArray(timeUUID)); 
             ks.insert(authorValue, authorsColumnPath, key.getBytes());
             
             //And do it for all others
             ks.insert("_All-Authors_", authorsColumnPath, key.getBytes());
             
             
		}catch (Exception et){
			System.out.println("Can't Create a new Article "+et);
			return false;
		}finally{
			try{
				CassandraHosts.releaseClient(client);
			}catch(Exception et){
				System.out.println("Pool acn't be released");
				return false;
			}
		}
		return true;
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
