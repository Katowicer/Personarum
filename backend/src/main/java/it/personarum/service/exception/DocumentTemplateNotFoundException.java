package it.personarum.service.exception;

/**
 * Segnala la condizione applicativa rappresentata da DocumentTemplateNotFoundException.
 */
public class DocumentTemplateNotFoundException extends RuntimeException {

    public DocumentTemplateNotFoundException(Long id) {
        super("Template con id " + id + " non trovato");
    }
}
