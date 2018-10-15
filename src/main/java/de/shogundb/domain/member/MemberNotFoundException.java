package de.shogundb.domain.member;

public class MemberNotFoundException extends Exception {
    public MemberNotFoundException() {
        super();
    }

    public MemberNotFoundException(String message) {
        super(message);
    }

    public MemberNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MemberNotFoundException(Throwable cause) {
        super(cause);
    }

    public MemberNotFoundException(Long id) {
        super("Can't find member with id: " + id);
    }
}
