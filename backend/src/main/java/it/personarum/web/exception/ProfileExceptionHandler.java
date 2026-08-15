package it.personarum.web.exception;

import it.personarum.service.exception.ProfileNotFoundException;
import it.personarum.service.exception.ProfileFiscalCodeAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Converte le eccezioni applicative dei profili in risposte HTTP ProblemDetail.
 */
@RestControllerAdvice
public class ProfileExceptionHandler {

    /**
     * Restituisce una risposta 404 quando il profilo richiesto non esiste.
     *
     * @param exception eccezione gestita
     * @return dettaglio del problema HTTP
     */
    @ExceptionHandler(ProfileNotFoundException.class)
    public ProblemDetail handleProfileNotFound(ProfileNotFoundException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problem.setTitle("Profilo non trovato");

        return problem;
    }

    /**
     * Restituisce una risposta 409 quando il codice fiscale è già associato a un altro profilo.
     *
     * @param exception eccezione gestita
     * @return dettaglio del problema HTTP
     */
    @ExceptionHandler(ProfileFiscalCodeAlreadyExistsException.class)
    public ProblemDetail handleFiscalCodeAlreadyExists(ProfileFiscalCodeAlreadyExistsException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        problem.setTitle("Codice fiscale già utilizzato");

        return problem;
    }
}
