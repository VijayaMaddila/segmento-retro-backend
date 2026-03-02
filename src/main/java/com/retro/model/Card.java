package com.retro.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    
    @ManyToOne
    @JsonBackReference(value = "column-cards") 
    @JoinColumn(name = "board_column_id")
    private BoardColumn boardColumn;

    @ManyToOne
    @JsonIgnore 
    @JoinColumn(name = "created_by")
    private Users createdBy;

    
    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "card-comments")
    private List<Comment> comments = new ArrayList<>();

    @Column(nullable=false)
   private boolean deleted=false;
    
    public Card() {}

    public Card(Long id, String content, BoardColumn boardColumn, Users createdBy, List<Comment> comments,
			boolean deleted) {
		super();
		this.id = id;
		this.content = content;
		this.boardColumn = boardColumn;
		this.createdBy = createdBy;
		this.comments = comments;
		this.deleted = deleted;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public BoardColumn getBoardColumn() {
		return boardColumn;
	}

	public void setBoardColumn(BoardColumn boardColumn) {
		this.boardColumn = boardColumn;
	}

	public Users getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Users createdBy) {
		this.createdBy = createdBy;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

    
	

   
}