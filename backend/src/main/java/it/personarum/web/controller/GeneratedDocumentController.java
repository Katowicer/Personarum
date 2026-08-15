package it.personarum.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.personarum.domain.generation.GeneratedDocument;
import it.personarum.service.GeneratedDocumentPdfService;
import it.personarum.service.GeneratedDocumentService;
import it.personarum.web.dto.generation.GenerateDocumentRequest;
import it.personarum.web.dto.generation.GeneratedDocumentResponse;
import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Espone le operazioni REST di generazione, consultazione e download PDF dei documenti prodotti.
 */
@RestController
@RequestMapping("/api/profiles/{profileId}/generated-documents")
@Tag(name = "Documenti generati", description = "Generazione e consultazione dei documenti prodotti dai template")
@SecurityRequirement(name = "sessionCookie")
public class GeneratedDocumentController {

    private final GeneratedDocumentService service;
    private final GeneratedDocumentPdfService pdfService;

    /**
     * Crea il controller dei documenti generati.
     *
     * @param service    servizio applicativo di generazione
     * @param pdfService servizio di esportazione PDF
     */
    public GeneratedDocumentController(GeneratedDocumentService service, GeneratedDocumentPdfService pdfService) {
        this.service = service;
        this.pdfService = pdfService;
    }

    /**
     * Genera un documento per il profilo utilizzando il template e la strategia richiesti.
     *
     * @param profileId identificativo del profilo
     * @param request   parametri di generazione
     * @return documento generato e persistito
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Genera documento da template")
    @SecurityRequirement(name = "csrfToken")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Template e strategia di generazione", content = @Content(schema = @Schema(implementation = GenerateDocumentRequest.class)))
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Documento generato", content = @Content(schema = @Schema(implementation = GeneratedDocumentResponse.class))), @ApiResponse(responseCode = "400", description = "Template disabilitato o richiesta non valida"), @ApiResponse(responseCode = "404", description = "Profilo o template non trovato")})
    public GeneratedDocumentResponse generate(@PathVariable Long profileId, @Valid @RequestBody GenerateDocumentRequest request) {
        GeneratedDocument document = service.generate(profileId, request.templateId(), request.generationType());
        return GeneratedDocumentResponse.from(document);
    }

    /**
     * Restituisce lo storico dei documenti generati per il profilo.
     *
     * @param profileId identificativo del profilo
     * @return documenti generati ordinati dal più recente
     */
    @GetMapping
    @Operation(summary = "Elenca documenti generati del profilo")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Documenti restituiti"), @ApiResponse(responseCode = "404", description = "Profilo non trovato")})
    public List<GeneratedDocumentResponse> findAll(@PathVariable Long profileId) {
        return service.findAllByProfileId(profileId).stream().map(GeneratedDocumentResponse::from).toList();
    }

    /**
     * Recupera un documento generato appartenente al profilo.
     *
     * @param profileId  identificativo del profilo
     * @param documentId identificativo del documento generato
     * @return documento richiesto
     */
    @GetMapping("/{documentId}")
    @Operation(summary = "Recupera documento generato")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Documento restituito", content = @Content(schema = @Schema(implementation = GeneratedDocumentResponse.class))), @ApiResponse(responseCode = "404", description = "Profilo o documento non trovato", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    public GeneratedDocumentResponse findById(@PathVariable Long profileId, @PathVariable Long documentId) {
        return GeneratedDocumentResponse.from(service.findById(profileId, documentId));
    }

    /**
     * Esporta in PDF un documento generato.
     *
     * @param profileId  identificativo del profilo
     * @param documentId identificativo del documento generato
     * @return risposta HTTP contenente il PDF in allegato
     */
    @GetMapping(value = "/{documentId}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Scarica documento generato in PDF")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "PDF generato", content = @Content(mediaType = MediaType.APPLICATION_PDF_VALUE, schema = @Schema(type = "string", format = "binary"))), @ApiResponse(responseCode = "404", description = "Profilo o documento non trovato"), @ApiResponse(responseCode = "500", description = "Errore durante la generazione del PDF")})
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long profileId, @PathVariable Long documentId) {
        GeneratedDocument document = service.findById(profileId, documentId);
        byte[] pdf = pdfService.generate(document);
        String fileName = document.getId() + ".pdf";
        ContentDisposition contentDisposition = ContentDisposition.attachment().filename(fileName, StandardCharsets.UTF_8).build();

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).contentLength(pdf.length).header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString()).body(pdf);
    }
}
