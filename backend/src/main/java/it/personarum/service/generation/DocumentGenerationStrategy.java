package it.personarum.service.generation;

import it.personarum.domain.generation.DocumentGenerationType;
import it.personarum.domain.profile.Profile;
import it.personarum.domain.template.DocumentTemplate;

/**
 * Contratto dello Strategy Pattern per la produzione del contenuto di un documento.
 *
 * <p>Ogni implementazione dichiara il tipo di generazione supportato e incapsula
 * l'algoritmo con cui il contenuto finale viene costruito.</p>
 */
public interface DocumentGenerationStrategy {

    /**
     * Restituisce il tipo di generazione gestito dalla strategia.
     *
     * @return tipo di generazione supportato
     */
    DocumentGenerationType getType();

    /**
     * Genera il contenuto del documento a partire dal template e dal profilo.
     *
     * @param template template selezionato
     * @param profile  profilo dal quale ricavare i dati
     * @return contenuto testuale generato
     */
    String generate(DocumentTemplate template, Profile profile);
}
