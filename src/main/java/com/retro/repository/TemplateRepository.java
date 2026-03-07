package com.retro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.retro.model.Template;

public interface TemplateRepository extends JpaRepository<Template, Long> {


    List<Template> findByCategory(String category);

    
    List<Template> findByLanguage(String language);

    
    List<Template> findByCategoryAndLanguage(String category, String language);

    
    List<Template> findByIsDefaultTrue();

}