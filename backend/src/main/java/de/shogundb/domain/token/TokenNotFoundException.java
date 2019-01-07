package de.shogundb.domain.token;

public class TokenNotFoundException extends Exception {
    public TokenNotFoundException() {
        super();
    }

    public TokenNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenNotFoundException(Throwable cause) {
        super(cause);
    }

    public TokenNotFoundException(String token) {
        super("Can't find token : " + token);
    }
}
