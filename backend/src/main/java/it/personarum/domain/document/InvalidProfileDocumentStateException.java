package it.personarum.domain.document;

public class InvalidProfileDocumentStateException extends IllegalStateException {
    public InvalidProfileDocumentStateException(String message) {
        super(message);
    }
}
