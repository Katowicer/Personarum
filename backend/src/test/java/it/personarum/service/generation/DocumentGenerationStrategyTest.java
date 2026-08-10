package it.personarum.service.generation;

import it.personarum.domain.generation.DocumentGenerationType;
import it.personarum.domain.profile.Profile;
import it.personarum.domain.template.DocumentTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DocumentGenerationStrategyTest {

    private ProfilePlaceholderResolver resolver;
    private Profile profile;
    private DocumentTemplate template;

    @BeforeEach
    void setUp() {
        resolver = new ProfilePlaceholderResolver();

        profile = mock(Profile.class);
        template = mock(DocumentTemplate.class);

        when(profile.getFirstName()).thenReturn("Mario");
        when(profile.getLastName()).thenReturn("Rossi");
        when(profile.getFiscalCode()).thenReturn("RSSMRA90E10F205X");

        when(template.getContent()).thenReturn("Il sottoscritto {firstName} {lastName}");
    }

    @Test
    void standardStrategyShouldResolveTemplate() {
        StandardDocumentGenerationStrategy strategy = new StandardDocumentGenerationStrategy(resolver);

        assertThat(strategy.getType()).isEqualTo(DocumentGenerationType.STANDARD);
        assertThat(strategy.generate(template, profile)).isEqualTo("Il sottoscritto Mario Rossi");
    }

    @Test
    void profileSummaryStrategyShouldAddProfileSummary() {
        ProfileSummaryDocumentGenerationStrategy strategy = new ProfileSummaryDocumentGenerationStrategy(resolver);

        String result = strategy.generate(template, profile);
        assertThat(strategy.getType()).isEqualTo(DocumentGenerationType.PROFILE_SUMMARY);
        assertThat(result).contains("Il sottoscritto Mario Rossi").contains("DATI ANAGRAFICI").contains("Nome: Mario").contains("Cognome: Rossi").contains("Codice fiscale: RSSMRA90E10F205X");
    }
}
