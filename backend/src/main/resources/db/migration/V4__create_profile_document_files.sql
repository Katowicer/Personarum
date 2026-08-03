CREATE TABLE profile_document_files (
    document_id BIGINT PRIMARY KEY,
    original_file_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,
    file_content BYTEA NOT NULL,
    CONSTRAINT fk_profile_document_files_document FOREIGN KEY (document_id) REFERENCES profile_documents (id) ON DELETE CASCADE,
    CONSTRAINT ck_profile_document_files_size CHECK (
        file_size > 0
        AND file_size <= (5 * 1024 * 1024)
    )
);
