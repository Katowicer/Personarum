package it.personarum.service;

import it.personarum.domain.profile.Profile;
import it.personarum.repository.ProfileRepository;
import it.personarum.service.exception.ProfileFiscalCodeAlreadyExistsException;
import it.personarum.service.exception.ProfileNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Coordina i casi d'uso relativi alla gestione dei profili personali.
 *
 * <p>Il servizio applica i controlli che richiedono accesso alla persistenza, come
 * l'unicità del codice fiscale, delegando al dominio la validazione dei dati.</p>
 */
@Service
@Transactional(readOnly = true)
public class ProfileService {

    private final ProfileRepository profileRepository;

    /**
     * Crea il servizio dei profili.
     *
     * @param profileRepository repository utilizzato per la persistenza
     */
    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    /**
     * Persiste un profilo già costruito dopo aver verificato l'unicità del codice fiscale.
     *
     * @param profile profilo da salvare
     * @return profilo persistito
     * @throws NullPointerException                    se il profilo è nullo
     * @throws ProfileFiscalCodeAlreadyExistsException se il codice fiscale è già utilizzato
     */
    @Transactional
    public Profile create(Profile profile) {
        Objects.requireNonNull(profile, "Profilo obbligatorio");
        ensureFiscalCodeAvailable(profile.getFiscalCode());
        return profileRepository.save(profile);
    }

    /**
     * Crea e persiste un nuovo profilo.
     *
     * @param firstName  nome della persona
     * @param lastName   cognome della persona
     * @param birthDate  data di nascita opzionale
     * @param birthPlace luogo di nascita opzionale
     * @param fiscalCode codice fiscale opzionale
     * @param email      indirizzo email opzionale
     * @param phone      recapito telefonico opzionale
     * @return profilo creato e persistito
     * @throws IllegalArgumentException                se i dati violano le regole del dominio
     * @throws ProfileFiscalCodeAlreadyExistsException se il codice fiscale è già utilizzato
     */
    @Transactional
    public Profile create(String firstName, String lastName, LocalDate birthDate, String birthPlace, String fiscalCode, String email, String phone) {
        Profile profile = Profile.create(firstName, lastName, birthDate, birthPlace, fiscalCode, email, phone);
        ensureFiscalCodeAvailable(profile.getFiscalCode());
        return profileRepository.save(profile);
    }

    /**
     * Restituisce tutti i profili presenti.
     *
     * @return elenco dei profili
     */
    public List<Profile> findAll() {
        return profileRepository.findAll();
    }

    /**
     * Cerca un profilo tramite identificativo.
     *
     * @param id identificativo del profilo
     * @return profilo trovato
     * @throws ProfileNotFoundException se il profilo non esiste
     */
    public Profile findById(Long id) {
        return profileRepository.findById(id).orElseThrow(() -> new ProfileNotFoundException(id));
    }

    /**
     * Aggiorna i dati di un profilo esistente.
     *
     * <p>La disponibilità del nuovo codice fiscale viene verificata prima di modificare
     * l'entità, evitando di lasciare l'istanza gestita in uno stato parzialmente aggiornato
     * quando il controllo fallisce.</p>
     *
     * @param id         identificativo del profilo
     * @param firstName  nome aggiornato
     * @param lastName   cognome aggiornato
     * @param birthDate  data di nascita aggiornata
     * @param birthPlace luogo di nascita aggiornato
     * @param fiscalCode codice fiscale aggiornato
     * @param email      indirizzo email aggiornato
     * @param phone      recapito telefonico aggiornato
     * @return profilo aggiornato
     * @throws ProfileNotFoundException                se il profilo non esiste
     * @throws ProfileFiscalCodeAlreadyExistsException se il nuovo codice fiscale è già utilizzato
     * @throws IllegalArgumentException                se i dati violano le regole del dominio
     */
    @Transactional
    public Profile update(Long id, String firstName, String lastName, LocalDate birthDate, String birthPlace, String fiscalCode, String email, String phone) {
        Profile profile = findById(id);
        String normalizedFiscalCode = Profile.normalizeFiscalCode(fiscalCode);

        if (!Objects.equals(profile.getFiscalCode(), normalizedFiscalCode)) {
            ensureFiscalCodeAvailable(normalizedFiscalCode);
        }

        profile.changePersonalData(firstName, lastName, birthDate, birthPlace, normalizedFiscalCode);
        profile.changeContacts(email, phone);
        return profile;
    }

    /**
     * Elimina il profilo identificato.
     *
     * @param id identificativo del profilo da eliminare
     * @throws ProfileNotFoundException se il profilo non esiste
     */
    @Transactional
    public void delete(Long id) {
        profileRepository.delete(findById(id));
    }

    private void ensureFiscalCodeAvailable(String fiscalCode) {
        if (fiscalCode != null && profileRepository.existsByFiscalCode(fiscalCode)) {
            throw new ProfileFiscalCodeAlreadyExistsException(fiscalCode);
        }
    }
}
