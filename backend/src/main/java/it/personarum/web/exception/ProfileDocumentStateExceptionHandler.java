package it.personarum.web.exception;

import it.personarum.domain.document.InvalidProfileDocumentStateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Gestisce i tentativi di operazioni non consentite dallo stato corrente di un documento.
 */
@RestControllerAdvice
public class ProfileDocumentStateExceptionHandler {

    /**
     * Restituisce una risposta 409 quando lo stato del documento impedisce l’operazione richiesta.
     *
     * @param exception eccezione di transizione o modifica non consentita
     * @return dettaglio del problema HTTP
     */
    @ExceptionHandler(InvalidProfileDocumentStateException.class)
    public ProblemDetail handleInvalidState(InvalidProfileDocumentStateException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        problem.setTitle("Operazione non consentita nello stato corrente");

        return problem;
    }
}
