package com.retro.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "team_invitations")
public class TeamInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String token;

    private boolean accepted = false;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    public TeamInvitation() {}

    public TeamInvitation(String email, String token, Team team) {
        this.email = email;
        this.token = token;
        this.team = team;
    }

   
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public boolean isExpired() {
        return LocalDateTime.now().isAfter(createdAt.plusDays(7));
    }
}