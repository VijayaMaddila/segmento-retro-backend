package com.retro.model;

import jakarta.persistence.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "board_columns", indexes = {
    @Index(name = "idx_column_board_id",      columnList = "board_id"),
    @Index(name = "idx_column_deleted",        columnList = "deleted"),
    @Index(name = "idx_column_board_deleted",  columnList = "board_id, deleted"),
    @Index(name = "idx_column_position",       columnList = "board_id, position")
})
public class BoardColumn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private int position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference(value = "board-columns")
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    
    @OneToMany(mappedBy = "boardColumn", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Card> cards;

    private Boolean deleted = false;

    public BoardColumn() {}

    public BoardColumn(Long id, String title, int position, Board board, List<Card> cards, Boolean deleted) {
        this.id = id;
        this.title = title;
        this.position = position;
        this.board = board;
        this.cards = cards;
        this.deleted = deleted;
    }

    public Long getId()                         
	{ return id; }
    public void setId(Long id)                  
	{ this.id = id; }

    public String getTitle()                    
	{ return title; }
    public void setTitle(String title)          
	{ this.title = title; }

    public int getPosition()                    
	{ return position; }
    public void setPosition(int position)       
	{ this.position = position; }

    public Board getBoard()                     
	{ return board; }
    public void setBoard(Board board)           
	{ this.board = board; }

    public List<Card> getCards()                
	{ return cards; }
    public void setCards(List<Card> cards)      
	{ this.cards = cards; }

    public Boolean getDeleted()                 
	{ return deleted; }
    public void setDeleted(Boolean deleted)     
	{ this.deleted = deleted; }
}