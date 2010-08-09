package uk.ac.dundee.computing.aec.jBloggyAppy.Stores;
import java.util.*;
// This will store an article / Blogpost
public class ArticleStore {
	String Author;
	String Title;
	String Body;
	String Tags;
	Date pubDate;
	String Slug;
	
	public ArticleStore(){
	}
	
	public void setauthor(String Author){
		this.Author=Author;
	}
	public void settitle(String Value){
		this.Title=Value;
	}
	public void setbody(String Value){
		this.Body=Value;
	}
	public void settags(String Value){
		this.Tags=Value;
	}
	public void setpubDate(Date Value){
		System.out.println("Article Store setPubDate "+Value);
		this.pubDate=Value;
	}
	public void setslug(String Value){
		this.Slug=Value;
	}
	
	public String getauthor(){
		return Author;		
	}
	public String gettitle(){
		return Title;		
	}
	public String getbody(){
		return Body;		
	}
	public String gettags(){
		return Tags;		
	}
	public Date getpubDate(){
		System.out.println("Article Store getPubDate "+pubDate);
		return pubDate;		
	}
	public String getslug(){
		return Slug;
	}
}
