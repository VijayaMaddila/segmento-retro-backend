package com.retro.model;

import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private Users createdBy;

    @ManyToMany
    @JoinTable(
        name = "team_members",
        joinColumns = @JoinColumn(name = "team_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<Users> members;

    @Column(nullable = false)
    private boolean deleted = false;

    public Team() {}

    public Team(Long id, String name, Users createdBy, List<Users> members, boolean deleted) {
        super();
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