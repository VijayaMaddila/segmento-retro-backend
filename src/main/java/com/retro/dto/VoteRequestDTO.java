package com.retro.dto;


public class VoteRequestDTO {
    private Long cardId;   
    private Long userId;   

    
    public Long getCardId() 
    { 
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
}
