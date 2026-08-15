package it.personarum.web.controller;

import it.personarum.domain.template.DocumentTemplate;
import it.personarum.repository.DocumentTemplateRepository;
import it.personarum.web.dto.template.CreateDocumentTemplateRequest;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DocumentTemplateControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DocumentTemplateRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void shouldExposeOnlyEnabledTemplatesToOperator() throws Exception {
        repository.save(
            DocumentTemplate.create("Attivo", null, "Contenuto")
        );

        DocumentTemplate disabled = DocumentTemplate.create(
            "Disabilitato",
            null,
            "Contenuto"
        );
        disabled.disable();
        repository.save(disabled);

        mockMvc.perform(get("/api/templates").with(user("operator").roles("OPERATOR")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].name").value("Attivo"));
    }

    @Test
    void shouldRejectAdminTemplateEndpointForOperator() throws Exception {
        mockMvc.perform(get("/api/admin/templates").with(user("operator").roles("OPERATOR")))
            .andExpect(status().isForbidden());
    }

    @Test
    void shouldCreateTemplateAsAdmin() throws Exception {
        CreateDocumentTemplateRequest request =
            new CreateDocumentTemplateRequest(
                " Dichiarazione ",
                "Descrizione",
                "Ciao {firstName}"
            );

        mockMvc.perform(post("/api/admin/templates")
                .with(user("admin").roles("ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Dichiarazione"))
            .andExpect(jsonPath("$.enabled").value(true));
    }

    @Test
    void shouldRejectDuplicateTemplateNameAfterNormalization() throws Exception {
        repository.save(
            DocumentTemplate.create(
                "Dichiarazione",
                null,
                "Contenuto"
            )
        );

        CreateDocumentTemplateRequest request =
            new CreateDocumentTemplateRequest(
                "  Dichiarazione  ",
                null,
                "Altro contenuto"
            );

        mockMvc.perform(post("/api/admin/templates")
                .with(user("admin").roles("ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.title").value("Template già esistente"));
    }
}
