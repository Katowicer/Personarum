package it.personarum.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.personarum.domain.template.DocumentTemplate;
import it.personarum.service.DocumentTemplateService;
import it.personarum.web.dto.template.CreateDocumentTemplateRequest;
import it.personarum.web.dto.template.DocumentTemplateResponse;
import it.personarum.web.dto.template.UpdateDocumentTemplateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Espone le operazioni amministrative REST per la gestione dei template documentali.
 */
@RestController
@RequestMapping("/api/admin/templates")
@Tag(name = "Template documenti - amministrazione", description = "Gestione amministrativa dei template")
@SecurityRequirement(name = "sessionCookie")
public class AdminDocumentTemplateController {

    private final DocumentTemplateService service;

    /**
     * Crea il controller amministrativo dei template.
     *
     * @param service servizio applicativo dei template
     */
    public AdminDocumentTemplateController(DocumentTemplateService service) {
        this.service = service;
    }

    /**
     * Restituisce tutti i template, compresi quelli disabilitati.
     *
     * @return elenco completo dei template
     */
    @GetMapping
    @Operation(summary = "Elenca tutti i template")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Template restituiti con successo"), @ApiResponse(responseCode = "403", description = "Utente non amministratore")})
    public List<DocumentTemplateResponse> findAll() {
        return service.findAll().stream().map(DocumentTemplateResponse::from).toList();
    }

    /**
     * Recupera un template tramite identificativo.
     *
     * @param id identificativo del template
     * @return template richiesto
     */
    @GetMapping("/{id}")
    @Operation(summary = "Recupera template")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Template restituito", content = @Content(schema = @Schema(implementation = DocumentTemplateResponse.class))), @ApiResponse(responseCode = "404", description = "Template non trovato", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    public DocumentTemplateResponse findById(@PathVariable Long id) {
        return DocumentTemplateResponse.from(service.findById(id));
    }

    /**
     * Crea un nuovo template documentale.
     *
     * @param request dati del template da creare
     * @return template creato
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea template")
    @SecurityRequirement(name = "csrfToken")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Dati del nuovo template", content = @Content(schema = @Schema(implementation = CreateDocumentTemplateRequest.class)))
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Template creato", content = @Content(schema = @Schema(implementation = DocumentTemplateResponse.class))), @ApiResponse(responseCode = "400", description = "Dati non validi"), @ApiResponse(responseCode = "403", description = "Utente non autorizzato o token CSRF non valido"), @ApiResponse(responseCode = "409", description = "Nome template già utilizzato")})
    public DocumentTemplateResponse create(@Valid @RequestBody CreateDocumentTemplateRequest request) {
        DocumentTemplate template = service.create(request.name(), request.description(), request.content());
        return DocumentTemplateResponse.from(template);
    }

    /**
     * Aggiorna un template esistente.
     *
     * @param id      identificativo del template
     * @param request dati aggiornati
     * @return template aggiornato
     */
    @PutMapping("/{id}")
    @Operation(summary = "Aggiorna template")
    @SecurityRequirement(name = "csrfToken")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Dati aggiornati del template", content = @Content(schema = @Schema(implementation = UpdateDocumentTemplateRequest.class)))
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Template aggiornato"), @ApiResponse(responseCode = "400", description = "Dati non validi"), @ApiResponse(responseCode = "404", description = "Template non trovato"), @ApiResponse(responseCode = "409", description = "Nome template già utilizzato")})
    public DocumentTemplateResponse update(@PathVariable Long id, @Valid @RequestBody UpdateDocumentTemplateRequest request) {
        DocumentTemplate template = service.update(id, request.name(), request.description(), request.content(), request.enabled());
        return DocumentTemplateResponse.from(template);
    }
}
