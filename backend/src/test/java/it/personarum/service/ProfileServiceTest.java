package it.personarum.service;

import it.personarum.domain.profile.Profile;
import it.personarum.repository.ProfileRepository;
import it.personarum.service.exception.ProfileFiscalCodeAlreadyExistsException;
import it.personarum.service.exception.ProfileNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    private static final String FISCAL_CODE = "RSSMRA90E10F205X";

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private ProfileService profileService;

    @Test
    void shouldCreateProfile() {
        Profile profile = Profile.create(
            "Mario",
            "Rossi",
            null,
            null,
            FISCAL_CODE,
            null,
            null
        );

        when(profileRepository.existsByFiscalCode(FISCAL_CODE)).thenReturn(false);
        when(profileRepository.save(profile)).thenReturn(profile);

        Profile createdProfile = profileService.create(profile);

        assertSame(profile, createdProfile);
        verify(profileRepository).existsByFiscalCode(FISCAL_CODE);
        verify(profileRepository).save(profile);
        verifyNoMoreInteractions(profileRepository);
    }

    @Test
    void shouldFindAllProfiles() {
        Profile firstProfile = Profile.create(
            "Mario",
            "Rossi",
            null,
            null,
            "RSSMRA90E10F205X",
            null,
            null
        );

        Profile secondProfile = Profile.create(
            "Luigi",
            "Verdi",
            null,
            null,
            "VRDLGU85B20H501X",
            null,
            null
        );

        List<Profile> profiles = List.of(firstProfile, secondProfile);

        when(profileRepository.findAll()).thenReturn(profiles);

        List<Profile> result = profileService.findAll();

        assertEquals(profiles, result);
        verify(profileRepository).findAll();
        verifyNoMoreInteractions(profileRepository);
    }

    @Test
    void shouldFindProfileById() {
        Long profileId = 1L;

        Profile profile = Profile.create(
            "Mario",
            "Rossi",
            null,
            null,
            FISCAL_CODE,
            null,
            null
        );

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));

        Profile result = profileService.findById(profileId);

        assertSame(profile, result);
        verify(profileRepository).findById(profileId);
        verifyNoMoreInteractions(profileRepository);
    }

    @Test
    void shouldThrowWhenProfileDoesNotExist() {
        Long profileId = 99L;

        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        ProfileNotFoundException exception = assertThrows(
            ProfileNotFoundException.class,
            () -> profileService.findById(profileId)
        );

        assertEquals("Profilo con id 99 non trovato", exception.getMessage());
        verify(profileRepository).findById(profileId);
        verifyNoMoreInteractions(profileRepository);
    }

    @Test
    void shouldUpdateProfile() {
        Long profileId = 1L;
        String newFiscalCode = "VRDLGU85B20H501X";

        Profile profile = Profile.create(
            "Mario",
            "Rossi",
            null,
            null,
            FISCAL_CODE,
            null,
            null
        );

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));

        when(profileRepository.existsByFiscalCode(newFiscalCode)).thenReturn(false);

        Profile result = profileService.update(
            profileId,
            "Luigi",
            "Verdi",
            LocalDate.of(2000, 7, 7),
            "Roma",
            newFiscalCode,
            "LUIGI.VERDI@EXAMPLE.COM",
            " +393339876543 "
        );

        assertSame(profile, result);
        assertEquals("Luigi", result.getFirstName());
        assertEquals("Verdi", result.getLastName());
        assertEquals(LocalDate.of(2000, 7, 7), result.getBirthDate());
        assertEquals("Roma", result.getBirthPlace());
        assertEquals(newFiscalCode, result.getFiscalCode());
        assertEquals("luigi.verdi@example.com", result.getEmail());
        assertEquals("+393339876543", result.getPhone());

        verify(profileRepository).findById(profileId);
        verify(profileRepository).existsByFiscalCode(newFiscalCode);
        verifyNoMoreInteractions(profileRepository);
    }

    @Test
    void shouldUpdateProfileKeepingItsFiscalCode() {
        Long profileId = 1L;

        Profile profile = Profile.create(
            "Mario",
            "Rossi",
            null,
            null,
            FISCAL_CODE,
            null,
            null
        );

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));

        Profile result = profileService.update(
            profileId,
            "Mario",
            "Rossi",
            LocalDate.of(1990, 5, 10),
            "Milano",
            FISCAL_CODE,
            "mario.rossi@example.com",
            "+393331234567"
        );

        assertSame(profile, result);
        assertEquals(FISCAL_CODE, result.getFiscalCode());
        assertEquals(LocalDate.of(1990, 5, 10), result.getBirthDate());
        assertEquals("Milano", result.getBirthPlace());
        assertEquals("mario.rossi@example.com", result.getEmail());
        assertEquals("+393331234567", result.getPhone());

        verify(profileRepository).findById(profileId);
        verifyNoMoreInteractions(profileRepository);
    }

    @Test
    void shouldRejectUpdateWhenFiscalCodeBelongsToAnotherProfile() {
        Long profileId = 1L;
        String occupiedFiscalCode = "VRDLGU85B20H501X";

        Profile profile = Profile.create(
            "Mario",
            "Rossi",
            null,
            null,
            FISCAL_CODE,
            null,
            null
        );

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(profileRepository.existsByFiscalCode(occupiedFiscalCode)).thenReturn(true);

        ProfileFiscalCodeAlreadyExistsException exception = assertThrows(
            ProfileFiscalCodeAlreadyExistsException.class,
            () -> profileService.update(
                profileId,
                "Luigi",
                "Verdi",
                LocalDate.of(1995, 5, 20),
                "Roma",
                occupiedFiscalCode,
                "luigi.verdi@example.com",
                "+393339876543"
            )
        );

        assertEquals("Esiste già un profilo con codice fiscale " + occupiedFiscalCode, exception.getMessage());

        verify(profileRepository).findById(profileId);
        verify(profileRepository).existsByFiscalCode(occupiedFiscalCode);
        verifyNoMoreInteractions(profileRepository);
    }

    @Test
    void shouldNormalizeFiscalCodeBeforeCheckingUpdateDuplicate() {
        Long profileId = 1L;
        String occupiedFiscalCode = "VRDLGU85B20H501X";
        Profile profile = Profile.create("Mario", "Rossi", null, null, FISCAL_CODE, null, null);

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(profileRepository.existsByFiscalCode(occupiedFiscalCode)).thenReturn(true);

        assertThrows(ProfileFiscalCodeAlreadyExistsException.class, () -> profileService.update(
            profileId, "Mario", "Rossi", null, null, "  vrdlgu85b20h501x  ", null, null
        ));

        assertEquals(FISCAL_CODE, profile.getFiscalCode());
        verify(profileRepository).existsByFiscalCode(occupiedFiscalCode);
    }

    @Test
    void shouldDeleteProfile() {
        Long profileId = 1L;

        Profile profile = Profile.create(
            "Mario",
            "Rossi",
            null,
            null,
            FISCAL_CODE,
            null,
            null
        );

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));

        profileService.delete(profileId);

        verify(profileRepository).findById(profileId);
        verify(profileRepository).delete(profile);
        verifyNoMoreInteractions(profileRepository);
    }

    @Test
    void shouldCreateProfileFromPersonalData() {
        when(profileRepository.existsByFiscalCode(FISCAL_CODE))
            .thenReturn(false);

        when(profileRepository.save(any(Profile.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        Profile result = profileService.create(
            "  Mario  ",
            "  Rossi  ",
            LocalDate.of(1990, 5, 10),
            "  Milano  ",
            "  rssmra90e10f205x  ",
            "  MARIO.ROSSI@EXAMPLE.COM  ",
            "  +393331234567  "
        );

        assertEquals("Mario", result.getFirstName());
        assertEquals("Rossi", result.getLastName());
        assertEquals(LocalDate.of(1990, 5, 10), result.getBirthDate());
        assertEquals("Milano", result.getBirthPlace());
        assertEquals(FISCAL_CODE, result.getFiscalCode());
        assertEquals("mario.rossi@example.com", result.getEmail());
        assertEquals("+393331234567", result.getPhone());

        verify(profileRepository).existsByFiscalCode(FISCAL_CODE);
        verify(profileRepository).save(result);
        verifyNoMoreInteractions(profileRepository);
    }
}
