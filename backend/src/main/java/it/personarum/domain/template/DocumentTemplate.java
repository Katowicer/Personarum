package it.personarum.domain.template;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Rappresenta un template persistente utilizzato per generare documenti a partire dai dati di un profilo.
 */
@Entity
@Table(name = "document_templates")
public class DocumentTemplate {

    private static final int MAX_NAME_LENGTH = 120;
    private static final int MAX_DESCRIPTION_LENGTH = 500;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = MAX_NAME_LENGTH)
    private String name;

    @Column(length = MAX_DESCRIPTION_LENGTH)
    private String description;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private boolean enabled;

    protected DocumentTemplate() {
    }

    private DocumentTemplate(String name, String description, String content) {
        changeDetails(name, description, content);
        enabled = true;
    }

    /**
     * Crea un nuovo template inizialmente abilitato.
     *
     * @param name        nome del template
     * @param description descrizione opzionale
     * @param content     contenuto testuale del template
     * @return nuovo template non ancora persistito
     * @throws IllegalArgumentException se nome, descrizione o contenuto non rispettano i vincoli
     */
    public static DocumentTemplate create(String name, String description, String content) {
        return new DocumentTemplate(name, description, content);
    }

    /**
     * Aggiorna nome, descrizione e contenuto del template.
     *
     * @param name        nuovo nome
     * @param description nuova descrizione opzionale
     * @param content     nuovo contenuto
     * @throws IllegalArgumentException se i dati non rispettano i vincoli del dominio
     */
    public void changeDetails(String name, String description, String content) {
        this.name = normalizeName(name);
        this.description = normalizeOptional(description, MAX_DESCRIPTION_LENGTH, "La descrizione non può superare 500 caratteri");

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Contenuto template obbligatorio");
        }
        this.content = content.trim();
    }

    /**
     * Abilita il template rendendolo disponibile per la generazione.
     */
    public void enable() {
        enabled = true;
    }

    /**
     * Disabilita il template impedendone l'uso per nuove generazioni.
     */
    public void disable() {
        enabled = false;
    }

    /**
     * Normalizza e valida il nome di un template.
     *
     * @param name nome da normalizzare
     * @return nome privo di spazi esterni
     * @throws IllegalArgumentException se il nome è assente o supera 120 caratteri
     */
    public static String normalizeName(String name) {
        return requireText(name, "Nome template obbligatorio", MAX_NAME_LENGTH);
    }

    private static String requireText(String value, String message, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }

        String normalized = value.trim();
        if (normalized.length() > maxLength) {
            throw new IllegalArgumentException("Il nome del template non può superare " + maxLength + " caratteri");
        }
        return normalized;
    }

    private static String normalizeOptional(String value, int maxLength, String message) {
        if (value == null || value.isBlank()) {
            return null;
        }

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
