package it.personarum.web.exception;

import it.personarum.service.exception.UserAccountNotFoundException;
import it.personarum.service.exception.UserAccountUsernameAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Converte le eccezioni applicative degli account utente in risposte HTTP ProblemDetail.
 */
@RestControllerAdvice
public class UserAccountExceptionHandler {

    /**
     * Restituisce una risposta 404 quando l’account richiesto non esiste.
     *
     * @param exception eccezione gestita
     * @return dettaglio del problema HTTP
     */

    @ExceptionHandler(UserAccountNotFoundException.class)
    public ProblemDetail handleNotFound(UserAccountNotFoundException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problem.setTitle("Utente non trovato");

        return problem;
    }

    /**
     * Restituisce una risposta 409 quando lo username è già utilizzato.
     *
     * @param exception eccezione gestita
     * @return dettaglio del problema HTTP
     */
    @ExceptionHandler(UserAccountUsernameAlreadyExistsException.class)
    public ProblemDetail handleUsernameAlreadyExists(UserAccountUsernameAlreadyExistsException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        problem.setTitle("Username già utilizzato");

        return problem;
    }
}
