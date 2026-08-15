package it.personarum.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.personarum.domain.user.UserAccount;
import it.personarum.service.UserAccountService;
import it.personarum.web.dto.admin.ChangeUserPasswordRequest;
import it.personarum.web.dto.admin.CreateUserAccountRequest;
import it.personarum.web.dto.admin.UpdateUserAccountRequest;
import it.personarum.web.dto.admin.UserAccountResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
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
 * Espone le operazioni amministrative REST per la gestione degli account utente.
 */
@RestController
@RequestMapping("/api/admin/users")
@Tag(name = "Utenti - amministrazione", description = "Gestione degli account da parte degli amministratori")
@SecurityRequirement(name = "sessionCookie")
public class AdminUserAccountController {

    private final UserAccountService userAccountService;

    /**
     * Crea il controller amministrativo degli account.
     *
     * @param userAccountService servizio applicativo degli account utente
     */
    public AdminUserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    /**
     * Restituisce tutti gli account presenti.
     *
     * @return elenco degli account
     */
    @GetMapping
    @Operation(summary = "Elenca utenti")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Utenti restituiti con successo"), @ApiResponse(responseCode = "403", description = "Utente non amministratore")})
    public List<UserAccountResponse> findAll() {
        return userAccountService.findAll().stream().map(UserAccountResponse::from).toList();
    }

    /**
     * Recupera un account tramite identificativo.
     *
     * @param id identificativo dell'account
     * @return account richiesto
     */
    @GetMapping("/{id}")
    @Operation(summary = "Recupera utente")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Utente restituito", content = @Content(schema = @Schema(implementation = UserAccountResponse.class))), @ApiResponse(responseCode = "404", description = "Utente non trovato", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    public UserAccountResponse findById(@PathVariable Long id) {
        return UserAccountResponse.from(userAccountService.findById(id));
    }

    /**
     * Crea un nuovo account applicativo.
     *
     * @param request username, password e ruolo del nuovo account
     * @return account creato
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea utente")
    @SecurityRequirement(name = "csrfToken")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Dati del nuovo account", content = @Content(schema = @Schema(implementation = CreateUserAccountRequest.class)))
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Utente creato"), @ApiResponse(responseCode = "400", description = "Dati non validi"), @ApiResponse(responseCode = "403", description = "Utente non autorizzato o token CSRF non valido"), @ApiResponse(responseCode = "409", description = "Username già utilizzato")})
    public UserAccountResponse create(@Valid @RequestBody CreateUserAccountRequest request) {
        UserAccount userAccount = userAccountService.create(request.username(), request.password(), request.role());
        return UserAccountResponse.from(userAccount);
    }

    /**
     * Aggiorna ruolo e stato di abilitazione dell'account.
     *
     * @param id      identificativo dell'account
     * @param request dati aggiornati
     * @return account aggiornato
     */
    @PutMapping("/{id}")
    @Operation(summary = "Aggiorna utente")
    @SecurityRequirement(name = "csrfToken")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Utente aggiornato"), @ApiResponse(responseCode = "400", description = "Dati non validi"), @ApiResponse(responseCode = "404", description = "Utente non trovato")})
    public UserAccountResponse update(@PathVariable Long id, @Valid @RequestBody UpdateUserAccountRequest request) {
        UserAccount userAccount = userAccountService.update(id, request.role(), request.enabled());
        return UserAccountResponse.from(userAccount);
    }

    /**
     * Sostituisce la password dell'account identificato.
     *
     * @param id      identificativo dell'account
     * @param request nuova password
     * @return account aggiornato
     */
    @PutMapping("/{id}/password")
    @Operation(summary = "Modifica password utente")
    @SecurityRequirement(name = "csrfToken")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Password modificata"), @ApiResponse(responseCode = "400", description = "Password non valida"), @ApiResponse(responseCode = "404", description = "Utente non trovato")})
    public UserAccountResponse changePassword(@PathVariable Long id, @Valid @RequestBody ChangeUserPasswordRequest request) {
        UserAccount userAccount = userAccountService.changePassword(id, request.password());
        return UserAccountResponse.from(userAccount);
    }
}
