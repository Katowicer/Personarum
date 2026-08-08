package it.personarum.web.dto.admin;

import it.personarum.domain.user.Role;
import jakarta.validation.constraints.NotNull;

public record UpdateUserAccountRequest(@NotNull Role role, boolean enabled) {
}
