package emailclient;

public class Mailbox {

	private int mailboxID;
	private String type;
	
	
	public Mailbox(int mailBoxID, String type) {
		this.mailboxID = mailBoxID;
		this.type = type;
	}
	
	public void addEmail(Email email) {
		
	}
	
	public void removeEmail(Email email) {
		
	}
	
	public Email[] searchEmail(String query) {
		return null;
	}
	

	public int getMailBoxID() {
		return mailboxID;
	}
	
	public String getType() {
		return type;
	}
	
}
