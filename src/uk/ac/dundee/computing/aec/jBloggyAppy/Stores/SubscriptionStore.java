package uk.ac.dundee.computing.aec.jBloggyAppy.Stores;

public class SubscriptionStore {
	private String Author;
	private String Tag;
	public SubscriptionStore(){
	}
	
	public void setauthor(String Author){
		this.Author=Author;
	}
	
	public void settag(String Tag){
		this.Tag=Tag;
	}
	
	public String getauthor(){
		return Author;
	}
	
	public String gettag(){
		return Tag;
	}

}
