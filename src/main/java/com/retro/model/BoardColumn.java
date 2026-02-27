package com.retro.model;

import jakarta.persistence.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;


@Entity
@Table(name = "board_columns")
public class BoardColumn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private int position;

    // Board -> Column (back reference)
    @ManyToOne
    @JsonBackReference(value = "board-columns") // matches Board.columns
    @JoinColumn(name = "board_id")
    private Board board;

    // Column -> Cards (managed reference, must have unique value)
    @OneToMany(mappedBy = "boardColumn", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "column-cards") // <- unique value
    private List<Card> cards;

    // Constructors
    public BoardColumn() {}

    public BoardColumn(String title, int position, Board board) {
        this.title = title;
        this.position = position;
        this.board = board;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }

    public Board getBoard() { return board; }
    public void setBoard(Board board) { this.board = board; }

    public List<Card> getCards() { return cards; }
    public void setCards(List<Card> cards) { this.cards = cards; }
}