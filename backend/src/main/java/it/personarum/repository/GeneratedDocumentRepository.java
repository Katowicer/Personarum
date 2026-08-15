package it.personarum.repository;

import it.personarum.domain.generation.GeneratedDocument;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository JPA per i documenti generati.
 */
public interface GeneratedDocumentRepository extends JpaRepository<GeneratedDocument, Long> {

    /**
     * Restituisce i documenti generati per un profilo, dal più recente al meno recente.
     *
     * @param profileId identificativo del profilo
     * @return documenti generati con profilo e template già caricati
     */
    @EntityGraph(attributePaths = {"profile", "template"})
    List<GeneratedDocument> findAllByProfileIdOrderByCreatedAtDesc(Long profileId);

    /**
     * Cerca un documento generato verificandone l'appartenenza al profilo.
     *
     * @param id        identificativo del documento generato
     * @param profileId identificativo del profilo
     * @return documento trovato, se presente e appartenente al profilo
     */
    @EntityGraph(attributePaths = {"profile", "template"})
    Optional<GeneratedDocument> findByIdAndProfileId(Long id, Long profileId);
}
