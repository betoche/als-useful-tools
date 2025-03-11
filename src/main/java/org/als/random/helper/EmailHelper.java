package org.als.random.helper;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailHelper {

    public static void sendMailcatcherTestmail(){
        final String username = "your_email@example.com"; // Doesn't really matter for Mailcatcher
        final String password = ""; // Mailcatcher doesn't require a password.
        final String to = "recipient@example.com";
        final String subject = "Test Email from Java";
        final String body = "This is a test email sent using Java and Mailcatcher.";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "false"); // Mailcatcher doesn't require authentication
        props.put("mail.smtp.starttls.enable", "false"); // Mailcatcher doesn't use TLS
        props.put("mail.smtp.host", "192.168.1.37"); // Mailcatcher's default host
        props.put("mail.smtp.port", "1025"); // Mailcatcher's default SMTP port
        props.put("mail.smtp.ssl.trust", "*"); // Trust all certificates (not recommended for production).

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);

            System.out.println("Email sent successfully to Mailcatcher!");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        sendMailcatcherTestmail();
    }
}
