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
import me.prettyprint.cassandra.service.PoolExhaustedException;

import org.apache.cassandra.thrift.*;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import uk.ac.dundee.computing.aec.jBloggyAppy.Stores.*;

public class ArticleConnector {
	String Host=null;
	CassandraClientPool pool;
	
	public ArticleConnector(){
		pool = CassandraClientPoolFactory.INSTANCE.get();
		
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
           		 	    long lDate=byteArrayToLong(bDate);
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
				pool.releaseClient(client);
			}catch(Exception et){
				System.out.println("Pool can't be released");
				return null;
			}
		}    
		return Article;
	}
	
	//Title, Body and Author and are all needed
	public boolean AddArticle(ArticleStore Article){
		
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
             
             //Lets test the long convesion routines.
             /*
             System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
             long tempnow = System.currentTimeMillis();
             Date tempDate= new Date(tempnow);
             System.out.println("now "+tempnow);
             System.out.println("Native Date "+tempDate);
             
             //Convert to Byte Array and print
             byte btempnow[]=longToByteArray(tempnow);
             System.out.print("Byte Array ");
             displayByteArrayAsHex(btempnow);
             
             //and Convert it back again
             long converted =byteArrayToLong(btempnow);
             tempDate=new Date(converted);
             System.out.println("converted now "+converted);
             System.out.println("converted  Date "+tempDate);
             System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
             */
             
            	 long now = System.currentTimeMillis();
	             Long lnow=new Long(now);
	             System.out.println("ArticleConnector addArticle lnow "+lnow);
	             
            	
            	 columnPath.setColumn(columnName.getBytes());
            	 ks.insert(key, columnPath, longToByteArray(now));
           
             // And set the number of posts to 0
             columnName = "numPosts";
        	 Long lValue = new Long(0); 
        	 value=lValue.toString();
        	 columnPath.setColumn(columnName.getBytes());
        	 ks.insert(key, columnPath, value.getBytes());
        	 
        	 
        	 //Now we need to deal with the tags and authors indexes
        	 String[] Tags = SplitTags(Tag);
        	 java.util.UUID timeUUID=getTimeUUID();
             
             for (int i=0;i<Tags.length; i++){
             	
             	String tagKey=Tags[i];
             	tagsColumnPath.setColumn(asByteArray(timeUUID)); //This is the name of the value pair.  a time;
             	
             	ks.insert(tagKey, tagsColumnPath, key.getBytes());
             	
             }
         
             
             //Now add this post to the Authors Posts column family
             authorsColumnPath.setColumn(asByteArray(timeUUID)); 
             ks.insert(authorValue, authorsColumnPath, key.getBytes());
             
             //And do it for all others
             ks.insert("_All-Authors_", authorsColumnPath, key.getBytes());
             
             
		}catch (Exception et){
			System.out.println("Can't Create a new Article "+et);
			return false;
		}finally{
			try{
				pool.releaseClient(client);
			}catch(Exception et){
				System.out.println("Pool acn't be released");
				return false;
			}
		}
		return true;
	}
	
	
	
	public void setHost(String Host){
		  this.Host=Host;	
		}
		
		//This Connects to a named host.  A servlet can use this to load balance
		private CassandraClient Connect(String Host) throws IllegalStateException, PoolExhaustedException, Exception{
			
	        CassandraClient client = pool.borrowClient(Host, 9160);
	        return client;
		}
		
		//This just connects to the stored host.  This can be used so that
		//an instance of Authorconnector always goes to the same host
		private CassandraClient Connect() throws IllegalStateException, PoolExhaustedException, Exception{
			System.out.println("Host "+this.Host);
	        CassandraClient client = pool.borrowClient(this.Host, 9160);
	        return client;
		}
	
		//From: http://www.captain.at/howto-java-convert-binary-data.php
		public static long arr2long (byte[] arr, int start) {
			int i = 0;
			int len = 4;
			int cnt = 0;
			byte[] tmp = new byte[len];
			for (i = start; i < (start + len); i++) {
				tmp[cnt] = arr[i];
				cnt++;
			}
			long accum = 0;
			i = 0;
			for ( int shiftBy = 0; shiftBy < 32; shiftBy += 8 ) {
				accum |= ( (long)( tmp[i] & 0xff ) ) << shiftBy;
				i++;
			}
			return accum;
		}
		
		private String[] SplitTags(String Tags){
			String args[] = null;
			
			StringTokenizer st = SplitString(Tags);
			args = new String[st.countTokens()+1];  //+1 for _No_Tag_
			//Lets assume the number is the last argument
			
			int argv=0;
			while (st.hasMoreTokens ()) {;
				args[argv]=new String();
				args[argv]=st.nextToken();
				argv++;
				} 
			args[argv]= "_No-Tag_";
			return args;
			}
			
		  private StringTokenizer SplitString(String str){
		  		return new StringTokenizer (str,",");

		  }
		  
		  public static java.util.UUID getTimeUUID()
		     {
		             return java.util.UUID.fromString(new com.eaio.uuid.UUID().toString());
		     }
		  
		  public static byte[] asByteArray(java.util.UUID uuid)
		     {
			  
		         long msb = uuid.getMostSignificantBits();
		         long lsb = uuid.getLeastSignificantBits();
		         byte[] buffer = new byte[16];

		         for (int i = 0; i < 8; i++) {
		                 buffer[i] = (byte) (msb >>> 8 * (7 - i));
		         }
		         for (int i = 8; i < 16; i++) {
		                 buffer[i] = (byte) (lsb >>> 8 * (7 - i));
		         }

		         return buffer;
		     }
		  
		  private  byte[] longToByteArray(long value)
		    {
			 byte[] buffer = new byte[8]; //longs are 8 bytes I believe
			 for (int i = 7; i >= 0; i--) { //fill from the right
				 buffer[i]= (byte)(value & 0x00000000000000ff); //get the bottom byte
				 
				 //System.out.print(""+Integer.toHexString((int)buffer[i])+",");
		        value=value >>> 8; //Shift the value right 8 bits
		    }
		    return buffer;
		    }

		 private long byteArrayToLong(byte[] buffer){
			  long value=0;
			  long multiplier=1;
			  for (int i = 7; i >= 0; i--) { //get from the right
				 
				  //System.out.println(Long.toHexString(multiplier)+"\t"+Integer.toHexString((int)buffer[i]));
				  value=value+(buffer[i] & 0xff)*multiplier; // add the value * the hex mulitplier
				  multiplier=multiplier <<8;
			  }
			  return value;
		 }
		 
		 private void displayByteArrayAsHex(byte[] buffer){
			  int byteArrayLength=buffer.length;
			  for (int i = 0; i < byteArrayLength; i++) {
				  int val=(int)buffer[i];
				 // System.out.print(Integer.toHexString(val)+",");
			  }
			  
			  //System.out.println();
		 }

}
