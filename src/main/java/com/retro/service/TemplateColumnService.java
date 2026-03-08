package com.retro.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
    public List<TemplateColumn> getColumnsByTemplateId(Long templateId) {
        return templateColumnRepository.findByTemplateId(templateId);
    }

    // GET COLUMNS BY CATEGORY
    public List<TemplateColumn> getColumnsByCategory(String category) {
        return templateColumnRepository.findByTemplateCategory(category);
    }

    // GET COLUMNS BY LANGUAGE
    public List<TemplateColumn> getColumnsByLanguage(String language) {
        return templateColumnRepository.findByTemplateLanguage(language);
    }

    // GET COLUMNS BY CATEGORY AND LANGUAGE
    public List<TemplateColumn> getColumnsByCategoryAndLanguage(String category, String language) {
        return templateColumnRepository.findByTemplateCategoryAndLanguage(category, language);
    }

    // ADD COLUMN TO TEMPLATE
    @Transactional
    public TemplateColumn addColumn(Long templateId, String name, Integer position) {
        
        TemplateColumn column = new TemplateColumn();
        column.setName(name);
        column.setPosition(position);
        column.setTemplate(templateRepository.getReferenceById(templateId));
        return templateColumnRepository.save(column);
    }

    // DELETE COLUMN
    @Transactional
    public void deleteColumn(Long id) {
        if (!templateColumnRepository.existsById(id)) {
            throw new RuntimeException("Template column not found with id: " + id);
        }
        templateColumnRepository.deleteById(id);
    }
}