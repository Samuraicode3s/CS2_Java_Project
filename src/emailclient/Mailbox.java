package emailclient;

import java.util.ArrayList;

public class Mailbox {

    private int mailboxID;
    private String type;
    private ArrayList<Email> emails;

    public Mailbox(int mailBoxID, String type) {
        this.mailboxID = mailBoxID;
        this.type = type;
        this.emails = new ArrayList<>();
    }

    public void addEmail(Email email) {
        emails.add(email);
        System.out.println("Email added to " + type + " mailbox.");
    }

    public void removeEmail(Email email) {
        if (emails.remove(email)) {
            System.out.println("Email removed from " + type + " mailbox.");
        } else {
            System.out.println("Email not found in mailbox.");
        }
    }

    public Email[] searchEmail(String query) {

        ArrayList<Email> results = new ArrayList<>();

        for (Email email : emails) {

            if (email.getSubject().toLowerCase().contains(query.toLowerCase())
                    || email.getBody().toLowerCase().contains(query.toLowerCase())
                    || email.getSender().toLowerCase().contains(query.toLowerCase())) {

                results.add(email);
            }
        }

        return results.toArray(new Email[0]);
    }

    public int getMailBoxID() {
        return mailboxID;
    }

    public String getType() {
        return type;
    }

}