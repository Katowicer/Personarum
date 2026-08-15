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
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Espone le operazioni REST sui documenti personali e sui relativi file allegati.
 *
 * <p>La classe mantiene nello stesso controller i metadati del documento, le transizioni
 * di stato e il file allegato perché appartengono alla medesima risorsa REST annidata
 * nel profilo. La logica applicativa rimane delegata ai servizi dedicati.</p>
 */
@RestController
@RequestMapping("/api/profiles/{profileId}/documents")
@Tag(name = "Documenti dei profili", description = "Gestione dei documenti e dei file associati ai profili")
@SecurityRequirement(name = "sessionCookie")
public class ProfileDocumentController {

    private final ProfileDocumentService profileDocumentService;
    private final ProfileDocumentFileService profileDocumentFileService;

    /**
     * Crea il controller dei documenti personali.
     *
     * @param profileDocumentService     servizio applicativo dei documenti del profilo
     * @param profileDocumentFileService servizio applicativo dei file allegati ai documenti
     */
    public ProfileDocumentController(ProfileDocumentService profileDocumentService, ProfileDocumentFileService profileDocumentFileService) {
        this.profileDocumentService = profileDocumentService;
        this.profileDocumentFileService = profileDocumentFileService;
    }

    /**
     * Associa un nuovo documento al profilo indicato.
     *
     * @param profileId identificativo del profilo
     * @param request   metadati del documento da creare
     * @return rappresentazione del documento creato
     * @throws it.personarum.service.exception.ProfileNotFoundException se il profilo non esiste
     * @throws IllegalArgumentException                                 se i metadati non rispettano le regole del dominio
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea documento del profilo", description = "Associa un nuovo documento al profilo indicato.")
    @SecurityRequirement(name = "csrfToken")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Documento creato con successo", content = @Content(schema = @Schema(implementation = ProfileDocumentResponse.class))), @ApiResponse(responseCode = "400", description = "Dati del documento non validi", content = @Content(schema = @Schema(implementation = ProblemDetail.class))), @ApiResponse(responseCode = "401", description = "Sessione di autenticazione mancante o non valida"), @ApiResponse(responseCode = "403", description = "Token CSRF mancante o non valido"), @ApiResponse(responseCode = "404", description = "Profilo non trovato", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    public ProfileDocumentResponse create(@PathVariable Long profileId, @Valid @RequestBody CreateProfileDocumentRequest request) {
        ProfileDocument document = profileDocumentService.create(profileId, request.type(), request.documentNumber(), request.issuingAuthority(), request.issueDate(), request.expirationDate(), request.notes());
        return ProfileDocumentResponse.from(document);
    }

    /**
     * Restituisce tutti i documenti associati al profilo.
     *
     * @param profileId identificativo del profilo
     * @return elenco dei documenti del profilo
     * @throws it.personarum.service.exception.ProfileNotFoundException se il profilo non esiste
     */
    @GetMapping
    @Operation(summary = "Elenca documenti del profilo", description = "Restituisce tutti i documenti associati al profilo.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Documenti restituiti con successo"), @ApiResponse(responseCode = "401", description = "Sessione di autenticazione mancante o non valida"), @ApiResponse(responseCode = "404", description = "Profilo non trovato", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    public List<ProfileDocumentResponse> findAll(@PathVariable Long profileId) {
        return profileDocumentService.findAllByProfileId(profileId).stream().map(ProfileDocumentResponse::from).toList();
    }

    /**
     * Recupera un documento appartenente al profilo indicato.
     *
     * @param profileId  identificativo del profilo
     * @param documentId identificativo del documento
     * @return documento richiesto
     * @throws it.personarum.service.exception.ProfileNotFoundException         se il profilo non esiste
     * @throws it.personarum.service.exception.ProfileDocumentNotFoundException se il documento non esiste o non appartiene al profilo
     */
    @GetMapping("/{documentId}")
    @Operation(summary = "Recupera documento del profilo", description = "Restituisce il documento indicato se appartiene al profilo.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Documento restituito con successo", content = @Content(schema = @Schema(implementation = ProfileDocumentResponse.class))), @ApiResponse(responseCode = "401", description = "Sessione di autenticazione mancante o non valida"), @ApiResponse(responseCode = "404", description = "Profilo o documento non trovato", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    public ProfileDocumentResponse findById(@PathVariable Long profileId, @PathVariable Long documentId) {
        return ProfileDocumentResponse.from(profileDocumentService.findById(profileId, documentId));
    }

    /**
     * Aggiorna i metadati di un documento attivo.
     *
     * @param profileId  identificativo del profilo
     * @param documentId identificativo del documento
     * @param request    metadati aggiornati del documento
     * @return documento aggiornato
     * @throws it.personarum.service.exception.ProfileDocumentNotFoundException   se il documento non esiste o non appartiene al profilo
     * @throws it.personarum.domain.document.InvalidProfileDocumentStateException se il documento non è modificabile nello stato corrente
     * @throws IllegalArgumentException                                           se i metadati non rispettano le regole del dominio
     */
    @PutMapping("/{documentId}")
    @Operation(summary = "Aggiorna documento del profilo", description = "Aggiorna i metadati di un documento attivo associato al profilo.")
    @SecurityRequirement(name = "csrfToken")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Documento aggiornato con successo", content = @Content(schema = @Schema(implementation = ProfileDocumentResponse.class))), @ApiResponse(responseCode = "400", description = "Metadati del documento non validi", content = @Content(schema = @Schema(implementation = ProblemDetail.class))), @ApiResponse(responseCode = "401", description = "Sessione di autenticazione mancante o non valida"), @ApiResponse(responseCode = "403", description = "Token CSRF mancante o non valido"), @ApiResponse(responseCode = "404", description = "Profilo o documento non trovato", content = @Content(schema = @Schema(implementation = ProblemDetail.class))), @ApiResponse(responseCode = "409", description = "Documento non modificabile nello stato corrente", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    public ProfileDocumentResponse update(@PathVariable Long profileId, @PathVariable Long documentId, @Valid @RequestBody UpdateProfileDocumentRequest request) {
        ProfileDocument document = profileDocumentService.update(profileId, documentId, request.type(), request.documentNumber(), request.issuingAuthority(), request.issueDate(), request.expirationDate(), request.notes());
        return ProfileDocumentResponse.from(document);
    }

    /**
     * Elimina un documento associato al profilo.
     *
     * @param profileId  identificativo del profilo
     * @param documentId identificativo del documento
     * @throws it.personarum.service.exception.ProfileDocumentNotFoundException se il documento non esiste o non appartiene al profilo
     */
    @DeleteMapping("/{documentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Elimina documento del profilo", description = "Elimina un documento associato al profilo.")
    @SecurityRequirement(name = "csrfToken")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Documento eliminato con successo"), @ApiResponse(responseCode = "401", description = "Sessione di autenticazione mancante o non valida"), @ApiResponse(responseCode = "403", description = "Token CSRF mancante o non valido"), @ApiResponse(responseCode = "404", description = "Profilo o documento non trovato", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    public void delete(@PathVariable Long profileId, @PathVariable Long documentId) {
        profileDocumentService.delete(profileId, documentId);
    }

