package com.retro.dto;

import java.util.List;

public class VoteStatsDTO {
    private Long cardId;
    private String cardContent;
    private Integer totalVotes;
    private List<VoteResponseDTO> voters;
    private boolean currentUserVoted;

    public VoteStatsDTO() {}

    public VoteStatsDTO(Long cardId, String cardContent, Integer totalVotes, List<VoteResponseDTO> voters, boolean currentUserVoted) {
        this.cardId = cardId;
        this.cardContent = cardContent;
        this.totalVotes = totalVotes;
        this.voters = voters;
        this.currentUserVoted = currentUserVoted;
    }

    // Getters and Setters
    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public String getCardContent() {
        return cardContent;
    }

    public void setCardContent(String cardContent) {
        this.cardContent = cardContent;
    }

    public Integer getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(Integer totalVotes) {
        this.totalVotes = totalVotes;
    }

    public List<VoteResponseDTO> getVoters() {
        return voters;
    }

    public void setVoters(List<VoteResponseDTO> voters) {
        this.voters = voters;
    }

    public boolean isCurrentUserVoted() {
        return currentUserVoted;
    }

    public void setCurrentUserVoted(boolean currentUserVoted) {
        this.currentUserVoted = currentUserVoted;
    }
}
