package uk.ac.dundee.computing.aec.jBloggyAppy.Connectors;
import static me.prettyprint.cassandra.utils.StringUtils.string;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import me.prettyprint.cassandra.service.CassandraClient;
import me.prettyprint.cassandra.service.CassandraClientPool;
import me.prettyprint.cassandra.service.CassandraClientPoolFactory;
import me.prettyprint.cassandra.service.Keyspace;
import me.prettyprint.cassandra.service.PoolExhaustedException;

import org.apache.cassandra.thrift.*;

import java.util.List;
import java.util.Map;

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
	
	

}
