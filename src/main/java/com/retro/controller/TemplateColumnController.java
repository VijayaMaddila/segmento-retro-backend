package com.retro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.retro.dto.TemplateColumnDTO;
import com.retro.model.TemplateColumn;
import com.retro.service.TemplateColumnService;

@RestController
@RequestMapping("/api/template-columns")
public class TemplateColumnController {

    @Autowired
    private TemplateColumnService templateColumnService;

    // GET COLUMNS BY TEMPLATE ID
    @GetMapping("/template/{templateId}")
    public ResponseEntity<List<TemplateColumn>> getColumnsByTemplate(@PathVariable Long templateId) {
        List<TemplateColumn> columns = templateColumnService.getColumnsByTemplateId(templateId);
        return ResponseEntity.ok(columns);
    }

    // ADD COLUMN TO TEMPLATE
    @PostMapping("/{templateId}")
    public ResponseEntity<TemplateColumn> addColumn(@PathVariable Long templateId,
                                                     @RequestBody TemplateColumnDTO columnDto) {
        TemplateColumn column = templateColumnService.addColumn(
                templateId,
                columnDto.getName(),
                columnDto.getPosition()
        );
        return ResponseEntity.ok(column);
    }

    // DELETE COLUMN
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteColumn(@PathVariable Long id) {
        try {
            templateColumnService.deleteColumn(id);
            return ResponseEntity.ok("Template column deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}