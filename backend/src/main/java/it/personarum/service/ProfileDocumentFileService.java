package it.personarum.service;

import it.personarum.domain.document.ProfileDocument;
import it.personarum.domain.document.ProfileDocumentFile;
import it.personarum.repository.ProfileDocumentFileRepository;
import it.personarum.repository.ProfileDocumentRepository;
import it.personarum.service.exception.ProfileDocumentFileNotFoundException;
import it.personarum.service.exception.ProfileDocumentNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Set;

/**
 * Gestisce il caricamento, il recupero e la rimozione dei file associati ai documenti di un profilo.
 */
@Service
@Transactional(readOnly = true)
public class ProfileDocumentFileService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("application/pdf", "image/jpeg", "image/png");

    private final ProfileDocumentRepository profileDocumentRepository;
    private final ProfileDocumentFileRepository profileDocumentFileRepository;

    /**
     * Crea il servizio dei file documentali.
     *
     * @param profileDocumentRepository     repository dei documenti personali
     * @param profileDocumentFileRepository repository dei file allegati
     */
    public ProfileDocumentFileService(ProfileDocumentRepository profileDocumentRepository, ProfileDocumentFileRepository profileDocumentFileRepository) {
        this.profileDocumentRepository = profileDocumentRepository;
        this.profileDocumentFileRepository = profileDocumentFileRepository;
    }

    /**
     * Carica o sostituisce il file associato a un documento attivo.
     *
     * @param profileId        identificativo del profilo
     * @param documentId       identificativo del documento
     * @param originalFileName nome originale del file
     * @param contentType      tipo MIME dichiarato dal client
     * @param fileContent      contenuto binario del file
     * @return file persistito
     * @throws ProfileDocumentNotFoundException                                   se il documento non appartiene al profilo
     * @throws it.personarum.domain.document.InvalidProfileDocumentStateException se il documento è archiviato
     * @throws IllegalArgumentException                                           se il formato o il contenuto del file non è valido
     */
    @Transactional
    public ProfileDocumentFile upload(Long profileId, Long documentId, String originalFileName, String contentType, byte[] fileContent) {
        ProfileDocument document = findDocument(profileId, documentId);
        document.ensureEditable();
        String normalizedContentType = validateContentType(contentType);

        ProfileDocumentFile documentFile = profileDocumentFileRepository.findById(documentId).map(existingFile -> {
            existingFile.replace(originalFileName, normalizedContentType, fileContent);
            return existingFile;
        }).orElseGet(() -> ProfileDocumentFile.create(document, originalFileName, normalizedContentType, fileContent));

        return profileDocumentFileRepository.save(documentFile);
    }

    /**
     * Recupera il file associato a un documento.
     *
     * @param profileId  identificativo del profilo
     * @param documentId identificativo del documento
     * @return file associato
     * @throws ProfileDocumentNotFoundException     se il documento non appartiene al profilo
     * @throws ProfileDocumentFileNotFoundException se il documento non possiede un file allegato
     */
    public ProfileDocumentFile download(Long profileId, Long documentId) {
        findDocument(profileId, documentId);
        return profileDocumentFileRepository.findById(documentId).orElseThrow(() -> new ProfileDocumentFileNotFoundException(profileId, documentId));
    }

    /**
     * Elimina il file allegato senza eliminare i metadati del documento.
     *
     * @param profileId  identificativo del profilo
     * @param documentId identificativo del documento
     * @throws ProfileDocumentNotFoundException                                   se il documento non appartiene al profilo
     * @throws ProfileDocumentFileNotFoundException                               se il file non esiste
     * @throws it.personarum.domain.document.InvalidProfileDocumentStateException se il documento è archiviato
     */
    @Transactional
    public void delete(Long profileId, Long documentId) {
        ProfileDocument document = findDocument(profileId, documentId);
        document.ensureEditable();
        ProfileDocumentFile documentFile = profileDocumentFileRepository.findById(documentId).orElseThrow(() -> new ProfileDocumentFileNotFoundException(profileId, documentId));
        profileDocumentFileRepository.delete(documentFile);
    }

    private ProfileDocument findDocument(Long profileId, Long documentId) {
        return profileDocumentRepository.findByIdAndProfileId(documentId, profileId).orElseThrow(() -> new ProfileDocumentNotFoundException(profileId, documentId));
    }

    private String validateContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("Il tipo del file è obbligatorio");
        }

        String normalizedContentType = contentType.trim().toLowerCase(Locale.ROOT);
        if (!ALLOWED_CONTENT_TYPES.contains(normalizedContentType)) {
            throw new IllegalArgumentException("Formato file non supportato. Sono ammessi PDF, JPEG e PNG");
        }
        return normalizedContentType;
    }
}
