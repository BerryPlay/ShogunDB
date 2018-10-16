package de.shogundb.domain.seminar;

public class SeminarNotFoundException extends Exception {
    public SeminarNotFoundException() {
        super();
    }

    public SeminarNotFoundException(String message) {
        super(message);
    }

    public SeminarNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public SeminarNotFoundException(Throwable cause) {
        super(cause);
    }

    public SeminarNotFoundException(Long id) {
        super("Can't find seminar with id: " + id);
    }
}
