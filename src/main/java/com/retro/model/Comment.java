package com.retro.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
@Table(name = "comments", indexes = {
    @Index(name = "idx_comment_card_id",      columnList = "card_id"),
    @Index(name = "idx_comment_user_id",      columnList = "user_id"),
    @Index(name = "idx_comment_deleted",      columnList = "deleted"),
    @Index(name = "idx_comment_card_deleted", columnList = "card_id, deleted")
})
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ Added nullable = false — a comment without a message should be rejected at DB level
    @Column(nullable = false)
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id")
    @JsonBackReference(value = "card-comments")
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private Users user;

    @Column(nullable = false)
    private boolean deleted = false;

    public Comment() {}

    public Comment(Long id, String message, Card card, Users user, boolean deleted) {
        this.id = id;
        this.message = message;
        this.card = card;
        this.user = user;
        this.deleted = deleted;
    }

    public Long getId()                   { return id; }
    public void setId(Long id)            { this.id = id; }

    public String getMessage()            { return message; }
    public void setMessage(String msg)    { this.message = msg; }

    public Card getCard()                 { return card; }
    public void setCard(Card card)        { this.card = card; }

    public Users getUser()                { return user; }
    public void setUser(Users user)       { this.user = user; }

    public boolean isDeleted()            { return deleted; }
    public void setDeleted(boolean d)     { this.deleted = d; }
}