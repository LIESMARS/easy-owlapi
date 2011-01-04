package cz.cvut.kbss.owl2query.model;

@SuppressWarnings("serial")
public class OWL2QueryException extends RuntimeException {

	public OWL2QueryException(final String message) {
		super(message);
	}

	public OWL2QueryException(final Throwable t) {
		super(t);
	}

	public OWL2QueryException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
