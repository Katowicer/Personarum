package it.personarum.repository;

import it.personarum.domain.document.ProfileDocumentFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileDocumentFileRepository
    extends JpaRepository<ProfileDocumentFile, Long> {
}
