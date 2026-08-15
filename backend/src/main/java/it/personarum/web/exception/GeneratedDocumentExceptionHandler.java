package it.personarum.web.exception;

import it.personarum.service.exception.DocumentTemplateDisabledException;
import it.personarum.service.exception.GeneratedDocumentNotFoundException;
import it.personarum.service.exception.GeneratedDocumentPdfException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Converte le eccezioni relative ai documenti generati in risposte HTTP ProblemDetail.
 */
@RestControllerAdvice
public class GeneratedDocumentExceptionHandler {

    /**
     * Restituisce una risposta 404 quando il documento generato richiesto non esiste.
     *
     * @param exception eccezione gestita
     * @return dettaglio del problema HTTP
     */
    @ExceptionHandler(GeneratedDocumentNotFoundException.class)
    public ProblemDetail handleNotFound(GeneratedDocumentNotFoundException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problem.setTitle("Documento generato non trovato");

        return problem;
    }

    /**
     * Restituisce una risposta 400 quando si tenta di usare un template disabilitato.
     *
     * @param exception eccezione gestita
     * @return dettaglio del problema HTTP
     */
    @ExceptionHandler(DocumentTemplateDisabledException.class)
    public ProblemDetail handleDisabledTemplate(DocumentTemplateDisabledException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        problem.setTitle("Template non disponibile");

        return problem;
    }

    /**
     * Restituisce una risposta 500 quando la creazione del PDF non può essere completata.
     *
     * @param exception eccezione gestita
     * @return dettaglio del problema HTTP
     */
    @ExceptionHandler(GeneratedDocumentPdfException.class)
    public ProblemDetail handlePdfGeneration(GeneratedDocumentPdfException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        problem.setTitle("Errore generazione PDF");

        return problem;
    }
}
