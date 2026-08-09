package it.personarum.web.exception;

import it.personarum.domain.document.InvalidProfileDocumentStateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProfileDocumentStateExceptionHandler {

    @ExceptionHandler(InvalidProfileDocumentStateException.class)
    public ProblemDetail handleInvalidState(InvalidProfileDocumentStateException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        problem.setTitle("Operazione non consentita nello stato corrente");

        return problem;
    }
}
