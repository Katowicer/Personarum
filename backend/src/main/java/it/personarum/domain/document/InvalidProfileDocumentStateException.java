package it.personarum.domain.document;

/**
 * Segnala un’operazione incompatibile con lo stato corrente di un documento personale.
 */
public class InvalidProfileDocumentStateException extends IllegalStateException {
    public InvalidProfileDocumentStateException(String message) {
        super(message);
    }
}
