package it.personarum.domain.template;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class DocumentTemplateTest {

    @Test
    void shouldCreateEnabledTemplate() {
        DocumentTemplate template = DocumentTemplate.create(" Dichiarazione ", " Template semplice ", "Io {nome} {cognome}");

        assertThat(template.getName()).isEqualTo("Dichiarazione");
        assertThat(template.getDescription()).isEqualTo("Template semplice");
        assertThat(template.getContent()).isEqualTo("Io {nome} {cognome}");
        assertThat(template.isEnabled()).isTrue();
    }

    @Test
    void shouldRejectBlankName() {
        assertThatThrownBy(() -> DocumentTemplate.create(" ", null, "Contenuto")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectBlankContent() {
        assertThatThrownBy(() -> DocumentTemplate.create("Template", null, " ")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldDisableAndEnableTemplate() {
        DocumentTemplate template = DocumentTemplate.create("Template", null, "Contenuto");
        template.disable();
        assertThat(template.isEnabled()).isFalse();

        template.enable();
        assertThat(template.isEnabled()).isTrue();
    }
}
