package de.shogundb.domain.championship;

public class ChampionshipNotFoundException extends Exception {
    public ChampionshipNotFoundException() {
        super();
    }

    public ChampionshipNotFoundException(String message) {
        super(message);
    }

    public ChampionshipNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChampionshipNotFoundException(Throwable cause) {
        super(cause);
    }

    public ChampionshipNotFoundException(Long id) {
        super("Can't find championship with id: " + id);
    }
}
