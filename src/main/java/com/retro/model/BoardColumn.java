package com.retro.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name="columns")
public class BoardColumn {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String name;

    private Integer position;

    @ManyToOne
    @JoinColumn(name="board_id")
    private Board board;

    @OneToMany(mappedBy="column", cascade=CascadeType.ALL)
    private List<Card> cards;

    public BoardColumn()
    {
    	
    }

	public BoardColumn(Long id, String name, Integer position, Board board, List<Card> cards) {
		super();
		this.id = id;
		this.name = name;
		this.position = position;
		this.board = board;
		this.cards = cards;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}
    
}