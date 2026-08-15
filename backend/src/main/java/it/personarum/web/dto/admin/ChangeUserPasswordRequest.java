package it.personarum.web.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Trasferisce i dati dell’operazione ChangeUserPasswordRequest tra API REST e livello applicativo.
 */
public record ChangeUserPasswordRequest(@NotBlank @Size(min = 8, max = 100) String password) {
}
