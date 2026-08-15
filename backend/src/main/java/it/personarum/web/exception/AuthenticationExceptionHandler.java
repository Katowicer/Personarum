package it.personarum.web.exception;

import org.springframework.http.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

/**
 * Converte gli errori di autenticazione in risposte HTTP ProblemDetail.
 */
@RestControllerAdvice
public class AuthenticationExceptionHandler {

    /**
     * Restituisce una risposta 401 per un tentativo di autenticazione non valido.
     *
     * @param exception eccezione di autenticazione
     * @return dettaglio del problema HTTP
     */
    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthenticationException(AuthenticationException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Username o password non validi.");

        problem.setTitle("Autenticazione fallita");

        return problem;
    }
}
