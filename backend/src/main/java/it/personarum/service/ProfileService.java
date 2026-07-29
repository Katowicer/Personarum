package it.personarum.service;

import it.personarum.domain.profile.Profile;
import it.personarum.repository.ProfileRepository;
import it.personarum.service.exception.ProfileNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import it.personarum.service.exception.ProfileFiscalCodeAlreadyExistsException;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Transactional
    public Profile create(Profile profile) throws ProfileFiscalCodeAlreadyExistsException {
        Objects.requireNonNull(profile, "Profilo obbligatorio");
        ensureFiscalCodeAvailable(profile.getFiscalCode());

        return profileRepository.save(profile);
    }

    @Transactional
    public Profile create(String firstName, String lastName, LocalDate birthDate, String birthPlace, String fiscalCode, String email,  String phone) throws ProfileFiscalCodeAlreadyExistsException {
        Profile profile = Profile.create(firstName, lastName, birthDate, birthPlace, fiscalCode, email, phone);

        ensureFiscalCodeAvailable(profile.getFiscalCode());
        return profileRepository.save(profile);
    }

    public List<Profile> findAll() {
        return profileRepository.findAll();
    }

    public Profile findById(Long id) throws ProfileNotFoundException {
        return profileRepository
            .findById(id)
            .orElseThrow(() -> new ProfileNotFoundException(id));
    }

    @Transactional
    public Profile update(Long id, String firstName, String lastName, LocalDate birthDate, String birthPlace, String fiscalCode, String email, String phone) throws ProfileFiscalCodeAlreadyExistsException {
        Profile profile = findById(id);

        String previousFiscalCode = profile.getFiscalCode();

        profile.changePersonalData(firstName, lastName, birthDate, birthPlace, fiscalCode);
        profile.changeContacts(email, phone);

        if (!Objects.equals(previousFiscalCode, fiscalCode)) {
            ensureFiscalCodeAvailable(fiscalCode);
        }

        return profile;
    }

    @Transactional
    public void delete(Long id) {
        Profile profile = findById(id);

        profileRepository.delete(profile);
    }

    private void ensureFiscalCodeAvailable(String fiscalCode) throws ProfileFiscalCodeAlreadyExistsException {
        if (fiscalCode != null && profileRepository.existsByFiscalCode(fiscalCode)) {
            throw new ProfileFiscalCodeAlreadyExistsException(fiscalCode);
        }
    }
}
