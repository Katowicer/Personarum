package it.personarum.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.personarum.service.DocumentTemplateService;
import it.personarum.web.dto.template.DocumentTemplateResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
@Tag(name = "Document templates", description = "Template disponibili per la generazione")
@SecurityRequirement(name = "sessionCookie")
public class DocumentTemplateController {

    private final DocumentTemplateService service;

    public DocumentTemplateController(
        DocumentTemplateService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List enabled templates")
    public List<DocumentTemplateResponse> findEnabled() {
        return service
            .findEnabled()
            .stream()
            .map(DocumentTemplateResponse::from)
            .toList();
    }
}
