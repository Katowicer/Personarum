package it.personarum.web.dto.generation;

import it.personarum.domain.generation.DocumentGenerationType;
import jakarta.validation.constraints.NotNull;

/**
 * Trasferisce i dati dell’operazione GenerateDocumentRequest tra API REST e livello applicativo.
 */
public record GenerateDocumentRequest(@NotNull Long templateId, @NotNull DocumentGenerationType generationType) {
}
