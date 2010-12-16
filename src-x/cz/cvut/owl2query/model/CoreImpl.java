// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package cz.cvut.owl2query.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
public class CoreImpl extends AbstractQueryAtom implements CoreAtom {

	private List<Variable> distVars = null;
	private List<GroundTerm> consts = null;

	private Collection<Variable> undistVars = null;

	private Collection<QueryAtom> atoms;

	public CoreImpl(final List<Term> arguments,
			final Collection<Variable> uv, final Collection<QueryAtom> atoms) {
		super(QueryPredicate.UndistVarCore, arguments);

		this.atoms = atoms;
		this.undistVars = uv;
	}    

	private void setup() {
		distVars = new ArrayList<Variable>();
		consts = new ArrayList<GroundTerm>();
		for (final Term a : arguments) {
			if ( a.isVariable() ) {
				distVars.add(a.asVariable());
			} else {
				consts.add(a.asGroundTerm());
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.sparqldl.model.CoreIF#getConstants()
	 */
	public Collection<GroundTerm> getConstants() {
		if (consts == null) {
			setup();
		}

		return consts;
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.sparqldl.model.CoreIF#getDistVars()
	 */
	public Collection<Variable> getDistVars() {
		if (distVars == null) {
			setup();
		}

		return distVars;
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.sparqldl.model.CoreIF#getUndistVars()
	 */
	public Collection<Variable> getUndistVars() {
		if (undistVars == null) {
			setup();
		}

		return undistVars;
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.sparqldl.model.CoreIF#apply(org.mindswap.pellet.sparqldl.model.ResultBinding)
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
