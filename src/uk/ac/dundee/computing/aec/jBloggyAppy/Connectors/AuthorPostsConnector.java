package uk.ac.dundee.computing.aec.jBloggyAppy.Connectors;
import static me.prettyprint.cassandra.utils.StringUtils.string;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.KeyRange;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;

import me.prettyprint.cassandra.service.CassandraClient;
import me.prettyprint.cassandra.service.CassandraClientPool;
import me.prettyprint.cassandra.service.CassandraClientPoolFactory;
import me.prettyprint.cassandra.service.Keyspace;
import me.prettyprint.cassandra.service.PoolExhaustedException;

import uk.ac.dundee.computing.aec.jBloggyAppy.Stores.AuthorStore;
import uk.ac.dundee.computing.aec.jBloggyAppy.Stores.PostStore;
public class AuthorPostsConnector {
	
	String Host=null;
	CassandraClientPool pool;
	
	public AuthorPostsConnector(){
		pool = CassandraClientPoolFactory.INSTANCE.get();
		
	}
	//Get a list of all posts by an Author
	// use _All-Authors_ for all Authors
	public List<PostStore> getAuthorPosts(String Author) 
	{
		List <PostStore> Posts =  new LinkedList<PostStore>();
		
		CassandraClient client=null;
		try{
			client=Connect();
		}catch (Exception et){
			System.out.println("get Author Posts Can't Connect"+et);
			return null;
		}
		System.out.println("Posts for Author "+Author);
		try{
			Keyspace ks = client.getKeyspace("BloggyAppy");
            //retrieve sample data
            ColumnParent columnParent = new ColumnParent("AuthorPosts");
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
            columnRange.setCount(200); //Maximum we will only get 200 posts

            slicePredicate.setSlice_range(columnRange);

            //count of max retrieving keys
            KeyRange keyRange = new KeyRange(1);  //Maximum number of keys to get
            keyRange.setStart_key(Author);
            keyRange.setEnd_key(Author);
            Map<String, List<Column>> map = ks.getRangeSlices(columnParent, slicePredicate, keyRange);

            //printing keys with columns
            for (String key : map.keySet()) {
                List<Column> columns = map.get(key);
                //print key
                System.out.println("Key " +key);
                
                for (Column column : columns) {
                	PostStore pStore =new PostStore();
                    //print columns with values
                	java.util.UUID Name=toUUID(column.getName()) ;
                	String Value=string(column.getValue());
             
                    System.out.println("\t" + Name + "\t ==\t" + Value);
                    pStore.settitle(string(column.getValue()));
                    Posts.add(pStore);
                
                }
               
            }

            // This line makes sure that even if the client had failures and recovered, a correct
            // releaseClient is called, on the up to date client.
            client = ks.getClient();
            
		}catch (Exception et){
			System.out.println("Can't get Authors "+et);
			return null;
		}finally{
			try{
				pool.releaseClient(client);
			}catch(Exception et){
				System.out.println("Pool acn't be released");
				return null;
			}
		}
		return Posts;
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
		
		public static java.util.UUID toUUID( byte[] uuid )
	    {
	    long msb = 0;
	    long lsb = 0;
	    assert uuid.length == 16;
	    for (int i=0; i<8; i++)
	        msb = (msb << 8) | (uuid[i] & 0xff);
	    for (int i=8; i<16; i++)
	        lsb = (lsb << 8) | (uuid[i] & 0xff);
	    long mostSigBits = msb;
	    long leastSigBits = lsb;

	    com.eaio.uuid.UUID u = new com.eaio.uuid.UUID(msb,lsb);
	    return java.util.UUID.fromString(u.toString());
	    }
	  


}
