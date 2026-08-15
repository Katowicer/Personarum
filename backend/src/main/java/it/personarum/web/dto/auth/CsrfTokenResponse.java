package it.personarum.web.dto.auth;

import org.springframework.security.web.csrf.CsrfToken;

/**
 * Rappresenta il token CSRF e i nomi utilizzati dal client per inviarlo nelle richieste protette.
 */
public record CsrfTokenResponse(String token, String headerName, String parameterName) {

    /**
     * Converte il token CSRF di Spring Security nella rappresentazione REST.
     *
     * @param csrfToken token CSRF della richiesta corrente
     * @return DTO contenente valore e nomi del token
     */
    public static CsrfTokenResponse from(CsrfToken csrfToken) {
        return new CsrfTokenResponse(csrfToken.getToken(), csrfToken.getHeaderName(), csrfToken.getParameterName());
    }

}
