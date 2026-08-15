package it.personarum.service.exception;

/**
 * Segnala la condizione applicativa rappresentata da ProfileDocumentFileNotFoundException.
 */
public class ProfileDocumentFileNotFoundException extends RuntimeException {

    public ProfileDocumentFileNotFoundException(Long profileId, Long documentId) {
        super("File del documento con id " + documentId + " non trovato per il profilo " + profileId);
    }
}
