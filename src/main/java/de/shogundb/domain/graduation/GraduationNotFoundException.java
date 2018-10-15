package de.shogundb.domain.graduation;

public class GraduationNotFoundException extends Exception {
    public GraduationNotFoundException() {
        super();
    }

    public GraduationNotFoundException(String message) {
        super(message);
    }

    public GraduationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public GraduationNotFoundException(Throwable cause) {
        super(cause);
    }

    public GraduationNotFoundException(Long id) {
        super("Can't find graduation with id: " + id);
    }
}