    /**
     * Porta il documento nello stato archiviato, rendendolo non modificabile.
     *
     * @param profileId  identificativo del profilo
     * @param documentId identificativo del documento
     * @return documento archiviato
     * @throws it.personarum.service.exception.ProfileDocumentNotFoundException   se il documento non esiste o non appartiene al profilo
     * @throws it.personarum.domain.document.InvalidProfileDocumentStateException se la transizione non è consentita
     */
    @PutMapping("/{documentId}/archive")
    @Operation(summary = "Archivia documento del profilo", description = "Archivia il documento rendendolo non modificabile.")
    @SecurityRequirement(name = "csrfToken")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Documento archiviato con successo", content = @Content(schema = @Schema(implementation = ProfileDocumentResponse.class))), @ApiResponse(responseCode = "401", description = "Sessione di autenticazione mancante o non valida"), @ApiResponse(responseCode = "403", description = "Token CSRF mancante o non valido"), @ApiResponse(responseCode = "404", description = "Profilo o documento non trovato", content = @Content(schema = @Schema(implementation = ProblemDetail.class))), @ApiResponse(responseCode = "409", description = "Transizione di stato non consentita", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    public ProfileDocumentResponse archive(@PathVariable Long profileId, @PathVariable Long documentId) {
        return ProfileDocumentResponse.from(profileDocumentService.archive(profileId, documentId));
    }

    /**
     * Ripristina un documento archiviato riportandolo nello stato attivo.
     *
     * @param profileId  identificativo del profilo
     * @param documentId identificativo del documento
     * @return documento ripristinato
     * @throws it.personarum.service.exception.ProfileDocumentNotFoundException   se il documento non esiste o non appartiene al profilo
     * @throws it.personarum.domain.document.InvalidProfileDocumentStateException se la transizione non è consentita
     */
    @PutMapping("/{documentId}/restore")
    @Operation(summary = "Ripristina documento del profilo", description = "Riporta nello stato attivo un documento precedentemente archiviato.")
    @SecurityRequirement(name = "csrfToken")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Documento ripristinato con successo", content = @Content(schema = @Schema(implementation = ProfileDocumentResponse.class))), @ApiResponse(responseCode = "401", description = "Sessione di autenticazione mancante o non valida"), @ApiResponse(responseCode = "403", description = "Token CSRF mancante o non valido"), @ApiResponse(responseCode = "404", description = "Profilo o documento non trovato", content = @Content(schema = @Schema(implementation = ProblemDetail.class))), @ApiResponse(responseCode = "409", description = "Transizione di stato non consentita", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    public ProfileDocumentResponse restore(@PathVariable Long profileId, @PathVariable Long documentId) {
        return ProfileDocumentResponse.from(profileDocumentService.restore(profileId, documentId));
    }

    /**
     * Carica o sostituisce il file binario associato al documento indicato.
     *
     * @param profileId  identificativo del profilo
     * @param documentId identificativo del documento
     * @param file       file da associare al documento
     * @throws IOException                                                        se non è possibile leggere il contenuto del file ricevuto
     * @throws it.personarum.service.exception.ProfileDocumentNotFoundException   se il documento non esiste o non appartiene al profilo
     * @throws it.personarum.domain.document.InvalidProfileDocumentStateException se il documento non è modificabile nello stato corrente
     * @throws IllegalArgumentException                                           se il file è vuoto o ha un formato non supportato
     */
    @PutMapping(value = "/{documentId}/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Carica file del documento", description = "Carica o sostituisce il file associato al documento. Sono ammessi PDF, JPEG e PNG fino a 5 MiB.")
    @SecurityRequirement(name = "csrfToken")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "File caricato o sostituito con successo"), @ApiResponse(responseCode = "400", description = "File vuoto o formato non supportato", content = @Content(schema = @Schema(implementation = ProblemDetail.class))), @ApiResponse(responseCode = "401", description = "Sessione di autenticazione mancante o non valida"), @ApiResponse(responseCode = "403", description = "Token CSRF mancante o non valido"), @ApiResponse(responseCode = "404", description = "Profilo o documento non trovato", content = @Content(schema = @Schema(implementation = ProblemDetail.class))), @ApiResponse(responseCode = "409", description = "Documento non modificabile nello stato corrente", content = @Content(schema = @Schema(implementation = ProblemDetail.class))), @ApiResponse(responseCode = "413", description = "File superiore alla dimensione massima consentita", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    public void uploadFile(@PathVariable Long profileId, @PathVariable Long documentId, @RequestParam("file") MultipartFile file) throws IOException {
        profileDocumentFileService.upload(profileId, documentId, file.getOriginalFilename(), file.getContentType(), file.getBytes());
    }

    /**
     * Restituisce il file allegato predisponendo le intestazioni HTTP necessarie al download.
     *
     * @param profileId  identificativo del profilo
     * @param documentId identificativo del documento
     * @return risposta HTTP contenente il file e le relative informazioni di tipo e nome
     * @throws it.personarum.service.exception.ProfileDocumentFileNotFoundException se il file non è presente
     */
    @GetMapping("/{documentId}/file")
    @Operation(summary = "Scarica file del documento", description = "Scarica il file associato al documento indicato.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "File restituito con successo", content = {@Content(mediaType = "application/pdf", schema = @Schema(type = "string", format = "binary")), @Content(mediaType = "image/jpeg", schema = @Schema(type = "string", format = "binary")), @Content(mediaType = "image/png", schema = @Schema(type = "string", format = "binary"))}), @ApiResponse(responseCode = "401", description = "Sessione di autenticazione mancante o non valida"), @ApiResponse(responseCode = "404", description = "Profilo, documento o file non trovato", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long profileId, @PathVariable Long documentId) {
        ProfileDocumentFile documentFile = profileDocumentFileService.download(profileId, documentId);
        ContentDisposition contentDisposition = ContentDisposition.attachment().filename(documentFile.getOriginalFileName(), StandardCharsets.UTF_8).build();

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(documentFile.getContentType())).contentLength(documentFile.getFileSize()).header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString()).body(documentFile.getFileContent());
    }

    /**
     * Rimuove il file allegato mantenendo i metadati del documento.
     *
     * @param profileId  identificativo del profilo
     * @param documentId identificativo del documento
     * @throws it.personarum.service.exception.ProfileDocumentFileNotFoundException se il file non è presente
     * @throws it.personarum.domain.document.InvalidProfileDocumentStateException   se il documento non è modificabile nello stato corrente
     */
    @DeleteMapping("/{documentId}/file")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Elimina file del documento", description = "Elimina il file allegato mantenendo i metadati del documento.")
    @SecurityRequirement(name = "csrfToken")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "File eliminato con successo"), @ApiResponse(responseCode = "401", description = "Sessione di autenticazione mancante o non valida"), @ApiResponse(responseCode = "403", description = "Token CSRF mancante o non valido"), @ApiResponse(responseCode = "404", description = "Profilo, documento o file non trovato", content = @Content(schema = @Schema(implementation = ProblemDetail.class))), @ApiResponse(responseCode = "409", description = "Documento non modificabile nello stato corrente", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    public void deleteFile(@PathVariable Long profileId, @PathVariable Long documentId) {
        profileDocumentFileService.delete(profileId, documentId);
    }
}
