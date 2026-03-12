package emailclient;

public class SubEmail {

	private int subEmailID;
	private String subAddress;
	private String label;
	private String[] allowedSenders;
	private boolean isActive;

	public SubEmail(int subEmailID, String subAddress, String label, String[] allowedSenders, boolean isActive) {
		this.subEmailID = subEmailID;
		this.subAddress = subAddress;
		this.label = label;
		this.allowedSenders = allowedSenders;
		this.isActive = isActive;
	}

	public void activate() {

	}

	public void deactivate() {

	}

	public void addAllowedSenders(String senders) {

	}

	public void removeAllowedSender(String sender) {

	}

	public int getSubEmailID() {
		return subEmailID;
	}

	public String getSubAddress() {
		return subAddress;
	}

	public String getLabel() {
		return label;
	}

	public boolean getIsActive() {
		return isActive;
	}

}
