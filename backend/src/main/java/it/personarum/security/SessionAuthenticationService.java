package it.personarum.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

/**
 * Gestisce il login applicativo creando e persistendo il contesto di sicurezza nella sessione HTTP.
 */
@Service
public class SessionAuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final SessionAuthenticationStrategy sessionStrategy;
    private final SecurityContextHolderStrategy contextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();

    /**
     * Crea il servizio di autenticazione basato su sessione.
     *
     * @param authenticationManager     componente che verifica le credenziali
     * @param securityContextRepository repository che persiste il contesto di sicurezza nella sessione
     * @param sessionStrategy           strategia applicata dopo l'autenticazione, inclusa la rotazione dell'identificativo di sessione
     */
    public SessionAuthenticationService(AuthenticationManager authenticationManager,
                                        SecurityContextRepository securityContextRepository,
                                        SessionAuthenticationStrategy sessionStrategy) {
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
        this.sessionStrategy = sessionStrategy;
    }

    /**
     * Autentica username e password, applica la strategia di sessione e salva il nuovo contesto di sicurezza.
     *
     * @param username username dell'account
     * @param password password in chiaro ricevuta dal client
     * @param request  richiesta HTTP corrente
     * @param response risposta HTTP corrente
     * @return autenticazione verificata
     * @throws AuthenticationException se le credenziali non sono valide o l'account non può autenticarsi
     */
    public Authentication authenticate(String username, String password, HttpServletRequest request, HttpServletResponse response) {
        Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
        Authentication authentication = authenticationManager.authenticate(authenticationRequest);
        sessionStrategy.onAuthentication(authentication, request, response);

        SecurityContext context = contextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        contextHolderStrategy.setContext(context);
        securityContextRepository.saveContext(context, request, response);

        return authentication;
    }
}
