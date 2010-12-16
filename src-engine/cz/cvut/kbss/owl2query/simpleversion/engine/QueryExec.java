package cz.cvut.kbss.owl2query.simpleversion.engine;

import cz.cvut.kbss.owl2query.simpleversion.UnsupportedQueryException;
import cz.cvut.kbss.owl2query.simpleversion.model.QueryResult;

interface QueryExec<G> {

	public QueryResult<G> exec(InternalQuery<G> query)
			throws UnsupportedQueryException;

}
