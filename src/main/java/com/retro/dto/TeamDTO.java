package com.retro.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TeamDTO {

    private Long id;

    @NotBlank(message = "Team name is required")
    private String name;

    @NotNull(message = "createdBy is required")
    private Long createdBy;

    private List<Long> members;
    
    private List<MemberInfo> memberDetails;

    public static class MemberInfo {
        private Long id;
        private String name;
        private String email;

        public MemberInfo() {}

        public MemberInfo(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    
    public TeamDTO() {}

    public TeamDTO(Long id, String name, Long createdBy, List<Long> members) {
        this.id = id;
        this.name = name;
        this.createdBy = createdBy;
        this.members = members;
        this.memberDetails = List.of(); // Initialize to empty list instead of null
    }

    public TeamDTO(Long id, String name, Long createdBy, List<Long> members, List<MemberInfo> memberDetails) {
        this.id = id;
        this.name = name;
        this.createdBy = createdBy;
        this.members = members;
        this.memberDetails = memberDetails;
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

    public List<MemberInfo> getMemberDetails() 
    { return memberDetails; }
    public void setMemberDetails(List<MemberInfo> memberDetails) 
    { this.memberDetails = memberDetails; }
}