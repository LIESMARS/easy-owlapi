package cz.cvut.kbss.owl2query.complexversion.model;

import java.util.List;

abstract class AbstractQueryAtom implements QueryAtom {

	final QueryPredicate predicate;

	public AbstractQueryAtom(final QueryPredicate predicate) {
		this.predicate = predicate;
	}

	@Override
	public QueryPredicate getPredicate() {
		return predicate;
	}

	@Override
	public abstract List<Term> getTerms();
}
