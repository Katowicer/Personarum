package it.personarum.service.exception;

/**
 * Segnala la condizione applicativa rappresentata da ProfileDocumentNotFoundException.
 */
public class ProfileDocumentNotFoundException extends RuntimeException {

    public ProfileDocumentNotFoundException(Long profileId, Long documentId) {
        super("Documento con id " + documentId + " non trovato per il profilo " + profileId);
    }
}
