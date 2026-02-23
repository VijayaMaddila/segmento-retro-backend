package com.retro.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="votes")
public class Vote {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="card_id", nullable=false)
    private Card card;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private Users user;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Vote()
    {
    	
    }

	public Vote(Long id, Card card, Users user, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.card = card;
		this.user = user;
		this.createdAt = createdAt;
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

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
    
}