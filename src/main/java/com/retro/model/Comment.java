package com.retro.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;

@Entity
@Table(name="comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    @ManyToOne
    @JoinColumn(name = "card_id")
    @JsonBackReference(value = "card-comments")
    private Card card;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;
    
    @Column(nullable=false)
    private boolean deleted=false;
    
    public Comment() {}

	public Comment(Long id, String message, Card card, Users user, boolean deleted) {
		super();
		this.id = id;
		this.message = message;
		this.card = card;
		this.user = user;
		this.deleted = deleted;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	

   
}