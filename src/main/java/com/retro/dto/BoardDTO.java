package com.retro.dto;

public class BoardDTO {

    private String title;
    private Long templateId; // optional template to copy columns
    private Long userId;     // new field: who is creating the board

    // getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Long getTemplateId() { return templateId; }
    public void setTemplateId(Long templateId) { this.templateId = templateId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}