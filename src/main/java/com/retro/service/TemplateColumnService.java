package com.retro.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.retro.model.TemplateColumn;
import com.retro.repository.TemplateColumnRepository;
import com.retro.repository.TemplateRepository;

import jakarta.transaction.Transactional;

@Service
public class TemplateColumnService {

    @Autowired
    private TemplateColumnRepository templateColumnRepository;

    @Autowired
    private TemplateRepository templateRepository;

    // GET COLUMNS BY TEMPLATE ID
    @Cacheable(value = "templateColumns", key = "'template:' + #templateId")
    public List<TemplateColumn> getColumnsByTemplateId(Long templateId) {
        return templateColumnRepository.findByTemplateId(templateId);
    }

    // GET COLUMNS BY CATEGORY
    @Cacheable(value = "templateColumns", key = "'category:' + #category")
    public List<TemplateColumn> getColumnsByCategory(String category) {
        return templateColumnRepository.findByTemplateCategory(category);
    }

    // GET COLUMNS BY LANGUAGE
    @Cacheable(value = "templateColumns", key = "'language:' + #language")
    public List<TemplateColumn> getColumnsByLanguage(String language) {
        return templateColumnRepository.findByTemplateLanguage(language);
    }

    // GET COLUMNS BY CATEGORY AND LANGUAGE
    @Cacheable(value = "templateColumns", key = "'category:' + #category + ':language:' + #language")
    public List<TemplateColumn> getColumnsByCategoryAndLanguage(String category, String language) {
        return templateColumnRepository.findByTemplateCategoryAndLanguage(category, language);
    }

    // ADD COLUMN TO TEMPLATE
    @Transactional
    @CacheEvict(value = {"templateColumns", "templates"}, allEntries = true)
    public TemplateColumn addColumn(Long templateId, String name, Integer position) {
        
        TemplateColumn column = new TemplateColumn();
        column.setName(name);
        column.setPosition(position);
        column.setTemplate(templateRepository.getReferenceById(templateId));
        return templateColumnRepository.save(column);
    }

    // DELETE COLUMN
    @Transactional
    @CacheEvict(value = {"templateColumns", "templates"}, allEntries = true)
    public void deleteColumn(Long id) {
        if (!templateColumnRepository.existsById(id)) {
            throw new RuntimeException("Template column not found with id: " + id);
        }
        templateColumnRepository.deleteById(id);
    }
}