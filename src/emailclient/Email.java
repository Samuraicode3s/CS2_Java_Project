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

    public Email(String sender, String recipient, int emailID, String subject, String body, Date timeStamp, boolean isRead) {
        this.sender = sender;
        this.recipient = recipient;
        this.emailID = emailID;
        this.subject = subject;
        this.body = body;
        this.timeStamp = timeStamp;
        this.isRead = isRead;
    }

    // Simulates sending an email
    public void send() {
        System.out.println("Sending Email...");
        System.out.println("From: " + sender);
        System.out.println("To: " + recipient);
        System.out.println("Subject: " + subject);
        System.out.println("Body: " + body);
        System.out.println("Sent at: " + timeStamp);
        System.out.println("Email sent successfully.");
    }

    // Creates a reply email
    public Email reply() {
        String replySubject = "Re: " + subject;
        String replyBody = "\n\n----- Original Message -----\n" + body;

        Email replyEmail = new Email(
                recipient,     // sender becomes the original recipient
                sender,        // reply goes back to original sender
                emailID + 1,
                replySubject,
                replyBody,
                new Date(),
                false
        );

        return replyEmail;
    }

    // Creates a forwarded email
    public Email forward() {
        String forwardSubject = "Fwd: " + subject;
        String forwardBody = "\n\n----- Forwarded Message -----\nFrom: " + sender +
                "\nDate: " + timeStamp +
                "\nSubject: " + subject +
                "\n\n" + body;

        Email forwardedEmail = new Email(
                recipient,
                "",
                emailID + 1,
                forwardSubject,
                forwardBody,
                new Date(),
                false
        );

        return forwardedEmail;
    }

    // Simulates deleting an email
    public void delete() {
        System.out.println("Email with ID " + emailID + " has been deleted.");
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