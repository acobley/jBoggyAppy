package uk.ac.dundee.computing.aec.jBloggyAppy.Connectors;
import static me.prettyprint.cassandra.model.HFactory.createMutator;
import static me.prettyprint.cassandra.model.HFactory.createRangeSlicesQuery;
import static me.prettyprint.cassandra.utils.StringUtils.string;

import java.util.Date;
import java.util.List;
import java.util.Map;

import me.prettyprint.cassandra.model.ColumnQuery;
import me.prettyprint.cassandra.model.ColumnSlice;
import me.prettyprint.cassandra.model.HColumn;
import me.prettyprint.cassandra.model.HFactory;
import me.prettyprint.cassandra.model.KeyspaceOperator;
import me.prettyprint.cassandra.model.Mutator;
import me.prettyprint.cassandra.model.OrderedRows;
import me.prettyprint.cassandra.model.RangeSlicesQuery;
import me.prettyprint.cassandra.model.Result;
import me.prettyprint.cassandra.model.Row;
import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.serializers.UUIDSerializer;
import me.prettyprint.cassandra.service.CassandraClient;
import me.prettyprint.cassandra.service.Cluster;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.KeyRange;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;

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
		}catch (Exception et){
			System.out.println("get Articles Posts Can't Connect"+et);
			return null;
		}
		System.out.println("Posts for Article "+title);
		//For V2 API
		StringSerializer se = StringSerializer.get();
		try{
			
			KeyspaceOperator ko = HFactory.createKeyspaceOperator("BloggyAppy", c);  //V2
			//retrieve  data
			RangeSlicesQuery<String,String, String> s=createRangeSlicesQuery(ko,se, se, se);
			s.setColumnFamily("BlogEntries");
			s.setKeys(title, ""); //Set the Key
			s.setRange("", "", false, 100); //Set the range of columns (we want them all) 
			Result<OrderedRows<String,String, String>> r2 = s.execute();
			OrderedRows<String, String, String> rows = r2.get();
			ColumnSlice<String, String> slice;
		    
		    for (Row<String,String, String> row2 : rows) {
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
		
		/* V1
		CassandraClient client=null;
		try{
			client=Connect();
		}catch (Exception et){
			System.out.println("Can't Connect"+et);
			return false;
		}
		*/
		

		Cluster c; //V2
		try{
			
			c=CassandraHosts.getCluster();
		}catch (Exception et){
			System.out.println("get Articles Posts Can't Connect"+et);
			return false;
		}
		
		try{
			KeyspaceOperator ko = HFactory.createKeyspaceOperator("BloggyAppy", c);  //V2
			StringSerializer se = StringSerializer.get();
			LongSerializer le = LongSerializer.get();
			UUIDSerializer ue = UUIDSerializer.get();
			Mutator m = createMutator(ko,se);
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
	         Result<OrderedRows<String,String, Long>> r2 = s.execute();
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
	
	
	
		
		//This Connects to a named host.  
	@Deprecated
		private CassandraClient Connect(String Host) throws IllegalStateException, Exception{
			return CassandraHosts.getClient();
	        
		}
		
		
		private CassandraClient Connect() throws IllegalStateException, Exception{
			
			
	        return CassandraHosts.getClient();
	       
		}
	
		
		  
		  

}
