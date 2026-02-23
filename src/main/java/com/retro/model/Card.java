package com.retro.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="cards")
public class Card {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String content;

    @ManyToOne
    @JoinColumn(name="column_id")
    private BoardColumn column;

    @ManyToOne
    @JoinColumn(name="created_by")
    private Users createdBy;

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy="card", cascade=CascadeType.ALL)
    private List<Vote> votes;

    public Card()
    {
    	
    }

	public Card(Long id, String content, BoardColumn column, Users createdBy, LocalDateTime createdAt,
			List<Vote> votes) {
		super();
		this.id = id;
		this.content = content;
		this.column = column;
		this.createdBy = createdBy;
		this.createdAt = createdAt;
		this.votes = votes;
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

	public BoardColumn getColumn() {
		return column;
	}

	public void setColumn(BoardColumn column) {
		this.column = column;
	}

	public Users getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Users createdBy) {
		this.createdBy = createdBy;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public List<Vote> getVotes() {
		return votes;
	}

	public void setVotes(List<Vote> votes) {
		this.votes = votes;
	}
    
}