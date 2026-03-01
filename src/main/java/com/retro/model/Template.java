package com.retro.model;

import java.util.ArrayList;
import java.util.List;


import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

@Entity
@Table(name="templates")
public class Template {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=255)
    private String title;

    @JsonManagedReference
    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TemplateColumn> columns = new ArrayList<>();
    public Template() {}
	public Template(Long id, String title, List<TemplateColumn> columns) {
		super();
		this.id = id;
		this.title = title;
		this.columns = columns;
	}
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
	public List<TemplateColumn> getColumns() {
		return columns;
	}
	public void setColumns(List<TemplateColumn> columns) {
		this.columns = columns;
	}

   

   
}