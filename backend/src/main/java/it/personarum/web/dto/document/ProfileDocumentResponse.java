package it.personarum.web.dto.document;

import it.personarum.domain.document.DocumentType;
import it.personarum.domain.document.ProfileDocument;
import it.personarum.domain.document.ProfileDocumentStatus;

import java.time.LocalDate;

public record ProfileDocumentResponse(
    Long id,
    Long profileId,
    DocumentType type,
    ProfileDocumentStatus status,
    String documentNumber,
    String issuingAuthority,
    LocalDate issueDate,
    LocalDate expirationDate,
    String notes
) {

    public static ProfileDocumentResponse from(
        ProfileDocument document
    ) {
        return new ProfileDocumentResponse(
            document.getId(),
            document.getProfile().getId(),
            document.getType(),
            document.getStatus(),
            document.getDocumentNumber(),
            document.getIssuingAuthority(),
            document.getIssueDate(),
            document.getExpirationDate(),
            document.getNotes()
        );
    }
}
