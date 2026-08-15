package it.personarum.service;

import it.personarum.domain.generation.DocumentGenerationType;
import it.personarum.domain.generation.GeneratedDocument;
import it.personarum.domain.profile.Profile;
import it.personarum.domain.template.DocumentTemplate;
import it.personarum.repository.DocumentTemplateRepository;
import it.personarum.repository.GeneratedDocumentRepository;
import it.personarum.repository.ProfileRepository;
import it.personarum.service.exception.DocumentTemplateDisabledException;
import it.personarum.service.exception.DocumentTemplateNotFoundException;
import it.personarum.service.exception.GeneratedDocumentNotFoundException;
import it.personarum.service.exception.ProfileNotFoundException;
import it.personarum.service.generation.DocumentGenerationStrategy;
import it.personarum.service.generation.DocumentGenerationStrategyResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Coordina la generazione e la consultazione dei documenti prodotti a partire dai template.
 */
@Service
@Transactional(readOnly = true)
public class GeneratedDocumentService {

    private final ProfileRepository profileRepository;
    private final DocumentTemplateRepository templateRepository;
    private final GeneratedDocumentRepository generatedDocumentRepository;
    private final DocumentGenerationStrategyResolver strategyResolver;

    /**
     * Crea il servizio dei documenti generati.
     *
     * @param profileRepository repository dei profili
     * @param templateRepository repository dei template
     * @param generatedDocumentRepository repository dei documenti generati
     * @param strategyResolver selettore della strategia di generazione
     */
    public GeneratedDocumentService(ProfileRepository profileRepository, DocumentTemplateRepository templateRepository,
                                    GeneratedDocumentRepository generatedDocumentRepository,
                                    DocumentGenerationStrategyResolver strategyResolver) {
        this.profileRepository = profileRepository;
        this.templateRepository = templateRepository;
        this.generatedDocumentRepository = generatedDocumentRepository;
        this.strategyResolver = strategyResolver;
    }

    /**
     * Genera e persiste un documento utilizzando il template e la strategia richiesti.
     *
     * @param profileId identificativo del profilo
     * @param templateId identificativo del template
     * @param generationType tipo di generazione richiesto
     * @return documento generato
     * @throws ProfileNotFoundException se il profilo non esiste
     * @throws DocumentTemplateNotFoundException se il template non esiste
     * @throws DocumentTemplateDisabledException se il template è disabilitato
     * @throws IllegalArgumentException se non esiste una strategia per il tipo richiesto
     */
    @Transactional
    public GeneratedDocument generate(Long profileId, Long templateId, DocumentGenerationType generationType) {
        Profile profile = findProfile(profileId);
        DocumentTemplate template = findTemplate(templateId);

        if (!template.isEnabled()) {
            throw new DocumentTemplateDisabledException(templateId);
        }

        DocumentGenerationStrategy strategy = strategyResolver.resolve(generationType);
        String content = strategy.generate(template, profile);
        GeneratedDocument generatedDocument = GeneratedDocument.create(profile, template, generationType, content);
        return generatedDocumentRepository.save(generatedDocument);
    }

    /**
     * Restituisce lo storico dei documenti generati per un profilo.
     *
     * @param profileId identificativo del profilo
     * @return documenti ordinati dal più recente
     * @throws ProfileNotFoundException se il profilo non esiste
     */
    public List<GeneratedDocument> findAllByProfileId(Long profileId) {
        findProfile(profileId);
        return generatedDocumentRepository.findAllByProfileIdOrderByCreatedAtDesc(profileId);
    }

    /**
     * Recupera un documento generato verificandone l'appartenenza al profilo.
     *
     * @param profileId identificativo del profilo
     * @param documentId identificativo del documento generato
     * @return documento trovato
     * @throws ProfileNotFoundException se il profilo non esiste
     * @throws GeneratedDocumentNotFoundException se il documento non appartiene al profilo
     */
    public GeneratedDocument findById(Long profileId, Long documentId) {
        findProfile(profileId);
        return generatedDocumentRepository.findByIdAndProfileId(documentId, profileId)
            .orElseThrow(() -> new GeneratedDocumentNotFoundException(profileId, documentId));
    }

    private Profile findProfile(Long profileId) {
        return profileRepository.findById(profileId).orElseThrow(() -> new ProfileNotFoundException(profileId));
    }

    private DocumentTemplate findTemplate(Long templateId) {
        return templateRepository.findById(templateId).orElseThrow(() -> new DocumentTemplateNotFoundException(templateId));
    }
}
