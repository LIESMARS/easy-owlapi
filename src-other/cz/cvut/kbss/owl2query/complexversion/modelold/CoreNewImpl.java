// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package cz.cvut.kbss.owl2query.complexversion.modelold;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import cz.cvut.kbss.owl2query.complexversion.modelold.Query.VarType;
import cz.cvut.kbss.owl2query.complexversion.model.Term;
import cz.cvut.kbss.owl2query.complexversion.util.ATermUtils;

/**
 * <p>
 * Title: Implementation of the Core of undistinguished variables.
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
public class CoreNewImpl implements QueryAtom, Core {

	private final QueryAtom atom;

	private final Query query;

	public CoreNewImpl(final Collection<QueryAtom> atoms,
			final Collection<Term> uv, final KnowledgeBase kb) {
		query = new QueryImpl(kb, false);

		final List<Term> signature = new ArrayList<Term>();

		for (final QueryAtom atom : atoms) {
			query.add(atom);

			// this is nasty - remodeling will be fine
			switch (atom.getPredicate()) {
			case PropertyValue:
				final Term a1 = atom.getArguments().get(1);
				addI(atom.getArguments().get(0), signature, uv);
				addI(atom.getArguments().get(2), signature, uv);
				if (ATermUtils.isVar(a1)) {
					if (!uv.contains(a1)) {
						query.addDistVar(a1, VarType.PROPERTY);
					}
				}
			case Type:
				final Term aa1 = atom.getArguments().get(1);
				addI(atom.getArguments().get(0), signature, uv);
				if (ATermUtils.isVar(aa1)) {
					if (!uv.contains(aa1)) {
						query.addDistVar(aa1, VarType.CLASS);
					}
				}
				break;
			default:
				throw new IllegalArgumentException("Atom type "
						+ atom.getPredicate() + " is not supported in a core.");
			}
		}

		atom = new AbstractQueryAtom(QueryPredicate.UndistVarCore, signature);
	}

	private CoreNewImpl(final Query query, final QueryAtom atom) {
		this.atom = atom;
		this.query = query;
	}

	private void addI(Term aa0, final List<Term> signature,
			final Collection<Term> uv) {
		if (ATermUtils.isVar(aa0)) {
			if (!uv.contains(aa0)) {
				query.addDistVar(aa0, VarType.INDIVIDUAL);
				signature.add(aa0);
			}
		} else {
			signature.add(aa0);
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final CoreNewImpl other = (CoreNewImpl) obj;

		return atom.equals(other.atom) && query.equals(query);
	}

	public Set<Term> getConstants() {
		return query.getConstants();
	}

	public Set<Term> getDistVars() {
		return query.getDistVars();
	}

	public Set<Term> getUndistVars() {
		return query.getUndistVars();
	}

	public List<Term> getArguments() {
		return atom.getArguments();
	}

	public QueryPredicate getPredicate() {
		return atom.getPredicate();
	}

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
}