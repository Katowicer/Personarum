package it.personarum.web.controller;

import it.personarum.domain.profile.Profile;
import it.personarum.repository.ProfileRepository;
import it.personarum.web.dto.profile.CreateProfileRequest;
import it.personarum.web.dto.profile.UpdateProfileRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProfileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProfileRepository profileRepository;

    @BeforeEach
    void setUp() {
        profileRepository.deleteAll();
    }

    @Test
    void shouldRejectUnauthenticatedProfileList() throws Exception {
        mockMvc.perform(get("/api/profiles")).andExpect(status().isUnauthorized());
    }

    @Test
    void shouldCreateAndNormalizeProfile() throws Exception {
        CreateProfileRequest request = new CreateProfileRequest(
            "  Mario  ",
            "  Rossi  ",
            LocalDate.of(1990, 5, 10),
            " Milano ",
            "rssmra90e10f205x",
            "MARIO.ROSSI@EXAMPLE.COM",
            " +393331234567 "
        );

        mockMvc.perform(post("/api/profiles")
                .with(user("operator").roles("OPERATOR"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.firstName").value("Mario"))
            .andExpect(jsonPath("$.lastName").value("Rossi"))
            .andExpect(jsonPath("$.birthPlace").value("Milano"))
            .andExpect(jsonPath("$.fiscalCode").value("RSSMRA90E10F205X"))
            .andExpect(jsonPath("$.email").value("mario.rossi@example.com"))
            .andExpect(jsonPath("$.phone").value("+393331234567"));
    }

    @Test
    void shouldRejectCreateWithoutCsrf() throws Exception {
        CreateProfileRequest request = new CreateProfileRequest(
            "Mario",
            "Rossi",
            null,
            null,
            null,
            null,
            null
        );

        mockMvc.perform(post("/api/profiles")
                .with(user("operator").roles("OPERATOR"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)))
            .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnValidationProblemForInvalidProfile() throws Exception {
        CreateProfileRequest request = new CreateProfileRequest(
            " ",
            "Rossi",
            null,
            null,
            null,
            null,
            null
        );

        mockMvc.perform(post("/api/profiles")
                .with(user("operator").roles("OPERATOR"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("Richiesta non valida"))
            .andExpect(jsonPath("$.errors.firstName").exists());
    }

    @Test
    void shouldReturnConflictForDuplicateFiscalCode() throws Exception {
        profileRepository.save(
            Profile.create(
                "Mario",
                "Rossi",
                null,
                null,
                "RSSMRA90E10F205X",
                null,
                null
            )
        );

        CreateProfileRequest request = new CreateProfileRequest(
            "Luigi",
            "Verdi",
            null,
            null,
            "rssmra90e10f205x",
            null,
            null
        );

        mockMvc.perform(post("/api/profiles")
                .with(user("operator").roles("OPERATOR"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.title").value("Codice fiscale già utilizzato"));
    }

    @Test
    void shouldUpdateProfileKeepingSameFiscalCodeWithDifferentCase() throws Exception {
        Profile profile = profileRepository.save(
            Profile.create(
                "Mario",
                "Rossi",
                null,
                null,
                "RSSMRA90E10F205X",
                null,
                null
            )
        );

        UpdateProfileRequest request = new UpdateProfileRequest(
            "Mario",
            "Rossi",
            null,
            null,
            "rssmra90e10f205x",
            "mario@example.com",
            null
        );

        mockMvc.perform(put("/api/profiles/{id}", profile.getId())
                .with(user("operator").roles("OPERATOR"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fiscalCode").value("RSSMRA90E10F205X"));
    }

    @Test
    void shouldDeleteProfile() throws Exception {
        Profile profile = profileRepository.save(
            Profile.create(
                "Mario",
                "Rossi",
                null,
                null,
                null,
                null,
                null
            )
        );

        mockMvc.perform(delete("/api/profiles/{id}", profile.getId()).with(csrf()).with(user("operator").roles("OPERATOR"))).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/profiles/{id}", profile.getId()).with(user("operator").roles("OPERATOR"))).andExpect(status().isNotFound());
    }
}
