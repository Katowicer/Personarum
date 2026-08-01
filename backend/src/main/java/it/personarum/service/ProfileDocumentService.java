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

@Service
@Transactional(readOnly = true)
public class ProfileDocumentService {

    private final ProfileRepository profileRepository;
    private final ProfileDocumentRepository profileDocumentRepository;

    public ProfileDocumentService(
        ProfileRepository profileRepository,
        ProfileDocumentRepository profileDocumentRepository
    ) {
        this.profileRepository = profileRepository;
        this.profileDocumentRepository = profileDocumentRepository;
    }

    @Transactional
    public ProfileDocument create(
        Long profileId,
        DocumentType type,
        String documentNumber,
        String issuingAuthority,
        LocalDate issueDate,
        LocalDate expirationDate,
        String notes
    ) {
        Profile profile = findProfile(profileId);

        ProfileDocument document = ProfileDocument.create(
            profile,
            type,
            documentNumber,
            issuingAuthority,
            issueDate,
            expirationDate,
            notes
        );

        return profileDocumentRepository.save(document);
    }

    public List<ProfileDocument> findAllByProfileId(Long profileId) {
        findProfile(profileId);

        return profileDocumentRepository
            .findAllByProfileIdOrderByIdAsc(profileId);
    }

    public ProfileDocument findById(Long profileId, Long documentId) {
        findProfile(profileId);

        return profileDocumentRepository
            .findByIdAndProfileId(documentId, profileId)
            .orElseThrow(
                () -> new ProfileDocumentNotFoundException(
                    profileId,
                    documentId
                )
            );
    }

    @Transactional
    public ProfileDocument update(
        Long profileId,
        Long documentId,
        DocumentType type,
        String documentNumber,
        String issuingAuthority,
        LocalDate issueDate,
        LocalDate expirationDate,
        String notes
    ) {
        ProfileDocument document = findById(profileId, documentId);

        document.changeDetails(
            type,
            documentNumber,
            issuingAuthority,
            issueDate,
            expirationDate,
            notes
        );

        return document;
    }

    @Transactional
    public void delete(Long profileId, Long documentId) {
        ProfileDocument document = findById(profileId, documentId);

        profileDocumentRepository.delete(document);
    }

    private Profile findProfile(Long profileId) {
        return profileRepository
            .findById(profileId)
            .orElseThrow(() -> new ProfileNotFoundException(profileId));
    }
}
