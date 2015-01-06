package eBay;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class SendEmail{
	public static void sendEmail(String listingLink, String emailTo) {
		 
		final String username = "ebayemails123456@gmail.com";
		final String password = "sundaymorning65";
 
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
 
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });
 
		try {
 
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(emailTo));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(emailTo));
			message.setSubject("EBAY!");
			message.setText(listingLink);
 
			Transport.send(message);
 
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}