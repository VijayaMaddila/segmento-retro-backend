package com.retro.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "users", indexes = {
    @Index(name = "idx_users_email",   columnList = "email", unique = true),
    @Index(name = "idx_users_team_id", columnList = "team_id"),
    @Index(name = "idx_users_role",    columnList = "role")
})
public class Users implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String name;

    
    @JsonIgnore
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role { ADMIN, MEMBER }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    @JsonIgnore
    private Team team;

    
    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Board> boards;

    public Users() {}

    public Users(Long id, String email, String name, String password, Role role, Team team, List<Board> boards) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
        this.team = team;
        this.boards = boards;
    }

    public Long getId()                         
     { return id; }
    public void setId(Long id)                   
    { this.id = id; }

    public String getEmail()                     
    { return email; }
    public void setEmail(String email)           
    { this.email = email; }

    public String getName()                      
    { return name; }
    public void setName(String name)             
    { this.name = name; }

    @Override
    public String getPassword()                  
    { return password; }
    public void setPassword(String password)     
    { this.password = password; }

    public Role getRole()                        
    { return role; }
    public void setRole(Role role)               
    { this.role = role; }

    public Team getTeam()                        
    { return team; }
    public void setTeam(Team team)               
    { this.team = team; }

    public List<Board> getBoards()               
    { return boards; }
    public void setBoards(List<Board> boards)    
    { this.boards = boards; }

    // ── UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == null) return List.of();
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override public String getUsername()                
    { return email; }
    @Override public boolean isAccountNonExpired()       
    { return true; }
    @Override public boolean isAccountNonLocked()        
    { return true; }
    @Override public boolean isCredentialsNonExpired()   
    { return true; }
    @Override public boolean isEnabled()                 
    { return true; }
}