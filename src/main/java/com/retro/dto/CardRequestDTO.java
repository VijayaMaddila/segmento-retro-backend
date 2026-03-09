package com.retro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CardRequestDTO {

    @NotBlank(message = "Content must not be blank")
    private String content;

    @NotNull(message = "columnId is required")
    private Long columnId;

    @NotNull(message = "userId is required")
    private Long userId;

    private Long boardId;

    public String getContent()              
    { return content; }
    public void setContent(String content)  
    { this.content = content; }

    public Long getColumnId()               
    { return columnId; }
    public void setColumnId(Long columnId)  
    { this.columnId = columnId; }

    public Long getUserId()                 
    { return userId; }
    public void setUserId(Long userId)      
    { this.userId = userId; }

    public Long getBoardId()                
    { return boardId; }
    public void setBoardId(Long boardId)    
    { this.boardId = boardId; }
}