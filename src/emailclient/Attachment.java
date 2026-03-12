package emailclient;

public class Attachment {

	private String fileName;
	private long fileSize;

	public Attachment(String fileName, long fileSize) {
		this.fileName = fileName;
		this.fileSize = fileSize;
	}

	public void download() {

	}

	public String getFileName() {
		return fileName;
	}

	public long getFileSize() {
		return fileSize;
	}

}
