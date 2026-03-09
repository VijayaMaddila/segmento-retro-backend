package com.retro.dto;

import java.util.List;

public class TemplateDTO {

    private String title;
    private String description;
    private String category;
    private String language;
    private Integer usageCount;
    private Boolean isDefault;
    private List<TemplateColumnDTO> columns;

    public static class TemplateColumnDTO {
        private String name;
        private Integer position;
        
        public String getName() 
        { return name; }
        public void setName(String name) 
        { this.name = name; }

        public Integer getPosition() 
        { return position; }
        public void setPosition(Integer position) 
        { this.position = position; }
    }

    public String getTitle()
     { return title; }
    public void setTitle(String title)
     { this.title = title; }

    public String getDescription() 
    { return description; }
    public void setDescription(String description) 
    { this.description = description; }

    public String getCategory()
     { return category; }
    public void setCategory(String category)
     { this.category = category; }

    public String getLanguage() 
    { return language; }
    public void setLanguage(String language) 
    { this.language = language; }

    public Integer getUsageCount() 
    { return usageCount; }
    public void setUsageCount(Integer usageCount) 
    { this.usageCount = usageCount; }

    public Boolean getIsDefault() 
    { return isDefault; }
    public void setIsDefault(Boolean isDefault) 
    { this.isDefault = isDefault; }

    public List<TemplateColumnDTO> getColumns() 
    { return columns; }
    public void setColumns(List<TemplateColumnDTO> columns) 
    { this.columns = columns; }
}