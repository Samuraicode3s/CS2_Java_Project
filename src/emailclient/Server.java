package emailclient;

public class Server {

	private int serverID;
	private String serverName;

	public Server(int serverID, String serverName) {
		this.serverID = serverID;
		this.serverName = serverName;
	}

	public boolean connect() {
		return false;
	}

	public void sendData(Email data) {

	}

	public Email receiveData() {
		return null;
	}

	public int getServerID() {
		return serverID;
	}

	public String getServerName() {
		return serverName;
	}

}
