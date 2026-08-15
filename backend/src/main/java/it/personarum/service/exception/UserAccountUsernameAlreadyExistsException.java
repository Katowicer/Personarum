package it.personarum.service.exception;

/**
 * Segnala la condizione applicativa rappresentata da UserAccountUsernameAlreadyExistsException.
 */
public class UserAccountUsernameAlreadyExistsException extends RuntimeException {

    public UserAccountUsernameAlreadyExistsException(String username) {
        super("Username già utilizzato: " + username);
    }
}
