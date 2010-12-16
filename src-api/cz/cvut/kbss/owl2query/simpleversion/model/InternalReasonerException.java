package cz.cvut.kbss.owl2query.simpleversion.model;

@SuppressWarnings("serial")
public class InternalReasonerException extends RuntimeException {

	public InternalReasonerException() {
	}

	public InternalReasonerException(final Exception e) {
		super(e);
	}

	public InternalReasonerException(String string) {
		super(string);
	}

}
