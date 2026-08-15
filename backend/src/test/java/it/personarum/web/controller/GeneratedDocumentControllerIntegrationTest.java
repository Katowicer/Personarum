package it.personarum.web.controller;

import it.personarum.domain.generation.DocumentGenerationType;
import it.personarum.domain.profile.Profile;
import it.personarum.domain.template.DocumentTemplate;
import it.personarum.repository.DocumentTemplateRepository;
import it.personarum.repository.GeneratedDocumentRepository;
import it.personarum.repository.ProfileRepository;
import it.personarum.web.dto.generation.GenerateDocumentRequest;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GeneratedDocumentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GeneratedDocumentRepository generatedDocumentRepository;

    @Autowired
    private DocumentTemplateRepository templateRepository;

    @Autowired
    private ProfileRepository profileRepository;

    private Profile profile;
    private DocumentTemplate template;

    @BeforeEach
    void setUp() {
        generatedDocumentRepository.deleteAll();
        templateRepository.deleteAll();
        profileRepository.deleteAll();

        profile = profileRepository.save(
            Profile.create(
                "Mario",
                "Rossi",
                null,
                "Milano",
                "RSSMRA90E10F205X",
                "mario@example.com",
                null
            )
        );

        template = templateRepository.save(DocumentTemplate.create("Dichiarazione", null,"Nome: {firstName} {lastName}"));
    }

    @Test
    void shouldGenerateAndListDocument() throws Exception {
        GenerateDocumentRequest request = new GenerateDocumentRequest(template.getId(), DocumentGenerationType.STANDARD);

        mockMvc.perform(post("/api/profiles/{profileId}/generated-documents", profile.getId())
                .with(user("operator").roles("OPERATOR"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.templateName").value("Dichiarazione"))
            .andExpect(jsonPath("$.content").value("Nome: Mario Rossi"));

        mockMvc.perform(get("/api/profiles/{profileId}/generated-documents", profile.getId()).with(user("operator").roles("OPERATOR")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldDownloadGeneratedDocumentAsPdf() throws Exception {
        GenerateDocumentRequest request = new GenerateDocumentRequest(template.getId(), DocumentGenerationType.STANDARD);

        mockMvc.perform(post("/api/profiles/{profileId}/generated-documents", profile.getId())
                .with(user("operator").roles("OPERATOR"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)))
            .andExpect(status().isCreated());

        Long documentId = generatedDocumentRepository
            .findAllByProfileIdOrderByCreatedAtDesc(profile.getId())
            .getFirst()
            .getId();

        mockMvc.perform(get("/api/profiles/{profileId}/generated-documents/{documentId}/pdf", profile.getId(), documentId).with(user("operator").roles("OPERATOR")))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_PDF))
            .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString(documentId + ".pdf")))
            .andExpect(result ->
                org.assertj.core.api.Assertions.assertThat(result.getResponse().getContentAsByteArray()).isNotEmpty()
            );
    }

    @Test
    void shouldRejectDisabledTemplate() throws Exception {
        template.disable();
        templateRepository.save(template);

        GenerateDocumentRequest request = new GenerateDocumentRequest(template.getId(), DocumentGenerationType.STANDARD);

        mockMvc.perform(post("/api/profiles/{profileId}/generated-documents", profile.getId())
                .with(user("operator").roles("OPERATOR"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("Template non disponibile"));
    }
}
