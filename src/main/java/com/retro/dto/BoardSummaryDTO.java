package com.retro.dto;

import java.time.LocalDateTime;
import com.retro.model.Board;

public class BoardSummaryDTO {
    private Long id;
    private String title;
    private LocalDateTime createdAt;
    private Long createdById;
    private String createdByName;
    private Long teamId;
    private String teamName;

    public static BoardSummaryDTO fromEntity(Board board) {
        BoardSummaryDTO dto = new BoardSummaryDTO();
        dto.id = board.getId();
        dto.title = board.getTitle();
        dto.createdAt = board.getCreatedAt();
        
        if (board.getCreatedBy() != null) {
            dto.createdById = board.getCreatedBy().getId();
            dto.createdByName = board.getCreatedBy().getName();
        }
        
        if (board.getTeam() != null) {
            dto.teamId = board.getTeam().getId();
            dto.teamName = board.getTeam().getName();
        }
        
        return dto;
    }

    public Long getId() 
    { return id; }
    public void setId(Long id)
     { this.id = id; }
    
    public String getTitle() 
    { return title; }
    public void setTitle(String title) 
    { this.title = title; }
    
    public LocalDateTime getCreatedAt() 
    { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) 
    { this.createdAt = createdAt; }
    
    public Long getCreatedById() 
    { return createdById; }
    public void setCreatedById(Long createdById) 
    { this.createdById = createdById; }
    
    public String getCreatedByName() 
    { return createdByName; }
    public void setCreatedByName(String createdByName) 
    { this.createdByName = createdByName; }
    
    public Long getTeamId() 
    { return teamId; }
    public void setTeamId(Long teamId) 
    { this.teamId = teamId; }
    
    public String getTeamName() 
    { return teamName; }
    public void setTeamName(String teamName) 
    { this.teamName = teamName; }
}
