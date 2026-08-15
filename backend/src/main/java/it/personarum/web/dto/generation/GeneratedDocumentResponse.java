package it.personarum.web.dto.generation;

import it.personarum.domain.generation.DocumentGenerationType;
import it.personarum.domain.generation.GeneratedDocument;

import java.time.LocalDateTime;

/**
 * Rappresenta un documento generato e le informazioni necessarie alla sua consultazione.
 */
public record GeneratedDocumentResponse(Long id, Long profileId, Long templateId, String templateName,
                                        DocumentGenerationType generationType, String content,
                                        LocalDateTime createdAt) {

    /**
     * Converte un documento generato nella relativa rappresentazione REST.
     *
     * @param document documento generato da convertire
     * @return DTO con contenuto e riferimenti del documento generato
     */
    public static GeneratedDocumentResponse from(GeneratedDocument document) {
        return new GeneratedDocumentResponse(document.getId(), document.getProfile().getId(), document.getTemplate().getId(), document.getTemplate().getName(), document.getGenerationType(), document.getContent(), document.getCreatedAt());
    }
}
