package com.retro.dto;

public class VoteResponseDTO {
    private Long cardId;
    private long voteCount;
    private boolean userHasVoted;
    private long userRemainingVotes;

    public VoteResponseDTO() {}

    public VoteResponseDTO(Long cardId, long voteCount, boolean userHasVoted, long userRemainingVotes) {
        this.cardId = cardId;
        this.voteCount = voteCount;
        this.userHasVoted = userHasVoted;
        this.userRemainingVotes = userRemainingVotes;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public long getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(long voteCount) {
        this.voteCount = voteCount;
    }

    public boolean isUserHasVoted() {
        return userHasVoted;
    }

    public void setUserHasVoted(boolean userHasVoted) {
        this.userHasVoted = userHasVoted;
    }

    public long getUserRemainingVotes() {
        return userRemainingVotes;
    }

    public void setUserRemainingVotes(long userRemainingVotes) {
        this.userRemainingVotes = userRemainingVotes;
    }
}
