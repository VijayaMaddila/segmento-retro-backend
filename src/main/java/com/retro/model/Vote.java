package com.retro.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "votes",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"card_id", "user_id"})
    }
)
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @ManyToOne
    @JoinColumn(name = "card_id")
    @JsonBackReference(value = "card-votes") 
    private Card card;

   
    @ManyToOne
    @JoinColumn(name = "board_id")
    @JsonBackReference(value = "board-votes") 
    private Board board;

    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;
    
    @Column(name = "voted_at", nullable = false)
    private LocalDateTime votedAt = LocalDateTime.now();

   
    public Vote() {}


	public Vote(Long id, Card card, Board board, Users user, LocalDateTime votedAt) {
		super();
		this.id = id;
		this.card = card;
		this.board = board;
		this.user = user;
		this.votedAt = votedAt;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Card getCard() {
		return card;
	}


	public void setCard(Card card) {
		this.card = card;
	}


	public Board getBoard() {
		return board;
	}


	public void setBoard(Board board) {
		this.board = board;
	}


	public Users getUser() {
		return user;
	}


	public void setUser(Users user) {
		this.user = user;
	}


	public LocalDateTime getVotedAt() {
		return votedAt;
	}


	public void setVotedAt(LocalDateTime votedAt) {
		this.votedAt = votedAt;
	}

   
}