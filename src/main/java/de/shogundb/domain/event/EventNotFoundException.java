package de.shogundb.domain.event;

public class EventNotFoundException extends Exception {
    public EventNotFoundException() {
        super();
    }

    public EventNotFoundException(String message) {
        super(message);
    }

    public EventNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EventNotFoundException(Throwable cause) {
        super(cause);
    }

    public EventNotFoundException(Long id) {
        super("Can't find event with id: " + id);
    }
}
