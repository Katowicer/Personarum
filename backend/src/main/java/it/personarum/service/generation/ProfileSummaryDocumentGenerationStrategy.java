package it.personarum.service.generation;

import it.personarum.domain.generation.DocumentGenerationType;
import it.personarum.domain.profile.Profile;
import it.personarum.domain.template.DocumentTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProfileSummaryDocumentGenerationStrategy
    implements DocumentGenerationStrategy {

    private final ProfilePlaceholderResolver placeholderResolver;

    public ProfileSummaryDocumentGenerationStrategy(
        ProfilePlaceholderResolver placeholderResolver
    ) {
        this.placeholderResolver = placeholderResolver;
    }

    @Override
    public DocumentGenerationType getType() {
        return DocumentGenerationType.PROFILE_SUMMARY;
    }

    @Override
    public String generate(DocumentTemplate template, Profile profile) {
        String content = template.getContent() + """



            DATI ANAGRAFICI

            Nome: {firstName}
            Cognome: {lastName}
            Data di nascita: {birthDate}
            Luogo di nascita: {birthPlace}
            Codice fiscale: {fiscalCode}
            Email: {email}
            Telefono: {phone}
            """;

        return placeholderResolver.resolve(content, profile);
    }
}
