package it.personarum.domain.document.state;

import it.personarum.domain.document.ProfileDocumentStatus;

/**
 * Fornisce le implementazioni condivise dello State Pattern per i documenti personali.
 */
public final class ProfileDocumentStates {

    private static final ProfileDocumentState ACTIVE = new ActiveProfileDocumentState();
    private static final ProfileDocumentState ARCHIVED = new ArchivedProfileDocumentState();

    private ProfileDocumentStates() {
    }

    /**
     * Restituisce l'implementazione corrispondente allo stato persistito.
     *
     * @param status stato persistito del documento
     * @return oggetto stato da utilizzare per applicare le regole comportamentali
     */
    public static ProfileDocumentState from(ProfileDocumentStatus status) {
        return switch (status) {
            case ACTIVE -> ACTIVE;
            case ARCHIVED -> ARCHIVED;
        };
    }
}
