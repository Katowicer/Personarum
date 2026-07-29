package it.personarum.web.dto.profile;

import it.personarum.domain.profile.Profile;

import java.time.LocalDate;

public record ProfileResponse(
    Long id,
    String firstName,
    String lastName,
    LocalDate birthDate,
    String birthPlace,
    String fiscalCode,
    String email,
    String phone
) {
    public static ProfileResponse from(Profile profile) {
        return new ProfileResponse(
            profile.getId(),
            profile.getFirstName(),
            profile.getLastName(),
            profile.getBirthDate(),
            profile.getBirthPlace(),
            profile.getFiscalCode(),
            profile.getEmail(),
            profile.getPhone()
        );
    }
}
