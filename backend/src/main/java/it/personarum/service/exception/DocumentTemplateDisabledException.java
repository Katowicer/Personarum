package it.personarum.service.exception;

public class DocumentTemplateDisabledException extends RuntimeException {

    public DocumentTemplateDisabledException(Long templateId) {
        super("Template con id " + templateId + " non abilitato");
    }
}
