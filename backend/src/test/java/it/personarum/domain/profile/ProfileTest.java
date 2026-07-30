package it.personarum.domain.profile;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ProfileTest {

    @Test
    void shouldCreateAndNormalizeProfile() {
        Profile profile = Profile.create(
            "  Mario",
            " Rossi",
            LocalDate.of(2000, 7, 7),
            "  Milano  ",
            "rssmra90e10f205x",
            "MARIO.ROSSI@EXAMPLE.COM",
            "  +393331234567  "
        );

        assertAll(
            () -> assertNull(profile.getId()),
            () -> assertEquals("Mario", profile.getFirstName()),
            () -> assertEquals("Rossi", profile.getLastName()),
            () -> assertEquals(LocalDate.of(2000, 7, 7), profile.getBirthDate()),
            () -> assertEquals("Milano", profile.getBirthPlace()),
            () -> assertEquals("RSSMRA90E10F205X", profile.getFiscalCode()),
            () -> assertEquals("mario.rossi@example.com", profile.getEmail()),
            () -> assertEquals("+393331234567", profile.getPhone())
        );
    }

    @Test
    void shouldRejectBlankFirstName() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Profile.create(
                " ",
                "Rossi",
                null,
                null,
                null,
                null,
                null
            )
        );

        assertEquals("Nome obbligatorio", exception.getMessage());
    }

    @Test
    void shouldRejectBlankLastName() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Profile.create(
                "Mario",
                " ",
                null,
                null,
                null,
                null,
                null
            )
        );

        assertEquals("Cognome obbligatorio", exception.getMessage());
    }

    @Test
    void shouldRejectFutureBirthDate() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Profile.create(
                "Mario",
                "Rossi",
                LocalDate.now().plusDays(1),
                null,
                null,
                null,
                null
            )
        );

        assertEquals(
            "La data di nascita non può essere futura",
            exception.getMessage()
        );
    }

    @Test
    void shouldConvertBlankOptionalValuesToNull() {
        Profile profile = Profile.create(
            "Mario",
            "Rossi",
            null,
            " ",
            " ",
            " ",
            " "
        );

        assertAll(
            () -> assertNull(profile.getBirthDate()),
            () -> assertNull(profile.getBirthPlace()),
            () -> assertNull(profile.getFiscalCode()),
            () -> assertNull(profile.getEmail()),
            () -> assertNull(profile.getPhone())
        );
    }

    @Test
    void shouldChangePersonalDataAndContacts() {
        Profile profile = Profile.create(
            "Mario",
            "Rossi",
            null,
            null,
            null,
            null,
            null
        );

        profile.changePersonalData(
            "Luigi",
            "Verdi",
            LocalDate.of(2000, 7, 7),
            "Roma",
            "vrdlgu85b20h501x"
        );

        profile.changeContacts(
            "LUIGI.VERDI@EXAMPLE.COM",
            "+393339876543"
        );

        assertAll(
            () -> assertEquals("Luigi", profile.getFirstName()),
            () -> assertEquals("Verdi", profile.getLastName()),
            () -> assertEquals(LocalDate.of(2000, 7, 7), profile.getBirthDate()),
            () -> assertEquals("Roma", profile.getBirthPlace()),
            () -> assertEquals("VRDLGU85B20H501X", profile.getFiscalCode()),
            () -> assertEquals("luigi.verdi@example.com", profile.getEmail()),
            () -> assertEquals("+393339876543", profile.getPhone())
        );
    }
}
