package de.shogundb.domain.exam;

public class ExamNotFoundException extends Exception {
    public ExamNotFoundException() {
        super();
    }

    public ExamNotFoundException(String message) {
        super(message);
    }

    public ExamNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExamNotFoundException(Throwable cause) {
        super(cause);
    }

    public ExamNotFoundException(Long id) {
        super("Can't find exam with id: " + id);
    }
}
