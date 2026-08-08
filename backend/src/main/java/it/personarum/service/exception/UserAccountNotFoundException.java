package it.personarum.service.exception;

public class UserAccountNotFoundException extends RuntimeException {

    public UserAccountNotFoundException(Long id) {
        super("Utente con id " + id + " non trovato");
    }
}
