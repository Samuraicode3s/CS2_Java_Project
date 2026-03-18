package emailclient;

import java.util.Date;

public class Main {

	public static void main(String[] args) {

		System.out.println("Email client");

		// Create a mailbox
		Mailbox inbox = new Mailbox(1, "Inbox");

		// Create emails
		Email email1 = new Email(
				"alice@mail.com",
				"bob@mail.com",
				101,
				"Meeting Tomorrow",
				"Let's meet tomorrow at 10 AM.",
				new Date(),
				false);

		Email email2 = new Email(
				"professor@school.edu",
				"bob@mail.com",
				102,
				"Homework Reminder",
				"Don't forget the assignment due Friday.",
				new Date(),
				false);

		// Send emails
		email1.send();
		email2.send();

		// Add emails to mailbox
		inbox.addEmail(email1);
		inbox.addEmail(email2);

		// Search mailbox
		System.out.println("\nSearching for 'Homework'...");
		Email[] results = inbox.searchEmail("Homework");

		for (Email e : results) {
			System.out.println("Found email: " + e.getSubject());
		}

		// Reply to an email
		System.out.println("\nReplying to email...");
		Email replyEmail = email1.reply();
		replyEmail.send();

		// Forward an email
		System.out.println("\nForwarding email...");
		Email forwardedEmail = email2.forward();
		forwardedEmail.send();

		// Delete email
		System.out.println("\nDeleting email...");
		email1.delete();
		inbox.removeEmail(email1);

		// Test attachment
		System.out.println("\nTesting attachment download...");
		Attachment attachment = new Attachment("notes.txt", 500);
		attachment.download();

		System.out.println("\nProgram finished.");

	}

}