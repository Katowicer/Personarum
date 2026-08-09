package it.personarum.service.exception;

public class DocumentTemplateNameAlreadyExistsException extends RuntimeException {

    public DocumentTemplateNameAlreadyExistsException(String name) {
        super("Esiste già un template chiamato " + name);
    }
}
