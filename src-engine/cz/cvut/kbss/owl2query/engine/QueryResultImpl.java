// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This sourG code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package cz.cvut.kbss.owl2query.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cz.cvut.kbss.owl2query.model.QueryResult;
import cz.cvut.kbss.owl2query.model.ResultBinding;
import cz.cvut.kbss.owl2query.model.Variable;

/**
 * <p>
 * Title: Default implementation of {@link QueryResult}
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Petr Kremen
 */
class QueryResultImpl<G> implements QueryResult<G> {

	private Collection<ResultBinding<G>> bindings;

	private List<Variable<G>> resultVars;

	private InternalQuery<G> query;

	public QueryResultImpl(final InternalQuery<G> query) {
		this.query = query;
		this.resultVars = new ArrayList<Variable<G>>(query
				.getResultVars());

		if (query.isDistinct())
			bindings = new HashSet<ResultBinding<G>>();
		else
			bindings = new ArrayList<ResultBinding<G>>();
	}

	/**
	 * {@inheritDoc}
	 */
	public void add(ResultBinding<G> binding) {
		bindings.add(binding);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final QueryResultImpl<G> other = (QueryResultImpl<G>) obj;
		if (bindings == null) {
			if (other.bindings != null)
				return false;
		} else if (!bindings.equals(other.bindings))
			return false;
		if (resultVars == null) {
			if (other.resultVars != null)
				return false;
		} else if (!resultVars.equals(other.resultVars))
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Variable<G>> getResultVars() {
		return Collections.unmodifiableList(resultVars);
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result
				+ ((bindings == null) ? 0 : bindings.hashCode());
		result = PRIME * result
				+ ((resultVars == null) ? 0 : resultVars.hashCode());
		return result;
	}

	public boolean isDistinct() {
		return (bindings instanceof Set);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public Iterator<ResultBinding<G>> iterator() {
		return bindings.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	public int size() {
		return bindings.size();
	}

	@Override
	public String toString() {
		return bindings.toString();
	}
}
