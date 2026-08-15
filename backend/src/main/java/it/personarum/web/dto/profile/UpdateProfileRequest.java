package it.personarum.web.dto.profile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Trasferisce i dati dell’operazione UpdateProfileRequest tra API REST e livello applicativo.
 */
public record UpdateProfileRequest(@NotBlank @Size(max = 100) String firstName,
                                   @NotBlank @Size(max = 100) String lastName, @PastOrPresent LocalDate birthDate,
                                   @Size(max = 120) String birthPlace, @Size(max = 16) String fiscalCode,
                                   @Email @Size(max = 254) String email, @Size(max = 40) String phone) {
}
