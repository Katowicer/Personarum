package it.personarum.service.generation;

import it.personarum.domain.generation.DocumentGenerationType;
import it.personarum.domain.profile.Profile;
import it.personarum.domain.template.DocumentTemplate;
import org.springframework.stereotype.Component;

/**
 * Implementa la generazione standard sostituendo i placeholder presenti nel template.
 */
@Component
public class StandardDocumentGenerationStrategy implements DocumentGenerationStrategy {

    private final ProfilePlaceholderResolver placeholderResolver;

    /**
     * Crea la strategia standard.
     *
     * @param placeholderResolver componente che risolve i placeholder del profilo
     */
    public StandardDocumentGenerationStrategy(ProfilePlaceholderResolver placeholderResolver) {
        this.placeholderResolver = placeholderResolver;
    }

    @Override
    public DocumentGenerationType getType() {
        return DocumentGenerationType.STANDARD;
    }

    /**
     * Genera il contenuto sostituendo i placeholder presenti nel template.
     *
     * @param template template selezionato
     * @param profile  profilo dal quale ricavare i valori
     * @return contenuto del template con i placeholder risolti
     */
    @Override
    public String generate(DocumentTemplate template, Profile profile) {
        return placeholderResolver.resolve(template.getContent(), profile);
    }
}
