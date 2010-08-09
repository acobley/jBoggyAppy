package uk.ac.dundee.computing.aec.jBloggyAppy.Stores;

import java.util.Date;

public class CommentStore {
	String Author;
	String Body;
	Date pubDate;
	

	public CommentStore(){
	}
	
	
	public void setauthor(String Author){
		this.Author=Author;
	}

	public void setbody(String Value){
		this.Body=Value;
	}

	public void setpubDate(Date Value){
		System.out.println("Article Store setPubDate "+Value);
		this.pubDate=Value;
	}

	

	
	public String getauthor(){
		return Author;		
	}

	public String getbody(){
		return Body;		
	}

	public Date getpubDate(){
		System.out.println("Article Store getPubDate "+pubDate);
		return pubDate;		
	}


}
