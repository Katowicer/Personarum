package it.personarum.web.dto.template;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Trasferisce i dati dell’operazione CreateDocumentTemplateRequest tra API REST e livello applicativo.
 */
public record CreateDocumentTemplateRequest(@NotBlank @Size(max = 120) String name, @Size(max = 500) String description,
                                            @NotBlank String content) {
}
