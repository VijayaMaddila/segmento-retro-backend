package com.retro.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TeamDTO {

    private Long id;

    @NotBlank(message = "Team name is required")
    private String name;

    @NotNull(message = "createdBy is required")
    private Long createdBy;

    private List<Long> members;

    
    public TeamDTO() {}

    public TeamDTO(Long id, String name, Long createdBy, List<Long> members) {
        this.id = id;
        this.name = name;
        this.createdBy = createdBy;
        this.members = members;
    }

    public Long getId()                     
    { return id; }
    public void setId(Long id)              
    { this.id = id; }

    public String getName()                 
    { return name; }
    public void setName(String name)        
    { this.name = name; }

    public Long getCreatedBy()              
    { return createdBy; }
    public void setCreatedBy(Long createdBy)
    { this.createdBy = createdBy; }

    public List<Long> getMembers()          
    { return members; }
    public void setMembers(List<Long> m)    
    { this.members = m; }
}