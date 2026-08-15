package it.personarum.service.exception;

/**
 * Segnala la condizione applicativa rappresentata da UserAccountNotFoundException.
 */
public class UserAccountNotFoundException extends RuntimeException {

    public UserAccountNotFoundException(Long id) {
        super("Utente con id " + id + " non trovato");
    }
}
