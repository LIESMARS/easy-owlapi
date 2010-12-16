// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package cz.cvut.kbss.owl2query.complexversion.modelold;

import java.util.Collection;

import cz.cvut.kbss.owl2query.complexversion.model.Term;



/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: Clark & Parsia, LLC. <http://www.clarkparsia.com></p>
 *
 * @author Petr Kremen
 */
public interface Core extends QueryAtom {

	public abstract Collection<Term> getConstants();

	public abstract Collection<Term> getDistVars();

	public abstract Collection<Term> getUndistVars();
	
}