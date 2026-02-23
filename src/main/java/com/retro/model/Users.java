package com.retro.model;

import jakarta.persistence.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="users")
public class Users {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String name;

    @Column(nullable=false, unique=true)
    private String email;

    @JsonIgnore
    @Column(nullable=false)
    private String password;

    
    @OneToMany(mappedBy="createdBy")
    private List<Board> boards;

    @OneToMany(mappedBy="createdBy")
    private List<Card> cards;

    @OneToMany(mappedBy="user")
    private List<Vote> votes;

    public Users()
    {
    	
    }

	public Users(Long id, String name, String email, String password, List<Board> boards, List<Card> cards,
			List<Vote> votes) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
		this.boards = boards;
		this.cards = cards;
		this.votes = votes;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<Board> getBoards() {
		return boards;
	}

	public void setBoards(List<Board> boards) {
		this.boards = boards;
	}

	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}

	public List<Vote> getVotes() {
		return votes;
	}

	public void setVotes(List<Vote> votes) {
		this.votes = votes;
	}
    
}