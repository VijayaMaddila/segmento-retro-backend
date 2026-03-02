package com.retro.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

@Entity
@Table(name="boards")
public class Board {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String title;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name="created_by") 
    private Users createdBy;

    @ManyToOne
    @JoinColumn(name="team_id")
    private Team team; 

    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable=false)
    private boolean deleted=false;

    @JsonManagedReference(value = "board-columns")
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardColumn> columns;

    public Board() {}

	public Board(Long id, String title, Users createdBy, Team team, LocalDateTime createdAt, boolean deleted,
			List<BoardColumn> columns) {
		super();
		this.id = id;
		this.title = title;
		this.createdBy = createdBy;
		this.team = team;
		this.createdAt = createdAt;
		this.deleted = deleted;
		this.columns = columns;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Users getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Users createdBy) {
		this.createdBy = createdBy;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public List<BoardColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<BoardColumn> columns) {
		this.columns = columns;
	}
    
    

   
}