package it.personarum.domain.profile;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Locale;

@Entity
@Table(name = "profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "birth_place", length = 120)
    private String birthPlace;

    @Column(name = "fiscal_code", unique = true, length = 16)
    private String fiscalCode;

    @Column(length = 254)
    private String email;

    @Column(length = 40)
    private String phone;

    protected Profile() {
    }

    private Profile(String firstName, String lastName, LocalDate birthDate, String birthPlace, String fiscalCode, String email, String phone) {
        changePersonalData(firstName, lastName, birthDate, birthPlace, fiscalCode);
        changeContacts(email, phone);
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public String getFiscalCode() {
        return fiscalCode;
    }

    public void setFiscalCode(String fiscalCode) {
        this.fiscalCode = fiscalCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void changePersonalData(String firstName, String lastName, LocalDate birthDate, String birthPlace, String fiscalCode) throws IllegalArgumentException {
        this.firstName = normalizeRequired(firstName, "Nome obbligatorio");
        this.lastName = normalizeRequired(lastName, "Cognome obbligatorio");

        if (birthDate != null && birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La data di nascita non può essere futura");
        }

        this.birthDate = birthDate;
        this.birthPlace = normalizeOptional(birthPlace);
        this.fiscalCode = normalizeFiscalCode(fiscalCode);
    }

    public void changeContacts(String email, String phone) {
        this.email = normalizeEmail(email);
        this.phone = normalizeOptional(phone);
    }

    private static String normalizeRequired(String value, String errorMessage) throws IllegalArgumentException {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(errorMessage);
        }

        return value.trim();
    }

    private static String normalizeOptional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private static String normalizeFiscalCode(String fiscalCode) {
        String normalized = normalizeOptional(fiscalCode);

        if (normalized == null) {
            return null;
        }

        return normalized.toUpperCase(Locale.ROOT);
    }

    private static String normalizeEmail(String email) {
        String normalized = normalizeOptional(email);

        if (normalized == null) {
            return null;
        }

        return normalized.toLowerCase(Locale.ROOT);
    }


    public static Profile create(String firstName, String lastName, LocalDate birthDate, String birthPlace, String fiscalCode, String email, String phone) {
        return new Profile(firstName, lastName, birthDate, birthPlace, fiscalCode, email, phone);
    }
}
