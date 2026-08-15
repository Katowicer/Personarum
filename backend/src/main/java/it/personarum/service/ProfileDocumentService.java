package it.personarum.service;

import it.personarum.domain.document.DocumentType;
import it.personarum.domain.document.ProfileDocument;
import it.personarum.domain.profile.Profile;
import it.personarum.repository.ProfileDocumentRepository;
import it.personarum.repository.ProfileRepository;
import it.personarum.service.exception.ProfileDocumentNotFoundException;
import it.personarum.service.exception.ProfileNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Coordina i casi d'uso relativi ai documenti personali associati ai profili.
 */
@Service
@Transactional(readOnly = true)
public class ProfileDocumentService {

    private final ProfileRepository profileRepository;
    private final ProfileDocumentRepository profileDocumentRepository;

    /**
     * Crea il servizio dei documenti personali.
     *
     * @param profileRepository         repository dei profili
     * @param profileDocumentRepository repository dei documenti personali
     */
    public ProfileDocumentService(ProfileRepository profileRepository, ProfileDocumentRepository profileDocumentRepository) {
        this.profileRepository = profileRepository;
        this.profileDocumentRepository = profileDocumentRepository;
    }

    /**
     * Crea un documento associandolo al profilo indicato.
     *
     * @param profileId        identificativo del profilo
     * @param type             tipo di documento
     * @param documentNumber   numero identificativo opzionale
     * @param issuingAuthority ente emittente opzionale
     * @param issueDate        data di rilascio opzionale
     * @param expirationDate   data di scadenza opzionale
     * @param notes            note opzionali
     * @return documento creato e persistito
     * @throws ProfileNotFoundException se il profilo non esiste
     * @throws IllegalArgumentException se i dati non rispettano le regole del dominio
     */
    @Transactional
    public ProfileDocument create(Long profileId, DocumentType type, String documentNumber, String issuingAuthority, LocalDate issueDate, LocalDate expirationDate, String notes) {
        Profile profile = findProfile(profileId);
        ProfileDocument document = ProfileDocument.create(profile, type, documentNumber, issuingAuthority, issueDate, expirationDate, notes);
        return profileDocumentRepository.save(document);
    }

    /**
     * Restituisce tutti i documenti associati al profilo.
     *
     * @param profileId identificativo del profilo
     * @return documenti ordinati per identificativo
     * @throws ProfileNotFoundException se il profilo non esiste
     */
    public List<ProfileDocument> findAllByProfileId(Long profileId) {
        findProfile(profileId);
        return profileDocumentRepository.findAllByProfileIdOrderByIdAsc(profileId);
    }

    /**
     * Recupera un documento verificando che appartenga al profilo indicato.
     *
     * @param profileId  identificativo del profilo
     * @param documentId identificativo del documento
     * @return documento trovato
     * @throws ProfileNotFoundException         se il profilo non esiste
     * @throws ProfileDocumentNotFoundException se il documento non esiste per il profilo
     */
    public ProfileDocument findById(Long profileId, Long documentId) {
        findProfile(profileId);
        return profileDocumentRepository.findByIdAndProfileId(documentId, profileId).orElseThrow(() -> new ProfileDocumentNotFoundException(profileId, documentId));
    }

    /**
     * Aggiorna i metadati di un documento attivo.
     *
     * @param profileId        identificativo del profilo
     * @param documentId       identificativo del documento
     * @param type             tipo di documento
     * @param documentNumber   numero identificativo opzionale
     * @param issuingAuthority ente emittente opzionale
     * @param issueDate        data di rilascio opzionale
     * @param expirationDate   data di scadenza opzionale
     * @param notes            note opzionali
     * @return documento aggiornato
     * @throws ProfileDocumentNotFoundException                                   se il documento non esiste per il profilo
     * @throws it.personarum.domain.document.InvalidProfileDocumentStateException se il documento è archiviato
     * @throws IllegalArgumentException                                           se i dati non rispettano le regole del dominio
     */
    @Transactional
    public ProfileDocument update(Long profileId, Long documentId, DocumentType type, String documentNumber, String issuingAuthority, LocalDate issueDate, LocalDate expirationDate, String notes) {
        ProfileDocument document = findById(profileId, documentId);
        document.changeDetails(type, documentNumber, issuingAuthority, issueDate, expirationDate, notes);
        return document;
    }

    /**
     * Elimina un documento se lo stato corrente lo consente.
     *
     * @param profileId  identificativo del profilo
     * @param documentId identificativo del documento
     * @throws ProfileDocumentNotFoundException                                   se il documento non esiste per il profilo
     * @throws it.personarum.domain.document.InvalidProfileDocumentStateException se il documento non è eliminabile
     */
    @Transactional
    public void delete(Long profileId, Long documentId) {
        ProfileDocument document = findById(profileId, documentId);
        document.ensureEditable();
        profileDocumentRepository.delete(document);
    }

    /**
     * Archivia un documento modificandone lo stato tramite lo State Pattern.
     *
     * @param profileId  identificativo del profilo
     * @param documentId identificativo del documento
     * @return documento archiviato
     * @throws ProfileDocumentNotFoundException                                   se il documento non esiste per il profilo
     * @throws it.personarum.domain.document.InvalidProfileDocumentStateException se la transizione non è consentita
     */
    @Transactional
    public ProfileDocument archive(Long profileId, Long documentId) {
        ProfileDocument document = findById(profileId, documentId);
        document.archive();
        return document;
    }

    /**
     * Ripristina un documento archiviato tramite lo State Pattern.
     *
     * @param profileId  identificativo del profilo
     * @param documentId identificativo del documento
     * @return documento ripristinato
     * @throws ProfileDocumentNotFoundException                                   se il documento non esiste per il profilo
     * @throws it.personarum.domain.document.InvalidProfileDocumentStateException se la transizione non è consentita
     */
    @Transactional
    public ProfileDocument restore(Long profileId, Long documentId) {
        ProfileDocument document = findById(profileId, documentId);
        document.restore();
        return document;
    }

    private Profile findProfile(Long profileId) {
        return profileRepository.findById(profileId).orElseThrow(() -> new ProfileNotFoundException(profileId));
    }
}
