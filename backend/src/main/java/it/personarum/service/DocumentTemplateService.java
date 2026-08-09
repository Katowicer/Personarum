package it.personarum.service;

import it.personarum.domain.template.DocumentTemplate;
import it.personarum.repository.DocumentTemplateRepository;
import it.personarum.service.exception.DocumentTemplateNameAlreadyExistsException;
import it.personarum.service.exception.DocumentTemplateNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DocumentTemplateService {

    private final DocumentTemplateRepository repository;

    public DocumentTemplateService(DocumentTemplateRepository repository) {
        this.repository = repository;
    }

    public List<DocumentTemplate> findAll() {
        return repository.findAllByOrderByNameAsc();
    }

    public List<DocumentTemplate> findEnabled() {
        return repository.findAllByEnabledTrueOrderByNameAsc();
    }

    public DocumentTemplate findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new DocumentTemplateNotFoundException(id));
    }

    @Transactional
    public DocumentTemplate create(String name, String description, String content) {
        if (repository.existsByNameIgnoreCase(name)) {
            throw new DocumentTemplateNameAlreadyExistsException(name);
        }

        DocumentTemplate template = DocumentTemplate.create(name, description, content);

        return repository.save(template);
    }

    @Transactional
    public DocumentTemplate update(Long id, String name, String description, String content, boolean enabled) {
        DocumentTemplate template = findById(id);

        if (repository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new DocumentTemplateNameAlreadyExistsException(name);
        }

        template.changeDetails(name, description, content);

        if (enabled) { template.enable(); }
        else { template.disable(); }

        return template;
    }
}
