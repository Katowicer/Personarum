package it.personarum.service.generation;

import it.personarum.domain.generation.DocumentGenerationType;
import it.personarum.domain.profile.Profile;
import it.personarum.domain.template.DocumentTemplate;

public interface DocumentGenerationStrategy {

    DocumentGenerationType getType();

    String generate(DocumentTemplate template, Profile profile);
}
