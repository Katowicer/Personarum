ALTER TABLE profile_documents
ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';
ALTER TABLE profile_documents
ADD CONSTRAINT ck_profile_documents_status CHECK (status IN ('ACTIVE', 'ARCHIVED'));
