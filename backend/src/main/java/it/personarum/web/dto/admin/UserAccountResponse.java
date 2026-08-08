package it.personarum.web.dto.admin;

import it.personarum.domain.user.Role;
import it.personarum.domain.user.UserAccount;

public record UserAccountResponse(Long id, String username, Role role, boolean enabled) {
    public static UserAccountResponse from(UserAccount userAccount) {
        return new UserAccountResponse(
            userAccount.getId(),
            userAccount.getUsername(),
            userAccount.getRole(),
            userAccount.isEnabled()
        );
    }
}
