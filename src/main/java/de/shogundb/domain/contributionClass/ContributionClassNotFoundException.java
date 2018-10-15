package de.shogundb.domain.contributionClass;

public class ContributionClassNotFoundException extends Exception {
        public ContributionClassNotFoundException() {
            super();
        }

        public ContributionClassNotFoundException(String message) {
            super(message);
        }

        public ContributionClassNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }

        public ContributionClassNotFoundException(Throwable cause) {
            super(cause);
        }

        public ContributionClassNotFoundException(Long id) {
            super("Can't find contribution class with id: " + id);
        }
}
