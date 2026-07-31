package it.personarum.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SESSION_COOKIE_SCHEME = "sessionCookie";
    private static final String CSRF_TOKEN_SCHEME = "csrfToken";

    @Bean
    OpenAPI personarumOpenApi() {
        return new OpenAPI()
            .info(
                new Info()
                    .title("Personarum API")
                    .description(
                        "REST API per la gestione di profili e documentazione e template di documenti associati"
                    )
                    .version("1.0.0")
            )
            .components(
                new Components()
                    .addSecuritySchemes(
                        SESSION_COOKIE_SCHEME,
                        new SecurityScheme()
                            .type(SecurityScheme.Type.APIKEY)
                            .in(SecurityScheme.In.COOKIE)
                            .name("JSESSIONID")
                            .description(
                                "Server-side authentication session creata tramite POST /api/auth/login"
                            )
                    )
                    .addSecuritySchemes(
                        CSRF_TOKEN_SCHEME,
                        new SecurityScheme()
                            .type(SecurityScheme.Type.APIKEY)
                            .in(SecurityScheme.In.HEADER)
                            .name("X-CSRF-TOKEN")
                            .description(
                                "CSRF token preso da GET /api/auth/csrf"
                            )
                    )
            );
    }
}
