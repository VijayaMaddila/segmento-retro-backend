package com.retro.dto;

public class VoteRequestDTO {
    private Long userId;
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
