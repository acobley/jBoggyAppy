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
	CassandraClientPool pool;
	public AuthorConnector(){
		//We use a hashmap to define the fields we are expecting
		hm.put("Email","");
		hm.put("Twitter","");
		hm.put("Address", "");
		hm.put("Bio", "");
		//We don't worry about the Name, this will be the key
		
		pool = CassandraClientPoolFactory.INSTANCE.get();
	
	}
	
	public List<AuthorStore> getAuthors() 
	{
		List<AuthorStore> Authors= new LinkedList<AuthorStore>();
		AuthorStore Au=new AuthorStore();
		CassandraClient client=null;
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
            String start="";
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
                Au=new AuthorStore();
                Au.setname(key); //The key will be the name.
                
                System.out.println(key);
                for (Column column : columns) {
                    //print columns with values
                	 //if (hm.containsKey(column.getName())){
                		
                		 String Name=string(column.getName());
                		 String Value=string(column.getValue());
 
                		 if (Name.compareTo("Twitter")==0)
                			 Au.settwitterName(Value);
                		 if (Name.compareTo("Email")==0)
                			 Au.setemailName(Value);
                		 if (Name.compareTo("Bio")==0)
                			 Au.setbio(Value);
                		 if (Name.compareTo("Address")==0)
                			 Au.setaddress(Value);
                		 if (Name.compareTo("Tel")==0)
                			 Au.settel(Value);
         				
         				//}
                	 
                	 	System.out.println("\t" + string(column.getName()) + "\t ==\t" + string(column.getValue()));
                }
                Authors.add(Au);
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
		return Authors;
	}
	
	
	public AuthorStore getAuthor(String Author) 
	{
		System.out.println("Author conector getAuthor "+Author);
		AuthorStore Au=new AuthorStore();
		CassandraClient client=null;
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
            String start="";
            byte bStart[]=start.getBytes();
            columnRange.setStart(bStart);
            columnRange.setFinish(new byte[0]);
            //effect on columns order
            columnRange.setReversed(false);
           //count of max retrieving keys
            KeyRange keyRange = new KeyRange(1);
            keyRange.setStart_key(Author);
            keyRange.setEnd_key("");
            SlicePredicate slicePredicate = new SlicePredicate();
            slicePredicate.setSlice_range(columnRange);
            Map<String, List<Column>> map = ks.getRangeSlices(columnParent, slicePredicate, keyRange);
            
            //printing keys with columns
            for (String key : map.keySet()) {
                List<Column> columns = map.get(key);
                //print key
                
                Au.setname(key); //The key will be the name.
                
                System.out.println(key);
                for (Column column : columns) {
                    //print columns with values
                	 //if (hm.containsKey(column.getName())){
                		
                		 String Name=string(column.getName());
                		 String Value=string(column.getValue());
 
                		 if (Name.compareTo("Twitter")==0)
                			 Au.settwitterName(Value);
                		 if (Name.compareTo("Email")==0)
                			 Au.setemailName(Value);
                		 if (Name.compareTo("Bio")==0)
                			 Au.setbio(Value);
                		 if (Name.compareTo("Address")==0)
                			 Au.setaddress(Value);
                		 if (Name.compareTo("Tel")==0)
                			 Au.settel(Value);
                		 if (Name.compareTo("numPosts")==0){
                			 long lValue=byteArrayToLong(column.getValue());
                			 System.out.println("Author Connnector getAuthor "+lValue);
                			 Au.setnumPosts(lValue);
                		 }
 
                	 
                	 	System.out.println("Author Connnector getAuthor \t" + string(column.getName()) + "\t ==\t" + string(column.getValue()));
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
		return Au;
	}
	
	
	//This will add an Author  The only things in the AuthorStore needed is the Name/key and email,
	//All other are optional.
	public boolean AddAuthor(AuthorStore Author){
		
		if (Author.getname() == null){
			//If we don't have a name we can't add this user
			return false;
		}
		if (Author.getemailName()==null){
			//Same with Email, all other fields are optional
			return false;
		}
		System.out.println("Author conector addAuthor "+Author);
		
		CassandraClient client=null;
		try{
			client=Connect();
		}catch (Exception et){
			System.out.println("Can't Connect"+et);
			return false;
		}
		
		try{
			 Keyspace ks = client.getKeyspace("BloggyAppy");
			 ColumnPath columnPath = new ColumnPath("Authors");
			 String key = Author.getname();
             String columnName = "Email";
             String value = Author.getemailName();
             columnPath.setColumn(columnName.getBytes());
             ks.insert(key, columnPath, value.getBytes());
             if (Author.gettel()!=null){
            	 columnName = "Tel";
            	 value = Author.gettel(); 
            	 columnPath.setColumn(columnName.getBytes());
            	 ks.insert(key, columnPath, value.getBytes());
             }
             if (Author.getaddress()!=null){
            	 columnName = "Address";
            	 value = Author.getaddress(); 
            	 columnPath.setColumn(columnName.getBytes());
            	 ks.insert(key, columnPath, value.getBytes());
             }
             if (Author.gettwitterName()!=null){
            	 columnName = "Twitter";
            	 value = Author.gettwitterName(); 
            	 columnPath.setColumn(columnName.getBytes());
            	 ks.insert(key, columnPath, value.getBytes());
             }
             if (Author.getbio()!=null){
            	 columnName = "Bio";
            	 value = Author.getbio(); 
            	 columnPath.setColumn(columnName.getBytes());
            	 ks.insert(key, columnPath, value.getBytes());
             }
             // And set the number of posts to 0
             columnName = "numPosts";
        	 long lValue = 0;
        	 byte[] bValue=longToByteArray(lValue);
        	 columnPath.setColumn(columnName.getBytes());
        	 ks.insert(key, columnPath, bValue);
             
		}catch (Exception et){
			System.out.println("Can't Create a new Author "+et);
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
