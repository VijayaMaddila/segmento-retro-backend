package com.retro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.retro.model.TemplateColumn;

public interface TemplateColumnRepository extends JpaRepository<TemplateColumn,Long> {

	List<TemplateColumn> findByTemplateId(Long id);

}
