package it.personarum.web.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeUserPasswordRequest(@NotBlank @Size(min = 8, max = 100) String password) {
}
