package it.personarum.web.dto.profile;

import it.personarum.domain.profile.Profile;

import java.time.LocalDate;

/**
 * Rappresenta i dati anagrafici e di contatto di un profilo restituito dalle API.
 */
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
    /**
     * Converte un profilo di dominio nella relativa rappresentazione REST.
     *
     * @param profile profilo da convertire
     * @return DTO contenente i dati del profilo
     */
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
