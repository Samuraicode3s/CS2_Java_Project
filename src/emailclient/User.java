package emailclient;

public class User {
	private int userID;
	private String name;
	private String emailAddress;
	private String password;

	public User(int userID, String name, String emailAddress, String password) {
		this.userID = userID;
		this.name = name;
		this.emailAddress = emailAddress;
		this.password = password;
	}
	
	public boolean login() {
		return false;
	}
	
	public void logout() {
		
	}
	
	public void updateProfile() {
		
	}
	
	//Getters
	public int getUserID() {
		return userID;
	}
	
	public String getName() {
		return name;
	}
	
	
	public String getEmailAddress() {
		return emailAddress;
	}
	
	public String getPassword() {
		return password;
	}
	
	
	
	
	
	
}
