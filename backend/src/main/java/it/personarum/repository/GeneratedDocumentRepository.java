package it.personarum.repository;

import it.personarum.domain.generation.GeneratedDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GeneratedDocumentRepository extends JpaRepository<GeneratedDocument, Long> {

    List<GeneratedDocument> findAllByProfileIdOrderByCreatedAtDesc(Long profileId);

    Optional<GeneratedDocument> findByIdAndProfileId(Long id, Long profileId);
}
