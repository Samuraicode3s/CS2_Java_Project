package emailclient;

import java.util.Date;

public class Email {

	private String sender;
	private String recipient;
	private int emailID;
	private String subject;
	private String body;
	private Date timeStamp;
	private boolean isRead;

	public Email(String sender, String recipient, int emailID, String subject, String body, Date timeStamp,
			boolean isRead) {
		this.sender = sender;
		this.recipient = recipient;
		this.emailID = emailID;
		this.subject = subject;
		this.body = body;
		this.timeStamp = timeStamp;
		this.isRead = isRead;
	}

	public void send() {

	}

	public Email reply() {
		return null;
	}

	public Email forward() {
		return null;
	}

	public void delete() {

	}

	public String getSender() {
		return sender;
	}

	public String getRecipient() {
		return recipient;
	}

	public int getEmailID() {
		return emailID;
	}

	public String getSubject() {
		return subject;
	}

	public String getBody() {
		return body;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public boolean isRead() {
		return isRead;
	}

}
