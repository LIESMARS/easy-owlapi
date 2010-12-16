package cz.cvut.owl2query.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class CoreNewImpl implements CoreAtom {

	private final QueryAtom atom;
	private final Query query;

	public CoreNewImpl(final Collection<QueryAtom> atoms,
			final Collection<Variable> uv, final KnowledgeBase kb) {
		query = new QueryImpl(kb, false);

		final List<Term> signature = new ArrayList<Term>();

		for (final QueryAtom atom : atoms) {
			query.add(atom);

			if (atom.getPredicate().equals(QueryPredicate.Type)) {
				final Term aa1 = atom.getArguments().get(1);
				addI(atom.getArguments().get(0), signature, uv);
				if (aa1.isVariable() && (!uv.contains(aa1.asVariable()))) {
					query.addResultVar(aa1.asVariable());
				}
			} else if (atom.getPredicate().equals(QueryPredicate.PropertyValue)) {
				final Term a1 = atom.getArguments().get(1);
				addI(atom.getArguments().get(0), signature, uv);
				addI(atom.getArguments().get(2), signature, uv);
				if (a1.isVariable() && !uv.contains(a1)) {
					query.addResultVar(a1.asVariable());
				}
			}
		}

		atom = new AbstractQueryAtom(QueryPredicate.UndistVarCore, signature);
	}

	private CoreNewImpl(final Query query, final QueryAtom atom) {
		this.atom = atom;
		this.query = query;
	}

	private void addI(Term aa0, final List<Term> signature,
			final Collection<Variable> uv) {
		if (aa0.isVariable()) {
			if (!uv.contains(aa0.asVariable())) {
				query.addResultVar(aa0.asVariable());
				signature.add(aa0.asVariable());
			}
		} else {
			signature.add(aa0.asGroundTerm());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public CoreNewImpl apply(final ResultBinding binding) {
		return new CoreNewImpl(query.apply(binding), atom.apply(binding));
	}

	@Override
	public int hashCode() {
		return atom.hashCode() + 7 * query.hashCode();
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
		final CoreNewImpl other = (CoreNewImpl) obj;

		return atom.equals(other.atom) && query.equals(query);
	}

	// public Set<? extends GroundTerm> getConstants() {
	// return query.getConstants();
	// }

	// public Set<Term> getDistVars() {
	// return query.getDistVars();
	// }

	public Set<Variable> getUndistVars() {
		return query.getUndistVars();
	}

	public List<Term> getArguments() {
		return atom.getArguments();
	}

	// public QueryPredicate getPredicate() {
	// return atom.getPredicate();
	// }

	public Query getQuery() {
		return query;
	}

	public boolean isGround() {
		return atom.isGround();
	}

	@Override
	public String toString() {
		return atom.toString();
	}

	@Override
	public QueryPredicate getPredicate() {
		return QueryPredicate.UndistVarCore;
	}
}