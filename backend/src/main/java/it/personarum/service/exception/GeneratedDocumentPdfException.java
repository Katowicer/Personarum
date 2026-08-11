package it.personarum.service.exception;

public class GeneratedDocumentPdfException extends RuntimeException {

    public GeneratedDocumentPdfException(Throwable cause) {
        super("Impossibile generare il documento PDF", cause);
    }
}
