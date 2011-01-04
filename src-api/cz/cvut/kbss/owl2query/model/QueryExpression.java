package cz.cvut.kbss.owl2query.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class QueryExpression<T> implements Term<T> {

	protected final List<Term<T>> terms;

	public QueryExpression(final Term<T>... terms) {
		this.terms = Arrays.asList(terms);
	}

	@Override
	public abstract Term<T> apply(final Map<Variable<T>, GroundTerm<T>> binding);

	@Override
	public GroundTerm<T> asGroundTerm() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Variable<T> asVariable() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Variable<T>> getVariables() {
		Set<Variable<T>> c = new HashSet<Variable<T>>();
		for (final Term<T> tx : terms) {
			c.addAll(tx.getVariables());
		}
		return c;
	}

	@Override
	public boolean isGround() {
		return false;
	}

	@Override
	public boolean isVariable() {
		return false;
	}
	
	public String shortForm() {
		return toString();
	}
} 
