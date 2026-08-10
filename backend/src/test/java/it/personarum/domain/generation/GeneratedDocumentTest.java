package it.personarum.domain.generation;

import it.personarum.domain.profile.Profile;
import it.personarum.domain.template.DocumentTemplate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;

class GeneratedDocumentTest {

    @Test
    void shouldCreateGeneratedDocument() {
        Profile profile = mock(Profile.class);
        DocumentTemplate template = mock(DocumentTemplate.class);

        GeneratedDocument document = GeneratedDocument.create(profile, template, DocumentGenerationType.STANDARD, "Documento compilato");

        assertThat(document.getProfile()).isSameAs(profile);
        assertThat(document.getTemplate()).isSameAs(template);
        assertThat(document.getGenerationType()).isEqualTo(DocumentGenerationType.STANDARD);
        assertThat(document.getContent()).isEqualTo("Documento compilato");
        assertThat(document.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldRejectBlankContent() {
        assertThatThrownBy(() -> GeneratedDocument.create(mock(Profile.class), mock(DocumentTemplate.class), DocumentGenerationType.STANDARD, " ")).isInstanceOf(IllegalArgumentException.class);
    }
}
