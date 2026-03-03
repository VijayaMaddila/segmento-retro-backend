package com.retro.dto;

import jakarta.validation.constraints.NotNull;

public class VoteRequestDTO {
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Card ID is required")
    private Long cardId;

    public VoteRequestDTO() {}

    public VoteRequestDTO(Long userId, Long cardId) {
        this.userId = userId;
        this.cardId = cardId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }
}
