package it.personarum.repository;

import it.personarum.domain.profile.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    boolean existsByFiscalCode(String fiscalCode);
}
