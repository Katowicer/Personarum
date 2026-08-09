package it.personarum.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.personarum.domain.template.DocumentTemplate;
import it.personarum.service.DocumentTemplateService;
import it.personarum.web.dto.template.CreateDocumentTemplateRequest;
import it.personarum.web.dto.template.DocumentTemplateResponse;
import it.personarum.web.dto.template.UpdateDocumentTemplateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/templates")
@Tag(name = "Admin document templates", description = "Gestione amministrativa dei template")
@SecurityRequirement(name = "sessionCookie")
public class AdminDocumentTemplateController {

    private final DocumentTemplateService service;

    public AdminDocumentTemplateController(DocumentTemplateService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List all templates")
    public List<DocumentTemplateResponse> findAll() {
        return service.findAll().stream().map(DocumentTemplateResponse::from).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get template")
    public DocumentTemplateResponse findById(@PathVariable Long id) {
        return DocumentTemplateResponse.from(service.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create template")
    @SecurityRequirement(name = "csrfToken")
    public DocumentTemplateResponse create(@Valid @RequestBody CreateDocumentTemplateRequest request) {
        DocumentTemplate template = service.create(request.name(), request.description(), request.content());

        return DocumentTemplateResponse.from(template);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update template")
    @SecurityRequirement(name = "csrfToken")
    public DocumentTemplateResponse update(@PathVariable Long id, @Valid @RequestBody UpdateDocumentTemplateRequest request) {
        DocumentTemplate template = service.update(id, request.name(), request.description(), request.content(), request.enabled());

        return DocumentTemplateResponse.from(template);
    }
}
