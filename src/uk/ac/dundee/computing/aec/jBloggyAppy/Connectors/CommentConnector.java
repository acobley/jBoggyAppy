package uk.ac.dundee.computing.aec.jBloggyAppy.Connectors;

import static me.prettyprint.cassandra.utils.StringUtils.string;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.KeyRange;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.cassandra.thrift.SuperColumn;

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
		CommentStore co=null;
		CassandraClient client=null;
		try{
			client=Connect();
		}catch (Exception et){
			System.out.println("get comments Can't Connect"+et);
			return null;
		}
		System.out.println("Comments for article"+articleTitle);
		try{
			Keyspace ks = client.getKeyspace("BloggyAppy");
			
			 SlicePredicate slicePredicate = new SlicePredicate();
	            SliceRange columnRange = new SliceRange();
	            SliceRange sliceRange = new SliceRange();
	            sliceRange.setStart(new byte[] {});
	            sliceRange.setFinish(new byte[] {});
	           
	            ColumnParent columnParent = new ColumnParent("Comments");
	            ColumnPath cp = new ColumnPath("Comments");
      
	            SuperColumn sc = null;            
	            List<Column> cols;
	            Iterator<Column> itr; 

	            
	            /*****************************************/
	        	System.out.println("  Get SuperColumns /*****************************************/");
	            
	        	 columnRange.setStart(new byte[0]);  //We'll get t all.
		         columnRange.setFinish(new byte[0]); //Sets the last column name to get
		            //effect on columns order
		         columnRange.setReversed(false); //Changes order of columns returned in keyset
		         columnRange.setCount(10); //Maximum number of columsn in a key
		         KeyRange superkeyRange = new KeyRange(200);  //Maximum number of keys to get
		         superkeyRange.setStart_key(articleTitle);
		         superkeyRange.setEnd_key(articleTitle);
		            
		         slicePredicate.setSlice_range(columnRange);
		         Map<String, List<SuperColumn>> supermap =ks.getSuperRangeSlices(columnParent, slicePredicate, superkeyRange);
		         for (String key : supermap.keySet()) {
		                List<SuperColumn> columns = supermap.get(key);
		                //print key
		                System.out.println("Key "+key);
		                for (SuperColumn column : columns) {
		                    //print columns with values
		                	
		                	if (column==null){
		                		System.out.println("Column is Null");
		                	}else{
		                		String Name=string(column.getName()) ;
		                		
		                		System.out.println("Name"+Name);
		                		
		                		//The Column Name is a UUID so we don't convert it to a string
		                		cp.setSuper_column(column.getName());
		        	            sc = ks.getSuperColumn(key, cp);
		        	          
		        	            sc.getColumns();
		        	            
		        	            cols=sc.getColumns();
		        	            System.out.println("\tThis is a SuperColumn with "+string(sc.getName())+":"+sc.getColumns().size());
		        	            System.out.println("------------------------------------");
		        	            itr = cols.iterator(); 
		        	            while(itr.hasNext()) {

		        	                Column col = itr.next(); 
		        	                String colName=string(col.getName());
		                   		 	String colValue=string(col.getValue());
		        	                System.out.println("\t\t"+string(col.getName()) + "\t ==\t" + string(col.getValue()));
		        	                if (colName.compareTo("Author")==0)
		                   		 		co.setauthor(colValue);
		                   		 	if (colName.compareTo("Body")==0)
		                		 		co.setbody(colValue);

		                   		 	if (colName.compareTo("pubDate")==0){
		                   		 		byte[] bDate=col.getValue();
		                   		 	    long lDate=byteArrayToLong(bDate);
		                   		 		co.setpubDate(new Date(lDate));
		                   		 	}
		        	            
		        	            }
		        	            Comments.add(co);
	                		
		                	}
		                }
		            }
			
			
			
			
			
			
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
