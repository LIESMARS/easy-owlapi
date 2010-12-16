package cz.cvut.kbss.owl2query.simpleversion.parser;

import java.io.InputStream;
import java.util.logging.Logger;

import cz.cvut.kbss.owl2query.simpleversion.model.OWL2Ontology;
import cz.cvut.kbss.owl2query.simpleversion.model.OWL2Query;

public interface QueryParser<G> {
	public static Logger log = Logger.getLogger(QueryParser.class.getName());

	public OWL2Query<G> parse(final String queryString, OWL2Ontology<G> o)
			throws QueryParseException;

	public OWL2Query<G> parse(final InputStream stream, OWL2Ontology<G> o)
			throws QueryParseException;
}
