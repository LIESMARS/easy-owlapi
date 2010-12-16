package cz.cvut.kbss.owl2query.simpleversion.engine;

import java.util.Iterator;
import java.util.List;

import cz.cvut.kbss.owl2query.simpleversion.model.QueryResult;
import cz.cvut.kbss.owl2query.simpleversion.model.ResultBinding;
import cz.cvut.kbss.owl2query.simpleversion.model.Variable;

class CarthesianProductResult<G> implements QueryResult<G> {

	private final List<Variable<G>> resultVars;
	private final List<QueryResult<G>> queryResults;

	private int size;

	public CarthesianProductResult(final List<Variable<G>> resultVars,
			final List<QueryResult<G>> queryResults) {
		this.resultVars = resultVars;
		this.queryResults = queryResults;

		size = 1;
		for (final QueryResult<G> result : queryResults) {
			size *= result.size();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void add(ResultBinding<G> binding) {
		throw new UnsupportedOperationException(
				"CarthesianProductResult do not support addition!");
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Variable<G>> getResultVars() {
		return resultVars;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isDistinct() {
		for (final QueryResult<G> result : queryResults) {
			if (!result.isDistinct())
				return false;
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public Iterator<ResultBinding<G>> iterator() {
		return new CarthesianProductIterator<G>(queryResults);
	}

	/**
	 * {@inheritDoc}
	 */
	public int size() {
		return size;
	}
}
