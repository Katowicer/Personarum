package it.personarum.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.personarum.domain.generation.GeneratedDocument;
import it.personarum.service.GeneratedDocumentService;
import it.personarum.web.dto.generation.GenerateDocumentRequest;
import it.personarum.web.dto.generation.GeneratedDocumentResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profiles/{profileId}/generated-documents")
@Tag(name = "Generated documents", description = "Generazione e consultazione dei documenti prodotti dai template")
@SecurityRequirement(name = "sessionCookie")
public class GeneratedDocumentController {

    private final GeneratedDocumentService service;

    public GeneratedDocumentController(GeneratedDocumentService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Generate document from template")
    @SecurityRequirement(name = "csrfToken")
    public GeneratedDocumentResponse generate(@PathVariable Long profileId, @Valid @RequestBody GenerateDocumentRequest request) {
        GeneratedDocument document = service.generate(profileId, request.templateId(), request.generationType());
        return GeneratedDocumentResponse.from(document);
    }

    @GetMapping
    @Operation(summary = "List generated documents for profile")
    public List<GeneratedDocumentResponse> findAll(@PathVariable Long profileId) {
        return service.findAllByProfileId(profileId).stream().map(GeneratedDocumentResponse::from).toList();
    }

    @GetMapping("/{documentId}")
    @Operation(summary = "Get generated document")
    public GeneratedDocumentResponse findById(@PathVariable Long profileId, @PathVariable Long documentId) {
        return GeneratedDocumentResponse.from(service.findById(profileId, documentId));
    }
}
