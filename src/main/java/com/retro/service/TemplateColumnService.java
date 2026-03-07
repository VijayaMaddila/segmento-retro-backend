package com.retro.service;

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