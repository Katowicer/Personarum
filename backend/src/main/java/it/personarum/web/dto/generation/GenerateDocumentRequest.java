package it.personarum.web.dto.generation;

import it.personarum.domain.generation.DocumentGenerationType;
import jakarta.validation.constraints.NotNull;

public record GenerateDocumentRequest(@NotNull Long templateId, @NotNull DocumentGenerationType generationType) {
}
