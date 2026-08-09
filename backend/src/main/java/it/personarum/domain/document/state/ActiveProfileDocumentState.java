package it.personarum.domain.document.state;

import it.personarum.domain.document.InvalidProfileDocumentStateException;
import it.personarum.domain.document.ProfileDocumentStatus;

public class ActiveProfileDocumentState
    implements ProfileDocumentState {

    @Override
    public void ensureEditable() throws InvalidProfileDocumentStateException {
       // Non fa niente: un documento in stato ATTIVO può essere modificato
    }

    @Override
    public ProfileDocumentStatus archive() throws InvalidProfileDocumentStateException {
        return ProfileDocumentStatus.ARCHIVED;
    }

    @Override
    public ProfileDocumentStatus restore() throws InvalidProfileDocumentStateException {
        throw new InvalidProfileDocumentStateException("Il documento è attivo");
    }
}
