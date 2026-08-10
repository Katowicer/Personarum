package it.personarum.web.dto.generation;

import it.personarum.domain.generation.DocumentGenerationType;
import it.personarum.domain.generation.GeneratedDocument;

import java.time.LocalDateTime;

public record GeneratedDocumentResponse(Long id, Long profileId, Long templateId, String templateName,
                                        DocumentGenerationType generationType, String content,
                                        LocalDateTime createdAt) {

    public static GeneratedDocumentResponse from(GeneratedDocument document) {
        return new GeneratedDocumentResponse(document.getId(), document.getProfile().getId(), document.getTemplate().getId(), document.getTemplate().getName(), document.getGenerationType(), document.getContent(), document.getCreatedAt());
    }
}
