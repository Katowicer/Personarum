package it.personarum.service.exception;

public class GeneratedDocumentNotFoundException extends RuntimeException {

    public GeneratedDocumentNotFoundException(Long profileId, Long documentId) {
        super("Documento generato con id " + documentId + " non trovato per il profilo " + profileId);
    }
}
