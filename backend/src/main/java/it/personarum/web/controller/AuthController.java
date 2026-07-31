package it.personarum.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.personarum.security.SessionAuthenticationService;
import it.personarum.web.dto.auth.*;
import jakarta.servlet.http.*;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(
    name = "Authentication",
    description = "Gestione della session authentication e token CSRF"
)
public class AuthController {

    private final SessionAuthenticationService authenticationService;

    public AuthController(SessionAuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @GetMapping("/csrf")
    @Operation(
        summary = "Get CSRF token",
        description = "Returns il token CSRF necessario per le state-changing requests"
    )
    @ApiResponse(
        responseCode = "200",
        description = "CSRF token restituito con successo"
    )
    public CsrfTokenResponse csrf(CsrfToken csrfToken) {
        return CsrfTokenResponse.from(csrfToken);
    }

    @PostMapping("/login")
    @Operation(
        summary = "Authenticate user",
        description = "Validazione delle credenziali e creazione della server-side authentication session"
    )
    @SecurityRequirement(name = "csrfToken")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Autenticazione completata con successo"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Request body invalido"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Username o password invalide"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "CSRF token mancante o errato"
        )
    })
    public CurrentUserResponse login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = authenticationService.authenticate(loginRequest.username(), loginRequest.password(), request, response);

        return CurrentUserResponse.from(authentication);
    }

    @GetMapping("/me")
    @Operation(
        summary = "Get current user",
        description = "Restituisce l'utente associato alla sessione"
    )
    @SecurityRequirement(name = "sessionCookie")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Utenza restituita correttamente"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication session sbagliata o mancante"
        )
    })
    public CurrentUserResponse currentUser(Authentication authentication) {
        return CurrentUserResponse.from(authentication);
    }
}
