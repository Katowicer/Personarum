package it.personarum.domain.document;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

@Entity
@Table(name = "profile_document_files")
public class ProfileDocumentFile {

    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024;

    @Id
    @Column(name = "document_id")
    private Long documentId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_id", nullable = false, foreignKey = @ForeignKey(name = "fk_profile_document_files_document"))
    private ProfileDocument document;

    @Column(name = "original_file_name", nullable = false, length = 255)
    private String originalFileName;

    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "file_content", nullable = false, columnDefinition = "bytea")
    private byte[] fileContent;

    protected ProfileDocumentFile() {
    }

    private ProfileDocumentFile(
            ProfileDocument document,
            String originalFileName,
            String contentType,
            byte[] fileContent) {
        this.document = Objects.requireNonNull(document, "Documento obbligatorio");

        replace(originalFileName, contentType, fileContent);
    }

    public static ProfileDocumentFile create(
            ProfileDocument document,
            String originalFileName,
            String contentType,
            byte[] fileContent) {
        return new ProfileDocumentFile(document, originalFileName, contentType, fileContent);
    }

    public void replace(String originalFileName, String contentType, byte[] fileContent) {
        String normalizedFileName = normalizeRequired(originalFileName, "Nome del file obbligatorio");

        String normalizedContentType = normalizeRequired(contentType, "Tipo del file obbligatorio").toLowerCase(Locale.ROOT);

        byte[] validatedContent = validateContent(fileContent);

        if (normalizedFileName.length() > 255) {
            throw new IllegalArgumentException("Il nome del file non può superare 255 caratteri");
        }

        if (normalizedContentType.length() > 100) {
            throw new IllegalArgumentException("Il tipo del file non può superare 10 caratteri");
        }

        this.originalFileName = normalizedFileName;
        this.contentType = normalizedContentType;
        this.fileSize = (long) validatedContent.length;
        this.fileContent = validatedContent;
    }

    private String normalizeRequired(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }

        return value.trim();
    }

    private byte[] validateContent(byte[] content) {
        if (content == null || content.length == 0) {
            throw new IllegalArgumentException("Il contenuto del file è obbligatorio");
        }

        if (content.length > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Il file non può superare 5 MiB");
        }

        return Arrays.copyOf(content, content.length);
    }

    public Long getDocumentId() {
        return documentId;
    }

    public ProfileDocument getDocument() {
        return document;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public String getContentType() {
        return contentType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public byte[] getFileContent() {
        return Arrays.copyOf(fileContent, fileContent.length);
    }
}
