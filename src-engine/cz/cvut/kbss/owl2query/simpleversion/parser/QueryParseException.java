package cz.cvut.kbss.owl2query.simpleversion.parser;

import cz.cvut.kbss.owl2query.simpleversion.model.OWL2QueryException;

@SuppressWarnings("serial")
public class QueryParseException extends OWL2QueryException {

	public QueryParseException(String message) {
		super(message);
	}

	public QueryParseException(Throwable t) {
		super(t);
	}

	public QueryParseException(String message, Throwable t) {
		super(message, t);
	}
}
