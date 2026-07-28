package it.personarum.web.controller;

import it.personarum.domain.user.*;
import it.personarum.repository.UserAccountRepository;
import it.personarum.web.dto.auth.LoginRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.*;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userAccountRepository.deleteAll();
        userAccountRepository.save(UserAccount.create("operator", passwordEncoder.encode("Operator-local-2026!"), Role.OPERATOR));
    }

    @Test
    void shouldExposeCsrfToken() throws Exception {
        mockMvc
            .perform(get("/api/auth/csrf"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andExpect(jsonPath("$.headerName").value("X-CSRF-TOKEN"));
    }

    @Test
    void shouldRejectUnauthenticatedRequest() throws Exception {
        mockMvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());
    }

    @Test
    void shouldCreateAuthenticatedSession() throws Exception {
        LoginRequest request = new LoginRequest("operator", "Operator-local-2026!");

        MvcResult loginResult = mockMvc
            .perform(
                post("/api/auth/login")
                    .with(csrf())
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsBytes(request))
            ).andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("operator"))
            .andExpect(jsonPath("$.role").value("OPERATOR"))
            .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);

        Assertions.assertNotNull(session);

        mockMvc
            .perform(get("/api/auth/me").session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("operator"));
    }

    @Test
    void shouldRejectWrongPassword() throws Exception {
        LoginRequest request = new LoginRequest("operator", "wrong-password");

        mockMvc.perform(post("/api/auth/login").with(csrf()).contentType("application/json").content(objectMapper.writeValueAsBytes(request))).andExpect(status().isUnauthorized());
    }
}
