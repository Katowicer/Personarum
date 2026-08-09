package it.personarum.web.exception;

import it.personarum.service.exception.DocumentTemplateNameAlreadyExistsException;
import it.personarum.service.exception.DocumentTemplateNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DocumentTemplateExceptionHandler {

    @ExceptionHandler(DocumentTemplateNotFoundException.class)
    public ProblemDetail handleNotFound(DocumentTemplateNotFoundException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problem.setTitle("Template non trovato");

        return problem;
    }

    @ExceptionHandler(DocumentTemplateNameAlreadyExistsException.class)
    public ProblemDetail handleDuplicate(DocumentTemplateNameAlreadyExistsException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        problem.setTitle("Template già esistente");

        return problem;
    }
}
