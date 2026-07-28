package it.personarum.security;

import it.personarum.domain.user.UserAccount;
import it.personarum.repository.UserAccountRepository;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    public AppUserDetailsService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    @NullMarked
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String normalizedUsername = normalizeUsername(username);

        UserAccount account = userAccountRepository.findByUsername(normalizedUsername).orElseThrow(() -> new UsernameNotFoundException("Credenziali non valide"));

        return User.withUsername(account.getUsername())
            .password(account.getPasswordHash())
            .authorities("ROLE_" + account.getRole().name())
            .disabled(!account.isEnabled())
            .build();
    }

    private String normalizeUsername(String username) throws UsernameNotFoundException {
        if (username == null || username.isBlank()) {
            throw new UsernameNotFoundException("Credenziali non valide");
        }

        return username.trim().toLowerCase(Locale.ROOT);
    }
}
