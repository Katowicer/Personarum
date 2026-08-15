package it.personarum.security;

import it.personarum.domain.user.UserAccount;
import it.personarum.repository.UserAccountRepository;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Adatta gli account Personarum al contratto {@link UserDetailsService} utilizzato da Spring Security.
 */
@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    /**
     * Crea il servizio di caricamento degli utenti.
     *
     * @param userAccountRepository repository degli account applicativi
     */
    public AppUserDetailsService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    /**
     * Carica l'account richiesto e lo converte nel modello atteso da Spring Security.
     *
     * @param username username ricevuto dal meccanismo di autenticazione
     * @return dettagli dell'utente comprensivi di ruolo e stato di abilitazione
     * @throws UsernameNotFoundException se lo username è assente, non valido o non corrisponde a un account
     */
    @Override
    @NullMarked
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final String normalizedUsername;
        try {
            normalizedUsername = UserAccount.normalizeUsername(username);
        } catch (IllegalArgumentException exception) {
            throw new UsernameNotFoundException("Credenziali non valide", exception);
        }

        UserAccount account = userAccountRepository.findByUsername(normalizedUsername).orElseThrow(() -> new UsernameNotFoundException("Credenziali non valide"));

        return User.withUsername(account.getUsername()).password(account.getPasswordHash()).authorities("ROLE_" + account.getRole().name()).disabled(!account.isEnabled()).build();
    }
}
