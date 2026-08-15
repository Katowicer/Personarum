package it.personarum.web.dto.admin;

import it.personarum.domain.user.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Trasferisce i dati dell’operazione CreateUserAccountRequest tra API REST e livello applicativo.
 */
public record CreateUserAccountRequest(@NotBlank @Size(max = 80) String username,
                                       @NotBlank @Size(min = 8, max = 100) String password, @NotNull Role role) {
}
