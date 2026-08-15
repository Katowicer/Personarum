package it.personarum.service;

import it.personarum.domain.user.Role;
import it.personarum.domain.user.UserAccount;
import it.personarum.repository.UserAccountRepository;
import it.personarum.service.exception.UserAccountNotFoundException;
import it.personarum.service.exception.UserAccountUsernameAlreadyExistsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Coordina i casi d'uso amministrativi relativi agli account utente.
 */
@Service
@Transactional(readOnly = true)
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Crea il servizio degli account utente.
     *
     * @param userAccountRepository repository degli account
     * @param passwordEncoder       codificatore usato per memorizzare le password in forma sicura
     */
    public UserAccountService(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Crea un nuovo account dopo aver normalizzato lo username e verificato che sia disponibile.
     *
     * @param username username richiesto
     * @param password password in chiaro da codificare
     * @param role     ruolo iniziale dell'account
     * @return account creato e persistito
     * @throws UserAccountUsernameAlreadyExistsException se lo username normalizzato è già utilizzato
     * @throws IllegalArgumentException                  se lo username o la password non rispettano le precondizioni del dominio
     */
    @Transactional
    public UserAccount create(String username, String password, Role role) {
        String normalizedUsername = UserAccount.normalizeUsername(username);

        if (userAccountRepository.existsByUsernameIgnoreCase(normalizedUsername)) {
            throw new UserAccountUsernameAlreadyExistsException(normalizedUsername);
        }

        String passwordHash = passwordEncoder.encode(password);
        UserAccount userAccount = UserAccount.create(normalizedUsername, passwordHash, role);
        return userAccountRepository.save(userAccount);
    }

    /**
     * Restituisce tutti gli account presenti.
     *
     * @return elenco degli account
     */
    public List<UserAccount> findAll() {
        return userAccountRepository.findAll();
    }

    /**
     * Recupera un account tramite identificativo.
     *
     * @param id identificativo dell'account
     * @return account trovato
     * @throws UserAccountNotFoundException se l'account non esiste
     */
    public UserAccount findById(Long id) {
        return userAccountRepository.findById(id).orElseThrow(() -> new UserAccountNotFoundException(id));
    }

    /**
     * Aggiorna ruolo e stato di abilitazione dell'account.
     *
     * @param id      identificativo dell'account
     * @param role    nuovo ruolo
     * @param enabled nuovo stato di abilitazione
     * @return account aggiornato
     * @throws UserAccountNotFoundException se l'account non esiste
     */
    @Transactional
    public UserAccount update(Long id, Role role, boolean enabled) {
        UserAccount userAccount = findById(id);
        userAccount.changeRole(role);

        if (enabled) {
            userAccount.enable();
        } else {
            userAccount.disable();
        }

        return userAccountRepository.save(userAccount);
    }

    /**
     * Modifica la password di un account memorizzandone esclusivamente l'hash.
     *
     * @param id       identificativo dell'account
     * @param password nuova password in chiaro
     * @return account aggiornato
     * @throws UserAccountNotFoundException se l'account non esiste
     */
    @Transactional
    public UserAccount changePassword(Long id, String password) {
        UserAccount userAccount = findById(id);
        userAccount.changePasswordHash(passwordEncoder.encode(password));
        return userAccountRepository.save(userAccount);
    }
}
