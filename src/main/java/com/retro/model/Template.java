package com.retro.model;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
@Table(name = "templates", indexes = {
    @Index(name = "idx_template_category", columnList = "category"),                   
    @Index(name = "idx_template_language", columnList = "language"),                    
    @Index(name = "idx_template_is_default", columnList = "is_default"),              
    @Index(name = "idx_template_is_deleted", columnList = "is_deleted"),               
    @Index(name = "idx_template_category_deleted", columnList = "category, is_deleted"),
    @Index(name = "idx_template_default_deleted", columnList = "is_default, is_deleted")
})
public class Template {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String category;

    private String language;

    @Column(name = "usage_count")
    private Integer usageCount = 0;

    @Column(name = "is_default")
    private Boolean isDefault = false;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @JsonIgnore
    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TemplateColumn> columns = new ArrayList<>();

    public Template() {}

    public Template(Long id, String title, List<TemplateColumn> columns) {
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public Integer getUsageCount() {
		return usageCount;
	}
	public void setUsageCount(Integer usageCount) {
		this.usageCount = usageCount;
	}
	public Boolean getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}
	public Boolean getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	public List<TemplateColumn> getColumns() {
		return columns;
	}
	public void setColumns(List<TemplateColumn> columns) {
		this.columns = columns;
	}

   

   
}