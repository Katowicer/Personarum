package it.personarum.service;

import it.personarum.domain.template.DocumentTemplate;
import it.personarum.repository.DocumentTemplateRepository;
import it.personarum.service.exception.DocumentTemplateNameAlreadyExistsException;
import it.personarum.service.exception.DocumentTemplateNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Coordina i casi d'uso relativi ai template utilizzati per la generazione dei documenti.
 */
@Service
@Transactional(readOnly = true)
public class DocumentTemplateService {

    private final DocumentTemplateRepository repository;

    /**
     * Crea il servizio dei template documentali.
     *
     * @param repository repository dei template
     */
    public DocumentTemplateService(DocumentTemplateRepository repository) {
        this.repository = repository;
    }

    /**
     * Restituisce tutti i template, compresi quelli disabilitati, ordinati per nome.
     *
     * @return elenco completo dei template
     */
    public List<DocumentTemplate> findAll() {
        return repository.findAllByOrderByNameAsc();
    }

    /**
     * Restituisce esclusivamente i template abilitati, ordinati per nome.
     *
     * @return elenco dei template disponibili agli utenti
     */
    public List<DocumentTemplate> findEnabled() {
        return repository.findAllByEnabledTrueOrderByNameAsc();
    }

    /**
     * Recupera un template tramite identificativo.
     *
     * @param id identificativo del template
     * @return template trovato
     * @throws DocumentTemplateNotFoundException se il template non esiste
     */
    public DocumentTemplate findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new DocumentTemplateNotFoundException(id));
    }

    /**
     * Crea un nuovo template verificando l'unicità del nome normalizzato.
     *
     * @param name        nome del template
     * @param description descrizione opzionale
     * @param content     contenuto del template
     * @return template creato e persistito
     * @throws DocumentTemplateNameAlreadyExistsException se il nome è già utilizzato
     * @throws IllegalArgumentException                   se i dati non rispettano le regole del dominio
     */
    @Transactional
    public DocumentTemplate create(String name, String description, String content) {
        String normalizedName = DocumentTemplate.normalizeName(name);

        if (repository.existsByNameIgnoreCase(normalizedName)) {
            throw new DocumentTemplateNameAlreadyExistsException(normalizedName);
        }

        DocumentTemplate template = DocumentTemplate.create(normalizedName, description, content);
        return repository.save(template);
    }

    /**
     * Aggiorna i dati e lo stato di abilitazione di un template esistente.
     *
     * @param id          identificativo del template
     * @param name        nuovo nome
     * @param description nuova descrizione opzionale
     * @param content     nuovo contenuto
     * @param enabled     stato di abilitazione richiesto
     * @return template aggiornato
     * @throws DocumentTemplateNotFoundException          se il template non esiste
     * @throws DocumentTemplateNameAlreadyExistsException se il nuovo nome è usato da un altro template
     * @throws IllegalArgumentException                   se i dati non rispettano le regole del dominio
     */
    @Transactional
    public DocumentTemplate update(Long id, String name, String description, String content, boolean enabled) {
        DocumentTemplate template = findById(id);
        String normalizedName = DocumentTemplate.normalizeName(name);

        if (repository.existsByNameIgnoreCaseAndIdNot(normalizedName, id)) {
            throw new DocumentTemplateNameAlreadyExistsException(normalizedName);
        }

        template.changeDetails(normalizedName, description, content);
        if (enabled) {
            template.enable();
        } else {
            template.disable();
        }
        return template;
    }
}
