package com.retro.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class SlackService {

    @Value("${slack.notifications.enabled:true}")
    private boolean notificationsEnabled;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Send to team-specific webhook or default
    @Async
    public void sendBoardCreated(String boardTitle, String creatorName, Long boardId, String teamWebhookUrl) {
        String boardLink = frontendUrl + "/board/" + boardId;
        String details = "*Created by:* " + creatorName + "\n*Board:* " + boardTitle;
        sendNotificationWithLink("Board Created", boardTitle, details, boardLink, "Join Board", "🎉", "#36a64f", teamWebhookUrl);
    }

    @Async
    public void sendRetroSessionStarted(String boardTitle, Long boardId, String startedBy, String teamWebhookUrl) {
        String boardLink = frontendUrl + "/board/" + boardId;
        String details = "*Board:* " + boardTitle + "\n*Started by:* " + startedBy + "\n\n🚀 *Retro session is now live!*";
        sendNotificationWithLink("Retro Session Started", boardTitle, details, boardLink, "Join Now", "🚀", "#FF6B6B", teamWebhookUrl);
    }

    @Async
    public void sendBoardUpdated(String oldTitle, String newTitle, String teamWebhookUrl) {
        String details = "*Old:* " + oldTitle + "\n*New:* " + newTitle;
        sendNotification("Board Updated", newTitle, details, "📝", "#2196F3", teamWebhookUrl);
    }

    @Async
    public void sendBoardDeleted(String boardTitle, String teamWebhookUrl) {
        sendNotification("Board Deleted", boardTitle, "Board has been removed", "🗑️", "#f44336", teamWebhookUrl);
    }

    @Async
    public void sendCardCreated(String columnTitle, String content, String creatorName, String teamWebhookUrl) {
        String details = "*Column:* " + columnTitle + "\n*Content:* " + content + "\n*By:* " + creatorName;
        sendNotification("New Card Added", content, details, "💡", "#9C27B0", teamWebhookUrl);
    }

    @Async
    public void sendCardUpdated(String columnTitle, String oldContent, String newContent, String teamWebhookUrl) {
        String details = "*Column:* " + columnTitle + "\n*Old:* " + oldContent + "\n*New:* " + newContent;
        sendNotification("Card Updated", newContent, details, "✏️", "#FF9800", teamWebhookUrl);
    }

    @Async
    public void sendCardDeleted(String columnTitle, String content, String teamWebhookUrl) {
        String details = "*Column:* " + columnTitle + "\n*Content:* " + content;
        sendNotification("Card Deleted", content, details, "🗑️", "#f44336", teamWebhookUrl);
    }

    @Async
    public void sendColumnAdded(String boardTitle, String columnTitle, String teamWebhookUrl) {
        if (boardTitle == null || columnTitle == null) {
            System.err.println("Skipping Slack notification: null values provided");
            return;
        }
        String details = "*Board:* " + boardTitle + "\n*Column:* " + columnTitle;
        sendNotification("Column Added", columnTitle, details, "📋", "#00BCD4", teamWebhookUrl);
    }

    @Async
    public void sendColumnRenamed(String oldTitle, String newTitle, String teamWebhookUrl) {
        if (oldTitle == null || newTitle == null) {
            System.err.println("Skipping Slack notification: null values provided");
            return;
        }
        String details = "*Old:* " + oldTitle + "\n*New:* " + newTitle;
        sendNotification("Column Renamed", newTitle, details, "✏️", "#FF9800", teamWebhookUrl);
    }

    @Async
    public void sendColumnDeleted(String columnTitle, String teamWebhookUrl) {
        if (columnTitle == null) {
            System.err.println("Skipping Slack notification: null values provided");
            return;
        }
        sendNotification("Column Deleted", columnTitle, "Column has been removed", "🗑️", "#f44336", teamWebhookUrl);
    }

    @Async
    public void sendCommentAdded(String cardContent, String comment, String userName, String teamWebhookUrl) {
        String details = "*Card:* " + cardContent + "\n*Comment:* " + comment + "\n*By:* " + userName;
        sendNotification("New Comment", comment, details, "💬", "#607D8B", teamWebhookUrl);
    }

    @Async
    public void sendCommentUpdated(String cardContent, String oldComment, String newComment, String userName, String teamWebhookUrl) {
        String details = "*Card:* " + cardContent + "\n*Old:* " + oldComment + "\n*New:* " + newComment + "\n*By:* " + userName;
        sendNotification("Comment Updated", newComment, details, "✏️", "#FF9800", teamWebhookUrl);
    }

    @Async
    public void sendCommentDeleted(String cardContent, String comment, String teamWebhookUrl) {
        String details = "*Card:* " + cardContent + "\n*Comment:* " + comment;
        sendNotification("Comment Deleted", comment, details, "🗑️", "#f44336", teamWebhookUrl);
    }

    @Async
    public void sendVoteAdded(String cardContent, String userName, long totalVotes, String teamWebhookUrl) {
        String details = "*Card:* " + cardContent + "\n*By:* " + userName + "\n*Total Votes:* " + totalVotes;
        sendNotification("Vote Added", cardContent, details, "👍", "#4CAF50", teamWebhookUrl);
    }

    @Async
    public void sendVoteRemoved(String cardContent, String userName, long totalVotes, String teamWebhookUrl) {
        String details = "*Card:* " + cardContent + "\n*By:* " + userName + "\n*Remaining Votes:* " + totalVotes;
        sendNotification("Vote Removed", cardContent, details, "👎", "#FF5722", teamWebhookUrl);
    }

    private void sendNotificationWithLink(String title, String subject, String details, String link, String buttonText, String emoji, String color, String teamWebhookUrl) {
        // Only send if team has configured webhook (no fallback to default)
        if (!notificationsEnabled || teamWebhookUrl == null || teamWebhookUrl.isEmpty()) {
            return;
        }

        try {
            ObjectNode payload = objectMapper.createObjectNode();
            ArrayNode blocks = objectMapper.createArrayNode();

            // Header block
            ObjectNode headerBlock = objectMapper.createObjectNode();
            headerBlock.put("type", "header");
            ObjectNode headerText = objectMapper.createObjectNode();
            headerText.put("type", "plain_text");
            headerText.put("text", emoji + " " + title);
            headerText.put("emoji", true);
            headerBlock.set("text", headerText);
            blocks.add(headerBlock);

            // Details section with thumbnail image
            ObjectNode detailsBlock = objectMapper.createObjectNode();
            detailsBlock.put("type", "section");
            ObjectNode detailsText = objectMapper.createObjectNode();
            detailsText.put("type", "mrkdwn");
            detailsText.put("text", details);
            detailsBlock.set("text", detailsText);
            
            // Add thumbnail image
            ObjectNode accessory = objectMapper.createObjectNode();
            accessory.put("type", "image");
            accessory.put("image_url", getImageUrlForNotification(title));
            accessory.put("alt_text", title);
            detailsBlock.set("accessory", accessory);
            
            blocks.add(detailsBlock);

            // Action button with link
            ObjectNode actionsBlock = objectMapper.createObjectNode();
            actionsBlock.put("type", "actions");
            ArrayNode elements = objectMapper.createArrayNode();
            ObjectNode buttonElement = objectMapper.createObjectNode();
            buttonElement.put("type", "button");
            
            ObjectNode btnText = objectMapper.createObjectNode();
            btnText.put("type", "plain_text");
            btnText.put("text", buttonText);
            btnText.put("emoji", true);
            buttonElement.set("text", btnText);
            
            buttonElement.put("url", link);
            buttonElement.put("style", "primary");
            elements.add(buttonElement);
            actionsBlock.set("elements", elements);
            blocks.add(actionsBlock);

            // Timestamp context
            ObjectNode contextBlock = objectMapper.createObjectNode();
            contextBlock.put("type", "context");
            ArrayNode contextElements = objectMapper.createArrayNode();
            ObjectNode timeElement = objectMapper.createObjectNode();
            timeElement.put("type", "mrkdwn");
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss"));
            timeElement.put("text", "🕐 " + timestamp);
            contextElements.add(timeElement);
            contextBlock.set("elements", contextElements);
            blocks.add(contextBlock);

            // Divider
            ObjectNode divider = objectMapper.createObjectNode();
            divider.put("type", "divider");
            blocks.add(divider);

            payload.set("blocks", blocks);
            payload.put("text", title + ": " + subject);

            // Add attachment for color bar
            ArrayNode attachments = objectMapper.createArrayNode();
            ObjectNode attachment = objectMapper.createObjectNode();
            attachment.put("color", color);
            attachments.add(attachment);
            payload.set("attachments", attachments);

            String jsonPayload = objectMapper.writeValueAsString(payload);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(jsonPayload, headers);
            restTemplate.postForEntity(teamWebhookUrl, request, String.class);

        } catch (Exception e) {
            // Log error but don't fail the main operation
            System.err.println("Failed to send Slack notification: " + e.getMessage());
        }
    }

    private void sendNotification(String title, String subject, String details, String emoji, String color, String teamWebhookUrl) {
        // Only send if team has configured webhook
        if (!notificationsEnabled || teamWebhookUrl == null || teamWebhookUrl.isEmpty()) {
            return;
        }

        try {
            ObjectNode payload = objectMapper.createObjectNode();
            ArrayNode blocks = objectMapper.createArrayNode();

            // Header block
            ObjectNode headerBlock = objectMapper.createObjectNode();
            headerBlock.put("type", "header");
            ObjectNode headerText = objectMapper.createObjectNode();
            headerText.put("type", "plain_text");
            headerText.put("text", emoji + " " + title);
            headerText.put("emoji", true);
            headerBlock.set("text", headerText);
            blocks.add(headerBlock);

            // Details section with thumbnail image
            ObjectNode detailsBlock = objectMapper.createObjectNode();
            detailsBlock.put("type", "section");
            ObjectNode detailsText = objectMapper.createObjectNode();
            detailsText.put("type", "mrkdwn");
            detailsText.put("text", details);
            detailsBlock.set("text", detailsText);
            
            // Add thumbnail image
            ObjectNode accessory = objectMapper.createObjectNode();
            accessory.put("type", "image");
            accessory.put("image_url", getImageUrlForNotification(title));
            accessory.put("alt_text", title);
            detailsBlock.set("accessory", accessory);
            
            blocks.add(detailsBlock);

            // Timestamp context
            ObjectNode contextBlock = objectMapper.createObjectNode();
            contextBlock.put("type", "context");
            ArrayNode contextElements = objectMapper.createArrayNode();
            ObjectNode timeElement = objectMapper.createObjectNode();
            timeElement.put("type", "mrkdwn");
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss"));
            timeElement.put("text", "🕐 " + timestamp);
            contextElements.add(timeElement);
            contextBlock.set("elements", contextElements);
            blocks.add(contextBlock);

            // Divider
            ObjectNode divider = objectMapper.createObjectNode();
            divider.put("type", "divider");
            blocks.add(divider);

            payload.set("blocks", blocks);
            payload.put("text", title + ": " + subject);

            // Add attachment for color bar
            ArrayNode attachments = objectMapper.createArrayNode();
            ObjectNode attachment = objectMapper.createObjectNode();
            attachment.put("color", color);
            attachments.add(attachment);
            payload.set("attachments", attachments);

            String jsonPayload = objectMapper.writeValueAsString(payload);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(jsonPayload, headers);
            restTemplate.postForEntity(teamWebhookUrl, request, String.class);

        } catch (Exception e) {
            // Log error but don't fail the main operation
            System.err.println("Failed to send Slack notification: " + e.getMessage());
        }
    }

    // Helper method to get professional image URLs for each notification type
    private String getImageUrlForNotification(String title) {
        // Using high-quality, professional icons from a reliable CDN
        switch (title) {
            case "Board Created":
                return "https://img.icons8.com/fluency/96/create-new.png";
            case "Retro Session Started":
                return "https://img.icons8.com/fluency/96/rocket.png";
            case "Board Updated":
                return "https://img.icons8.com/fluency/96/edit.png";
            case "Board Deleted":
                return "https://img.icons8.com/fluency/96/delete-forever.png";
            case "Column Added":
                return "https://img.icons8.com/fluency/96/add-column.png";
            case "Column Renamed":
                return "https://img.icons8.com/fluency/96/rename.png";
            case "Column Deleted":
                return "https://img.icons8.com/fluency/96/delete-column.png";
            case "New Card Added":
                return "https://img.icons8.com/fluency/96/note.png";
            case "Card Updated":
                return "https://img.icons8.com/fluency/96/edit-property.png";
            case "Card Deleted":
                return "https://img.icons8.com/fluency/96/delete-document.png";
            case "New Comment":
                return "https://img.icons8.com/fluency/96/comments.png";
            case "Comment Updated":
                return "https://img.icons8.com/fluency/96/edit-message.png";
            case "Comment Deleted":
                return "https://img.icons8.com/fluency/96/delete-message.png";
            case "Vote Added":
                return "https://img.icons8.com/fluency/96/thumbs-up.png";
            case "Vote Removed":
                return "https://img.icons8.com/fluency/96/thumbs-down.png";
            default:
                return "https://img.icons8.com/fluency/96/notification.png";
        }
    }
}
