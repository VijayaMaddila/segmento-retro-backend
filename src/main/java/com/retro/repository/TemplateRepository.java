package com.retro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.retro.model.Template;

public interface TemplateRepository extends JpaRepository<Template,Long> {

}
