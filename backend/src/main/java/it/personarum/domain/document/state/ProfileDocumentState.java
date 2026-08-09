package it.personarum.domain.document.state;

import it.personarum.domain.document.InvalidProfileDocumentStateException;
import it.personarum.domain.document.ProfileDocumentStatus;

public interface ProfileDocumentState {

    void ensureEditable() throws InvalidProfileDocumentStateException;

    ProfileDocumentStatus archive() throws InvalidProfileDocumentStateException;

    ProfileDocumentStatus restore() throws InvalidProfileDocumentStateException;
}
