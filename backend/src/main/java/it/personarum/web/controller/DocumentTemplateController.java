package it.personarum.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.personarum.service.DocumentTemplateService;
import it.personarum.web.dto.template.DocumentTemplateResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Espone in sola lettura i template abilitati disponibili agli utenti autenticati.
 */
@RestController
@RequestMapping("/api/templates")
@Tag(name = "Template documenti", description = "Consultazione dei template abilitati per la generazione")
@SecurityRequirement(name = "sessionCookie")
public class DocumentTemplateController {

    private final DocumentTemplateService service;

    /**
     * Crea il controller di consultazione dei template.
     *
     * @param service servizio applicativo dei template
     */
    public DocumentTemplateController(DocumentTemplateService service) {
        this.service = service;
    }

    /**
     * Restituisce i template attualmente abilitati.
     *
     * @return elenco dei template disponibili per la generazione
     */
    @GetMapping
    @Operation(summary = "Elenca template abilitati", description = "Restituisce esclusivamente i template abilitati, ordinati per nome.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Template restituiti con successo"), @ApiResponse(responseCode = "401", description = "Sessione di autenticazione mancante o non valida")})
    public List<DocumentTemplateResponse> findEnabled() {
        return service.findEnabled().stream().map(DocumentTemplateResponse::from).toList();
    }
}
