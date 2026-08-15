package it.personarum.service.exception;

/**
 * Segnala la condizione applicativa rappresentata da ProfileFiscalCodeAlreadyExistsException.
 */
public class ProfileFiscalCodeAlreadyExistsException extends RuntimeException {

    public ProfileFiscalCodeAlreadyExistsException(String fiscalCode) {
        super("Esiste già un profilo con codice fiscale " + fiscalCode);
    }
}
