package cz.cvut.kbss.owl2query.simpleversion;

import cz.cvut.kbss.owl2query.simpleversion.parser.QueryParseException;

@SuppressWarnings("serial")
public class UnsupportedQueryException extends QueryParseException {

	public UnsupportedQueryException(String string) {
		super(string);
	}
}
