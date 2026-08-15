package it.personarum.domain.document.state;

import it.personarum.domain.document.InvalidProfileDocumentStateException;
import it.personarum.domain.document.ProfileDocumentStatus;

/**
 * Contratto dello State Pattern che definisce il comportamento di un documento personale
 * in funzione del proprio stato persistito.
 */
public interface ProfileDocumentState {

    /**
     * Verifica che il documento possa essere modificato nello stato corrente.
     *
     * @throws InvalidProfileDocumentStateException se lo stato non consente modifiche
     */
    void ensureEditable() throws InvalidProfileDocumentStateException;

    /**
     * Calcola lo stato risultante da una richiesta di archiviazione.
     *
     * @return nuovo stato del documento
     * @throws InvalidProfileDocumentStateException se il documento non può essere archiviato
     */
    ProfileDocumentStatus archive() throws InvalidProfileDocumentStateException;

    /**
     * Calcola lo stato risultante da una richiesta di ripristino.
     *
     * @return nuovo stato del documento
     * @throws InvalidProfileDocumentStateException se il documento non può essere ripristinato
     */
    ProfileDocumentStatus restore() throws InvalidProfileDocumentStateException;
}
