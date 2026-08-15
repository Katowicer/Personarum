package it.personarum.config;

import it.personarum.domain.user.*;
import it.personarum.repository.UserAccountRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Inizializza, quando esplicitamente abilitato, gli account di sviluppo configurati tramite variabili d’ambiente.
 */
@Component
public class UserAccountSeeder implements ApplicationRunner {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final boolean enabled;
    private final String adminPassword;
    private final String operatorPassword;

    public UserAccountSeeder(
        UserAccountRepository userAccountRepository,
        PasswordEncoder passwordEncoder,
        @Value("${app.security.seed.enabled:false}") boolean enabled,
        @Value("${app.security.seed.admin-password:}") String adminPassword,
        @Value("${app.security.seed.operator-password:}") String operatorPassword) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.enabled = enabled;
        this.adminPassword = adminPassword;
        this.operatorPassword = operatorPassword;
    }

    /**
     * Esegue l’inizializzazione opzionale prevista all’avvio dell’applicazione.
     *
     * @param arguments argomenti di avvio, non utilizzati dal seeder
     */
    @Override
    @Transactional
    public void run(@NonNull ApplicationArguments arguments) {
        if (!enabled) {
            return;
        }

        createIfMissing("admin", adminPassword, Role.ADMIN);
        createIfMissing("operator", operatorPassword, Role.OPERATOR);
    }

    private void createIfMissing(String username, String rawPassword, Role role) {
        if (rawPassword == null || rawPassword.length() < 12) {
            throw new IllegalStateException("La password iniziale di " + username + " deve contenere almeno 12 caratteri");
        }

        if (userAccountRepository.existsByUsernameIgnoreCase(username)) {
            return;
        }

        UserAccount account = UserAccount.create(username, passwordEncoder.encode(rawPassword), role);

        userAccountRepository.save(account);
    }
}
