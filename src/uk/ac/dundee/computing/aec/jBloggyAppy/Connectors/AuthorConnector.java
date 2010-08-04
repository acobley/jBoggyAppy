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
public class AuthorConnector {
	
	String Host=null;
	private HashMap hm = new HashMap(); //We'll use this to make sure we only get fields we expect from the DB
	public AuthorConnector(){
		//We use a hashmap to define the fields we are expecting
		hm.put("Email","");
		hm.put("Twitter","");
		hm.put("Address", "");
		hm.put("Bio", "");
		//We don't worry about the Name, this will be the key
		
	
	}
	
	public List<AuthorStore> getAuthors()
	{
		List<AuthorStore> Authors= new LinkedList<AuthorStore>();
		AuthorStore Au=new AuthorStore();
		CassandraClient client=null;
		
		HashMap hm = new HashMap(); //We'll use this to make sure we only get fields we expect from the DB
		hm.put("Email","");
		hm.put("Twitter","");
		hm.put("Address", "");
		hm.put("Bio", "");
		hm.put("Name", "");
		
		try{
			client=Connect();
		}catch (Exception et){
			System.out.println("Can't Connect"+et);
			return null;
		}
		
		try{
			Keyspace ks = client.getKeyspace("BloggyAppy");
            //retrieve sample data
            ColumnParent columnParent = new ColumnParent("Authors");

           

            /**
             * this effect how many columns we are want to retrieve
             * also check slicePredicate.setColumn_names(java.util.List<byte[]> column_names)
             * .setColumn_names(new ArrayList<byte[]>()); no columns retrievied at all
             */
            SliceRange columnRange = new SliceRange();
            columnRange.setCount(100);
            String start="Email";
            byte bStart[]=start.getBytes();
            columnRange.setStart(bStart);
            columnRange.setFinish(new byte[0]);
            //effect on columns order
            columnRange.setReversed(false);
           //count of max retrieving keys
            KeyRange keyRange = new KeyRange();
            keyRange.setStart_key("");
            keyRange.setEnd_key("");
            SlicePredicate slicePredicate = new SlicePredicate();
            slicePredicate.setSlice_range(columnRange);
            Map<String, List<Column>> map = ks.getRangeSlices(columnParent, slicePredicate, keyRange);

            //printing keys with columns
            for (String key : map.keySet()) {
                List<Column> columns = map.get(key);
                //print key
                Au.setname(key); //The key will be the name.
                
                //System.out.println(key);
                for (Column column : columns) {
                    //print columns with values
                	 if (hm.containsKey(column.getName())){
                		 //At this point we need to add to the list 
         		    	//hm.put(column.getName(), column.getValue());
         				
         			}
                	 
                    System.out.println("\t" + string(column.getName()) + "\t ==\t" + string(column.getValue()));
                }
            }

            // This line makes sure that even if the client had failures and recovered, a correct
            // releaseClient is called, on the up to date client.
            client = ks.getClient();

		}catch (Exception et){
			System.out.println("Can't get Authors "+et);
			return null;
		}
		return hm;
	}
	
	public void setHost(String Host){
	  this.Host=Host;	
	}
	
	//This Connects to a named host.  A servlet can use this to load balance
	private CassandraClient Connect(String Host) throws IllegalStateException, PoolExhaustedException, Exception{
		CassandraClientPool pool = CassandraClientPoolFactory.INSTANCE.get();
        CassandraClient client = pool.borrowClient(Host, 9160);
        return client;
	}
	
	//This just connects to the stored host.  This can be used so that
	//an instance of Authorconnector always goes to the same host
	private CassandraClient Connect() throws IllegalStateException, PoolExhaustedException, Exception{
		CassandraClientPool pool = CassandraClientPoolFactory.INSTANCE.get();
        CassandraClient client = pool.borrowClient(this.Host, 9160);
        return client;
	}
	

}
