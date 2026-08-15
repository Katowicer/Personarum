package it.personarum.repository;

import it.personarum.domain.user.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository JPA per gli account utente.
 */
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    /**
     * Cerca un account tramite username normalizzato.
     *
     * @param username username da cercare
     * @return account corrispondente, se presente
     */
    Optional<UserAccount> findByUsername(String username);

    /**
     * Verifica l'esistenza di uno username senza distinguere maiuscole e minuscole.
     *
     * @param username username da verificare
     * @return {@code true} se lo username è già presente
     */
    boolean existsByUsernameIgnoreCase(String username);
}
