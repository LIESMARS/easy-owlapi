package cz.cvut.kbss.owl2query.engine;

import cz.cvut.kbss.owl2query.UnsupportedQueryException;
import cz.cvut.kbss.owl2query.model.QueryResult;

interface QueryExec<G> {

	public QueryResult<G> exec(InternalQuery<G> query)
			throws UnsupportedQueryException;

}
