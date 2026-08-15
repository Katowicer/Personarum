package it.personarum.repository;

import it.personarum.domain.template.DocumentTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository JPA per i template documentali.
 */
public interface DocumentTemplateRepository extends JpaRepository<DocumentTemplate, Long> {

    /**
     * Restituisce tutti i template ordinati alfabeticamente per nome.
     *
     * @return elenco completo dei template
     */
    List<DocumentTemplate> findAllByOrderByNameAsc();

    /**
     * Restituisce i soli template abilitati ordinati alfabeticamente per nome.
     *
     * @return elenco dei template disponibili per la generazione
     */
    List<DocumentTemplate> findAllByEnabledTrueOrderByNameAsc();

    /**
     * Verifica se esiste un template con il nome indicato ignorando maiuscole e minuscole.
     *
     * @param name nome da verificare
     * @return {@code true} se il nome è già utilizzato
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Verifica se un nome è utilizzato da un template diverso da quello indicato.
     *
     * @param name nome da verificare
     * @param id   identificativo del template da escludere
     * @return {@code true} se un altro template utilizza lo stesso nome
     */
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
