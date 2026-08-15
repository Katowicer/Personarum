package it.personarum.web.dto.template;

import it.personarum.domain.template.DocumentTemplate;

/**
 * Rappresenta un template documentale restituito dalle API.
 */
public record DocumentTemplateResponse(Long id, String name, String description, String content, boolean enabled) {

    /**
     * Converte un template di dominio nella relativa rappresentazione REST.
     *
     * @param template template da convertire
     * @return DTO contenente i dati del template
     */
    public static DocumentTemplateResponse from(DocumentTemplate template) {
        return new DocumentTemplateResponse(template.getId(), template.getName(), template.getDescription(), template.getContent(), template.isEnabled());
    }
}
