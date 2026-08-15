package it.personarum.web.dto.admin;

import it.personarum.domain.user.Role;
import jakarta.validation.constraints.NotNull;

/**
 * Trasferisce i dati dell’operazione UpdateUserAccountRequest tra API REST e livello applicativo.
 */
public record UpdateUserAccountRequest(@NotNull Role role, boolean enabled) {
}
