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

@Service
@Transactional(readOnly = true)
public class GeneratedDocumentService {

    private final ProfileRepository profileRepository;
    private final DocumentTemplateRepository templateRepository;
    private final GeneratedDocumentRepository generatedDocumentRepository;
    private final DocumentGenerationStrategyResolver strategyResolver;

    public GeneratedDocumentService(ProfileRepository profileRepository, DocumentTemplateRepository templateRepository, GeneratedDocumentRepository generatedDocumentRepository, DocumentGenerationStrategyResolver strategyResolver) {
        this.profileRepository = profileRepository;
        this.templateRepository = templateRepository;
        this.generatedDocumentRepository = generatedDocumentRepository;
        this.strategyResolver = strategyResolver;
    }

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

    public List<GeneratedDocument> findAllByProfileId(Long profileId) {
        findProfile(profileId);
        return generatedDocumentRepository.findAllByProfileIdOrderByCreatedAtDesc(profileId);
    }

    public GeneratedDocument findById(Long profileId, Long documentId) {
        findProfile(profileId);
        return generatedDocumentRepository.findByIdAndProfileId(documentId, profileId).orElseThrow(() -> new GeneratedDocumentNotFoundException(profileId, documentId));
    }

    private Profile findProfile(Long profileId) {
        return profileRepository.findById(profileId).orElseThrow(() -> new ProfileNotFoundException(profileId));
    }

    private DocumentTemplate findTemplate(Long templateId) {
        return templateRepository.findById(templateId).orElseThrow(() -> new DocumentTemplateNotFoundException(templateId));
    }
}
