package com.retro.dto;

import java.time.LocalDateTime;

public class VoteResponseDTO {
    private Long voteId;
    private Long userId;
    private String userName;
    private String userEmail;
    private Long cardId;
    private LocalDateTime votedAt;

    public VoteResponseDTO() {}

    public VoteResponseDTO(Long voteId, Long userId, String userName, String userEmail, Long cardId, LocalDateTime votedAt) {
        this.voteId = voteId;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.cardId = cardId;
        this.votedAt = votedAt;
    }

    // Getters and Setters
    public Long getVoteId() {
        return voteId;
    }

    public void setVoteId(Long voteId) {
        this.voteId = voteId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public LocalDateTime getVotedAt() {
        return votedAt;
    }

    public void setVotedAt(LocalDateTime votedAt) {
        this.votedAt = votedAt;
    }
}
