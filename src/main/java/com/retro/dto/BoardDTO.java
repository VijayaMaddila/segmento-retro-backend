package com.retro.dto;

public class BoardDTO {

            
    private String title;    
    private Long userId;     
    private Long teamId;     
    private Long templateId;
	public BoardDTO(String title, Long userId, Long teamId, Long templateId) {
		super();
		this.title = title;
		this.userId = userId;
		this.teamId = teamId;
		this.templateId = templateId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getTeamId() {
		return teamId;
	}
	public void setTeamId(Long teamId) {
		this.teamId = teamId;
	}
	public Long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

   
    
}