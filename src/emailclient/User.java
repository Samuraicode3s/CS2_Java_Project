                                                                                                                                                                                                                                                                                                                                         
package emailclient;

import java.util.Scanner;

public class User {

	private int userID;
	private String name;
	private String emailAddress;
	private String password;
	private boolean loggedIn;

	public User(int userID, String name, String emailAddress, String password) {
		this.userID = userID;
		this.name = name;
		this.emailAddress = emailAddress;
		this.password = password;
		this.loggedIn = false;
	}

	public boolean login() {

		Scanner scanner = new Scanner(System.in);

		System.out.print("Enter email: ");
		String inputEmail = scanner.nextLine();

		System.out.print("Enter password: ");
		String inputPassword = scanner.nextLine();

		if (inputEmail.equals(emailAddress) && inputPassword.equals(password)) {
			loggedIn = true;
			System.out.println("Login successful. Welcome " + name + "!");
			return true;
		} else {
			System.out.println("Invalid email or password.");
			return false;
		}
	}

	public void logout() {

		if (loggedIn) {
			loggedIn = false;
			System.out.println("User " + name + " has logged out.");
		} else {
			System.out.println("User is not currently logged in.");
		}

	}

	public void updateProfile() {

		Scanner scanner = new Scanner(System.in);

		System.out.print("Enter new name: ");
		name = scanner.nextLine();

		System.out.print("Enter new email address: ");
		emailAddress = scanner.nextLine();

		System.out.print("Enter new password: ");
		password = scanner.nextLine();

		System.out.println("Profile updated successfully.");
	}

	// Getters
	public int getUserID() {
		return userID;
	}

	public String getName() {
		return name;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public String getPassword() {
		return password;
	}

}
