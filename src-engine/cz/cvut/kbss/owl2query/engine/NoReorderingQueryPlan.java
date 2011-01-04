package cz.cvut.kbss.owl2query.engine;

import java.util.ListIterator;

import cz.cvut.kbss.owl2query.model.ResultBinding;

class NoReorderingQueryPlan<G> extends QueryPlan<G> {

	private ListIterator<QueryAtom<G>> i;

	public NoReorderingQueryPlan(InternalQuery<G> query) {
		super(query);

		i = query.getAtoms().listIterator();
	}

	@Override
	public QueryAtom<G> next(final ResultBinding<G> binding) {
		final QueryAtom<G> a = i.next();

		if (a.isGround()) {
			return a;
		} else {
			return a.apply(binding);
		}
	}

	@Override
	public boolean hasNext() {
		return i.hasNext();
	}

	@Override
	public void back() {
		i.previous();
	}

	@Override
	public void reset() {
		i = query.getAtoms().listIterator();
	}
}
