package it.personarum.service.generation;

import it.personarum.domain.generation.DocumentGenerationType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class DocumentGenerationStrategyResolver {

    private final Map<DocumentGenerationType, DocumentGenerationStrategy> strategies;

    public DocumentGenerationStrategyResolver(
        List<DocumentGenerationStrategy> strategies
    ) {
        this.strategies = new EnumMap<>(DocumentGenerationType.class);

        for (DocumentGenerationStrategy strategy : strategies) {
            this.strategies.put(strategy.getType(), strategy);
        }
    }

    public DocumentGenerationStrategy resolve(DocumentGenerationType type) {
        DocumentGenerationStrategy strategy = strategies.get(type);

        if (strategy == null) {
            throw new IllegalArgumentException(
                "Strategia di generazione non supportata: " + type
            );
        }

        return strategy;
    }
}
