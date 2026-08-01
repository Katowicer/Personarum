package it.personarum.repository;

import it.personarum.domain.document.ProfileDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfileDocumentRepository extends JpaRepository<ProfileDocument, Long> {

    List<ProfileDocument> findAllByProfileIdOrderByIdAsc(Long profileId);

    Optional<ProfileDocument> findByIdAndProfileId(Long documentId, Long profileId);
}
