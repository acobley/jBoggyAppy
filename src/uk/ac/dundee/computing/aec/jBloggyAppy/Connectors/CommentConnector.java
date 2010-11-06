package uk.ac.dundee.computing.aec.jBloggyAppy.Connectors;

import static me.prettyprint.cassandra.utils.StringUtils.string;
import static uk.ac.dundee.computing.aec.utils.Convertors.*;
import uk.ac.dundee.computing.aec.jBloggyAppy.Stores.*;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Arrays;

import static me.prettyprint.hector.api.factory.HFactory.createRangeSlicesQuery;
import static me.prettyprint.hector.api.factory.HFactory.createRangeSuperSlicesQuery;
import static me.prettyprint.hector.api.factory.HFactory.createSliceQuery;
import static me.prettyprint.hector.api.factory.HFactory.createSuperColumn;
import static me.prettyprint.hector.api.factory.HFactory.createMutator;


import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.*;

import me.prettyprint.hector.api.mutation.MutationResult;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.HSuperColumn;
import me.prettyprint.hector.api.beans.OrderedSuperRows;
import me.prettyprint.hector.api.beans.SuperRow;
import me.prettyprint.hector.api.beans.SuperSlice;
 
//import me.prettyprint.hector.api.beans.KeyspaceOperator;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import me.prettyprint.hector.api.query.RangeSuperSlicesQuery;
import me.prettyprint.hector.api.query.SliceQuery;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.serializers.UUIDSerializer;

import me.prettyprint.cassandra.service.CassandraClient;

import uk.ac.dundee.computing.aec.jBloggyAppy.Stores.PostStore;
import uk.ac.dundee.computing.aec.jBloggyAppy.Stores.TagStore;
import uk.ac.dundee.computing.aec.utils.MyConsistancyLevel;

public class CommentConnector {

	
	public CommentConnector(){
	
		
	}

