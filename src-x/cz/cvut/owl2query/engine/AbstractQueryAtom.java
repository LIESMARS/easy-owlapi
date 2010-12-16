package cz.cvut.owl2query.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.cvut.owl2query.model.QueryAtom;
import cz.cvut.owl2query.model.QueryPredicate;
import cz.cvut.owl2query.model.ResultBinding;
import cz.cvut.owl2query.model.Term;

/**
 * <p>
 * Title: Abstract implementation of the query atom.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Czech Technical University in Prague, <http://www.cvut.cz>
 * </p>
 * 
 * @author Petr Kremen
 */
class AbstractQueryAtom implements QueryAtom {

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
			if (a.isVariable()) {
				ground = false;
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
			if (a.isVariable()) {
				if (binding.isBound(a.asVariable())) {
					newArguments.add(binding.getValue(a.asVariable()));
				} else {
					newArguments.add(a);
				}
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
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
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

			sb.append(a.toString());
		}

		return predicate + "(" + sb.toString() + ")";
	}
}
