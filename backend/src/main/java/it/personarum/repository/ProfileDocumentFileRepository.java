package it.personarum.repository;

import it.personarum.domain.document.ProfileDocumentFile;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository JPA per i contenuti binari allegati ai documenti dei profili.
 */
public interface ProfileDocumentFileRepository extends JpaRepository<ProfileDocumentFile, Long> {
}
