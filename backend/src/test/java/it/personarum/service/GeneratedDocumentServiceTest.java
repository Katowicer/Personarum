package it.personarum.service;

import it.personarum.domain.generation.DocumentGenerationType;
import it.personarum.domain.generation.GeneratedDocument;
import it.personarum.domain.profile.Profile;
import it.personarum.domain.template.DocumentTemplate;
import it.personarum.repository.DocumentTemplateRepository;
import it.personarum.repository.GeneratedDocumentRepository;
import it.personarum.repository.ProfileRepository;
import it.personarum.service.exception.DocumentTemplateDisabledException;
import it.personarum.service.exception.GeneratedDocumentNotFoundException;
import it.personarum.service.exception.ProfileNotFoundException;
import it.personarum.service.generation.DocumentGenerationStrategy;
import it.personarum.service.generation.DocumentGenerationStrategyResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeneratedDocumentServiceTest {

    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private DocumentTemplateRepository templateRepository;
    @Mock
    private GeneratedDocumentRepository generatedDocumentRepository;
    @Mock
    private DocumentGenerationStrategyResolver strategyResolver;
    @Mock
    private DocumentGenerationStrategy strategy;

    private GeneratedDocumentService service;

    @BeforeEach
    void setUp() {
        service = new GeneratedDocumentService(profileRepository, templateRepository, generatedDocumentRepository, strategyResolver);
    }

    @Test
    void shouldGenerateAndPersistDocument() {
        Profile profile = Profile.create("Mario", "Rossi", null, null, null, null, null);
        DocumentTemplate template = DocumentTemplate.create("Dichiarazione", null, "Ciao {firstName}");

        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(templateRepository.findById(2L)).thenReturn(Optional.of(template));
        when(strategyResolver.resolve(DocumentGenerationType.STANDARD)).thenReturn(strategy);
        when(strategy.generate(template, profile)).thenReturn("Ciao Mario");
        when(generatedDocumentRepository.save(org.mockito.ArgumentMatchers.any(GeneratedDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GeneratedDocument result = service.generate(1L, 2L, DocumentGenerationType.STANDARD);

        assertThat(result.getProfile()).isSameAs(profile);
        assertThat(result.getTemplate()).isSameAs(template);
        assertThat(result.getGenerationType()).isEqualTo(DocumentGenerationType.STANDARD);
        assertThat(result.getContent()).isEqualTo("Ciao Mario");
        verify(strategy).generate(template, profile);
    }

    @Test
    void shouldRejectDisabledTemplate() {
        Profile profile = Profile.create("Mario", "Rossi", null, null, null, null, null);
        DocumentTemplate template = DocumentTemplate.create("Dichiarazione", null, "Contenuto");
        template.disable();

        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(templateRepository.findById(2L)).thenReturn(Optional.of(template));

        assertThatThrownBy(() -> service.generate(1L, 2L, DocumentGenerationType.STANDARD)).isInstanceOf(DocumentTemplateDisabledException.class);
    }

    @Test
    void shouldListGeneratedDocumentsForExistingProfile() {
        Profile profile = Profile.create("Mario", "Rossi", null, null, null, null, null);
        DocumentTemplate template = DocumentTemplate.create("Dichiarazione", null, "Contenuto");
        GeneratedDocument generated = GeneratedDocument.create(profile, template, DocumentGenerationType.STANDARD, "Contenuto");

        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(generatedDocumentRepository.findAllByProfileIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(generated));

        assertThat(service.findAllByProfileId(1L)).containsExactly(generated);
    }

    @Test
    void shouldRejectUnknownProfileWhenListing() {
        when(profileRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findAllByProfileId(99L)).isInstanceOf(ProfileNotFoundException.class);
    }

    @Test
    void shouldFindGeneratedDocumentOwnedByProfile() {
        Profile profile = Profile.create("Mario", "Rossi", null, null, null, null, null);
        DocumentTemplate template = DocumentTemplate.create("Dichiarazione", null, "Contenuto");
        GeneratedDocument generated = GeneratedDocument.create(profile, template, DocumentGenerationType.STANDARD, "Contenuto");

        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(generatedDocumentRepository.findByIdAndProfileId(5L, 1L)).thenReturn(Optional.of(generated));

        assertThat(service.findById(1L, 5L)).isSameAs(generated);
    }

    @Test
    void shouldRejectGeneratedDocumentOwnedByAnotherProfile() {
        Profile profile = Profile.create("Mario", "Rossi", null, null, null, null, null);
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(generatedDocumentRepository.findByIdAndProfileId(5L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(1L, 5L)).isInstanceOf(GeneratedDocumentNotFoundException.class);
    }
}
