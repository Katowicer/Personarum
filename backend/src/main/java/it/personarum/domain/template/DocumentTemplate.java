package it.personarum.domain.template;

import jakarta.persistence.*;

@Entity
@Table(name = "document_templates")
public class DocumentTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private boolean enabled;

    protected DocumentTemplate() {
    }

    private DocumentTemplate(String name, String description, String content) {
        changeDetails(name, description, content);
        this.enabled = true;
    }

    public static DocumentTemplate create(String name, String description, String content) {
        return new DocumentTemplate(name, description, content);
    }

    public void changeDetails(String name, String description, String content) {
        this.name = requireText(name, "Nome template obbligatorio", 120);

        this.description = normalizeOptional(description, 500, "La descrizione non può superare 500 caratteri");

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Contenuto template obbligatorio");
        }

        this.content = content.trim();
    }

    public void enable() {
        enabled = true;
    }

    public void disable() {
        enabled = false;
    }

    private static String requireText(String value, String message, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }

        String normalized = value.trim();

        if (normalized.length() > maxLength) {
            throw new IllegalArgumentException(message);
        }

        return normalized;
    }

    private static String normalizeOptional(String value, int maxLength, String message) {
        if (value == null || value.isBlank()) { return null; }

        String normalized = value.trim();

        if (normalized.length() > maxLength) {
            throw new IllegalArgumentException(message);
        }

        return normalized;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getContent() {
        return content;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
