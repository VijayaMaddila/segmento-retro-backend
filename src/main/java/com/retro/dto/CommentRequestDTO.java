package com.retro.dto;

public class CommentRequestDTO {
    private Long cardId;
    private Long userId;
    private String content;
	public CommentRequestDTO(Long cardId, Long userId, String content) {
		super();
		this.cardId = cardId;
		this.userId = userId;
		this.content = content;
	}
	public Long getCardId() {
		return cardId;
	}
	public void setCardId(Long cardId) {
		this.cardId = cardId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

   
}