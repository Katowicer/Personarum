package it.personarum.service.exception;

/**
 * Segnala la condizione applicativa rappresentata da DocumentTemplateDisabledException.
 */
public class DocumentTemplateDisabledException extends RuntimeException {

    public DocumentTemplateDisabledException(Long templateId) {
        super("Template con id " + templateId + " non abilitato");
    }
}
