package cz.cvut.kbss.owl2query;

import cz.cvut.kbss.owl2query.parser.QueryParseException;

@SuppressWarnings("serial")
public class UnsupportedQueryException extends QueryParseException {

	public UnsupportedQueryException(String string) {
		super(string);
	}
}
