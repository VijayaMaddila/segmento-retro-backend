package com.retro.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
            String magicLinkUrl,
            Long boardId) throws MessagingException {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(recipientEmail);
            helper.setSubject("New Board: " + boardTitle + " - " + teamName);
            helper.setFrom("noreply@segmentoretro.com");
            
            // Get current date
            String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
            
            // Build magic link with boardId for direct board access
            String directBoardLink = magicLinkUrl + "&boardId=" + boardId;
            
            // Build professional HTML email
            String htmlContent = buildBoardNotificationHtml(
                recipientName,
                boardTitle,
                teamName,
                creatorName,
                currentDate,
                directBoardLink,
                magicLinkUrl
            );

            helper.setText(htmlContent, true); // true = HTML
            
            System.out.println("Sending board creation email to: " + recipientEmail);
            mailSender.send(message);
            System.out.println("✅ Board creation email sent successfully to: " + recipientEmail);
        } catch (Exception e) {
            System.err.println("❌ Failed to send board creation email to: " + recipientEmail + " - " + e.getMessage());
            throw new MessagingException("Failed to send email: " + e.getMessage());
        }
    }
    
    /**
     * Build professional HTML email template for board assignment notification
     */
    private String buildBoardNotificationHtml(
            String recipientName,
            String boardTitle,
            String teamName,
            String creatorName,
            String createdDate,
            String directBoardLink,
            String dashboardLink) {
        
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "  <style>" +
                "    body {" +
                "      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;" +
                "      background-color: #f5f7fa;" +
                "      margin: 0;" +
                "      padding: 0;" +
                "    }" +
                "    .email-container {" +
                "      max-width: 600px;" +
                "      margin: 40px auto;" +
                "      background: white;" +
                "      border-radius: 12px;" +
                "      overflow: hidden;" +
                "      box-shadow: 0 4px 12px rgba(0,0,0,0.1);" +
                "    }" +
                "    .email-header {" +
                "      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);" +
                "      padding: 30px;" +
                "      text-align: center;" +
                "    }" +
                "    .email-header h1 {" +
                "      color: white;" +
                "      margin: 0;" +
                "      font-size: 24px;" +
                "      font-weight: 600;" +
                "    }" +
                "    .email-body {" +
                "      padding: 40px 30px;" +
                "    }" +
                "    .email-body h2 {" +
                "      color: #1a1d23;" +
                "      font-size: 20px;" +
                "      margin: 0 0 20px 0;" +
                "    }" +
                "    .email-body p {" +
                "      color: #4b5563;" +
                "      font-size: 16px;" +
                "      line-height: 1.6;" +
                "      margin: 0 0 15px 0;" +
                "    }" +
                "    .board-info {" +
                "      background: #f8f9fb;" +
                "      border-left: 4px solid #667eea;" +
                "      padding: 20px;" +
                "      margin: 25px 0;" +
                "      border-radius: 6px;" +
                "    }" +
                "    .board-info-item {" +
                "      display: flex;" +
                "      margin-bottom: 12px;" +
                "    }" +
                "    .board-info-item:last-child {" +
                "      margin-bottom: 0;" +
                "    }" +
                "    .board-info-label {" +
                "      font-weight: 600;" +
                "      color: #374151;" +
                "      min-width: 100px;" +
                "    }" +
                "    .board-info-value {" +
                "      color: #6b7280;" +
                "    }" +
                "    .cta-button {" +
                "      display: inline-block;" +
                "      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);" +
                "      color: white !important;" +
                "      text-decoration: none;" +
                "      padding: 14px 32px;" +
                "      border-radius: 8px;" +
                "      font-size: 16px;" +
                "      font-weight: 600;" +
                "      margin: 20px 0;" +
                "      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);" +
                "    }" +
                "    .secondary-link {" +
                "      color: #667eea;" +
                "      text-decoration: none;" +
                "      font-weight: 500;" +
                "    }" +
                "    .email-footer {" +
                "      background: #f8f9fb;" +
                "      padding: 20px 30px;" +
                "      text-align: center;" +
                "      color: #9ca3af;" +
                "      font-size: 14px;" +
                "    }" +
                "    .divider {" +
                "      height: 1px;" +
                "      background: #e5e7eb;" +
                "      margin: 25px 0;" +
                "    }" +
                "  </style>" +
                "</head>" +
                "<body>" +
                "  <div class='email-container'>" +
                "    <div class='email-header'>" +
                "      <h1>🎯 New Board Created</h1>" +
                "    </div>" +
                "    " +
                "    <div class='email-body'>" +
                "      <h2>Hi " + recipientName + ",</h2>" +
                "      " +
                "      <p>" + creatorName + " has created a new board <strong>" + boardTitle + "</strong> for your team <strong>" + teamName + "</strong>.</p>" +
                "      " +
                "      <div class='board-info'>" +
                "        <div class='board-info-item'>" +
                "          <span class='board-info-label'>Board Name:</span>" +
                "          <span class='board-info-value'>" + boardTitle + "</span>" +
                "        </div>" +
                "        <div class='board-info-item'>" +
                "          <span class='board-info-label'>Team:</span>" +
                "          <span class='board-info-value'>" + teamName + "</span>" +
                "        </div>" +
                "        <div class='board-info-item'>" +
                "          <span class='board-info-label'>Created By:</span>" +
                "          <span class='board-info-value'>" + creatorName + "</span>" +
                "        </div>" +
                "        <div class='board-info-item'>" +
                "          <span class='board-info-label'>Created On:</span>" +
                "          <span class='board-info-value'>" + createdDate + "</span>" +
                "        </div>" +
                "      </div>" +
                "      " +
                "      <p>Click the button below to open the board directly:</p>" +
                "      " +
                "      <center>" +
                "        <a href='" + directBoardLink + "' class='cta-button'>" +
                "          Open Board" +
                "        </a>" +
                "      </center>" +
                "      " +
                "      <p style='text-align: center; margin-top: 15px;'>" +
                "        Or <a href='" + dashboardLink + "' class='secondary-link'>view all your boards</a>" +
                "      </p>" +
                "      " +
                "      <div class='divider'></div>" +
                "      " +
                "      <p style='font-size: 14px; color: #9ca3af;'>" +
                "        This magic link will log you in automatically and take you directly to the board. The link will expire in 24 hours." +
                "      </p>" +
                "    </div>" +
                "    " +
                "    <div class='email-footer'>" +
                "      <p>© 2026 SegmentoRetro. All rights reserved.</p>" +
                "      <p>This is an automated notification. Please do not reply to this email.</p>" +
                "    </div>" +
                "  </div>" +
                "</body>" +
                "</html>";
    }
    
}
