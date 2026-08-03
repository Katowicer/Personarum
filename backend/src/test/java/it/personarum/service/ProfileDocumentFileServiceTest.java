package it.personarum.service;

import it.personarum.domain.document.ProfileDocument;
import it.personarum.domain.document.ProfileDocumentFile;
import it.personarum.repository.ProfileDocumentFileRepository;
import it.personarum.repository.ProfileDocumentRepository;
import it.personarum.service.exception.ProfileDocumentFileNotFoundException;
import it.personarum.service.exception.ProfileDocumentNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileDocumentFileServiceTest {

    private static final Long PROFILE_ID = 1L;
    private static final Long DOCUMENT_ID = 10L;

    @Mock
    private ProfileDocumentRepository profileDocumentRepository;

    @Mock
    private ProfileDocumentFileRepository profileDocumentFileRepository;

    private ProfileDocumentFileService service;

    @BeforeEach
    void setUp() {
        service = new ProfileDocumentFileService(profileDocumentRepository, profileDocumentFileRepository);
    }

    @Test
    void shouldUploadNewFile() {
        ProfileDocument document = org.mockito.Mockito.mock(ProfileDocument.class);
        byte[] content = "pdf-content".getBytes();

        when(profileDocumentRepository.findByIdAndProfileId(DOCUMENT_ID, PROFILE_ID)).thenReturn(Optional.of(document));
        when(profileDocumentFileRepository.findById(DOCUMENT_ID)).thenReturn(Optional.empty());
        when(profileDocumentFileRepository.save(any(ProfileDocumentFile.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        ProfileDocumentFile result = service.upload(
            PROFILE_ID,
            DOCUMENT_ID,
            " document.pdf ",
            "APPLICATION/PDF", content
        );

        assertThat(result.getDocument()).isSameAs(document);
        assertThat(result.getOriginalFileName()).isEqualTo("document.pdf");
        assertThat(result.getContentType()).isEqualTo("application/pdf");
        assertThat(result.getFileSize()).isEqualTo((long) content.length);
        assertThat(result.getFileContent()).isEqualTo(content);

        verify(profileDocumentFileRepository).save(result);
    }

    @Test
    void shouldReplaceExistingFile() {
        ProfileDocument document = org.mockito.Mockito.mock(ProfileDocument.class);

        ProfileDocumentFile existingFile = ProfileDocumentFile.create(
            document,
            "old.pdf",
            "application/pdf",
            "old-content".getBytes()
        );

        byte[] newContent = "new-content".getBytes();

        when(profileDocumentRepository.findByIdAndProfileId(DOCUMENT_ID, PROFILE_ID)).thenReturn(Optional.of(document));
        when(profileDocumentFileRepository.findById(DOCUMENT_ID)).thenReturn(Optional.of(existingFile));
        when(profileDocumentFileRepository.save(existingFile)).thenReturn(existingFile);

        ProfileDocumentFile result = service.upload(
            PROFILE_ID,
            DOCUMENT_ID,
            "new-image.png",
            "image/png",
            newContent
        );

        assertThat(result).isSameAs(existingFile);
        assertThat(result.getOriginalFileName()).isEqualTo("new-image.png");
        assertThat(result.getContentType()).isEqualTo("image/png");
        assertThat(result.getFileContent()).isEqualTo(newContent);

        verify(profileDocumentFileRepository).save(existingFile);
    }

    @Test
    void shouldRejectUnsupportedContentType() {
        ProfileDocument document = org.mockito.Mockito.mock(ProfileDocument.class);

        when(profileDocumentRepository.findByIdAndProfileId(DOCUMENT_ID, PROFILE_ID)).thenReturn(Optional.of(document));

        assertThatThrownBy(() -> service.upload(
            PROFILE_ID,
            DOCUMENT_ID,
            "document.txt",
            "text/plain",
            "content".getBytes()
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Formato file non supportato");

        verify(profileDocumentFileRepository, never()).save(any(ProfileDocumentFile.class));
    }

    @Test
    void shouldRejectUploadWhenDocumentDoesNotExist() {
        when(profileDocumentRepository.findByIdAndProfileId(DOCUMENT_ID, PROFILE_ID))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.upload(
            PROFILE_ID,
            DOCUMENT_ID,
            "document.pdf",
            "application/pdf",
            "content".getBytes()
        )).isInstanceOf(ProfileDocumentNotFoundException.class);

        verify(profileDocumentFileRepository, never()).save(any(ProfileDocumentFile.class));
    }

    @Test
    void shouldDownloadFile() {
        ProfileDocument document = org.mockito.Mockito.mock(
            ProfileDocument.class
        );

        ProfileDocumentFile documentFile = ProfileDocumentFile.create(
            document,
            "document.pdf",
            "application/pdf",
            "content".getBytes()
        );

        when(profileDocumentRepository.findByIdAndProfileId(DOCUMENT_ID, PROFILE_ID)).thenReturn(Optional.of(document));
        when(profileDocumentFileRepository.findById(DOCUMENT_ID)).thenReturn(Optional.of(documentFile));

        ProfileDocumentFile result = service.download(PROFILE_ID,  DOCUMENT_ID);

        assertThat(result).isSameAs(documentFile);
    }

    @Test
    void shouldThrowWhenFileDoesNotExist() {
        ProfileDocument document = org.mockito.Mockito.mock(ProfileDocument.class);

        when(profileDocumentRepository.findByIdAndProfileId(DOCUMENT_ID, PROFILE_ID)).thenReturn(Optional.of(document));
        when(profileDocumentFileRepository.findById(DOCUMENT_ID)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.download(PROFILE_ID, DOCUMENT_ID)).isInstanceOf(ProfileDocumentFileNotFoundException.class);
    }

    @Test
    void shouldDeleteFile() {
        ProfileDocument document = org.mockito.Mockito.mock(ProfileDocument.class);

        ProfileDocumentFile documentFile = ProfileDocumentFile.create(
            document,
            "document.pdf",
            "application/pdf",
            "content".getBytes()
        );

        when(profileDocumentRepository.findByIdAndProfileId(DOCUMENT_ID, PROFILE_ID)).thenReturn(Optional.of(document));
        when(profileDocumentFileRepository.findById(DOCUMENT_ID)).thenReturn(Optional.of(documentFile));

        service.delete(PROFILE_ID, DOCUMENT_ID);

        verify(profileDocumentFileRepository).delete(documentFile);
    }
}
