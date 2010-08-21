package uk.ac.dundee.computing.aec.jBloggyAppy.Connectors;
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
import me.prettyprint.cassandra.model.HFactory;
import me.prettyprint.cassandra.service.Cluster;
public class ArticleConnector {
	
	
	
	public ArticleConnector(){
		
		
	}
	
	public ArticleStore getArticle(String title){
		ArticleStore Article= new ArticleStore();
		CassandraClient client=null;
		try{
			client=Connect();
		}catch (Exception et){
			System.out.println("get Articles Posts Can't Connect"+et);
			return null;
		}
		System.out.println("Posts for Article "+title);
		try{
			Keyspace ks = client.getKeyspace("BloggyAppy");
			//retrieve sample data
            ColumnParent columnParent = new ColumnParent("BlogEntries");
			SlicePredicate slicePredicate = new SlicePredicate();

            /**
             * this effect how many columns we are want to retrieve
             * also check slicePredicate.setColumn_names(java.util.List<byte[]> column_names)
             * .setColumn_names(new ArrayList<byte[]>()); no columns retrievied at all
             */
            SliceRange columnRange = new SliceRange();
            
            //For these beware of the reversed state
            //columnRange.setStart(Start.getBytes());  //Sets the first column name to get
            columnRange.setStart(new byte[0]);  //We'll get them all.
            columnRange.setFinish(new byte[0]); //Sets the last column name to get
            //effect on columns order
            columnRange.setReversed(false); //Changes order of columns returned in keyset
            columnRange.setCount(20); //Maximum number of columsn in a key

            slicePredicate.setSlice_range(columnRange);

            //count of max retrieving keys
            KeyRange keyRange = new KeyRange(1);  //Maximum number of keys to get
            keyRange.setStart_key(title);
            keyRange.setEnd_key(title);
            Map<String, List<Column>> map = ks.getRangeSlices(columnParent, slicePredicate, keyRange);

            //printing keys with columns
            for (String key : map.keySet()) {
                List<Column> columns = map.get(key);
                //print key
                Article.settitle(key);
                System.out.println(key);
                for (Column column : columns) {
                    //print columns with values
                	String Name=string(column.getName());
           		 	String Value=string(column.getValue());

           		 	if (Name.compareTo("Author")==0)
           		 		Article.setauthor(Value);
           		 	if (Name.compareTo("Body")==0)
        		 		Article.setbody(Value);
           		 	if (Name.compareTo("Tags")==0)
        		 		Article.settags(Value);
           		 	if (Name.compareTo("Slug")==0)
        		 		Article.setslug(Value);
           		 	if (Name.compareTo("pubDate")==0){
           		 		byte[] bDate=column.getValue();
           		 	    long lDate=Convertors.byteArrayToLong(bDate);
           		 		Article.setpubDate(new Date(lDate));
           		 	}
           		 	
           		 	System.out.println("\t" + string(column.getName()) + "\t ==\t" + string(column.getValue()));
           	    
           		 	//Don't forget about the Date !
                
                }
            }

            // This line makes sure that even if the client had failures and recovered, a correct
            // releaseClient is called, on the up to date client.
            client = ks.getClient();
			
			
			
			
            
		}catch (Exception et){
			System.out.println("Can't get Article "+et);
			return null;
		}finally{
			try{
				CassandraHosts.releaseClient(client);
			}catch(Exception et){
				System.out.println("Pool can't be released");
				return null;
			}
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
