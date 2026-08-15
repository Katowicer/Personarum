package it.personarum.web.dto.auth;

import it.personarum.domain.user.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;

/**
 * Rappresenta l’identità e il ruolo dell’utente autenticato nella sessione corrente.
 */
public record CurrentUserResponse(String username, Role role) {

    /**
     * Crea la rappresentazione dell’utente corrente a partire dall’autenticazione Spring Security.
     *
     * @param authentication autenticazione corrente
     * @return DTO con username e ruolo applicativo
     * @throws IllegalStateException se non è disponibile un ruolo applicativo valido
     */
    public static CurrentUserResponse from(Authentication authentication) throws IllegalStateException {
        Role role = authentication
            .getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .filter(Objects::nonNull)
            .filter(authority -> authority.startsWith("ROLE_"))
            .map(authority -> authority.substring("ROLE_".length()))
            .map(Role::valueOf)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Ruolo utente non disponibile"));

        return new CurrentUserResponse(authentication.getName(), role);
    }
}
