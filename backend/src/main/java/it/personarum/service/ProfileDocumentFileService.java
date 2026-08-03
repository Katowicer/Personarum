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

@Service
@Transactional(readOnly = true)
public class ProfileDocumentFileService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
        "application/pdf",
        "image/jpeg",
        "image/png"
    );

    private final ProfileDocumentRepository profileDocumentRepository;
    private final ProfileDocumentFileRepository profileDocumentFileRepository;

    public ProfileDocumentFileService(
        ProfileDocumentRepository profileDocumentRepository,
        ProfileDocumentFileRepository profileDocumentFileRepository
    ) {
        this.profileDocumentRepository = profileDocumentRepository;
        this.profileDocumentFileRepository = profileDocumentFileRepository;
    }

    @Transactional
    public ProfileDocumentFile upload(
        Long profileId,
        Long documentId,
        String originalFileName,
        String contentType,
        byte[] fileContent
    ) {
        ProfileDocument document = findDocument(profileId, documentId);
        String normalizedContentType = validateContentType(contentType);

        ProfileDocumentFile documentFile = profileDocumentFileRepository
            .findById(documentId)
            .map(existingFile -> {
                existingFile.replace(originalFileName, normalizedContentType, fileContent);
                return existingFile;
            })
            .orElseGet(() -> ProfileDocumentFile.create(document, originalFileName, normalizedContentType, fileContent));

        return profileDocumentFileRepository.save(documentFile);
    }

    public ProfileDocumentFile download(Long profileId, Long documentId) {
        findDocument(profileId, documentId);

        return profileDocumentFileRepository
            .findById(documentId)
            .orElseThrow(() -> new ProfileDocumentFileNotFoundException(profileId, documentId));
    }

    @Transactional
    public void delete(Long profileId, Long documentId) {
        ProfileDocumentFile documentFile = download(profileId, documentId);
        profileDocumentFileRepository.delete(documentFile);
    }

    private ProfileDocument findDocument(Long profileId, Long documentId) {
        return profileDocumentRepository
            .findByIdAndProfileId(documentId, profileId)
            .orElseThrow(() -> new ProfileDocumentNotFoundException(profileId, documentId));
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
