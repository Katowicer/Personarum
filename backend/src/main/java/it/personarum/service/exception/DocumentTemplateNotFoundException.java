package it.personarum.service.exception;

public class DocumentTemplateNotFoundException extends RuntimeException {

    public DocumentTemplateNotFoundException(Long id) {
        super("Template con id " + id + " non trovato");
    }
}
