package it.personarum.domain.document.state;

import it.personarum.domain.document.InvalidProfileDocumentStateException;
import it.personarum.domain.document.ProfileDocumentStatus;

public class ArchivedProfileDocumentState
    implements ProfileDocumentState {

    @Override
    public void ensureEditable() throws InvalidProfileDocumentStateException {
        throw new InvalidProfileDocumentStateException("Il documento archiviato non può essere modificato");
    }

    @Override
    public ProfileDocumentStatus archive() throws InvalidProfileDocumentStateException{
        throw new InvalidProfileDocumentStateException("Il documento è già archiviato");
    }

    @Override
    public ProfileDocumentStatus restore() throws InvalidProfileDocumentStateException {
        return ProfileDocumentStatus.ACTIVE;
    }
}
