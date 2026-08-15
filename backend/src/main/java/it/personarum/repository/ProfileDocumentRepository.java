package it.personarum.repository;

import it.personarum.domain.document.ProfileDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository JPA per i documenti associati ai profili.
 */
public interface ProfileDocumentRepository extends JpaRepository<ProfileDocument, Long> {

    /**
     * Restituisce i documenti di un profilo ordinati per identificativo crescente.
     *
     * @param profileId identificativo del profilo
     * @return documenti associati al profilo
     */
    List<ProfileDocument> findAllByProfileIdOrderByIdAsc(Long profileId);

    /**
     * Cerca un documento verificando contestualmente l'appartenenza al profilo.
     *
     * @param documentId identificativo del documento
     * @param profileId  identificativo del profilo
     * @return documento trovato, se presente e appartenente al profilo
     */
    Optional<ProfileDocument> findByIdAndProfileId(Long documentId, Long profileId);
}
