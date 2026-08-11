package it.personarum.web.exception;

import it.personarum.service.exception.DocumentTemplateDisabledException;
import it.personarum.service.exception.GeneratedDocumentNotFoundException;
import it.personarum.service.exception.GeneratedDocumentPdfException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GeneratedDocumentExceptionHandler {

    @ExceptionHandler(GeneratedDocumentNotFoundException.class)
    public ProblemDetail handleNotFound(GeneratedDocumentNotFoundException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problem.setTitle("Documento generato non trovato");

        return problem;
    }

    @ExceptionHandler(DocumentTemplateDisabledException.class)
    public ProblemDetail handleDisabledTemplate(DocumentTemplateDisabledException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        problem.setTitle("Template non disponibile");

        return problem;
    }

    @ExceptionHandler(GeneratedDocumentPdfException.class)
    public ProblemDetail handlePdfGeneration(GeneratedDocumentPdfException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        problem.setTitle("Errore generazione PDF");

        return problem;
    }
}
