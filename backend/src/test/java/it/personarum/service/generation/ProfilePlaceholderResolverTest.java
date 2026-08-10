package it.personarum.service.generation;

import it.personarum.domain.profile.Profile;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProfilePlaceholderResolverTest {

    private final ProfilePlaceholderResolver resolver = new ProfilePlaceholderResolver();

    @Test
    void shouldReplaceProfilePlaceholders() {
        Profile profile = mock(Profile.class);

        when(profile.getFirstName()).thenReturn("Mario");
        when(profile.getLastName()).thenReturn("Rossi");
        when(profile.getBirthDate()).thenReturn(LocalDate.of(1990, 5, 10));
        when(profile.getBirthPlace()).thenReturn("Milano");
        when(profile.getFiscalCode()).thenReturn("RSSMRA90E10F205X");
        when(profile.getEmail()).thenReturn("mario@example.com");
        when(profile.getPhone()).thenReturn("3331234567");

        String result = resolver.resolve("{firstName} {lastName} - {birthDate} - {fiscalCode}", profile);
        assertThat(result).isEqualTo("Mario Rossi - 10/05/1990 - RSSMRA90E10F205X");
    }

    @Test
    void shouldUseEmptyStringForMissingOptionalValues() {
        Profile profile = mock(Profile.class);

        when(profile.getFirstName()).thenReturn("Mario");
        when(profile.getLastName()).thenReturn("Rossi");

        String result = resolver.resolve("{firstName} {lastName} {email}", profile);
        assertThat(result).isEqualTo("Mario Rossi ");
    }
}
