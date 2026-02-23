package com.retro.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="boards")
public class Board {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String title;

    @ManyToOne
    @JoinColumn(name="created_by") 
    private Users createdBy;

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy="board", cascade=CascadeType.ALL)
    private List<BoardColumn> columns;

    public Board() {}

    public Board(Long id, String title, Users createdBy, LocalDateTime createdAt,
                 List<BoardColumn> columns) {
        this.id = id;
        this.title = title;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.columns = columns;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Users getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Users createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<BoardColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<BoardColumn> columns) {
        this.columns = columns;
    }
}