package it.personarum.web.dto.document;

import it.personarum.domain.document.DocumentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Trasferisce i dati dell’operazione CreateProfileDocumentRequest tra API REST e livello applicativo.
 */
public record CreateProfileDocumentRequest(@NotNull DocumentType type, @Size(max = 100) String documentNumber,
                                           @Size(max = 150) String issuingAuthority, @PastOrPresent LocalDate issueDate,
                                           LocalDate expirationDate, @Size(max = 1000) String notes) {
}
