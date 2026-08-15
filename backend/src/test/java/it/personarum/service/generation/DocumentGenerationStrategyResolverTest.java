package it.personarum.service.generation;

import it.personarum.domain.generation.DocumentGenerationType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DocumentGenerationStrategyResolverTest {

    @Test
    void shouldResolveStrategyByGenerationType() {
        DocumentGenerationStrategy standard = mock(DocumentGenerationStrategy.class);
        when(standard.getType()).thenReturn(DocumentGenerationType.STANDARD);

        DocumentGenerationStrategyResolver resolver = new DocumentGenerationStrategyResolver(List.of(standard));
        assertThat(resolver.resolve(DocumentGenerationType.STANDARD)).isSameAs(standard);
    }

    @Test
    void shouldRejectUnsupportedGenerationType() {
        DocumentGenerationStrategy standard = mock(DocumentGenerationStrategy.class);
        when(standard.getType()).thenReturn(DocumentGenerationType.STANDARD);

        DocumentGenerationStrategyResolver resolver = new DocumentGenerationStrategyResolver(List.of(standard));
        assertThatThrownBy(() -> resolver.resolve(DocumentGenerationType.PROFILE_SUMMARY)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("PROFILE_SUMMARY");
    }
}
