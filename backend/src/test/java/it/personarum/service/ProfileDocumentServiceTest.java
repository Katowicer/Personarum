package it.personarum.service;

import it.personarum.domain.document.DocumentType;
import it.personarum.domain.document.ProfileDocument;
import it.personarum.domain.profile.Profile;
import it.personarum.repository.ProfileDocumentRepository;
import it.personarum.repository.ProfileRepository;
import it.personarum.service.exception.ProfileDocumentNotFoundException;
import it.personarum.service.exception.ProfileNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileDocumentServiceTest {

    private static final Long PROFILE_ID = 1L;
    private static final Long DOCUMENT_ID = 10L;
    private static final Long OTHER_PROFILE_ID = 2L;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private ProfileDocumentRepository profileDocumentRepository;

    @InjectMocks
    private ProfileDocumentService profileDocumentService;

    @Test
    void shouldCreateProfileDocument() {
        Profile profile = createProfile();

        when(profileRepository.findById(PROFILE_ID)).thenReturn(Optional.of(profile));
        when(profileDocumentRepository.save(any(ProfileDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProfileDocument result = profileDocumentService.create(
            PROFILE_ID,
            DocumentType.IDENTITY_CARD,
            "  CA1234567  ",
            "  Comune di Milano  ",
            LocalDate.of(2020, 1, 15),
            LocalDate.of(2030, 1, 15),
            "  Documento principale  "
        );

        assertAll(
            () -> assertSame(profile, result.getProfile()),
            () -> assertEquals(DocumentType.IDENTITY_CARD, result.getType()),
            () -> assertEquals("CA1234567", result.getDocumentNumber()),
            () -> assertEquals("Comune di Milano", result.getIssuingAuthority()),
            () -> assertEquals(LocalDate.of(2020, 1, 15), result.getIssueDate()),
            () -> assertEquals(LocalDate.of(2030, 1, 15), result.getExpirationDate()),
            () -> assertEquals("Documento principale", result.getNotes()));

        verify(profileRepository).findById(PROFILE_ID);
        verify(profileDocumentRepository).save(result);
        verifyNoMoreInteractions(profileRepository, profileDocumentRepository);
    }

    @Test
    void shouldFindAllDocumentsForProfile() {
        Profile profile = createProfile();

        ProfileDocument firstDocument = ProfileDocument.create(
            profile,
            DocumentType.IDENTITY_CARD,
            "CA1234567",
            "Comune di Milano",
            LocalDate.of(2020, 1, 15),
            LocalDate.of(2030, 1, 15),
            null);

        ProfileDocument secondDocument = ProfileDocument.create(
            profile,
            DocumentType.PASSPORT,
            "YA9876543",
            "Questura di Como",
            LocalDate.of(2022, 5, 10),
            LocalDate.of(2032, 5, 10),
            null
        );

        List<ProfileDocument> documents = List.of(firstDocument, secondDocument);

        when(profileRepository.findById(PROFILE_ID)).thenReturn(Optional.of(profile));
        when(profileDocumentRepository.findAllByProfileIdOrderByIdAsc(PROFILE_ID)).thenReturn(documents);

        List<ProfileDocument> result = profileDocumentService.findAllByProfileId(PROFILE_ID);
        assertSame(documents, result);

        verify(profileRepository).findById(PROFILE_ID);
        verify(profileDocumentRepository).findAllByProfileIdOrderByIdAsc(PROFILE_ID);
        verifyNoMoreInteractions(profileRepository, profileDocumentRepository);
    }

    @Test
    void shouldRejectOperationWhenProfileDoesNotExist() {
        when(profileRepository.findById(PROFILE_ID)).thenReturn(Optional.empty());

        ProfileNotFoundException exception = assertThrows(
            ProfileNotFoundException.class,
            () -> profileDocumentService.findAllByProfileId(PROFILE_ID)
        );

        assertEquals("Profilo con id 1 non trovato", exception.getMessage());

        verify(profileRepository).findById(PROFILE_ID);
        verifyNoMoreInteractions(profileRepository);
        verifyNoInteractions(profileDocumentRepository);
    }

    @Test
    void shouldFindDocumentById() {
        Profile profile = createProfile();

        ProfileDocument document = ProfileDocument.create(
            profile,
            DocumentType.PASSPORT,
            "YA9876543",
            "Questura di Como",
            LocalDate.of(2007, 7, 7),
            LocalDate.of(2027, 7, 7),
            null
        );

        when(profileRepository.findById(PROFILE_ID)).thenReturn(Optional.of(profile));
        when(profileDocumentRepository.findByIdAndProfileId(DOCUMENT_ID, PROFILE_ID)).thenReturn(Optional.of(document));

        ProfileDocument result = profileDocumentService.findById(PROFILE_ID, DOCUMENT_ID);
        assertSame(document, result);

        verify(profileRepository).findById(PROFILE_ID);
        verify(profileDocumentRepository).findByIdAndProfileId(DOCUMENT_ID, PROFILE_ID);
        verifyNoMoreInteractions(profileRepository, profileDocumentRepository);
    }

    @Test
    void shouldThrowWhenDocumentDoesNotExist() {
        Profile profile = createProfile();

        when(profileRepository.findById(PROFILE_ID)).thenReturn(Optional.of(profile));
        when(profileDocumentRepository.findByIdAndProfileId(DOCUMENT_ID, PROFILE_ID)).thenReturn(Optional.empty());

        ProfileDocumentNotFoundException exception = assertThrows(
            ProfileDocumentNotFoundException.class,
            () -> profileDocumentService.findById(PROFILE_ID, DOCUMENT_ID)
        );

        assertEquals("Documento con id 10 non trovato per il profilo 1", exception.getMessage());

        verify(profileRepository).findById(PROFILE_ID);
        verify(profileDocumentRepository).findByIdAndProfileId(DOCUMENT_ID, PROFILE_ID);
        verifyNoMoreInteractions(profileRepository, profileDocumentRepository);
    }

    @Test
    void shouldRejectDocumentBelongingToAnotherProfile() {
        Profile requestedProfile = createProfile();

        when(profileRepository.findById(OTHER_PROFILE_ID)).thenReturn(Optional.of(requestedProfile));

        /*
         * Il documento potrebbe essere stato inserito ma non essere associato al
         * profilo indicato.
         */
        when(profileDocumentRepository.findByIdAndProfileId(DOCUMENT_ID, OTHER_PROFILE_ID)).thenReturn(Optional.empty());

        ProfileDocumentNotFoundException exception = assertThrows(
            ProfileDocumentNotFoundException.class,
            () -> profileDocumentService.findById(OTHER_PROFILE_ID, DOCUMENT_ID)
        );

        assertEquals("Documento con id 10 non trovato per il profilo 2", exception.getMessage());

        verify(profileRepository).findById(OTHER_PROFILE_ID);
        verify(profileDocumentRepository).findByIdAndProfileId(DOCUMENT_ID, OTHER_PROFILE_ID);
        verifyNoMoreInteractions(profileRepository, profileDocumentRepository);
    }

    @Test
    void shouldUpdateProfileDocument() {
        Profile profile = createProfile();

        ProfileDocument document = ProfileDocument.create(
            profile,
            DocumentType.IDENTITY_CARD,
            "CA1234567",
            "Comune di Milano",
            LocalDate.of(2020, 1, 15),
            LocalDate.of(2030, 1, 15),
            null);

        when(profileRepository.findById(PROFILE_ID)).thenReturn(Optional.of(profile));
        when(profileDocumentRepository.findByIdAndProfileId(DOCUMENT_ID, PROFILE_ID)).thenReturn(Optional.of(document));

        ProfileDocument result = profileDocumentService.update(
            PROFILE_ID,
            DOCUMENT_ID,
            DocumentType.PASSPORT,
            "  YA9876543  ",
            "  Questura di Como  ",
            LocalDate.of(2024, 2, 20),
            LocalDate.of(2034, 2, 20),
            "  Documento aggiornato  "
        );

        assertSame(document, result);

        assertAll(
            () -> assertEquals(DocumentType.PASSPORT, result.getType()),
            () -> assertEquals("YA9876543", result.getDocumentNumber()),
            () -> assertEquals("Questura di Como", result.getIssuingAuthority()),
            () -> assertEquals(LocalDate.of(2024, 2, 20), result.getIssueDate()),
            () -> assertEquals(LocalDate.of(2034, 2, 20), result.getExpirationDate()),
            () -> assertEquals("Documento aggiornato", result.getNotes()));

        verify(profileRepository).findById(PROFILE_ID);
        verify(profileDocumentRepository).findByIdAndProfileId(DOCUMENT_ID, PROFILE_ID);
        verifyNoMoreInteractions(profileRepository, profileDocumentRepository);
    }

    @Test
    void shouldDeleteProfileDocument() {
        Profile profile = createProfile();

        ProfileDocument document = ProfileDocument.create(
            profile,
            DocumentType.PASSPORT,
            "YA9876543",
            "Questura di Como",
            LocalDate.of(2022, 5, 10),
            LocalDate.of(2032, 5, 10),
            null
        );

        when(profileRepository.findById(PROFILE_ID)).thenReturn(Optional.of(profile));
        when(profileDocumentRepository.findByIdAndProfileId(DOCUMENT_ID, PROFILE_ID)).thenReturn(Optional.of(document));

        profileDocumentService.delete(PROFILE_ID, DOCUMENT_ID);

        verify(profileRepository).findById(PROFILE_ID);
        verify(profileDocumentRepository).findByIdAndProfileId(DOCUMENT_ID, PROFILE_ID);
        verify(profileDocumentRepository).delete(document);
        verifyNoMoreInteractions(profileRepository, profileDocumentRepository);
    }

    private Profile createProfile() {
        return Profile.create(
            "Mario",
            "Rossi",
            LocalDate.of(1990, 5, 10),
            "Milano",
            "RSSMRA90E10F205X",
            "mario.rossi@example.com",
            "+393331234567"
        );
    }
}
