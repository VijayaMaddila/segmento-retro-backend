package com.retro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.retro.dto.TemplateColumnDTO;
import com.retro.model.Template;
import com.retro.model.TemplateColumn;
import com.retro.repository.TemplateColumnRepository;
import com.retro.repository.TemplateRepository;

@RestController
@RequestMapping("/api/template-columns")
public class TemplateColumnController {

    @Autowired
    private TemplateColumnRepository templateColumnRepository;

    @Autowired
    private TemplateRepository templateRepository;

    // Add a column to a template
    @PostMapping("/{templateId}")
    public TemplateColumn addColumn(@PathVariable Long templateId,
                                    @RequestBody TemplateColumnDTO columnDto) {

        Template template = templateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        TemplateColumn column = new TemplateColumn();
        column.setName(columnDto.getName());
        column.setPosition(columnDto.getPosition());
        column.setTemplate(template);

        return templateColumnRepository.save(column);
    }

    // Delete a column
    @DeleteMapping("/{id}")
    public String deleteColumn(@PathVariable Long id) {
        templateColumnRepository.deleteById(id);
        return "Template column deleted successfully";
    }
}