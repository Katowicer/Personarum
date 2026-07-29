package it.personarum.web.exception;

import it.personarum.service.exception.ProfileNotFoundException;
import it.personarum.service.exception.ProfileFiscalCodeAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProfileExceptionHandler {

    @ExceptionHandler(ProfileNotFoundException.class)
    public ProblemDetail handleProfileNotFound(ProfileNotFoundException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problem.setTitle("Profilo non trovato");

        return problem;
    }

    @ExceptionHandler(ProfileFiscalCodeAlreadyExistsException.class)
    public ProblemDetail handleFiscalCodeAlreadyExists(ProfileFiscalCodeAlreadyExistsException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        problem.setTitle("Codice fiscale già utilizzato");

        return problem;
    }
}
