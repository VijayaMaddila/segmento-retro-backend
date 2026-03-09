package com.retro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CommentRequestDTO {

    @NotNull(message = "cardId is required")
    private Long cardId;

    @NotNull(message = "userId is required")
    private Long userId;

    @NotBlank(message = "content must not be blank")
    private String content;

    
    public CommentRequestDTO() {}

    public CommentRequestDTO(Long cardId, Long userId, String content) {
        this.cardId = cardId;
        this.userId = userId;
        this.content = content;
    }

    public Long getCardId()                 
    { return cardId; }
    public void setCardId(Long cardId)      
    { this.cardId = cardId; }

    public Long getUserId()                 
    { return userId; }
    public void setUserId(Long userId)      
    { this.userId = userId; }

    public String getContent()              
    { return content; }
    public void setContent(String content)  
    { this.content = content; }
}