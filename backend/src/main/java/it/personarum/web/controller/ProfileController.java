package it.personarum.web.controller;

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
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse create(@Valid @RequestBody CreateProfileRequest request) {
        Profile profile = profileService.create(request.firstName(), request.lastName(), request.birthDate(), request.birthPlace(), request.fiscalCode(), request.email(), request.phone());

        return ProfileResponse.from(profile);
    }

    @GetMapping
    public List<ProfileResponse> findAll() {
        return profileService.findAll().stream().map(ProfileResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ProfileResponse findById(@PathVariable Long id) {
        return ProfileResponse.from(profileService.findById(id));
    }

    @PutMapping("/{id}")
    public ProfileResponse update(@PathVariable Long id, @Valid @RequestBody UpdateProfileRequest request) {
        Profile profile = profileService.update(id, request.firstName(), request.lastName(), request.birthDate(), request.birthPlace(), request.fiscalCode(), request.email(), request.phone());

        return ProfileResponse.from(profile);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        profileService.delete(id);
    }
}
