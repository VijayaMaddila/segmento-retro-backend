package com.retro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.retro.model.TemplateColumn;

public interface TemplateColumnRepository extends JpaRepository<TemplateColumn,Long> {

	List<TemplateColumn> findByTemplateId(Long id);

	@Query("SELECT tc FROM TemplateColumn tc JOIN tc.template t WHERE t.category = :category AND t.isDeleted = false ORDER BY t.id, tc.position")
	List<TemplateColumn> findByTemplateCategory(@Param("category") String category);

	@Query("SELECT tc FROM TemplateColumn tc JOIN tc.template t WHERE t.language = :language AND t.isDeleted = false ORDER BY t.id, tc.position")
	List<TemplateColumn> findByTemplateLanguage(@Param("language") String language);

	@Query("SELECT tc FROM TemplateColumn tc JOIN tc.template t WHERE t.category = :category AND t.language = :language AND t.isDeleted = false ORDER BY t.id, tc.position")
	List<TemplateColumn> findByTemplateCategoryAndLanguage(@Param("category") String category, @Param("language") String language);

}
