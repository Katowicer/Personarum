package it.personarum.web.controller;

import it.personarum.security.SessionAuthenticationService;
import it.personarum.web.dto.auth.*;
import jakarta.servlet.http.*;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final SessionAuthenticationService authenticationService;

    public AuthController(SessionAuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @GetMapping("/csrf")
    public CsrfTokenResponse csrf(CsrfToken csrfToken) {
        return CsrfTokenResponse.from(csrfToken);
    }

    @PostMapping("/login")
    public CurrentUserResponse login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = authenticationService.authenticate(loginRequest.username(), loginRequest.password(), request, response);

        return CurrentUserResponse.from(authentication);
    }

    @GetMapping("/me")
    public CurrentUserResponse currentUser(Authentication authentication) {
        return CurrentUserResponse.from(authentication);
    }
}
