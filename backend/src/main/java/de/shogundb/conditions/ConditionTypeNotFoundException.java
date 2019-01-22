package de.shogundb.conditions;

public class ConditionTypeNotFoundException extends Exception {
    public ConditionTypeNotFoundException() {
        super();
    }

    public ConditionTypeNotFoundException(String message) {
        super(message);
    }

    public ConditionTypeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConditionTypeNotFoundException(Throwable cause) {
        super(cause);
    }
}
