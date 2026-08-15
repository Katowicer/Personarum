package it.personarum.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Gestisce in modo centralizzato gli errori generati durante
 * l'elaborazione delle richieste REST.
 *
 * <p>La classe converte le principali eccezioni di validazione e
 * di dominio in risposte HTTP strutturate secondo {@link ProblemDetail}.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gestisce gli errori prodotti dalla validazione Jakarta Validation
     * dei DTO ricevuti dai controller.
     *
     * @param exception eccezione contenente gli errori di validazione
     * @return risposta HTTP 400 con il dettaglio dei campi non validi
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Uno o più campi della richiesta non sono validi.");
        problem.setTitle("Richiesta non valida");

        Map<String, String> errors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error -> errors.putIfAbsent(error.getField(), error.getDefaultMessage()));
        problem.setProperty("errors", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    /**
     * Gestisce gli errori dovuti a dati che violano una regola
     * applicativa o di dominio.
     *
     * @param exception eccezione contenente la descrizione dell'errore
     * @return risposta HTTP 400 con il dettaglio dell'errore
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgument(IllegalArgumentException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        problem.setTitle("Dati non validi");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }
}
