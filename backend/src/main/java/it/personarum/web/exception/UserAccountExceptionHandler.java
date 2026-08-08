package it.personarum.web.exception;

import it.personarum.service.exception.UserAccountNotFoundException;
import it.personarum.service.exception.UserAccountUsernameAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserAccountExceptionHandler {

    @ExceptionHandler(UserAccountNotFoundException.class)
    public ProblemDetail handleNotFound(UserAccountNotFoundException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problem.setTitle("Utente non trovato");

        return problem;
    }

    @ExceptionHandler(UserAccountUsernameAlreadyExistsException.class)
    public ProblemDetail handleUsernameAlreadyExists(UserAccountUsernameAlreadyExistsException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        problem.setTitle("Username già utilizzato");

        return problem;
    }
}
