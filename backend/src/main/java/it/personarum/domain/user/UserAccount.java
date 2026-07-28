package it.personarum.domain.user;

import jakarta.persistence.*;

import java.util.Locale;
import java.util.Objects;

@Entity
@Table(name = "user_accounts")
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false)
    private boolean enabled;

    protected UserAccount() {
    }

    private UserAccount(String username, String passwordHash, Role role) throws IllegalArgumentException, NullPointerException {
        this.username = normalizeUsername(username);
        changePasswordHash(passwordHash);
        changeRole(role);
        this.enabled = true;
    }

    public static UserAccount create(String username, String passwordHash, Role role) {
        return new UserAccount(username, passwordHash, role);
    }

    public void changePasswordHash(String passwordHash) throws IllegalArgumentException {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash obbligatoria");
        }

        this.passwordHash = passwordHash;
    }

    public void changeRole(Role role) throws NullPointerException {
        this.role = Objects.requireNonNull(role, "Ruolo obbligatorio");
    }

    public void enable() {
        enabled = true;
    }

    public void disable() {
        enabled = false;
    }

    private static String normalizeUsername(String username)throws IllegalArgumentException {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username obbligatorio");
        }

        return username.trim().toLowerCase(Locale.ROOT);
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
