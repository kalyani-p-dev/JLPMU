package com.jlp.pmu.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jlp.pmu.models.Template;

public interface TemplateRepository extends JpaRepository<Template, Integer>{

	//boolean existsByTemplateName(String templateName);

	Optional<Template> findById(int tempId);

	Optional<Template> findBytemplateDescription(String string);

	Optional<Template> findBytemplateName(String templateName);

}
