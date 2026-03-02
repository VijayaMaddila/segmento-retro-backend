package com.retro.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendInviteEmail(String to, String teamName, String inviteLink) {
        try {
            // Validate email format
            InternetAddress emailAddr = new InternetAddress(to);
            emailAddr.validate();

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("You're invited to join " + teamName);
            message.setText("Hi,\n\nYou've been invited to join the team \"" + teamName + "\".\n"
                    + "Click the link below to join:\n"
                    + inviteLink + "\n\nThis link will expire in 7 days.\n\nThanks!");
            
            System.out.println("Sending invitation email to: " + to);
            mailSender.send(message);
            System.out.println("✅ Invitation email sent successfully to: " + to);
        } catch (AddressException e) {
            System.err.println("❌ Invalid email address: " + to);
            throw new RuntimeException("Invalid email address: " + to);
        } catch (Exception e) {
            System.err.println("❌ Failed to send invitation email to: " + to + " - " + e.getMessage());
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
    
    public void sendBoardCreationEmail(
            String recipientEmail,
            String recipientName,
            String boardTitle,
            String teamName,
            String creatorName,
            String boardUrl) throws MessagingException {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(recipientEmail);
            helper.setSubject("New Board Created: " + boardTitle);
            
            String htmlContent = "<h3>Hi " + recipientName + ",</h3>"
                    + "<p>" + creatorName + " has created a new board \"" + boardTitle + "\" for your team \"" + teamName + "\".</p>"
                    + "<p><a href='" + boardUrl + "'>Open Board</a></p>";

            helper.setText(htmlContent, true); // true = HTML
            
            System.out.println("Sending board creation email to: " + recipientEmail);
            mailSender.send(message);
            System.out.println("✅ Board creation email sent successfully to: " + recipientEmail);
        } catch (Exception e) {
            System.err.println("❌ Failed to send board creation email to: " + recipientEmail + " - " + e.getMessage());
            throw new MessagingException("Failed to send email: " + e.getMessage());
        }
    }
    
    // Test email functionality
    public void sendTestEmail(String to) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Test Email from Retro Board");
            message.setText("This is a test email to verify email configuration is working correctly.\n\n"
                    + "If you received this email, your email service is configured properly!");
            
            System.out.println("Sending test email to: " + to);
            mailSender.send(message);
            System.out.println("✅ Test email sent successfully to: " + to);
        } catch (Exception e) {
            System.err.println("❌ Failed to send test email to: " + to + " - " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send test email: " + e.getMessage());
        }
    }
}