package it.personarum.web.exception;

import it.personarum.service.exception.ProfileDocumentFileNotFoundException;
import it.personarum.service.exception.ProfileDocumentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProfileDocumentExceptionHandler {

    @ExceptionHandler(ProfileDocumentNotFoundException.class)
    public ProblemDetail handleProfileDocumentNotFound(ProfileDocumentNotFoundException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problem.setTitle("Documento non trovato");

        return problem;
    }

    @ExceptionHandler(ProfileDocumentFileNotFoundException.class)
    public ProblemDetail handleProfileDocumentFileNotFound(ProfileDocumentFileNotFoundException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problem.setTitle("File del documento non trovato");

        return problem;
    }
}
