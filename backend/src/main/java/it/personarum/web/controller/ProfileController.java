package it.personarum.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profiles")
@Tag(
    name = "Profiles",
    description = "Management of personal profiles"
)
@SecurityRequirement(name = "sessionCookie")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create profile",
        description = "Crea un profilo a seguito della validazione e normalizzazione dei dati"
    )
    @Parameter(
        name = "X-CSRF-TOKEN",
        description = "CSRF preso da GET /api/auth/csrf",
        required = true,
        in = ParameterIn.HEADER
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Profilo creato con successo"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dati del profilo non validi"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication session sbalgiata o mancante"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "CSRF token mancante o errato"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Codice fiscale associato ad altro profilo"
        )
    })

    public ProfileResponse create(@Valid @RequestBody CreateProfileRequest request) {
        Profile profile = profileService.create(request.firstName(), request.lastName(), request.birthDate(), request.birthPlace(), request.fiscalCode(), request.email(), request.phone());

        return ProfileResponse.from(profile);
    }

    @GetMapping
    @Operation(
        summary = "List profiles",
        description = "Restituisce l'elenco completo dei profili."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Profili restituiti con successo"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication session sbagliata o mancante"
        )
    })
    public List<ProfileResponse> findAll() {
        return profileService.findAll().stream().map(ProfileResponse::from).toList();
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get profile",
        description = "Restituisce il profilo avente il codice identificativo riportato."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Profilo restituito con successo"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication session sbagliata o mancante"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Profilo non trovato"
        )
    })
    public ProfileResponse findById(@PathVariable Long id) {
        return ProfileResponse.from(profileService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update profile",
        description = "Aggiorna i dati di un profilo esistente."
    )
    @Parameter(
        name = "X-CSRF-TOKEN",
        description = "CSRF preso da GET /api/auth/csrf",
        required = true,
        in = ParameterIn.HEADER
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Profilo restituito con successo"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dati profilo invalidi"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication session sbagliata o mancante"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "CSRF token mancante o errato"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Profilo non trovato"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Codice fiscale associato ad altro profilo"
        )
    })
    public ProfileResponse update(@PathVariable Long id, @Valid @RequestBody UpdateProfileRequest request) {
        Profile profile = profileService.update(id, request.firstName(), request.lastName(), request.birthDate(), request.birthPlace(), request.fiscalCode(), request.email(), request.phone());

        return ProfileResponse.from(profile);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete profile",
        description = "Elimina il profilo avente codice identificativo riportato."
    )
    @Parameter(
        name = "X-CSRF-TOKEN",
        description = "CSRF token preso da GET /api/auth/csrf",
        required = true,
        in = ParameterIn.HEADER
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Profilo eliminato con successo"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication session sbagliata o mancante"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "CSRF token mancante o errato"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Profilo non trovato"
        )
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        profileService.delete(id);
    }
}
