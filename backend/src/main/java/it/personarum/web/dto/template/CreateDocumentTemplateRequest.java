package it.personarum.web.dto.template;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateDocumentTemplateRequest(
        @NotBlank @Size(max = 120) String name,

        @Size(max = 500) String description,

        @NotBlank String content) {
}
