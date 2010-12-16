package cz.cvut.owl2query.model;

public class QueryEvaluationException extends RuntimeException {

    public QueryEvaluationException(Throwable cause) {
        super(cause);
    }

    public QueryEvaluationException(String message, Throwable cause) {
        super(message, cause);
    }

    public QueryEvaluationException(String message) {
        super(message);
    }

    public QueryEvaluationException() {
    }
}
