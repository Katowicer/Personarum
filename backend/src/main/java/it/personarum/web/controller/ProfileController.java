package it.personarum.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.personarum.domain.profile.Profile;
import it.personarum.service.ProfileService;
import it.personarum.web.dto.profile.CreateProfileRequest;
import it.personarum.web.dto.profile.ProfileResponse;
import it.personarum.web.dto.profile.UpdateProfileRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
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

/**
 * Espone le operazioni REST per la gestione dei profili personali.
 *
 * <p>Il controller delega la logica applicativa a {@link ProfileService} e si occupa
 * esclusivamente della traduzione tra richieste HTTP, DTO ed entità di dominio.</p>
 */
@RestController
@RequestMapping("/api/profiles")
@Tag(name = "Profili", description = "Gestione dei profili personali")
@SecurityRequirement(name = "sessionCookie")
public class ProfileController {

    private final ProfileService profileService;

    /**
     * Crea il controller dei profili.
     *
     * @param profileService servizio applicativo dei profili
     */
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    /**
     * Crea un nuovo profilo applicando validazione e normalizzazione dei dati.
     *
     * @param request dati del profilo da creare
     * @return rappresentazione del profilo creato
     * @throws it.personarum.service.exception.ProfileFiscalCodeAlreadyExistsException se il codice fiscale è già associato a un altro profilo
     * @throws IllegalArgumentException                                                se i dati non rispettano le regole del dominio
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea profilo", description = "Crea un profilo dopo la validazione e la normalizzazione dei dati.")
    @Parameter(name = "X-CSRF-TOKEN", description = "Token CSRF ottenuto da GET /api/auth/csrf", required = true, in = ParameterIn.HEADER)
    @SecurityRequirement(name = "csrfToken")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Profilo creato con successo", content = @Content(schema = @Schema(implementation = ProfileResponse.class))), @ApiResponse(responseCode = "400", description = "Dati del profilo non validi", content = @Content(schema = @Schema(implementation = ProblemDetail.class))), @ApiResponse(responseCode = "401", description = "Sessione di autenticazione mancante o non valida"), @ApiResponse(responseCode = "403", description = "Token CSRF mancante o non valido"), @ApiResponse(responseCode = "409", description = "Codice fiscale già associato a un altro profilo", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    public ProfileResponse create(@Valid @RequestBody CreateProfileRequest request) {
        Profile profile = profileService.create(request.firstName(), request.lastName(), request.birthDate(), request.birthPlace(), request.fiscalCode(), request.email(), request.phone());
        return ProfileResponse.from(profile);
    }

    /**
     * Restituisce tutti i profili disponibili.
     *
     * @return elenco dei profili
     */
    @GetMapping
    @Operation(summary = "Elenca profili", description = "Restituisce l'elenco completo dei profili.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Profili restituiti con successo"), @ApiResponse(responseCode = "401", description = "Sessione di autenticazione mancante o non valida")})
    public List<ProfileResponse> findAll() {
        return profileService.findAll().stream().map(ProfileResponse::from).toList();
    }

    /**
     * Recupera un profilo tramite il relativo identificativo.
     *
     * @param id identificativo univoco del profilo
     * @return profilo richiesto
     * @throws it.personarum.service.exception.ProfileNotFoundException se il profilo non esiste
     */
    @GetMapping("/{id}")
    @Operation(summary = "Recupera profilo", description = "Restituisce il profilo identificato dal valore indicato.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Profilo restituito con successo", content = @Content(schema = @Schema(implementation = ProfileResponse.class))), @ApiResponse(responseCode = "401", description = "Sessione di autenticazione mancante o non valida"), @ApiResponse(responseCode = "404", description = "Profilo non trovato", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    public ProfileResponse findById(@PathVariable Long id) {
        return ProfileResponse.from(profileService.findById(id));
    }

    /**
     * Aggiorna i dati del profilo identificato.
     *
     * @param id      identificativo univoco del profilo da aggiornare
     * @param request dati aggiornati del profilo
     * @return rappresentazione aggiornata del profilo
     * @throws it.personarum.service.exception.ProfileNotFoundException                se il profilo non esiste
     * @throws it.personarum.service.exception.ProfileFiscalCodeAlreadyExistsException se il codice fiscale è già associato a un altro profilo
     * @throws IllegalArgumentException                                                se i dati non rispettano le regole del dominio
     */
    @PutMapping("/{id}")
    @Operation(summary = "Aggiorna profilo", description = "Aggiorna i dati di un profilo esistente.")
    @Parameter(name = "X-CSRF-TOKEN", description = "Token CSRF ottenuto da GET /api/auth/csrf", required = true, in = ParameterIn.HEADER)
    @SecurityRequirement(name = "csrfToken")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Profilo aggiornato con successo", content = @Content(schema = @Schema(implementation = ProfileResponse.class))), @ApiResponse(responseCode = "400", description = "Dati del profilo non validi", content = @Content(schema = @Schema(implementation = ProblemDetail.class))), @ApiResponse(responseCode = "401", description = "Sessione di autenticazione mancante o non valida"), @ApiResponse(responseCode = "403", description = "Token CSRF mancante o non valido"), @ApiResponse(responseCode = "404", description = "Profilo non trovato", content = @Content(schema = @Schema(implementation = ProblemDetail.class))), @ApiResponse(responseCode = "409", description = "Codice fiscale già associato a un altro profilo", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    public ProfileResponse update(@PathVariable Long id, @Valid @RequestBody UpdateProfileRequest request) {
        Profile profile = profileService.update(id, request.firstName(), request.lastName(), request.birthDate(), request.birthPlace(), request.fiscalCode(), request.email(), request.phone());
        return ProfileResponse.from(profile);
    }

    /**
     * Elimina il profilo identificato.
     *
     * @param id identificativo univoco del profilo da eliminare
     * @throws it.personarum.service.exception.ProfileNotFoundException se il profilo non esiste
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Elimina profilo", description = "Elimina il profilo identificato dal valore indicato.")
    @Parameter(name = "X-CSRF-TOKEN", description = "Token CSRF ottenuto da GET /api/auth/csrf", required = true, in = ParameterIn.HEADER)
    @SecurityRequirement(name = "csrfToken")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Profilo eliminato con successo"), @ApiResponse(responseCode = "401", description = "Sessione di autenticazione mancante o non valida"), @ApiResponse(responseCode = "403", description = "Token CSRF mancante o non valido"), @ApiResponse(responseCode = "404", description = "Profilo non trovato", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    public void delete(@PathVariable Long id) {
        profileService.delete(id);
    }
}
