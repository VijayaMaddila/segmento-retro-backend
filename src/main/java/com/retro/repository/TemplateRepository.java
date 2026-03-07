package com.retro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.retro.model.Template;

public interface TemplateRepository extends JpaRepository<Template, Long> {

    List<Template> findByCategory(String category);

    List<Template> findByLanguage(String language);

    List<Template> findByCategoryAndLanguage(String category, String language);

    List<Template> findByIsDefaultTrue();
    
    @Modifying
    @Query("UPDATE Template t SET t.usageCount = t.usageCount + 1 WHERE t.id = :id")
    void incrementUsageCount(@Param("id") Long id);
}