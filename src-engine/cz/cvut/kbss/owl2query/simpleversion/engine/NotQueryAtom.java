package cz.cvut.kbss.owl2query.simpleversion.engine;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cz.cvut.kbss.owl2query.simpleversion.model.Term;

class NotQueryAtom<G> implements QueryAtom<G> {
	private InternalQuery<G> query;

	public NotQueryAtom(InternalQuery<G> atom) {
		this.query = atom;
	}

	public QueryAtom<G> apply(final Map<? extends Term<G>, ? extends Term<G>> binding) {
		return new NotQueryAtom<G>(query.apply(binding));
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof NotQueryAtom))
			return false;
		return query.equals(((NotQueryAtom<G>) obj).query);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Term<G>> getArguments() {
		return Arrays.asList((Term<G>[]) query.getResultVars().toArray(
				new Term[] {}));
	}

	public InternalQuery<G> getQuery() {
		return query;
	}

	/**
	 * {@inheritDoc}
	 */
	public QueryPredicate getPredicate() {
		return QueryPredicate.Not;
	}

	@Override
	public int hashCode() {
		return 17 * query.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isGround() {
		return query.getDistVars().isEmpty();
	}

	@Override
	public String toString() {
		return "Not(" + query + ")";
	}
}
