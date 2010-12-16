// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package cz.cvut.kbss.owl2query.complexversion.modelold;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.cvut.kbss.owl2query.complexversion.model.Term;
import cz.cvut.kbss.owl2query.complexversion.util.ATermUtils;

/**
 * <p>
 * Title: Abstract implementation of the query atom.
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
public class AbstractQueryAtom implements QueryAtom {

	protected final QueryPredicate predicate;

	protected final List<Term> arguments;

	protected boolean ground;

	public AbstractQueryAtom(final QueryPredicate predicate,
			final Term... arguments) {
		this(predicate, Arrays.asList(arguments));
	}

	public AbstractQueryAtom(final QueryPredicate predicate,
			final List<Term> arguments) {
		if (predicate == null) {
			throw new RuntimeException("Predicate cannot be null.");
		}

		this.predicate = predicate;
		this.arguments = arguments;
		// this.vars = new HashSet<Term>();
		//
		ground = true;
		for (final Term a : arguments) {
			if (ATermUtils.isVar(a)) {
				ground = false;
				// vars.add(a);
				break;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public QueryPredicate getPredicate() {
		return predicate;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Term> getArguments() {
		return arguments;
	}

	// /**
	// * {@inheritDoc}
	// */
	// public Set<Term> getVariables() {
	// return vars;
	// }
	//
	/**
	 * {@inheritDoc}
	 */
	public boolean isGround() {
		return ground;
	}

	/**
	 * {@inheritDoc}
	 */
	public QueryAtom apply(final ResultBinding binding) {
		final List<Term> newArguments = new ArrayList<Term>();

		for (final Term a : arguments) {
			if (binding.isBound(a)) {
				newArguments.add(binding.getValue(a));
			} else {
				newArguments.add(a);
			}
		}

		return new AbstractQueryAtom(predicate, newArguments);
	}

	@Override
	public int hashCode() {
		return 31 * predicate.hashCode() + arguments.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AbstractQueryAtom other = (AbstractQueryAtom) obj;

		return predicate.equals(other.predicate)
				&& arguments.equals(other.arguments);
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer();

		for (int i = 0; i < arguments.size(); i++) {
			final Term a = arguments.get(i);
			if (i > 0) {
				sb.append(", ");
			}

			sb.append(ATermUtils.toString(a));
		}

		return predicate + "(" + sb.toString() + ")";
	}
}
