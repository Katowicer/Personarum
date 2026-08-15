package it.personarum.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.personarum.security.SessionAuthenticationService;
import it.personarum.web.dto.auth.CsrfTokenResponse;
import it.personarum.web.dto.auth.CurrentUserResponse;
import it.personarum.web.dto.auth.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Espone le operazioni REST per autenticazione, sessione corrente e token CSRF.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticazione", description = "Gestione della sessione di autenticazione e del token CSRF")
public class AuthController {

    private final SessionAuthenticationService authenticationService;

    /**
     * Crea il controller di autenticazione.
     *
     * @param authenticationService servizio di autenticazione della sessione
     */
    public AuthController(SessionAuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Restituisce il token CSRF associato alla sessione corrente.
     *
     * @param csrfToken token creato da Spring Security
     * @return dati necessari al client per inviare il token nelle richieste protette
     */
    @GetMapping("/csrf")
    @Operation(summary = "Recupera token CSRF", description = "Restituisce il token CSRF necessario per le richieste che modificano lo stato.")
    @ApiResponse(responseCode = "200", description = "Token CSRF restituito con successo", content = @Content(schema = @Schema(implementation = CsrfTokenResponse.class)))
    public CsrfTokenResponse csrf(CsrfToken csrfToken) {
        return CsrfTokenResponse.from(csrfToken);
    }

    /**
     * Autentica le credenziali ricevute e crea la sessione applicativa.
     *
     * @param loginRequest credenziali ricevute dalla richiesta
     * @param request      richiesta HTTP utilizzata per creare la sessione
     * @param response     risposta HTTP utilizzata dalla strategia di autenticazione
     * @return utente autenticato
     * @throws org.springframework.security.core.AuthenticationException se le credenziali non sono valide
     */
    @PostMapping("/login")
    @Operation(summary = "Autentica utente", description = "Valida le credenziali e crea la sessione di autenticazione sul server.")
    @SecurityRequirement(name = "csrfToken")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Credenziali dell'utente", content = @Content(schema = @Schema(implementation = LoginRequest.class)))
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Autenticazione completata", content = @Content(schema = @Schema(implementation = CurrentUserResponse.class))), @ApiResponse(responseCode = "400", description = "Corpo della richiesta non valido", content = @Content(schema = @Schema(implementation = ProblemDetail.class))), @ApiResponse(responseCode = "401", description = "Username o password non validi", content = @Content(schema = @Schema(implementation = ProblemDetail.class))), @ApiResponse(responseCode = "403", description = "Token CSRF mancante o non valido", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    public CurrentUserResponse login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = authenticationService.authenticate(loginRequest.username(), loginRequest.password(), request, response);
        return CurrentUserResponse.from(authentication);
    }

    /**
     * Restituisce l'utente associato alla sessione autenticata.
     *
     * @param authentication autenticazione corrente fornita da Spring Security
     * @return dati essenziali dell'utente autenticato
     */
    @GetMapping("/me")
    @Operation(summary = "Recupera utente corrente", description = "Restituisce l'utente associato alla sessione corrente.")
    @SecurityRequirement(name = "sessionCookie")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Utente restituito con successo", content = @Content(schema = @Schema(implementation = CurrentUserResponse.class))), @ApiResponse(responseCode = "401", description = "Sessione di autenticazione mancante o non valida", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    public CurrentUserResponse currentUser(Authentication authentication) {
        return CurrentUserResponse.from(authentication);
    }
}
