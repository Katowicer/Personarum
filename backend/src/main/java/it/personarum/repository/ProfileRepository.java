package it.personarum.repository;

import it.personarum.domain.profile.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository JPA per la persistenza dei profili personali.
 */
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    /**
     * Verifica se esiste un profilo con il codice fiscale indicato.
     *
     * @param fiscalCode codice fiscale già normalizzato
     * @return {@code true} se il codice fiscale è già presente
     */
    boolean existsByFiscalCode(String fiscalCode);
}
