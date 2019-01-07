package de.shogundb.domain.graduation;

public class GraduationMemberNotFoundException extends Exception {
    public GraduationMemberNotFoundException() {
        super();
    }

    public GraduationMemberNotFoundException(String message) {
        super(message);
    }

    public GraduationMemberNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public GraduationMemberNotFoundException(Throwable cause) {
        super(cause);
    }

    public GraduationMemberNotFoundException(Long id) {
        super("Can't find graduation member link with id: " + id);
    }
}
