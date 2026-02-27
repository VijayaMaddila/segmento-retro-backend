package com.retro.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    // Card -> BoardColumn
    @ManyToOne
    @JsonBackReference(value = "column-cards") // matches BoardColumn.cards
    @JoinColumn(name = "board_column_id")
    private BoardColumn boardColumn;

    // Card -> Users (creator) — optional: just ignore for JSON
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "created_by")
    private Users createdBy;

    // Card -> Comments
    @JsonManagedReference(value = "card-comments")
    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    // Card -> Votes
    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "card-votes")
    private List<Vote> votes;

    // ---------------- Constructors ----------------
    public Card() {}

    public Card(String content, BoardColumn boardColumn, Users createdBy) {
        this.content = content;
        this.boardColumn = boardColumn;
        this.createdBy = createdBy;
    }

    // ---------------- Getters & Setters ----------------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public BoardColumn getBoardColumn() { return boardColumn; }
    public void setBoardColumn(BoardColumn boardColumn) { this.boardColumn = boardColumn; }

    public Users getCreatedBy() { return createdBy; }
    public void setCreatedBy(Users createdBy) { this.createdBy = createdBy; }

    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }

    public List<Vote> getVotes() { return votes; }
    public void setVotes(List<Vote> votes) { this.votes = votes; }
}