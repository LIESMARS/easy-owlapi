package cz.cvut.owl2query.model;

public class InternalReasonerException extends RuntimeException {

    public InternalReasonerException(Throwable cause) {
        super(cause);
    }

    public InternalReasonerException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalReasonerException(String message) {
        super(message);
    }

    public InternalReasonerException() {
    }

    
}
