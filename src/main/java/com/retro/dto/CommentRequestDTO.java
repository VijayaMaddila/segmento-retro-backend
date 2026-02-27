package com.retro.dto;

public class CommentRequestDTO {
    private Long cardId;
    private Long userId;
    private String content;

    // getters and setters
    public Long getCardId() { return cardId; }
    public void setCardId(Long cardId) { this.cardId = cardId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}