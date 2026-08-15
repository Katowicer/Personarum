package it.personarum.domain.generation;

import it.personarum.domain.profile.Profile;
import it.personarum.domain.template.DocumentTemplate;
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

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Rappresenta l'istantanea persistita di un documento ottenuto applicando una strategia di generazione a un template.
 *
 * <p>Il contenuto viene salvato insieme ai riferimenti al profilo e al template, così una generazione
 * già eseguita rimane consultabile anche se il template viene successivamente modificato.</p>
 */
@Entity
@Table(name = "generated_documents")
public class GeneratedDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false, foreignKey = @ForeignKey(name = "fk_generated_documents_profile"))
    private Profile profile;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "template_id", nullable = false, foreignKey = @ForeignKey(name = "fk_generated_documents_template"))
    private DocumentTemplate template;

    @Enumerated(EnumType.STRING)
    @Column(name = "generation_type", nullable = false, length = 30)
    private DocumentGenerationType generationType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected GeneratedDocument() {
    }

    private GeneratedDocument(Profile profile, DocumentTemplate template, DocumentGenerationType generationType, String content) {
        this.profile = Objects.requireNonNull(profile, "Profilo obbligatorio");
        this.template = Objects.requireNonNull(template, "Template obbligatorio");
        this.generationType = Objects.requireNonNull(generationType, "Strategia obbligatoria");

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Contenuto generato obbligatorio");
        }

        this.content = content;
        createdAt = LocalDateTime.now();
    }

    /**
     * Crea una nuova istantanea di documento generato.
     *
     * @param profile        profilo utilizzato per la generazione
     * @param template       template utilizzato
     * @param generationType tipo di strategia applicata
     * @param content        contenuto finale generato
     * @return nuovo documento generato non ancora persistito
     * @throws NullPointerException     se profilo, template o tipo di generazione sono nulli
     * @throws IllegalArgumentException se il contenuto è assente
     */
    public static GeneratedDocument create(Profile profile, DocumentTemplate template, DocumentGenerationType generationType, String content) {
        return new GeneratedDocument(profile, template, generationType, content);
    }

    public Long getId() {
        return id;
    }

    public Profile getProfile() {
        return profile;
    }

    public DocumentTemplate getTemplate() {
        return template;
    }

    public DocumentGenerationType getGenerationType() {
        return generationType;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
