package it.personarum.domain.document.state;

import it.personarum.domain.document.InvalidProfileDocumentStateException;
import it.personarum.domain.document.ProfileDocumentStatus;

/**
 * Implementa il comportamento di un documento personale nello stato attivo.
 */
public class ActiveProfileDocumentState implements ProfileDocumentState {

    /**
     * Conferma che un documento attivo può essere modificato.
     */
    @Override
    public void ensureEditable() {
        // Nessuna eccezione: lo stato attivo consente le modifiche.
    }

    /**
     * Archivia il documento attivo.
     *
     * @return stato {@link ProfileDocumentStatus#ARCHIVED}
     */
    @Override
    public ProfileDocumentStatus archive() {
        return ProfileDocumentStatus.ARCHIVED;
    }

    /**
     * Rifiuta il ripristino perché il documento è già attivo.
     *
     * @return non restituisce alcun valore perché l'operazione non è valida
     * @throws InvalidProfileDocumentStateException sempre, poiché il documento è già attivo
     */
    @Override
    public ProfileDocumentStatus restore() {
        throw new InvalidProfileDocumentStateException("Il documento è attivo");
    }
}
