package it.personarum.domain.document;

import it.personarum.domain.document.state.ProfileDocumentState;
import it.personarum.domain.document.state.ProfileDocumentStates;
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
 * Rappresenta un documento personale associato a un profilo.
 *
 * <p>Lo stato del documento è gestito mediante lo State Pattern: un documento attivo
 * può essere modificato, mentre un documento archiviato è trattato come sola lettura.</p>
 */
@Entity
@Table(name = "profile_documents")
public class ProfileDocument {

    private static final int MAX_DOCUMENT_NUMBER_LENGTH = 100;
    private static final int MAX_ISSUING_AUTHORITY_LENGTH = 150;
    private static final int MAX_NOTES_LENGTH = 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false, foreignKey = @ForeignKey(name = "fk_profile_documents_profile"))
    private Profile profile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private DocumentType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProfileDocumentStatus status;

    @Column(name = "document_number", length = MAX_DOCUMENT_NUMBER_LENGTH)
    private String documentNumber;

    @Column(name = "issuing_authority", length = MAX_ISSUING_AUTHORITY_LENGTH)
    private String issuingAuthority;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(length = MAX_NOTES_LENGTH)
    private String notes;

    protected ProfileDocument() {
    }

    private ProfileDocument(Profile profile, DocumentType type, String documentNumber, String issuingAuthority, LocalDate issueDate, LocalDate expirationDate, String notes) {
        this.profile = Objects.requireNonNull(profile, "Profilo obbligatorio");
        status = ProfileDocumentStatus.ACTIVE;
        changeDetails(type, documentNumber, issuingAuthority, issueDate, expirationDate, notes);
    }

    /**
     * Crea un documento inizialmente attivo e associato al profilo indicato.
     *
     * @param profile          profilo proprietario del documento
     * @param type             tipologia del documento
     * @param documentNumber   numero identificativo opzionale
     * @param issuingAuthority ente emittente opzionale
     * @param issueDate        data di rilascio opzionale
     * @param expirationDate   data di scadenza opzionale
     * @param notes            note opzionali
     * @return nuovo documento non ancora persistito
     * @throws NullPointerException     se profilo o tipologia sono nulli
     * @throws IllegalArgumentException se le date o i campi testuali non sono validi
     */
    public static ProfileDocument create(Profile profile, DocumentType type, String documentNumber, String issuingAuthority, LocalDate issueDate, LocalDate expirationDate, String notes) {
        return new ProfileDocument(profile, type, documentNumber, issuingAuthority, issueDate, expirationDate, notes);
    }

    /**
     * Aggiorna i metadati del documento se lo stato corrente consente modifiche.
     *
     * @param type             nuova tipologia del documento
     * @param documentNumber   nuovo numero identificativo opzionale
     * @param issuingAuthority nuovo ente emittente opzionale
     * @param issueDate        nuova data di rilascio opzionale
     * @param expirationDate   nuova data di scadenza opzionale
     * @param notes            nuove note opzionali
     * @throws InvalidProfileDocumentStateException se il documento non è modificabile
     * @throws NullPointerException                 se la tipologia è nulla
     * @throws IllegalArgumentException             se le date o i campi testuali non sono validi
     */
    public void changeDetails(DocumentType type, String documentNumber, String issuingAuthority, LocalDate issueDate, LocalDate expirationDate, String notes) {
        state().ensureEditable();
        this.type = Objects.requireNonNull(type, "Tipo documento obbligatorio");

        if (issueDate != null && issueDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La data di rilascio non può essere futura");
        }
        if (issueDate != null && expirationDate != null && expirationDate.isBefore(issueDate)) {
            throw new IllegalArgumentException("La data di scadenza non può precedere la data di rilascio");
        }

        this.documentNumber = normalizeOptional(documentNumber, MAX_DOCUMENT_NUMBER_LENGTH, "Il numero del documento non può superare 100 caratteri");
        this.issuingAuthority = normalizeOptional(issuingAuthority, MAX_ISSUING_AUTHORITY_LENGTH, "L'ente emittente non può superare 150 caratteri");
        this.issueDate = issueDate;
        this.expirationDate = expirationDate;
        this.notes = normalizeOptional(notes, MAX_NOTES_LENGTH, "Le note non possono superare 1000 caratteri");
    }

    /**
     * Archivia il documento applicando la transizione prevista dallo stato corrente.
     *
     * @throws InvalidProfileDocumentStateException se il documento è già archiviato
     */
    public void archive() {
        status = state().archive();
    }

    /**
     * Ripristina il documento applicando la transizione prevista dallo stato corrente.
     *
     * @throws InvalidProfileDocumentStateException se il documento è già attivo
     */
    public void restore() {
        status = state().restore();
    }

    /**
     * Verifica che il documento sia modificabile nello stato corrente.
     *
     * @throws InvalidProfileDocumentStateException se il documento è archiviato
     */
    public void ensureEditable() {
        state().ensureEditable();
    }

    private ProfileDocumentState state() {
        return ProfileDocumentStates.from(status);
    }

    private static String normalizeOptional(String value, int maxLength, String lengthMessage) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.trim();
        if (normalized.length() > maxLength) {
            throw new IllegalArgumentException(lengthMessage);
        }
        return normalized;
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
