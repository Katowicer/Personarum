ALTER TABLE profile_documents DROP CONSTRAINT IF EXISTS ck_profile_documents_type;
UPDATE profile_documents
SET type = 'FISCAL_CARD'
WHERE type = 'HEALTH_CARD';
ALTER TABLE profile_documents
ADD CONSTRAINT ck_profile_documents_type CHECK (
        type IN (
            'IDENTITY_CARD',
            'DRIVING_LICENSE',
            'PASSPORT',
            'FISCAL_CARD',
            'OTHER'
        )
    );
