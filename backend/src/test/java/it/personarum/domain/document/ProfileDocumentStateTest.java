package it.personarum.domain.document;

import it.personarum.domain.profile.Profile;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ProfileDocumentStateTest {

    @Test
    void shouldCreateActiveDocument() {
        ProfileDocument document = createDocument();
        assertThat(document.getStatus()).isEqualTo(ProfileDocumentStatus.ACTIVE);
    }

    @Test
    void shouldArchiveDocument() {
        ProfileDocument document = createDocument();
        document.archive();
        assertThat(document.getStatus()).isEqualTo(ProfileDocumentStatus.ARCHIVED);
    }

    @Test
    void shouldPreventEditingArchivedDocument() {
        ProfileDocument document = createDocument();

        document.archive();
        assertThatThrownBy(() -> document.changeDetails(
            DocumentType.PASSPORT,
            "AA123",
            null,
            null,
            null,
            null)
        ).isInstanceOf(InvalidProfileDocumentStateException.class);
    }

    @Test
    void shouldRestoreArchivedDocument() {
        ProfileDocument document = createDocument();

        document.archive();
        document.restore();

        assertThat(document.getStatus()).isEqualTo(ProfileDocumentStatus.ACTIVE);
    }

    @Test
    void shouldPreventArchivingTwice() {
        ProfileDocument document = createDocument();

        document.archive();
        assertThatThrownBy(document::archive).isInstanceOf(InvalidProfileDocumentStateException.class);
    }

    @Test
    void shouldPreventRestoringActiveDocument() {
        ProfileDocument document = createDocument();

        assertThatThrownBy(document::restore).isInstanceOf(InvalidProfileDocumentStateException.class);
    }

    private ProfileDocument createDocument() {
        return ProfileDocument.create(
            mock(Profile.class),
            DocumentType.PASSPORT,
            "AA123",
            null,
            null,
            null,
            null
        );
    }
}
