package it.personarum.service.exception;

public class ProfileNotFoundException extends RuntimeException {

    public ProfileNotFoundException(Long id) {
        super("Profilo con id " + id + " non trovato");
    }
}
