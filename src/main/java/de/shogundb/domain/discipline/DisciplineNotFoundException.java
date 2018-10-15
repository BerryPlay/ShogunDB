package de.shogundb.domain.discipline;

public class DisciplineNotFoundException extends Exception {
    public DisciplineNotFoundException() {
        super();
    }

    public DisciplineNotFoundException(String message) {
        super(message);
    }

    public DisciplineNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public DisciplineNotFoundException(Throwable cause) {
        super(cause);
    }

    public DisciplineNotFoundException(Long id) {
        super("Can't find discipline with id: " + id);
    }
}
