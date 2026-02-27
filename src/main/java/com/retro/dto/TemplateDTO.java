package com.retro.dto;

import java.util.List;

public class TemplateDTO {

    private String title;
    private List<TemplateColumnDTO> columns;

    public static class TemplateColumnDTO {
        private String name;
        private Integer position;
        // getters & setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Integer getPosition() { return position; }
        public void setPosition(Integer position) { this.position = position; }
    }

    // getters & setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<TemplateColumnDTO> getColumns() { return columns; }
    public void setColumns(List<TemplateColumnDTO> columns) { this.columns = columns; }
}