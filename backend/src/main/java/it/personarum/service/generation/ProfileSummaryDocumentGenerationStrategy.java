package it.personarum.service.generation;

import it.personarum.domain.generation.DocumentGenerationType;
import it.personarum.domain.profile.Profile;
import it.personarum.domain.template.DocumentTemplate;
import org.springframework.stereotype.Component;

/**
 * Genera un documento estendendo il template con un riepilogo dei dati anagrafici del profilo.
 */
@Component
public class ProfileSummaryDocumentGenerationStrategy implements DocumentGenerationStrategy {

    private final ProfilePlaceholderResolver placeholderResolver;

    /**
     * Crea la strategia di riepilogo del profilo.
     *
     * @param placeholderResolver componente che risolve i placeholder del profilo
     */
    public ProfileSummaryDocumentGenerationStrategy(ProfilePlaceholderResolver placeholderResolver) {
        this.placeholderResolver = placeholderResolver;
    }

    @Override
    public DocumentGenerationType getType() {
        return DocumentGenerationType.PROFILE_SUMMARY;
    }

    /**
     * Genera il contenuto aggiungendo al template una sezione con i principali dati del profilo.
     *
     * @param template template selezionato
     * @param profile  profilo dal quale ricavare i valori
     * @return contenuto completo con i placeholder risolti
     */
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
