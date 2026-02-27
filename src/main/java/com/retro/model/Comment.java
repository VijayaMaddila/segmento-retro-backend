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

    // Comment -> Card
    @ManyToOne
    @JoinColumn(name = "card_id")
    @JsonBackReference(value = "card-comments") // match Card.comments
    private Card card;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;
    
    public Comment() {}

    public Comment(Long id, String message, Card card, Users user) {
        this.id = id;
        this.message = message;
        this.card = card;
        this.user = user;
    }

    // ---------------- Getters & Setters ----------------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Card getCard() { return card; }
    public void setCard(Card card) { this.card = card; }

    public Users getUser() { return user; }
    public void setUser(Users user) { this.user = user; }
}