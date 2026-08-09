package it.personarum.repository;

import it.personarum.domain.template.DocumentTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentTemplateRepository extends JpaRepository<DocumentTemplate, Long> {

    List<DocumentTemplate> findAllByOrderByNameAsc();

    List<DocumentTemplate> findAllByEnabledTrueOrderByNameAsc();

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
