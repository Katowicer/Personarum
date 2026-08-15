package it.personarum.service.exception;

/**
 * Segnala la condizione applicativa rappresentata da ProfileNotFoundException.
 */
public class ProfileNotFoundException extends RuntimeException {

    public ProfileNotFoundException(Long id) {
        super("Profilo con id " + id + " non trovato");
    }
}
