package uk.ac.dundee.computing.aec.jBloggyAppy.Connectors;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import uk.ac.dundee.computing.aec.utils.*;

import static me.prettyprint.hector.api.factory.HFactory.createRangeSlicesQuery;

import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.*;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;

import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.cassandra.serializers.StringSerializer;

import me.prettyprint.cassandra.service.*;
import me.prettyprint.hector.api.mutation.*;



import uk.ac.dundee.computing.aec.jBloggyAppy.Stores.*;
public class AuthorConnector {

	private HashMap<String,String> hm = new HashMap<String, String>(); //We'll use this to make sure we only get fields we expect from the DB
	
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

		Cluster c; //V2
		try{
			c=CassandraHosts.getCluster();
			CassandraHosts.getHosts();
		}catch (Exception et){
			System.out.println("get Articles Posts Can't Connect"+et);
			return null;
		}
		ConsistencyLevelPolicy mcl = new MyConsistancyLevel();
		StringSerializer se = StringSerializer.get();
		try{
			Keyspace ko = HFactory.createKeyspace("BloggyAppy", c);  //V2
			ko.setConsistencyLevelPolicy(mcl);
			//retrieve  data
			OrderedRows<String, String, String> rows = null;
			ColumnSlice<String, String> slice=null;
			try{

				//retrieve  data
				RangeSlicesQuery<String,String, String> s=createRangeSlicesQuery(ko,se, se, se);
				
				s.setColumnFamily("Authors");
				
				s.setKeys("", ""); //Set the Key
				s.setRange("", "", false, 100); //Set the range of columns (we want them all) 
				QueryResult<OrderedRows<String,String, String>> r2 = s.execute();
				rows = r2.get();
			}catch(Exception et){
				System.out.println("Cant make Query on Article connector"+et);
				return null;
			}
			for (Row<String,String, String> row2 : rows) {
		    	
		      System.out.println("key "+row2.getKey());
		      System.out.flush();
		      slice = row2.getColumnSlice();
		      Au=new AuthorStore();
		      Au.setname(row2.getKey());
		      
		      for (HColumn<String, String> column : slice.getColumns()) {
		        
		    	  	String Name=column.getName();
         		 	String Value=column.getValue();

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
        			 byte[] bValue=se.toBytes(Value);
        			 long lValue=0;
        			 System.out.println("Byte Length "+bValue.length);
        			 if (bValue.length==8){// Protect against bad data
        				lValue =Convertors.byteArrayToLong(bValue);
        				 System.out.println("Author Connnector getAuthor numPosts"+lValue);
        			 }
        			 Au.setnumPosts(lValue);
        		 }	
	        
		      }
		      Authors.add(Au);
		      
		    }

		}catch (Exception et){
			System.out.println("Can't get Authors "+et);
			return null;
		}finally{
		
		}
		return Authors;
	}
	
	
	public AuthorStore getAuthor(String Author) 
	{
		
		AuthorStore Au=new AuthorStore();

		Cluster c; //V2
		try{
			
			c=CassandraHosts.getCluster();
			CassandraHosts.getHosts();
		}catch (Exception et){
			System.out.println("get Articles Posts Can't Connect"+et);
			return null;
		}
		ConsistencyLevelPolicy mcl = new MyConsistancyLevel();
		StringSerializer se = StringSerializer.get();
		try{
			Keyspace ko = HFactory.createKeyspace("BloggyAppy", c);  //V2
			ko.setConsistencyLevelPolicy(mcl);
			//retrieve  data
			OrderedRows<String, String, String> rows = null;
			ColumnSlice<String, String> slice=null;
			try{

				//retrieve  data
				RangeSlicesQuery<String,String, String> s=createRangeSlicesQuery(ko,se, se, se);
				
				s.setColumnFamily("Authors");
				
				s.setKeys(Author, Author); //Set the Key
				s.setRange("", "", false, 100); //Set the range of columns (we want them all) 
				QueryResult<OrderedRows<String,String, String>> r2 = s.execute();
				rows = r2.get();
			}catch(Exception et){
				System.out.println("Cant make Query on Article connector"+et);
				return null;
			}
			for (Row<String,String, String> row2 : rows) {
		    	
		      System.out.println("key "+row2.getKey());
		      System.out.flush();
		      slice = row2.getColumnSlice();
		      Au=new AuthorStore();
		      Au.setname(row2.getKey());
		      
		      for (HColumn<String, String> column : slice.getColumns()) {
		        
		    	  	String Name=column.getName();
         		 	String Value=column.getValue();
         		 	System.out.println("NAme: "+Name+" : "+Value);
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
        			 byte[] bValue=se.toBytes(Value);
        			 long lValue=0;
        			 System.out.println("Byte Length "+bValue.length);
        			 if (bValue.length==8){// Protect against bad data
        				lValue =Convertors.byteArrayToLong(bValue);
        				 System.out.println("Author Connnector getAuthor numPosts"+lValue);
        			 }
        			 Au.setnumPosts(lValue);
        		 }	
	        
		      }
		    }

		}catch (Exception et){
			System.out.println("Can't get Authors "+et);
			return null;
		}finally{
		
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
	
		Cluster c; //V2
		try{
			
			c=CassandraHosts.getCluster();
			CassandraHosts.getHosts();
		}catch (Exception et){
			System.out.println("get Articles Posts Can't Connect"+et);
			return false;
		}
		ConsistencyLevelPolicy mcl = new MyConsistancyLevel();
		StringSerializer se = StringSerializer.get();
		
		try{
			Keyspace ko = HFactory.createKeyspace("BloggyAppy", c);  //V2
			ko.setConsistencyLevelPolicy(mcl);

			Mutator<String> m = HFactory.createMutator(ko,se);

			 //ColumnPath emailColumnPath = new ColumnPath("RegisteredOpenIdEmails");
			 String key = Author.getname();
             String columnName = "Email";
             String value = Author.getemailName();
             m.insert(key, "Authors",
         		    HFactory.createStringColumn(columnName, value));

             if (Author.gettel()!=null){
            	 columnName = "Tel";
            	 value = Author.gettel(); 
            	 m.insert(key, "Authors",
              		    HFactory.createStringColumn(columnName, value));
             }
             if (Author.getaddress()!=null){
            	 columnName = "Address";
            	 value = Author.getaddress(); 
            	 m.insert(key, "Authors",
              		    HFactory.createStringColumn(columnName, value));
             }
             if (Author.gettwitterName()!=null){
            	 columnName = "Twitter";
            	 value = Author.gettwitterName(); 
            	 m.insert(key, "Authors",
              		    HFactory.createStringColumn(columnName, value));
             }
             if (Author.getbio()!=null){
            	 columnName = "Bio";
            	 value = Author.getbio(); 
            	 m.insert(key, "Authors",
              		    HFactory.createStringColumn(columnName, value));
             }
             // And set the number of posts to 0
             columnName = "numPosts";
        	 long lValue = 0;
        	 
        	 byte[] bValue=Convertors.longToByteArray(lValue);
        	 //columnPath.setColumn(columnName.getBytes());
        	 //ks.insert(key, columnPath, bValue);
        	 value=se.fromBytes(bValue);
        	 m.insert(key, "Authors",
          		    HFactory.createStringColumn(columnName, value));
        	 
        	 //Now add the email and name to the RegisteredOpenIdEmails index column family
        	 value=Author.getname();
        	 columnName="RegisteredAuthor";
             //emailColumnPath.setColumn(columnName.getBytes());
             key=Author.getemailName();
             //ks.insert(key, emailColumnPath, value.getBytes());
             m.insert(key, "RegisteredOpenIdEmails",
           		    HFactory.createStringColumn(columnName, value));
             
             
		}catch (Exception et){
			System.out.println("Can't Create a new Author "+et);
			return false;
		}finally{
			
		}
		return true;
	}
	
	public AuthorStore getAuthorFromEmail(String Email){
		System.out.println("Author conector getAuthorfromEmail "+Email);
		AuthorStore Au=new AuthorStore();
		Cluster c; //V2
		try{
			
			c=CassandraHosts.getCluster();
			CassandraHosts.getHosts();
		}catch (Exception et){
			System.out.println("get Articles Posts Can't Connect"+et);
			return null;
		}
		ConsistencyLevelPolicy mcl = new MyConsistancyLevel();
		StringSerializer se = StringSerializer.get();
		
		try{
			Keyspace ko = HFactory.createKeyspace("BloggyAppy", c);  //V2
			ko.setConsistencyLevelPolicy(mcl);
			//retrieve  data
			OrderedRows<String, String, String> rows = null;
			ColumnSlice<String, String> slice=null;
			try{

				//retrieve  data
				RangeSlicesQuery<String,String, String> s=createRangeSlicesQuery(ko,se, se, se);
				
				s.setColumnFamily("RegisteredOpenIdEmails");
				
				s.setKeys(Email,Email); //Set the Key
				s.setRange("", "", false, 100); //Set the range of columns (we want them all) 
				QueryResult<OrderedRows<String,String, String>> r2 = s.execute();
				rows = r2.get();
			}catch(Exception et){
				System.out.println("Cant make Query on Article connector"+et);
				return null;
			}
			for (Row<String,String, String> row2 : rows) {
		    	
		      System.out.println("key "+row2.getKey());
		      System.out.flush();
		      slice = row2.getColumnSlice();
		      Au=new AuthorStore();
		      Au.setname(row2.getKey());
		      
		      for (HColumn<String, String> column : slice.getColumns()) {
		        
		    	  String Name=column.getName();
         		 String Value=column.getValue();

         		 if (Name.compareTo("RegisteredAuthor")==0)
         			 Au.setname(Value);
	        
		      }
		    }

		}catch (Exception et){
			System.out.println("Can't get Authors "+et);
			return null;
		}finally{
			
		}
		return Au;
	}

}
