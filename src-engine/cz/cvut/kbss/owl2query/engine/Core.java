package cz.cvut.kbss.owl2query.engine;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cz.cvut.kbss.owl2query.model.GroundTerm;
import cz.cvut.kbss.owl2query.model.Term;

class Core<G> implements QueryAtom<G> {
	private InternalQuery<G> query;

	private Term<G> term;

	private GroundTerm<G> rollUp;

	public Core(final Term<G> term, final GroundTerm<G> rollUp,
			InternalQuery<G> atom) {
		this.query = atom;
		this.term = term;
		this.rollUp = rollUp;
	}

	public QueryAtom<G> apply(
			final Map<? extends Term<G>, ? extends Term<G>> binding) {
		if (binding.containsKey(term)) {
			return new Core<G>(binding.get(term), rollUp, query.apply(binding));
		} else {
			return new Core<G>(term, rollUp, query.apply(binding));
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Core))
			return false;

		final Core<G> c = (Core<G>) obj;

		return query.equals(c.query) && term.equals(c.term);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Term<G>> getArguments() {
		return Arrays.asList(term);
	}

	public Term<G> getTerm() {
		return term;
	}

	public GroundTerm<G> getRollUp() {
		return rollUp;
	}

	public InternalQuery<G> getQuery() {
		return query;
	}

	/**
	 * {@inheritDoc}
	 */
	public QueryPredicate getPredicate() {
		return QueryPredicate.Core;
	}

	@Override
	public int hashCode() {
		return 17 * query.hashCode() + 3 * term.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isGround() {
		return term.isGround();
	}

	@Override
	public String toString() {
		return getPredicate().shortForm() + "(" + term + ": " + query + ")";
	}
}
