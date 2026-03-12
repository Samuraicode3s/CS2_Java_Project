package emailclient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Attachment {

    private String fileName;
    private long fileSize;

    public Attachment(String fileName, long fileSize) {
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    public void download() {
        try {
            File file = new File(fileName);
            FileOutputStream fos = new FileOutputStream(file);

            byte[] data = new byte[(int) fileSize];
            fos.write(data);

            fos.close();

            System.out.println("File downloaded successfully: " + fileName);
        } 
        catch (IOException e) {
            System.out.println("Error downloading file: " + e.getMessage());
        }
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

}
