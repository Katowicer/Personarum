package it.personarum.domain.document;

import it.personarum.domain.profile.Profile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

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

        changeDetails(
            type,
            documentNumber,
            issuingAuthority,
            issueDate,
            expirationDate,
            notes
        );
    }

    /**
     * Crea un documento associato al profilo indicato.
     */
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

    /**
     * Modifica i metadati del documento.
     */
    public void changeDetails(
        DocumentType type,
        String documentNumber,
        String issuingAuthority,
        LocalDate issueDate,
        LocalDate expirationDate,
        String notes
    ) throws IllegalArgumentException {
        this.type = Objects.requireNonNull(
            type, "Tipo documento obbligatorio"
        );

        if (issueDate != null && issueDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La data di rilascio non può essere futura");
        }

        if (issueDate != null && expirationDate != null && expirationDate.isBefore(issueDate)) {
            throw new IllegalArgumentException(
                "La data di scadenza non può precedere la data di rilascio"
            );
        }

        this.documentNumber = normalizeOptional(documentNumber);
        this.issuingAuthority = normalizeOptional(issuingAuthority);
        this.issueDate = issueDate;
        this.expirationDate = expirationDate;
        this.notes = normalizeOptional(notes);
    }

    private static String normalizeOptional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public DocumentType getType() {
        return type;
    }

    public void setType(DocumentType type) {
        this.type = type;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getIssuingAuthority() {
        return issuingAuthority;
    }

    public void setIssuingAuthority(String issuingAuthority) {
        this.issuingAuthority = issuingAuthority;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
