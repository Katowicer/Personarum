package it.personarum.service.generation;

import it.personarum.domain.generation.DocumentGenerationType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Seleziona la strategia di generazione compatibile con il tipo richiesto.
 *
 * <p>Le implementazioni disponibili vengono fornite da Spring e indicizzate una sola
 * volta alla costruzione del resolver.</p>
 */
@Component
public class DocumentGenerationStrategyResolver {

    private final Map<DocumentGenerationType, DocumentGenerationStrategy> strategies;

    /**
     * Costruisce il resolver indicizzando le strategie per tipo.
     *
     * @param strategies strategie di generazione registrate nel contesto Spring
     */
    public DocumentGenerationStrategyResolver(List<DocumentGenerationStrategy> strategies) {
        this.strategies = new EnumMap<>(DocumentGenerationType.class);

        for (DocumentGenerationStrategy strategy : strategies) {
            this.strategies.put(strategy.getType(), strategy);
        }
    }

    /**
     * Individua la strategia compatibile con il tipo di generazione richiesto.
     *
     * @param type tipo di generazione richiesto
     * @return strategia associata al tipo
     * @throws IllegalArgumentException se non è registrata alcuna strategia per il tipo richiesto
     */
    public DocumentGenerationStrategy resolve(DocumentGenerationType type) {
        DocumentGenerationStrategy strategy = strategies.get(type);

        if (strategy == null) {
            throw new IllegalArgumentException("Strategia di generazione non supportata: " + type);
        }

        return strategy;
    }
}
