
package emailclient;

import java.util.ArrayList;
import java.util.Date;

public class Server {

	private int serverID;
	private String serverName;
	private boolean connected;

	private ArrayList<Email> serverStorage;

	public Server(int serverID, String serverName) {
		this.serverID = serverID;
		this.serverName = serverName;
		this.connected = false;
		this.serverStorage = new ArrayList<>();
	}

	public boolean connect() {

		if (!connected) {
			connected = true;
			System.out.println("Connected to server: " + serverName);
		}

		return connected;
	}

	public void sendData(Email data) {

		if (!connected) {
			System.out.println("Server not connected. Cannot send email.");
			return;
		}

		serverStorage.add(data);
		System.out.println("Email sent to server: " + data.getSubject());
	}

	public Email receiveData() {

		if (!connected) {
			System.out.println("Server not connected. Cannot receive email.");
			return null;
		}

		if (serverStorage.isEmpty()) {
			System.out.println("No emails available on server.");
			return null;
		}

		Email received = serverStorage.remove(0);

		System.out.println("Email received from server: " + received.getSubject());

		return received;
	}

	public int getServerID() {
		return serverID;
	}

	public String getServerName() {
		return serverName;
	}

}

