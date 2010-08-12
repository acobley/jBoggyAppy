package uk.ac.dundee.computing.aec.jBloggyAppy.Stores;

public class UserStore {
	private boolean LoggedIn=false;
	private String Name="";
	public UserStore(){
		
	}
	
	public void setloggedIn(String Name){
		this.Name=Name;
		LoggedIn=true;
		
	}
	
	public boolean isloggedIn(){
		System.out.println("Logged "+LoggedIn);
		return LoggedIn;
	}
	
	public String getname(){
		return Name;
	}
	
	public void logout(){
		LoggedIn=false;
		Name="";
	}
}
