package com.ipv.sensetrace.mailservice.intern;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Mail {

	// Recipient's email ID needs to be mentioned.
	String sendto;

	// Sender's email ID needs to be mentioned
	String from;

	// Create property object
	Properties properties = System.getProperties();

	String username = "";
	String password = "";
	String ceperror = "";
	String staticproblems = "";

	public void RegisterCEPError(String msg) {
		if (ceperror.equals("")) {
			ceperror = msg;
		} else {
			ceperror = ceperror + "<br>" + msg;
		}
	}

	public void RegisterStaticProblem(String msg) {
		if (staticproblems.equals("")) {
			staticproblems = msg;
		} else {
			staticproblems = staticproblems + "<br>" + msg;
		}
	}

	public Mail(String from_ref, String sendto_ref, String smtp_server,
			String smtp_port, String username_ref, String pwd_ref,
			boolean smtp_auth, boolean starttls) {
		from = from_ref;
		sendto = sendto_ref;
		username = username_ref;
		password = pwd_ref;
		System.out.println("from: " + from);
		System.out.println("sendto: " + sendto);
		System.out.println("smtp_server: " + smtp_server);
		System.out.println("smtp_port: " + smtp_port);
		System.out.println("username: " + username);
		System.out.println("password: " + password);
		System.out.println("auth: " + smtp_auth);
		System.out.println("starttls: " + starttls);
		// Enable starttls and auth
		properties.put("mail.smtp.starttls.enable", starttls);
		properties.put("mail.smtp.auth", smtp_auth);

		// Setup mail serveraddress and port
		// check if stmp_server is set.
		if (smtp_server == null) {
			System.out
					.println("Smtp-Server not set in config file. Will Exit System!");
			System.exit(0);
		} else {
			properties.setProperty("mail.smtp.host", smtp_server);
		}

		if (smtp_port == null) {
			System.out
					.println("Port of smtp-server not set in config file. Exit System!");
			System.exit(0);
		} else {
			properties.put("mail.smtp.port", smtp_port);
		}

		if (smtp_auth == true && (username == null || pwd_ref == null)) {
			System.out
					.println("You activated SMTP authentication but did not set user and password. Exit System!");
			System.exit(0);
		}

	}

	public void SendFolderNotAvailable() {
		System.out.println("Sending mail...");

		// Get the default Session object.
		Session session = Session.getDefaultInstance(properties,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			message.addRecipients(Message.RecipientType.TO,
					(InternetAddress.parse(sendto)));

			message.setSubject("Import can't start.");
			// Send the actual HTML message, as big as you like
			message.setContent(
					"<p> Folder with the csv files from datalogger not available.</p>",
					"text/html");

			// Send message
			Transport.send(message);
			System.out.println("successfull.");
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
		ceperror = "";
		staticproblems = "";
	}

	public void SendCVSFilesIncomplete() {
		System.out.println("Sending mail...");

		// Get the default Session object.
		Session session = Session.getDefaultInstance(properties,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			message.addRecipients(Message.RecipientType.TO,
					(InternetAddress.parse(sendto)));

			message.setSubject("Import can't start.");
			// Send the actual HTML message, as big as you like
			message.setContent(
					"<p> Number of CSV files to small. Seems a datalogger is down. Please check the datalogger export. "
							+ "If number of files is complete restart the import.</p>",
					"text/html");

			// Send message
			Transport.send(message);
			System.out.println("successfull.");
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
		ceperror = "";
		staticproblems = "";
	}

	public void SendStatusMail() {
		System.out.println("Sending mail...");

		// Get the default Session object.
		Session session = Session.getDefaultInstance(properties,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			message.addRecipients(Message.RecipientType.TO,
					(InternetAddress.parse(sendto)));

			// Set Subject: header field
			if (staticproblems.equals("") && ceperror.equals("")) {
				staticproblems = "All timestamps are up to date.";
				message.setSubject("Dataimport: No problems detected.");
				// Send the actual HTML message, as big as you like
				message.setContent("<p> Import was successfull.</p>",
						"text/html");
			} else {

				if (ceperror.equals("")) {
					ceperror = "no message";
				}

				message.setSubject("Problem detected.");
				if (!staticproblems.equals("")) {
					staticproblems = staticproblems
							+ "<p>"
							+ "Please fix the problem or deactivate broken sensor(s) "
							+ "with the flag \"active=false\" in SensorML-File.";
				} else {
					staticproblems = "No problem detected.";
				}
				// Send the actual HTML message, as big as you like
				message.setContent("<h2> Status information</h2>"
						+ "<h3> Sensorids with timestamps older 1 day:</h2>"
						+ "<p>" + staticproblems

						+ "<br><h3> CEP error message(s):</h2>" + "<p>"
						+ ceperror + "</p>", "text/html");
			}
			// Send message
			Transport.send(message);
			System.out.println("successfull.");
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
		ceperror = "";
		staticproblems = "";
	}

}
