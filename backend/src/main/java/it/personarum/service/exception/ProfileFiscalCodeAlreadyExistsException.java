package it.personarum.service.exception;

public class ProfileFiscalCodeAlreadyExistsException extends RuntimeException {

    public ProfileFiscalCodeAlreadyExistsException(String fiscalCode) {
        super("Esiste già un profilo con codice fiscale " + fiscalCode);
    }
}
