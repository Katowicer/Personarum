package it.personarum.domain.document;

import it.personarum.domain.document.state.ProfileDocumentState;
import it.personarum.domain.document.state.ProfileDocumentStates;
import it.personarum.domain.profile.Profile;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Rappresenta un documento associato a un profilo personale.
 */
@Entity
@Table(name = "profile_documents")
public class ProfileDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "profile_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_profile_documents_profile")
    )
    private Profile profile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private DocumentType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProfileDocumentStatus status;

    @Column(name = "document_number", length = 100)
    private String documentNumber;

    @Column(name = "issuing_authority", length = 150)
    private String issuingAuthority;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(length = 1000)
    private String notes;

    protected ProfileDocument() {
    }

    private ProfileDocument(
        Profile profile,
        DocumentType type,
        String documentNumber,
        String issuingAuthority,
        LocalDate issueDate,
        LocalDate expirationDate,
        String notes
    ) {
        this.profile = Objects.requireNonNull(profile, "Profilo obbligatorio");
        this.status = ProfileDocumentStatus.ACTIVE;

        changeDetails(
            type,
            documentNumber,
            issuingAuthority,
            issueDate,
            expirationDate,
            notes
        );
    }

    public static ProfileDocument create(
        Profile profile,
        DocumentType type,
        String documentNumber,
        String issuingAuthority,
        LocalDate issueDate,
        LocalDate expirationDate,
        String notes
    ) {
        return new ProfileDocument(
            profile,
            type,
            documentNumber,
            issuingAuthority,
            issueDate,
            expirationDate,
            notes
        );
    }

    public void changeDetails(
        DocumentType type,
        String documentNumber,
        String issuingAuthority,
        LocalDate issueDate,
        LocalDate expirationDate,
        String notes
    ) throws NullPointerException {
        state().ensureEditable();

        this.type = Objects.requireNonNull(type, "Tipo documento obbligatorio");

        if (issueDate != null && issueDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La data di rilascio non può essere futura");
        }

        if (issueDate != null && expirationDate != null && expirationDate.isBefore(issueDate)) {
            throw new IllegalArgumentException("La data di scadenza non può precedere la data di rilascio");
        }

        this.documentNumber = normalizeOptional(documentNumber);
        this.issuingAuthority = normalizeOptional(issuingAuthority);
        this.issueDate = issueDate;
        this.expirationDate = expirationDate;
        this.notes = normalizeOptional(notes);
    }

    public void archive() {
        status = state().archive();
    }

    public void restore() {
        status = state().restore();
    }

    public void ensureEditable() { state().ensureEditable(); }

    private ProfileDocumentState state() {
        return ProfileDocumentStates.from(status);
    }

    private static String normalizeOptional(String value) {
        if (value == null || value.isBlank()) { return null; }
        return value.trim();
    }

    public Long getId() {
        return id;
    }

    public Profile getProfile() {
        return profile;
    }

    public DocumentType getType() {
        return type;
    }

    public ProfileDocumentStatus getStatus() {
        return status;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public String getIssuingAuthority() {
        return issuingAuthority;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public String getNotes() {
        return notes;
    }
}
