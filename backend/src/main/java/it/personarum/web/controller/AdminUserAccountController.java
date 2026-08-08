package it.personarum.web.controller;

import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@Tag(
    name = "Admin users",
    description = "Gestione degli utenti da parte degli admin"
)
@SecurityRequirement(name = "sessionCookie")
public class AdminUserAccountController {

    private final UserAccountService userAccountService;

    public AdminUserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @GetMapping
    @Operation(summary = "List users")
    public List<UserAccountResponse> findAll() {
        return userAccountService
            .findAll()
            .stream()
            .map(UserAccountResponse::from)
            .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user")
    public UserAccountResponse findById(@PathVariable Long id) {
        return UserAccountResponse.from(userAccountService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create user")
    public UserAccountResponse create(@Valid @RequestBody CreateUserAccountRequest request) {
        UserAccount userAccount = userAccountService.create(request.username(), request.password(), request.role());
        return UserAccountResponse.from(userAccount);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user")
    public UserAccountResponse update(@PathVariable Long id, @Valid @RequestBody UpdateUserAccountRequest request) {
        UserAccount userAccount = userAccountService.update(id, request.role(), request.enabled());

        return UserAccountResponse.from(userAccount);
    }

    @PutMapping("/{id}/password")
    @Operation(summary = "Change user password")
    public UserAccountResponse changePassword(@PathVariable Long id, @Valid @RequestBody ChangeUserPasswordRequest request) {
        UserAccount userAccount = userAccountService.changePassword(id, request.password());
        return UserAccountResponse.from(userAccount);
    }
}
