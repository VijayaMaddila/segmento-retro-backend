package com.retro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.retro.dto.TemplateDTO;
import com.retro.model.Template;
import com.retro.service.TemplateService;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    @Autowired
    private TemplateService templateService;

    @PostMapping
    public ResponseEntity<Template> createTemplate(@RequestBody TemplateDTO templateDto) {
        return ResponseEntity.ok(templateService.createTemplate(templateDto));
    }

    @GetMapping
    public ResponseEntity<List<Template>> getAllTemplates() {
        return ResponseEntity.ok(templateService.getAllTemplates());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Template>> getTemplatesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(templateService.getByCategory(category));
    }

    @GetMapping("/language/{language}")
    public ResponseEntity<List<Template>> getTemplatesByLanguage(@PathVariable String language) {
        return ResponseEntity.ok(templateService.getByLanguage(language));
    }

    @GetMapping("/default")
    public ResponseEntity<List<Template>> getDefaultTemplates() {
        return ResponseEntity.ok(templateService.getDefaultTemplates());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Template> updateTemplate(@PathVariable Long id,
                                                    @RequestBody TemplateDTO templateDto) {
        return ResponseEntity.ok(templateService.updateTemplate(id, templateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTemplate(@PathVariable Long id) {
        try {
            templateService.deleteTemplate(id);
            return ResponseEntity.ok("Template deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/use")
    public ResponseEntity<Template> incrementUsageCount(@PathVariable Long id) {
        return ResponseEntity.ok(templateService.incrementUsageCount(id));
    }
}