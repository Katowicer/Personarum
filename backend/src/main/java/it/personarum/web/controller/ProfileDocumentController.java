package it.personarum.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.personarum.domain.document.ProfileDocument;
import it.personarum.domain.document.ProfileDocumentFile;
import it.personarum.service.ProfileDocumentFileService;
import it.personarum.service.ProfileDocumentService;
import it.personarum.web.dto.document.CreateProfileDocumentRequest;
import it.personarum.web.dto.document.ProfileDocumentResponse;
import it.personarum.web.dto.document.UpdateProfileDocumentRequest;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/profiles/{profileId}/documents")
@Tag(
    name = "Profile documents",
    description = "Gestione dei documenti associati ai profili"
)
@SecurityRequirement(name = "sessionCookie")
public class ProfileDocumentController {

    private final ProfileDocumentService profileDocumentService;
    private final ProfileDocumentFileService profileDocumentFileService;

    public ProfileDocumentController(ProfileDocumentService profileDocumentService, ProfileDocumentFileService profileDocumentFileService) {
        this.profileDocumentService = profileDocumentService;
        this.profileDocumentFileService = profileDocumentFileService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create profile document",
        description = "Associa un nuovo documento al profilo indicato."
    )
    @SecurityRequirement(name = "csrfToken")
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Documento creato con successo"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dati del documento non validi"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Sessione di autenticazione mancante o non valida"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Token CSRF mancante o non valido"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Profilo non trovato"
        )
    })
    public ProfileDocumentResponse create(@PathVariable Long profileId, @Valid @RequestBody CreateProfileDocumentRequest request) {
        ProfileDocument document = profileDocumentService.create(
            profileId,
            request.type(),
            request.documentNumber(),
            request.issuingAuthority(),
            request.issueDate(),
            request.expirationDate(),
            request.notes()
        );

        return ProfileDocumentResponse.from(document);
    }

    @GetMapping
    @Operation(
        summary = "List profile documents",
        description = "Restituisce tutti i documenti associati al profilo."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Documenti restituiti con successo"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Sessione di autenticazione mancante o non valida"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Profilo non trovato"
        )
    })
    public List<ProfileDocumentResponse> findAll(@PathVariable Long profileId) {
        return profileDocumentService
            .findAllByProfileId(profileId)
            .stream()
            .map(ProfileDocumentResponse::from)
            .toList();
    }

    @GetMapping("/{documentId}")
    @Operation(
        summary = "Get profile document",
        description = "Restituisce un documento appartenente al profilo riportato."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Documento restituito con successo"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Sessione di autenticazione mancante o non valida"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Profilo o documento non trovato"
        )
    })
    public ProfileDocumentResponse findById(@PathVariable Long profileId, @PathVariable Long documentId) {
        ProfileDocument document = profileDocumentService.findById(profileId, documentId);

        return ProfileDocumentResponse.from(document);
    }

    @PutMapping("/{documentId}")
    @Operation(
        summary = "Update profile document",
        description = "Sostituisce i metadati di un documento associato al profilo."
    )
    @SecurityRequirement(name = "csrfToken")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Documento aggiornato con successo"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Metadati del documento non validi"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Sessione di autenticazione mancante o non valida"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Token CSRF mancante o non valido"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Profilo o documento non trovato"
        )
    })
    public ProfileDocumentResponse update(@PathVariable Long profileId, @PathVariable Long documentId, @Valid @RequestBody UpdateProfileDocumentRequest request) {
        ProfileDocument document = profileDocumentService.update(
            profileId,
            documentId,
            request.type(),
            request.documentNumber(),
            request.issuingAuthority(),
            request.issueDate(),
            request.expirationDate(),
            request.notes()
        );

        return ProfileDocumentResponse.from(document);
    }

    @DeleteMapping("/{documentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete profile document",
        description = "Elimina un documento associato al profilo."
    )
    @SecurityRequirement(name = "csrfToken")
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Documento eliminato con successo"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Sessione di autenticazione mancante o non valida"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Token CSRF mancante o non valido"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Profilo o documento non trovato"
        )
    })
    public void delete(@PathVariable Long profileId, @PathVariable Long documentId) {
        profileDocumentService.delete(profileId, documentId);
    }

    @PutMapping("/{documentId}/archive")
    @Operation(
        summary = "Archive profile document",
        description = "Archivia il documento rendendolo non modificabile."
    )
    @SecurityRequirement(name = "csrfToken")
    public ProfileDocumentResponse archive(@PathVariable Long profileId, @PathVariable Long documentId) {
        ProfileDocument document = profileDocumentService.archive(profileId, documentId);
        return ProfileDocumentResponse.from(document);
    }

    @PutMapping("/{documentId}/restore")
    @Operation(
        summary = "Restore profile document",
        description = "Ripristina un documento precedentemente archiviato."
    )
    @SecurityRequirement(name = "csrfToken")
    public ProfileDocumentResponse restore(@PathVariable Long profileId, @PathVariable Long documentId) {
        ProfileDocument document = profileDocumentService.restore(profileId, documentId);
        return ProfileDocumentResponse.from(document);
    }

    @PutMapping(value = "/{documentId}/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Upload document file",
        description = """
        Carica un file oppure sostituisce quello già legato al documento.
        Sono ammessi file PDF, JPEG e PNG (per ora) fino a 5 MiB.
        """
    )
    @SecurityRequirement(name = "csrfToken")
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "File caricato o sostituito con successo"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "File vuoto o formato non supportato"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Sessione di autenticazione mancante o non valida"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Token CSRF mancante o non valido"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Profilo o documento non trovato"
        ),
        @ApiResponse(
            responseCode = "413",
            description = "File superiore alla dimensione massima consentita"
        )
    })
    public void uploadFile(@PathVariable Long profileId, @PathVariable Long documentId, @RequestParam("file") MultipartFile file) throws IOException {
        profileDocumentFileService.upload(
            profileId,
            documentId,
            file.getOriginalFilename(),
            file.getContentType(),
            file.getBytes()
        );
    }

    /* Uso ResponseEntity in quanto bisogna impostare diverse specifiche HTTP per i file */
    @GetMapping("/{documentId}/file")
    @Operation(
        summary = "Download document file",
        description = "Scarica il file associato al documento indicato."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "File restituito con successo",
            content = {
                @Content(
                    mediaType = "application/pdf",
                    schema = @Schema(type = "string", format = "binary")
                ),
                @Content(
                    mediaType = "image/jpeg",
                    schema = @Schema(type = "string", format = "binary")
                ),
                @Content(
                    mediaType = "image/png",
                    schema = @Schema(type = "string", format = "binary")
                )
            }
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Sessione di autenticazione mancante o non valida"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Profilo, documento o file non trovato"
        )
    })
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long profileId, @PathVariable Long documentId) {
        ProfileDocumentFile documentFile = profileDocumentFileService.download(profileId, documentId);

        ContentDisposition contentDisposition = ContentDisposition
            .attachment()
            .filename(documentFile.getOriginalFileName(), StandardCharsets.UTF_8)
            .build();

        return ResponseEntity
            .ok()
            .contentType(MediaType.parseMediaType(documentFile.getContentType()))
            .contentLength(documentFile.getFileSize())
            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
            .body(documentFile.getFileContent());
    }

    @DeleteMapping("/{documentId}/file")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete document file",
        description = """
        Elimina solo il file allegato mantenendo i dati del documento.
        """
    )
    @SecurityRequirement(name = "csrfToken")
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "File eliminato con successo"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Sessione di autenticazione mancante o non valida"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Token CSRF mancante o non valido"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Profilo, documento o file non trovato"
        )
    })
    public void deleteFile(@PathVariable Long profileId, @PathVariable Long documentId) {
        profileDocumentFileService.delete(profileId, documentId);
    }
}
