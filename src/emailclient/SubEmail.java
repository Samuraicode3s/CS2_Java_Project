package emailclient;

import java.util.Arrays;

public class SubEmail {

	private String subEmailID;
	private String ownerEmail;
	private String subAddress;
	private String label;
	private String[] allowedSenders;
	private boolean isActive;

	public SubEmail(String subEmailID, String ownerEmail, String subAddress, String label, String[] allowedSenders, boolean isActive) {
		this.subEmailID = subEmailID;
		this.ownerEmail = ownerEmail;
		this.subAddress = subAddress;
		this.label = label;
		this.allowedSenders = allowedSenders;
		this.isActive = isActive;
	}

	public void activate() {
		isActive = true;
		System.out.println("SubEmail " + subAddress + " is now active.");
	}

	public void deactivate() {
		isActive = false;
		System.out.println("SubEmail " + subAddress + " is now inactive.");
	}

	public void addAllowedSender(String sender) {
		if (sender == null || sender.trim().isEmpty()) {
			return;
		}

		if (allowedSenders == null) {
			allowedSenders = new String[] { sender };
			return;
		}

		for (int i = 0; i < allowedSenders.length; i++) {
			if (allowedSenders[i].equalsIgnoreCase(sender)) {
				return;
			}
		}

		String[] newSenders = Arrays.copyOf(allowedSenders, allowedSenders.length + 1);
		newSenders[newSenders.length - 1] = sender;
		allowedSenders = newSenders;

		System.out.println(sender + " added to allowed senders.");
	}

	public void removeAllowedSender(String sender) {
		if (allowedSenders == null || allowedSenders.length == 0) {
			System.out.println("No allowed senders to remove.");
			return;
		}

		int index = -1;

		for (int i = 0; i < allowedSenders.length; i++) {
			if (allowedSenders[i].equalsIgnoreCase(sender)) {
				index = i;
				break;
			}
		}

		if (index == -1) {
			System.out.println("Sender not found.");
			return;
		}

		String[] newSenders = new String[allowedSenders.length - 1];

		int j = 0;
		for (int i = 0; i < allowedSenders.length; i++) {
			if (i != index) {
				newSenders[j] = allowedSenders[i];
				j++;
			}
		}

		allowedSenders = newSenders;

		System.out.println(sender + " removed from allowed senders.");
	}

	public boolean canReceiveFrom(String sender) {
		if (!isActive) {
			return false;
		}

		if (allowedSenders == null || allowedSenders.length == 0) {
			return true;
		}

		for (int i = 0; i < allowedSenders.length; i++) {
			if (allowedSenders[i].equalsIgnoreCase(sender)) {
				return true;
			}
		}

		return false;
	}

	public String allowedSendersAsJson() {
		if (allowedSenders == null || allowedSenders.length == 0) {
			return "[]";
		}

		StringBuilder sb = new StringBuilder("[");
		for (int i = 0; i < allowedSenders.length; i++) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append(FileStorage.jsonStr(allowedSenders[i]));
		}
		sb.append("]");
		return sb.toString();
	}

	public String toJson() {
		return "{"
			+ "\"id\":" + FileStorage.jsonStr(subEmailID) + ","
			+ "\"ownerEmail\":" + FileStorage.jsonStr(ownerEmail) + ","
			+ "\"subAddress\":" + FileStorage.jsonStr(subAddress) + ","
			+ "\"label\":" + FileStorage.jsonStr(label) + ","
			+ "\"allowedSenders\":" + allowedSendersAsJson() + ","
			+ "\"isActive\":" + isActive
			+ "}";
	}

	public String getSubEmailID() {
		return subEmailID;
	}

	public String getOwnerEmail() {
		return ownerEmail;
	}

	public String getSubAddress() {
		return subAddress;
	}

	public String getLabel() {
		return label;
	}

	public String[] getAllowedSenders() {
		return allowedSenders;
	}

	public boolean getIsActive() {
		return isActive;
	}
}