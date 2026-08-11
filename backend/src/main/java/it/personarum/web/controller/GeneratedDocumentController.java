package it.personarum.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.personarum.domain.generation.GeneratedDocument;
import it.personarum.service.GeneratedDocumentPdfService;
import it.personarum.service.GeneratedDocumentService;
import it.personarum.web.dto.generation.GenerateDocumentRequest;
import it.personarum.web.dto.generation.GeneratedDocumentResponse;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/profiles/{profileId}/generated-documents")
@Tag(name = "Generated documents", description = "Generazione e consultazione dei documenti prodotti dai template")
@SecurityRequirement(name = "sessionCookie")
public class GeneratedDocumentController {

    private final GeneratedDocumentService service;
    private final GeneratedDocumentPdfService pdfService;

    public GeneratedDocumentController(GeneratedDocumentService service, GeneratedDocumentPdfService pdfService)
    {
        this.service = service;
        this.pdfService = pdfService;
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

    @GetMapping(value = "/{documentId}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Download generated document as PDF")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long profileId, @PathVariable Long documentId) {
        GeneratedDocument document = service.findById(profileId, documentId);

        byte[] pdf = pdfService.generate(document);
        String fileName = document.getId() + ".pdf";
        ContentDisposition contentDisposition = ContentDisposition.attachment().filename(fileName, StandardCharsets.UTF_8).build();

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).contentLength(pdf.length).header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString()).body(pdf);
    }
}
