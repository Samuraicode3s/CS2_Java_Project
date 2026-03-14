
package emailclient;

import java.util.Arrays;

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
		isActive = true;
		System.out.println("SubEmail " + subAddress + " is now active.");
	}

	public void deactivate() {
		isActive = false;
		System.out.println("SubEmail " + subAddress + " is now inactive.");
	}

	public void addAllowedSenders(String sender) {

		if (allowedSenders == null) {
			allowedSenders = new String[]{sender};
			return;
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
			if (allowedSenders[i].equals(sender)) {
				index = i;
				break;
			}
		}

		if (index == -1) {
			System.out.println("Sender not found.");
			return;
		}

		String[] newSenders = new String[allowedSenders.length - 1];

		for (int i = 0, j = 0; i < allowedSenders.length; i++) {
			if (i != index) {
				newSenders[j++] = allowedSenders[i];
			}
		}

		allowedSenders = newSenders;

		System.out.println(sender + " removed from allowed senders.");
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
