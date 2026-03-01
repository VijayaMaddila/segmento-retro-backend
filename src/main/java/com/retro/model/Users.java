package com.retro.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String name;
    
    private String password;

    // User -> Team
    @ManyToOne
    @JoinColumn(name = "team_id")
    @JsonBackReference(value = "team-users")
    private Team team;

    
    @OneToMany(mappedBy = "createdBy")
    @JsonBackReference(value = "user-boards")
    private List<Board> boards;

    public Users() {}

	public Users(Long id, String email, String name, String password, Team team, List<Board> boards) {
		super();
		this.id = id;
		this.email = email;
		this.name = name;
		this.password = password;
		this.team = team;
		this.boards = boards;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public List<Board> getBoards() {
		return boards;
	}

	public void setBoards(List<Board> boards) {
		this.boards = boards;
	}

   
}