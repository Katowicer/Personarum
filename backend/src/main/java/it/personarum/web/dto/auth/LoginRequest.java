package it.personarum.web.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Trasferisce i dati dell’operazione LoginRequest tra API REST e livello applicativo.
 */
public record LoginRequest(@NotBlank @Size(max = 80) String username, @NotBlank @Size(max = 128) String password) {
}
