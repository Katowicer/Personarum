package it.personarum.service.exception;

public class ProfileDocumentFileNotFoundException extends RuntimeException {

    public ProfileDocumentFileNotFoundException(Long profileId, Long documentId) {
        super("File del documento con id " + documentId + " non trovato per il profilo " + profileId);
    }
}
