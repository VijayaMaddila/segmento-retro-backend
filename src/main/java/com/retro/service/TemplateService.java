package com.retro.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.retro.dto.TemplateDTO;
import com.retro.model.Template;
import com.retro.model.TemplateColumn;
import com.retro.repository.TemplateRepository;

import jakarta.transaction.Transactional;

@Service
public class TemplateService {

    @Autowired
    private TemplateRepository templateRepository;

    // CREATE TEMPLATE
    @Transactional
    public Template createTemplate(TemplateDTO dto) {
        Template template = new Template();
        template.setTitle(dto.getTitle());
        template.setDescription(dto.getDescription());
        template.setCategory(dto.getCategory());
        template.setLanguage(dto.getLanguage());
        template.setUsageCount(dto.getUsageCount() != null ? dto.getUsageCount() : 0);
        template.setIsDefault(dto.getIsDefault() != null ? dto.getIsDefault() : false);

        if (dto.getColumns() != null) {
            List<TemplateColumn> columns = new ArrayList<>();
            for (TemplateDTO.TemplateColumnDTO colDto : dto.getColumns()) {
                TemplateColumn col = new TemplateColumn();
                col.setName(colDto.getName());
                col.setPosition(colDto.getPosition());
                col.setTemplate(template);
                columns.add(col);
            }
            template.setColumns(columns);
        }

        return templateRepository.save(template);
    }

    // GET ALL TEMPLATES
    @Cacheable(value = "templates", key = "'all'")
    public List<Template> getAllTemplates() {
        return templateRepository.findAll();
    }

    // GET BY CATEGORY
    @Cacheable(value = "templates", key = "'category:' + #category")
    public List<Template> getByCategory(String category) {
        return templateRepository.findByCategory(category);
    }

    // GET BY LANGUAGE
    @Cacheable(value = "templates", key = "'language:' + #language")
    public List<Template> getByLanguage(String language) {
        return templateRepository.findByLanguage(language);
    }

    // GET DEFAULT TEMPLATES
    @Cacheable(value = "templates", key = "'defaults'")
    public List<Template> getDefaultTemplates() {
        return templateRepository.findByIsDefaultTrue();
    }

    // UPDATE TEMPLATE
    @Transactional
    @CacheEvict(value = "templates", allEntries = true)
    public Template updateTemplate(Long id, TemplateDTO dto) {
        Template template = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        template.setTitle(dto.getTitle());
        if (dto.getDescription() != null) template.setDescription(dto.getDescription());
        if (dto.getCategory()    != null) template.setCategory(dto.getCategory());
        if (dto.getLanguage()    != null) template.setLanguage(dto.getLanguage());
        if (dto.getUsageCount()  != null) template.setUsageCount(dto.getUsageCount());
        if (dto.getIsDefault()   != null) template.setIsDefault(dto.getIsDefault());

        
        template.getColumns().clear();
        if (dto.getColumns() != null) {
            for (TemplateDTO.TemplateColumnDTO colDto : dto.getColumns()) {
                TemplateColumn col = new TemplateColumn();
                col.setName(colDto.getName());
                col.setPosition(colDto.getPosition());
                col.setTemplate(template);
                template.getColumns().add(col);
            }
        }
        
        return template;
    }

    // DELETE TEMPLATE
    @Transactional
    @CacheEvict(value = "templates", allEntries = true)
    public void deleteTemplate(Long id) {
        if (!templateRepository.existsById(id)) {
            throw new RuntimeException("Template not found with id: " + id);
        }
        templateRepository.deleteById(id);
    }

    // INCREMENT USAGE COUNT
    @Transactional
    public Template incrementUsageCount(Long id) {
        templateRepository.incrementUsageCount(id);
        return templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found"));
    }
}