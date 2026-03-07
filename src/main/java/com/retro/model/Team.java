package com.retro.model;

import java.util.List;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
@Table(name = "teams", indexes = {
    @Index(name = "idx_team_created_by", columnList = "created_by"),        
    @Index(name = "idx_team_deleted", columnList = "deleted"),              
    @Index(name = "idx_team_created_by_deleted", columnList = "created_by, deleted")
})
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "created_by")
    private Users createdBy;

    @ManyToMany(fetch = FetchType.LAZY) 
    @JoinTable(
        name = "team_members",
        joinColumns = @JoinColumn(name = "team_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"),
        indexes = {
            @Index(name = "idx_team_members_team_id", columnList = "team_id"),
            @Index(name = "idx_team_members_user_id", columnList = "user_id")
        }
    )
    private List<Users> members;

    @Column(nullable = false)
    private boolean deleted = false;

    public Team() {}

    public Team(Long id, String name, Users createdBy, List<Users> members, boolean deleted) {
        this.id = id;
        this.name = name;
        this.createdBy = createdBy;
        this.members = members;
        this.deleted = deleted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Users getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Users createdBy) {
        this.createdBy = createdBy;
    }

    public List<Users> getMembers() {
        return members;
    }

    public void setMembers(List<Users> members) {
        this.members = members;
    }

    public boolean isDeleted() {   
        return deleted;
    }

    public void setDeleted(boolean deleted) {  
        this.deleted = deleted;
    }
}