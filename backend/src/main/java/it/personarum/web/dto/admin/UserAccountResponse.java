package it.personarum.web.dto.admin;

import it.personarum.domain.user.Role;
import it.personarum.domain.user.UserAccount;

/**
 * Rappresenta i dati pubblici di un account utente restituiti dalle API amministrative.
 */
public record UserAccountResponse(Long id, String username, Role role, boolean enabled) {
    /**
     * Converte un account di dominio nella relativa rappresentazione REST.
     *
     * @param userAccount account da convertire
     * @return DTO contenente i dati pubblici dell’account
     */
    public static UserAccountResponse from(UserAccount userAccount) {
        return new UserAccountResponse(userAccount.getId(), userAccount.getUsername(), userAccount.getRole(), userAccount.isEnabled());
    }
}
