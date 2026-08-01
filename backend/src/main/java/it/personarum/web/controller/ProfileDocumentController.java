package it.personarum.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.personarum.domain.document.ProfileDocument;
import it.personarum.service.ProfileDocumentService;
import it.personarum.web.dto.document.CreateProfileDocumentRequest;
import it.personarum.web.dto.document.ProfileDocumentResponse;
import it.personarum.web.dto.document.UpdateProfileDocumentRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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

    public ProfileDocumentController(ProfileDocumentService profileDocumentService) {
        this.profileDocumentService = profileDocumentService;
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
}
