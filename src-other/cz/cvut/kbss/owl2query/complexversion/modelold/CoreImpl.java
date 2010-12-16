// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package cz.cvut.kbss.owl2query.complexversion.modelold;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
public class CoreImpl extends AbstractQueryAtom implements Core {

	private List<Term> distVars = null;
	private List<Term> consts = null;

	private Collection<Term> undistVars = null;

	private Collection<QueryAtom> atoms;

	public CoreImpl(final List<Term> arguments,
			final Collection<Term> uv, final Collection<QueryAtom> atoms) {
		super(QueryPredicate.UndistVarCore, arguments);

		this.atoms = atoms;
		this.undistVars = uv;
	}

	private void setup() {
		distVars = new ArrayList<Term>();
		consts = new ArrayList<Term>();
		for (final Term a : arguments) {
			if (ATermUtils.isVar(a)) {
				distVars.add(a);
			} else {
				consts.add(a);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mindswap.pellet.sparqldl.modelold.CoreIF#getConstants()
	 */
	public Collection<Term> getConstants() {
		if (consts == null) {
			setup();
		}

		return consts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mindswap.pellet.sparqldl.modelold.CoreIF#getDistVars()
	 */
	public Collection<Term> getDistVars() {
		if (distVars == null) {
			setup();
		}

		return distVars;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mindswap.pellet.sparqldl.modelold.CoreIF#getUndistVars()
	 */
	public Collection<Term> getUndistVars() {
		if (undistVars == null) {
			setup();
		}

		return undistVars;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mindswap.pellet.sparqldl.modelold.CoreIF#apply(org.mindswap.pellet.sparqldl
	 * .modelold.ResultBinding)
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

		final List<QueryAtom> newAtoms = new ArrayList<QueryAtom>();

		for (final QueryAtom a : atoms) {
			newAtoms.add(a.apply(binding));
		}

		return new CoreImpl(newArguments, undistVars, newAtoms);
	}

	@Override
	public int hashCode() {
		return arguments.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final CoreImpl other = (CoreImpl) obj;

		return arguments.equals(other.arguments);
	}
}
