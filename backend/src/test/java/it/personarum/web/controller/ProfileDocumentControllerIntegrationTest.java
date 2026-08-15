package it.personarum.web.controller;

import it.personarum.domain.document.DocumentType;
import it.personarum.domain.document.ProfileDocument;
import it.personarum.domain.profile.Profile;
import it.personarum.repository.ProfileDocumentFileRepository;
import it.personarum.repository.ProfileDocumentRepository;
import it.personarum.repository.ProfileRepository;
import it.personarum.web.dto.document.CreateProfileDocumentRequest;
import it.personarum.web.dto.document.UpdateProfileDocumentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProfileDocumentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ProfileDocumentRepository documentRepository;

    @Autowired
    private ProfileDocumentFileRepository fileRepository;

    private Profile profile;

    @BeforeEach
    void setUp() {
        fileRepository.deleteAll();
        documentRepository.deleteAll();
        profileRepository.deleteAll();

        profile = profileRepository.save(
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
    }

    @Test
    void shouldCreateAndReadDocument() throws Exception {
        CreateProfileDocumentRequest request =
            new CreateProfileDocumentRequest(
                DocumentType.IDENTITY_CARD,
                "CA12345",
                "Comune di Milano",
                LocalDate.of(2024, 1, 10),
                LocalDate.of(2034, 1, 10),
                "Documento principale"
            );

        mockMvc.perform(post("/api/profiles/{profileId}/documents", profile.getId())
                .with(user("operator").roles("OPERATOR"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.type").value("IDENTITY_CARD"))
            .andExpect(jsonPath("$.status").value("ACTIVE"));

        mockMvc.perform(get("/api/profiles/{profileId}/documents", profile.getId()).with(user("operator").roles("OPERATOR")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldUploadAndDownloadFile() throws Exception {
        ProfileDocument document = documentRepository.save(
            ProfileDocument.create(
                profile,
                DocumentType.PASSPORT,
                "YA123",
                null,
                null,
                null,
                null
            )
        );

        byte[] payload = "contenuto-pdf".getBytes();

        MockMultipartFile file = new MockMultipartFile("file", "passaporto.pdf", MediaType.APPLICATION_PDF_VALUE, payload);

        mockMvc.perform(
            multipart(HttpMethod.PUT, "/api/profiles/{profileId}/documents/{documentId}/file", profile.getId(), document.getId())
                .file(file)
                .with(user("operator").roles("OPERATOR"))
                .with(csrf()))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/profiles/{profileId}/documents/{documentId}/file", profile.getId(), document.getId()).with(user("operator").roles("OPERATOR")))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_PDF))
            .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("passaporto.pdf")))
            .andExpect(content().bytes(payload));
    }

    @Test
    void shouldRejectUnsupportedFileType() throws Exception {
        ProfileDocument document = documentRepository.save(
            ProfileDocument.create(
                profile,
                DocumentType.OTHER,
                null,
                null,
                null,
                null,
                null
            )
        );

        MockMultipartFile file = new MockMultipartFile("file", "documento.txt", MediaType.TEXT_PLAIN_VALUE, "testo".getBytes());

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/profiles/{profileId}/documents/{documentId}/file", profile.getId(), document.getId())
                .file(file)
                .with(user("operator").roles("OPERATOR"))
                .with(csrf()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("Dati non validi"));
    }

    @Test
    void shouldEnforceArchivedStateAndAllowRestore() throws Exception {
        ProfileDocument document = documentRepository.save(
            ProfileDocument.create(
                profile,
                DocumentType.DRIVING_LICENSE,
                "AB123",
                null,
                null,
                null,
                null
            )
        );

        mockMvc.perform(put("/api/profiles/{profileId}/documents/{documentId}/archive", profile.getId(), document.getId())
                .with(user("operator").roles("OPERATOR"))
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("ARCHIVED"));

        UpdateProfileDocumentRequest update =
            new UpdateProfileDocumentRequest(
                DocumentType.DRIVING_LICENSE,
                "NEW",
                null,
                null,
                null,
                null
            );

        mockMvc.perform(put("/api/profiles/{profileId}/documents/{documentId}", profile.getId(), document.getId())
                .with(user("operator").roles("OPERATOR"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(update)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.title").value("Operazione non consentita nello stato corrente"));

        mockMvc.perform(put("/api/profiles/{profileId}/documents/{documentId}/restore", profile.getId(), document.getId())
                .with(user("operator").roles("OPERATOR"))
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void shouldDeleteAttachedFileWithoutDeletingDocument()
        throws Exception {

        ProfileDocument document = documentRepository.save(
            ProfileDocument.create(
                profile,
                DocumentType.OTHER,
                null,
                null,
                null,
                null,
                null
            )
        );

        MockMultipartFile file = new MockMultipartFile("file", "immagine.png", MediaType.IMAGE_PNG_VALUE, new byte[]{1, 2, 3});

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/profiles/{profileId}/documents/{documentId}/file", profile.getId(), document.getId())
                .file(file)
                .with(user("operator").roles("OPERATOR"))
                .with(csrf()))
            .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/profiles/{profileId}/documents/{documentId}/file", profile.getId(), document.getId())
                .with(user("operator").roles("OPERATOR"))
                .with(csrf()))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/profiles/{profileId}/documents/{documentId}", profile.getId(), document.getId()).with(user("operator").roles("OPERATOR")))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/profiles/{profileId}/documents/{documentId}/file", profile.getId(), document.getId()).with(user("operator").roles("OPERATOR")))
            .andExpect(status().isNotFound());
    }
}
