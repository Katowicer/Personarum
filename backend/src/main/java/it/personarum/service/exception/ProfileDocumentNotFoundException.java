package it.personarum.service.exception;

public class ProfileDocumentNotFoundException extends RuntimeException {

    public ProfileDocumentNotFoundException(Long profileId, Long documentId) {
        super("Documento con id " + documentId + " non trovato per il profilo " + profileId);
    }
}
