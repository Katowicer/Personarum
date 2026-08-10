package it.personarum.service.generation;

import it.personarum.domain.generation.DocumentGenerationType;
import it.personarum.domain.profile.Profile;
import it.personarum.domain.template.DocumentTemplate;
import org.springframework.stereotype.Component;

@Component
public class StandardDocumentGenerationStrategy
    implements DocumentGenerationStrategy {

    private final ProfilePlaceholderResolver placeholderResolver;

    public StandardDocumentGenerationStrategy(
        ProfilePlaceholderResolver placeholderResolver
    ) {
        this.placeholderResolver = placeholderResolver;
    }

    @Override
    public DocumentGenerationType getType() {
        return DocumentGenerationType.STANDARD;
    }

    @Override
    public String generate(DocumentTemplate template, Profile profile) {
        return placeholderResolver.resolve(template.getContent(), profile);
    }
}