	public List<CommentStore> getComments(String articleTitle){
		List <CommentStore> Comments =  new LinkedList<CommentStore>();
		CommentStore co=null;
		Cluster c; //V2
		try{
			
			c=CassandraHosts.getCluster();
			CassandraHosts.getHosts();
		}catch (Exception et){
			System.out.println("get Tag Posts Can't Connect"+et);
			return null;
		}
	
		
		//For V2 API
		StringSerializer se = StringSerializer.get();
		UUIDSerializer ue=UUIDSerializer.get();
		try{
			Keyspace ko=null;
			try {
				ko = HFactory.createKeyspace("BloggyAppy", c);  //V2
				ConsistencyLevelPolicy mcl = new MyConsistancyLevel();
				ko.setConsistencyLevelPolicy(mcl);
			}catch(Exception et){
				System.out.println("Comments KeyspaceOperator");
				return null;
			}
			System.out.println("Comments for article"+articleTitle);
			RangeSuperSlicesQuery<String, UUID,String, String> q = createRangeSuperSlicesQuery(ko, se, ue, se, se);
			q.setKeys(articleTitle, articleTitle);
			q.setColumnFamily("Comments");
			q.setRange(null, null, false, 100); // up to 100 comments
			QueryResult<OrderedSuperRows<String, UUID, String, String>> r = q.execute();
		    OrderedSuperRows<String, UUID, String, String> rows = r.get();
			System.out.println(rows.getCount()); //How many super rows ?
			SuperSlice<UUID, String, String> slice ;
			for (SuperRow<String, UUID, String, String> row2 : rows) {//This is stepping through the keys returned (but there should be only one)
			      slice = row2.getSuperSlice(); //These are the UUIDs coming out
			      for (HSuperColumn<UUID, String, String> column : slice.getSuperColumns()) {
			        System.out.println("Column "+column.getName());
			        List<HColumn<String,String>> columns =column.getColumns();
			        Iterator<HColumn<String,String>> it= columns.iterator();
			        co=new CommentStore();
			        while (it.hasNext()){ //Now we'll step through the returned columns
			        	HColumn<String,String> col=it.next();
			        	System.out.println("Column "+col.getName()+" : "+col.getValue());
			        	String colName=col.getName();
               		 	String colValue=col.getValue();
     	                if (colName.compareTo("Author")==0)
               		 		co.setauthor(colValue);
               		 	if (colName.compareTo("Comment")==0)
            		 		co.setbody(colValue);

               		 	if (colName.compareTo("pubDate")==0){
               		 		//Well deal with the date soon, its a string in the dB now
               		 		/*
               		 		byte[] bDate=col.getValue();
               		 	    long lDate=byteArrayToLong(bDate);
               		 	    */
               		 		co.setpubDate(new Date());
               		 		
               		 	}
			        }
			        Comments.add(co);
			     }
			 }

		    
		}catch (Exception et){
			System.out.println("General exception reading comments "+et);
		}
		/*
		
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

	        	System.out.println("Get SuperColumns ***********************");
	            
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
		        	            co=new CommentStore();
		        	            while(itr.hasNext()) {

		        	                Column col = itr.next(); 
		        	                String colName=string(col.getName());
		                   		 	String colValue=string(col.getValue());
		        	                System.out.println("\t\t"+string(col.getName()) + "\t ==\t" + string(col.getValue()));
		        	                if (colName.compareTo("Author")==0)
		                   		 		co.setauthor(colValue);
		                   		 	if (colName.compareTo("Comment")==0)
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
	*/	
		
		return Comments;
	}
	
	
public boolean AddComment(String title,CommentStore Comment){
		if (title==null){
			return false;
		}
		if (Comment.getauthor() == null){
			//If we don't have a name we can't add this user
			return false;
		}
		if (Comment.getbody()==null){
			//Same with body, all other fields are optional
			return false;
		}
		System.out.println("Comment Connector add comment to "+title);

		Cluster c; //V2
		try{
			
			c=CassandraHosts.getCluster();
			CassandraHosts.getHosts();
		}catch (Exception et){
			System.out.println("get Tag Posts Can't Connect"+et);
			return false;
		}
	
		
		//For V2 API
		StringSerializer se = StringSerializer.get();
		UUIDSerializer ue=UUIDSerializer.get();
		try{
			Keyspace ko=null;
			try {
				ko = HFactory.createKeyspace("BloggyAppy", c);  //V2
				ConsistencyLevelPolicy mcl = new MyConsistancyLevel();
				ko.setConsistencyLevelPolicy(mcl);
			}catch(Exception et){
				System.out.println("Comments KeyspaceOperator");
				return false;
			}
			//Create a mutator
			 Mutator<String> m = createMutator(ko, se);
			 String ColumnFamily="Comments";
			 //now lets create the comments for this super column
			 HColumn<String,String> c1= HFactory.createStringColumn("Author", Comment.getauthor());
			 HColumn<String,String> c2= HFactory.createStringColumn("Comment", Comment.getbody());
			 long now = System.currentTimeMillis();
			 String sNow=DateFormat.getInstance().format(now);
			 System.out.println("CommentDate "+sNow);
			 // Note the pubdate is a formated string for now
			 HColumn<String,String> c3= HFactory.createColumn("pubDate", sNow,se,se);
			 // We need a list of these
			 List<HColumn<String,String>> ColumnList =new  ArrayList<HColumn<String,String>>();
			 ColumnList.add(c1);
			 ColumnList.add(c2);
			 ColumnList.add(c3);
			 //Create the supercolumn with TimeUUID as the key
			 HSuperColumn<UUID, String, String> sc;
			 java.util.UUID timeUUID=getTimeUUID();
			 sc=createSuperColumn(timeUUID,ColumnList,ue,se,se);
			 System.out.println("sc"+sc);
			 //Add it as a supercolumn to the column family with the blog title as a key
			 m.addInsertion(title, ColumnFamily, sc);
			 //Don't forget to execute it !
			 m.execute();
		}catch (Exception et){
			System.out.println("Can't Create a new comment "+et);
			return false;
		}finally{
			try{
				//pool.releaseClient(client);
			}catch(Exception et){
				System.out.println("Pool acn't be released");
				return false;
			}
		}
		return true;
	}
	

		
}
