package it.personarum.domain.document;

import it.personarum.domain.profile.Profile;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ProfileDocumentTest {

    @Test
    void shouldCreateAndNormalizeDocument() {
        Profile profile = createProfile();
        LocalDate issueDate = LocalDate.of(2020, 7, 7);
        LocalDate expirationDate = LocalDate.of(2030, 7, 7);

        ProfileDocument document = ProfileDocument.create(
            profile,
            DocumentType.IDENTITY_CARD,
            "  CA1234567  ",
            "  Comune di Milano  ",
            issueDate,
            expirationDate,
            "  Documento principale  "
        );

        assertAll(
            () -> assertNull(document.getId()),
            () -> assertSame(profile, document.getProfile()),
            () -> assertEquals(DocumentType.IDENTITY_CARD, document.getType()),
            () -> assertEquals("CA1234567", document.getDocumentNumber()),
            () -> assertEquals("Comune di Milano", document.getIssuingAuthority()),
            () -> assertEquals(issueDate, document.getIssueDate()),
            () -> assertEquals(expirationDate, document.getExpirationDate()),
            () -> assertEquals("Documento principale", document.getNotes())
        );
    }

    @Test
    void shouldRejectNullProfile() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> ProfileDocument.create(
                null,
                DocumentType.PASSPORT,
                null,
                null,
                null,
                null,
                null
            )
        );

        assertEquals("Profilo obbligatorio", exception.getMessage());
    }

    @Test
    void shouldRejectNullDocumentType() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> ProfileDocument.create(
                createProfile(),
                null,
                null,
                null,
                null,
                null,
                null
            )
        );

        assertEquals("Tipo documento obbligatorio", exception.getMessage());
    }

    @Test
    void shouldRejectFutureIssueDate() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ProfileDocument.create(
                createProfile(),
                DocumentType.DRIVING_LICENSE,
                null,
                null,
                LocalDate.now().plusDays(1),
                null,
                null
            )
        );

        assertEquals(
            "La data di rilascio non può essere futura",
            exception.getMessage()
        );
    }

    @Test
    void shouldRejectExpirationDateBeforeIssueDate() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ProfileDocument.create(
                createProfile(),
                DocumentType.PASSPORT,
                null,
                null,
                LocalDate.of(2007, 7, 7),
                LocalDate.of(2000, 7, 7),
                null
            )
        );

        assertEquals(
            "La data di scadenza non può precedere la data di rilascio",
            exception.getMessage()
        );
    }

    @Test
    void shouldConvertBlankOptionalValuesToNull() {
        ProfileDocument document = ProfileDocument.create(
            createProfile(),
            DocumentType.OTHER,
            " ",
            " ",
            null,
            null,
            " "
        );

        assertAll(
            () -> assertNull(document.getDocumentNumber()),
            () -> assertNull(document.getIssuingAuthority()),
            () -> assertNull(document.getIssueDate()),
            () -> assertNull(document.getExpirationDate()),
            () -> assertNull(document.getNotes())
        );
    }

    @Test
    void shouldChangeDocumentDetails() {
        ProfileDocument document = ProfileDocument.create(
            createProfile(),
            DocumentType.IDENTITY_CARD,
            "PRIMA-123",
            "Comune di Milano",
            LocalDate.of(2007, 7, 7),
            LocalDate.of(2077, 7, 7),
            null
        );

        LocalDate newIssueDate = LocalDate.of(2006, 7, 7);
        LocalDate newExpirationDate = LocalDate.of(2777, 7, 7);

        document.changeDetails(
            DocumentType.PASSPORT,
            "  YA9876543  ",
            "  Questura di Como  ",
            newIssueDate,
            newExpirationDate,
            "  Documento aggiornato  "
        );

        assertAll(
            () -> assertEquals(DocumentType.PASSPORT, document.getType()),
            () -> assertEquals("YA9876543", document.getDocumentNumber()),
            () -> assertEquals("Questura di Como", document.getIssuingAuthority()),
            () -> assertEquals(newIssueDate, document.getIssueDate()),
            () -> assertEquals(newExpirationDate, document.getExpirationDate()),
            () -> assertEquals("Documento aggiornato", document.getNotes())
        );
    }

    private Profile createProfile() {
        return Profile.create(
            "Mario",
            "Rossi",
            LocalDate.of(2000, 7, 7),
            "Milano",
            "RSSMRA90E10F205X",
            "mario.rossi@example.com",
            "+393331234567"
        );
    }


}
