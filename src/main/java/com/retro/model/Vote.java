package com.retro.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"card_id", "user_id"})
    }
)
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Vote -> Card
    @ManyToOne
    @JoinColumn(name = "card_id")
    @JsonBackReference(value = "card-votes") // match Card.votes
    private Card card;

    // Vote -> Board
    @ManyToOne
    @JoinColumn(name = "board_id")
    @JsonBackReference(value = "board-votes") // match Board.votes
    private Board board;

    // Vote -> User
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    // Constructors
    public Vote() {}

    public Vote(Long id, Card card, Users user, Board board) {
        this.id = id;
        this.card = card;
        this.user = user;
        this.board = board;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Card getCard() { return card; }
    public void setCard(Card card) { this.card = card; }

    public Users getUser() { return user; }
    public void setUser(Users user) { this.user = user; }

    public Board getBoard() { return board; }
    public void setBoard(Board board) { this.board = board; }
}