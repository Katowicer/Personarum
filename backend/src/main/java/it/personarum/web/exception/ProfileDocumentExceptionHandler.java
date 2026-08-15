package it.personarum.web.exception;

import it.personarum.service.exception.ProfileDocumentFileNotFoundException;
import it.personarum.service.exception.ProfileDocumentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Converte le eccezioni relative ai documenti dei profili in risposte HTTP ProblemDetail.
 */
@RestControllerAdvice
public class ProfileDocumentExceptionHandler {

    /**
     * Restituisce una risposta 404 quando il documento richiesto non esiste nel profilo indicato.
     *
     * @param exception eccezione gestita
     * @return dettaglio del problema HTTP
     */
    @ExceptionHandler(ProfileDocumentNotFoundException.class)
    public ProblemDetail handleProfileDocumentNotFound(ProfileDocumentNotFoundException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problem.setTitle("Documento non trovato");

        return problem;
    }

    /**
     * Restituisce una risposta 404 quando il file associato al documento non esiste.
     *
     * @param exception eccezione gestita
     * @return dettaglio del problema HTTP
     */
    @ExceptionHandler(ProfileDocumentFileNotFoundException.class)
    public ProblemDetail handleProfileDocumentFileNotFound(ProfileDocumentFileNotFoundException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problem.setTitle("File del documento non trovato");

        return problem;
    }
}
