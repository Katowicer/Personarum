package it.personarum.service;

import it.personarum.domain.template.DocumentTemplate;
import it.personarum.repository.DocumentTemplateRepository;
import it.personarum.service.exception.DocumentTemplateNameAlreadyExistsException;
import it.personarum.service.exception.DocumentTemplateNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentTemplateServiceTest {

    @Mock
    private DocumentTemplateRepository repository;

    private DocumentTemplateService service;

    @BeforeEach
    void setUp() {
        service = new DocumentTemplateService(repository);
    }

    @Test
    void shouldCreateTemplate() {
        when(repository.existsByNameIgnoreCase("Dichiarazione")).thenReturn(false);

        when(repository.save(any(DocumentTemplate.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DocumentTemplate result = service.create("Dichiarazione", null, "Io {nome} {cognome}");

        assertThat(result.getName()).isEqualTo("Dichiarazione");

        verify(repository).save(any(DocumentTemplate.class));
    }

    @Test
    void shouldRejectDuplicateName() {
        when(repository.existsByNameIgnoreCase("Dichiarazione")).thenReturn(true);

        assertThatThrownBy(() -> service.create("Dichiarazione", null, "Contenuto")).isInstanceOf(DocumentTemplateNameAlreadyExistsException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void shouldFindTemplateById() {
        DocumentTemplate template = DocumentTemplate.create("Dichiarazione", null, "Contenuto");

        when(repository.findById(1L)).thenReturn(Optional.of(template));

        assertThat(service.findById(1L)).isSameAs(template);
    }

    @Test
    void shouldFailWhenTemplateDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L)).isInstanceOf(DocumentTemplateNotFoundException.class);
    }

    @Test
    void shouldUpdateTemplate() {
        DocumentTemplate template = DocumentTemplate.create("Vecchio", null, "Vecchio contenuto");

        when(repository.findById(1L)).thenReturn(Optional.of(template));

        when(repository.existsByNameIgnoreCaseAndIdNot("Nuovo", 1L)).thenReturn(false);

        DocumentTemplate result = service.update(1L, "Nuovo", "Descrizione", "Nuovo contenuto", false);

        assertThat(result.getName()).isEqualTo("Nuovo");

        assertThat(result.getContent()).isEqualTo("Nuovo contenuto");

        assertThat(result.isEnabled()).isFalse();
    }
}
