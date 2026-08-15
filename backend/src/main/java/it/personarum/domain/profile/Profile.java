package it.personarum.domain.profile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.Locale;

/**
 * Rappresenta il profilo anagrafico gestito da Personarum.
 *
 * <p>L'entità mantiene al proprio interno le regole di normalizzazione e i principali
 * vincoli coerenti con lo schema persistente.</p>
 */
@Entity
@Table(name = "profiles")
public class Profile {

    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_BIRTH_PLACE_LENGTH = 120;
    private static final int MAX_FISCAL_CODE_LENGTH = 16;
    private static final int MAX_EMAIL_LENGTH = 254;
    private static final int MAX_PHONE_LENGTH = 40;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = MAX_NAME_LENGTH)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = MAX_NAME_LENGTH)
    private String lastName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "birth_place", length = MAX_BIRTH_PLACE_LENGTH)
    private String birthPlace;

    @Column(name = "fiscal_code", unique = true, length = MAX_FISCAL_CODE_LENGTH)
    private String fiscalCode;

    @Column(length = MAX_EMAIL_LENGTH)
    private String email;

    @Column(length = MAX_PHONE_LENGTH)
    private String phone;
    
    protected Profile() {
    }

    private Profile(String firstName, String lastName, LocalDate birthDate, String birthPlace, String fiscalCode, String email, String phone) {
        changePersonalData(firstName, lastName, birthDate, birthPlace, fiscalCode);
        changeContacts(email, phone);
    }

    /**
     * Crea un nuovo profilo applicando le regole di validazione e normalizzazione del dominio.
     *
     * @param firstName  nome della persona
     * @param lastName   cognome della persona
     * @param birthDate  data di nascita, opzionale e non futura
     * @param birthPlace luogo di nascita opzionale
     * @param fiscalCode codice fiscale opzionale
     * @param email      indirizzo email opzionale
     * @param phone      recapito telefonico opzionale
     * @return nuovo profilo non ancora persistito
     * @throws IllegalArgumentException se i dati non rispettano i vincoli del dominio
     */
    public static Profile create(String firstName, String lastName, LocalDate birthDate, String birthPlace, String fiscalCode, String email, String phone) {
        return new Profile(firstName, lastName, birthDate, birthPlace, fiscalCode, email, phone);
    }

    /**
     * Aggiorna i dati anagrafici del profilo.
     *
     * @param firstName  nome della persona
     * @param lastName   cognome della persona
     * @param birthDate  data di nascita, opzionale e non futura
     * @param birthPlace luogo di nascita opzionale
     * @param fiscalCode codice fiscale opzionale
     * @throws IllegalArgumentException se i dati non rispettano i vincoli del dominio
     */
    public void changePersonalData(String firstName, String lastName, LocalDate birthDate, String birthPlace, String fiscalCode) {
        this.firstName = normalizeRequired(firstName, "Nome obbligatorio", MAX_NAME_LENGTH, "Il nome non può superare 100 caratteri");
        this.lastName = normalizeRequired(lastName, "Cognome obbligatorio", MAX_NAME_LENGTH, "Il cognome non può superare 100 caratteri");

        if (birthDate != null && birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La data di nascita non può essere futura");
        }

        this.birthDate = birthDate;
        this.birthPlace = normalizeOptional(birthPlace, MAX_BIRTH_PLACE_LENGTH, "Il luogo di nascita non può superare 120 caratteri");
        this.fiscalCode = normalizeFiscalCode(fiscalCode);
    }

    /**
     * Aggiorna i recapiti del profilo normalizzando i valori opzionali.
     *
     * @param email indirizzo email opzionale
     * @param phone recapito telefonico opzionale
     * @throws IllegalArgumentException se i valori superano le dimensioni persistibili
     */
    public void changeContacts(String email, String phone) {
        this.email = normalizeEmail(email);
        this.phone = normalizeOptional(phone, MAX_PHONE_LENGTH, "Il telefono non può superare 40 caratteri");
    }

    /**
     * Normalizza un codice fiscale secondo la stessa regola utilizzata dall'entità.
     *
     * @param fiscalCode codice fiscale da normalizzare; può essere nullo o vuoto
     * @return codice fiscale senza spazi esterni e in maiuscolo, oppure {@code null} se assente
     * @throws IllegalArgumentException se il codice fiscale supera 16 caratteri
     */
    public static String normalizeFiscalCode(String fiscalCode) {
        String normalized = normalizeOptional(fiscalCode, MAX_FISCAL_CODE_LENGTH, "Il codice fiscale non può superare 16 caratteri");
        return normalized == null ? null : normalized.toUpperCase(Locale.ROOT);
    }

    private static String normalizeRequired(String value, String requiredMessage, int maxLength, String lengthMessage) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(requiredMessage);
        }

        String normalized = value.trim();
        if (normalized.length() > maxLength) {
            throw new IllegalArgumentException(lengthMessage);
        }
        return normalized;
    }

    private static String normalizeOptional(String value, int maxLength, String lengthMessage) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.trim();
        if (normalized.length() > maxLength) {
            throw new IllegalArgumentException(lengthMessage);
        }
        return normalized;
    }

    private static String normalizeEmail(String email) {
        String normalized = normalizeOptional(email, MAX_EMAIL_LENGTH, "L'email non può superare 254 caratteri");
        return normalized == null ? null : normalized.toLowerCase(Locale.ROOT);
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public String getFiscalCode() {
        return fiscalCode;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}
