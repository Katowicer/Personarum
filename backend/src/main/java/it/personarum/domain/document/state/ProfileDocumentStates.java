package it.personarum.domain.document.state;

import it.personarum.domain.document.ProfileDocumentStatus;

public final class ProfileDocumentStates {

    private static final ProfileDocumentState ACTIVE = new ActiveProfileDocumentState();

    private static final ProfileDocumentState ARCHIVED = new ArchivedProfileDocumentState();

    private ProfileDocumentStates() {
    }

    public static ProfileDocumentState from(ProfileDocumentStatus status) {
        return switch (status) {
            case ACTIVE -> ACTIVE;
            case ARCHIVED -> ARCHIVED;
        };
    }
}
