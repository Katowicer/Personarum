package it.personarum.service.exception;

/**
 * Segnala la condizione applicativa rappresentata da DocumentTemplateNameAlreadyExistsException.
 */
public class DocumentTemplateNameAlreadyExistsException extends RuntimeException {

    public DocumentTemplateNameAlreadyExistsException(String name) {
        super("Esiste già un template chiamato " + name);
    }
}
