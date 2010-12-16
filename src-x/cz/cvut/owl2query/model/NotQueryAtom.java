// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package cz.cvut.owl2query.model;

import cz.cvut.owl2query.model.Term;
import java.util.List;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */
public class NotQueryAtom implements QueryAtom {
	private QueryAtom	atom;

	public NotQueryAtom(QueryAtom atom) {
		this.atom = atom;
	}

	public QueryAtom apply(final ResultBinding binding) {
		return new NotQueryAtom( atom.apply( binding ) );
	}

	@Override
	public boolean equals(Object obj) {
		if( !(obj instanceof NotQueryAtom) )
			return false;
		return atom.equals( ((NotQueryAtom) obj).atom );
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Term> getArguments() {
		return atom.getArguments();
	}

	public QueryAtom getAtom() {
		return atom;
	}

	/**
	 * {@inheritDoc}
	 */
	public QueryPredicate getPredicate() {
		return QueryPredicate.Not;
	}

	@Override
	public int hashCode() {
		return 17 * atom.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isGround() {
		return atom.isGround();
	}

	@Override
	public String toString() {
		return "Not(" + atom + ")";
	}
}
