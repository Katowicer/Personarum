package it.personarum.domain.document.state;

import it.personarum.domain.document.InvalidProfileDocumentStateException;
import it.personarum.domain.document.ProfileDocumentStatus;

/**
 * Implementa il comportamento di un documento personale nello stato archiviato.
 */
public class ArchivedProfileDocumentState implements ProfileDocumentState {

    /**
     * Rifiuta le modifiche a un documento archiviato.
     *
     * @throws InvalidProfileDocumentStateException sempre, poiché lo stato è di sola lettura
     */
    @Override
    public void ensureEditable() {
        throw new InvalidProfileDocumentStateException("Il documento archiviato non può essere modificato");
    }

    /**
     * Rifiuta una seconda archiviazione.
     *
     * @return non restituisce alcun valore perché l'operazione non è valida
     * @throws InvalidProfileDocumentStateException sempre, poiché il documento è già archiviato
     */
    @Override
    public ProfileDocumentStatus archive() {
        throw new InvalidProfileDocumentStateException("Il documento è già archiviato");
    }

    /**
     * Ripristina un documento archiviato.
     *
     * @return stato {@link ProfileDocumentStatus#ACTIVE}
     */
    @Override
    public ProfileDocumentStatus restore() {
        return ProfileDocumentStatus.ACTIVE;
    }
}
