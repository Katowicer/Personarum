package it.personarum.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Locale;
import java.util.Objects;

/**
 * Rappresenta un account applicativo con credenziali cifrate, ruolo e stato di abilitazione.
 *
 * <p>L'entità non memorizza mai password in chiaro: il livello applicativo deve fornire
 * esclusivamente l'hash prodotto dal {@code PasswordEncoder} configurato.</p>
 */
@Entity
@Table(name = "user_accounts")
public class UserAccount {

    private static final int MAX_USERNAME_LENGTH = 80;
    private static final int MAX_PASSWORD_HASH_LENGTH = 255;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = MAX_USERNAME_LENGTH)
    private String username;

    @Column(name = "password_hash", nullable = false, length = MAX_PASSWORD_HASH_LENGTH)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false)
    private boolean enabled;

    protected UserAccount() {
    }

    private UserAccount(String username, String passwordHash, Role role) {
        this.username = normalizeUsername(username);
        changePasswordHash(passwordHash);
        changeRole(role);
        enabled = true;
    }

    /**
     * Crea un account abilitato applicando le invarianti del dominio.
     *
     * @param username     username dell'account
     * @param passwordHash hash della password già codificato
     * @param role         ruolo iniziale
     * @return nuovo account non ancora persistito
     * @throws IllegalArgumentException se username o hash non sono validi
     * @throws NullPointerException     se il ruolo è nullo
     */
    public static UserAccount create(String username, String passwordHash, Role role) {
        return new UserAccount(username, passwordHash, role);
    }

    /**
     * Sostituisce l'hash della password.
     *
     * @param passwordHash nuovo hash della password
     * @throws IllegalArgumentException se il valore è assente o supera la dimensione persistibile
     */
    public void changePasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash obbligatoria");
        }
        if (passwordHash.length() > MAX_PASSWORD_HASH_LENGTH) {
            throw new IllegalArgumentException("La password codificata supera la dimensione massima consentita");
        }
        this.passwordHash = passwordHash;
    }

    /**
     * Modifica il ruolo dell'account.
     *
     * @param role nuovo ruolo
     * @throws NullPointerException se il ruolo è nullo
     */
    public void changeRole(Role role) {
        this.role = Objects.requireNonNull(role, "Ruolo obbligatorio");
    }

    /**
     * Abilita l'account all'autenticazione.
     */
    public void enable() {
        enabled = true;
    }

    /**
     * Disabilita l'account impedendone l'autenticazione.
     */
    public void disable() {
        enabled = false;
    }

    /**
     * Normalizza uno username secondo la regola utilizzata dall'entità.
     *
     * @param username username da normalizzare
     * @return username senza spazi esterni e in minuscolo
     * @throws IllegalArgumentException se lo username è assente o supera 80 caratteri
     */
    public static String normalizeUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username obbligatorio");
        }

        String normalized = username.trim().toLowerCase(Locale.ROOT);
        if (normalized.length() > MAX_USERNAME_LENGTH) {
            throw new IllegalArgumentException("Lo username non può superare 80 caratteri");
        }
        return normalized;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
