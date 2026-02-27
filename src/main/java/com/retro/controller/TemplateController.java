
package com.retro.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.retro.dto.TemplateDTO;
import com.retro.model.Template;
import com.retro.model.TemplateColumn;
import com.retro.repository.TemplateRepository;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    @Autowired
    private TemplateRepository templateRepository;

    @PostMapping
    public Template createTemplate(@RequestBody TemplateDTO templateDto) {

        Template template = new Template();
        template.setTitle(templateDto.getTitle());

        List<TemplateColumn> columns = new ArrayList<>();

        if (templateDto.getColumns() != null) {
            for (TemplateDTO.TemplateColumnDTO colDto : templateDto.getColumns()) {

                TemplateColumn col = new TemplateColumn();
                col.setName(colDto.getName());
                col.setPosition(colDto.getPosition());
                col.setTemplate(template);

                columns.add(col);
            }
        }

        template.setColumns(columns);

        return templateRepository.save(template);
    }

    @GetMapping
    public List<Template> getAllTemplates() {
        return templateRepository.findAll();
    }

    @PutMapping("/{id}")
    public Template updateTemplate(@PathVariable Long id, @RequestBody TemplateDTO templateDto) {

        Template template = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        template.setTitle(templateDto.getTitle());

        // Replace columns
        template.getColumns().clear();
        if (templateDto.getColumns() != null) {
            for (TemplateDTO.TemplateColumnDTO colDto : templateDto.getColumns()) {
                TemplateColumn col = new TemplateColumn();
                col.setName(colDto.getName());
                col.setPosition(colDto.getPosition());
                col.setTemplate(template);
                template.getColumns().add(col);
            }
        }

        return templateRepository.save(template);
    }

    @DeleteMapping("/{id}")
    public String deleteTemplate(@PathVariable Long id) {
        templateRepository.deleteById(id);
        return "Template deleted successfully";
    }
}