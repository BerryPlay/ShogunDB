package de.shogundb.domain.championship;

public class ChampionshipMemberNotFoundException extends Exception {
    ChampionshipMemberNotFoundException() {
        super();
    }

    public ChampionshipMemberNotFoundException(String message) {
        super(message);
    }

    public ChampionshipMemberNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChampionshipMemberNotFoundException(Throwable cause) {
        super(cause);
    }

    ChampionshipMemberNotFoundException(Long id) {
        super("Can't find championship member association with id: " + id);
    }
}
