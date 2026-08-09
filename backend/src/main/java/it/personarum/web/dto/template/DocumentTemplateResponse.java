package it.personarum.web.dto.template;

import it.personarum.domain.template.DocumentTemplate;

public record DocumentTemplateResponse(Long id, String name, String description, String content, boolean enabled) {

    public static DocumentTemplateResponse from(DocumentTemplate template) {
        return new DocumentTemplateResponse(template.getId(), template.getName(), template.getDescription(), template.getContent(), template.isEnabled());
    }
}
