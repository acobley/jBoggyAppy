package uk.ac.dundee.computing.aec.jBloggyAppy.Connectors;

import java.util.LinkedList;
import java.util.List;

import uk.ac.dundee.computing.aec.jBloggyAppy.Stores.CommentStore;
import me.prettyprint.cassandra.service.CassandraClient;
import me.prettyprint.cassandra.service.CassandraClientPool;
import me.prettyprint.cassandra.service.CassandraClientPoolFactory;
import me.prettyprint.cassandra.service.Keyspace;
import me.prettyprint.cassandra.service.PoolExhaustedException;

public class CommentConnector {

	
	String Host=null;
	CassandraClientPool pool;
	
	public CommentConnector(){
		pool = CassandraClientPoolFactory.INSTANCE.get();
		
	}
	
	public List<CommentStore> getComments(String articleTitle){
		List <CommentStore> Comments =  new LinkedList<CommentStore>();
		CassandraClient client=null;
		try{
			client=Connect();
		}catch (Exception et){
			System.out.println("get Author Posts Can't Connect"+et);
			return null;
		}
		System.out.println("Comments for article"+articleTitle);
		try{
			Keyspace ks = client.getKeyspace("BloggyAppy");
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
		
		
		return Comments;
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
		
}
