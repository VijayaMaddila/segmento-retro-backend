package com.retro.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "template_columns", indexes = {
    @Index(name = "idx_template_column_template_id", columnList = "template_id"),           // fetch columns by template
    @Index(name = "idx_template_column_position", columnList = "template_id, position")     // ⭐ ordered columns by template
})
public class TemplateColumn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Integer position;

    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "template_id")
    @JsonBackReference
    private Template template;

    public TemplateColumn() {}

    public TemplateColumn(Long id, String name, Integer position, Template template) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.template = template;
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

	public Template getTemplate() {
		return template;
	}

	public void setTemplate(Template template) {
		this.template = template;
	}
    
}
