package it.personarum.service.exception;

/**
 * Segnala la condizione applicativa rappresentata da GeneratedDocumentPdfException.
 */
public class GeneratedDocumentPdfException extends RuntimeException {

    public GeneratedDocumentPdfException(Throwable cause) {
        super("Impossibile generare il documento PDF", cause);
    }
}
