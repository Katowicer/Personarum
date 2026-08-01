package it.personarum.web.dto.document;

import it.personarum.domain.document.DocumentType;
import it.personarum.domain.document.ProfileDocument;

import java.time.LocalDate;

public record ProfileDocumentResponse(
    Long id,
    Long profileId,
    DocumentType type,
    String documentNumber,
    String issuingAuthority,
    LocalDate issueDate,
    LocalDate expirationDate,
    String notes
) {

    public static ProfileDocumentResponse from(ProfileDocument document) {
        return new ProfileDocumentResponse(
            document.getId(),
            document.getProfile().getId(),
            document.getType(),
            document.getDocumentNumber(),
            document.getIssuingAuthority(),
            document.getIssueDate(),
            document.getExpirationDate(),
            document.getNotes()
        );
    }
}
