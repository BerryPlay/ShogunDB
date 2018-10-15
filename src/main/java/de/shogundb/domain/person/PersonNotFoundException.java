package de.shogundb.domain.person;

public class PersonNotFoundException extends Exception {
    PersonNotFoundException() {
        super();
    }

    public PersonNotFoundException(String message) {
        super(message);
    }

    public PersonNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersonNotFoundException(Throwable cause) {
        super(cause);
    }

    public PersonNotFoundException(Long id) {
        super("Can't find person with id: " + id);
    }
}
