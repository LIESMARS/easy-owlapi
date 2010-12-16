// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package cz.cvut.kbss.owl2query.complexversion.modelold;

import java.util.Set;

import cz.cvut.kbss.owl2query.complexversion.model.Term;


/**
 * <p>
 * Title: Result Binding Interface
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
public interface ResultBinding extends Cloneable {

	/**
	 * Gets value for given variable.
	 * 
	 * @param variable
	 *            for which return the value
	 * @return binding for the variable
	 */
	public Term getValue(final Term var);

	/**
	 * Sets all variable bindings according to the bindings.
	 * 
	 * @param bindings
	 *            to be set.
	 */
	public void setValues(final ResultBinding bindings);

	/**
	 * Sets one variable binding.
	 * 
	 * @param bindings
	 *            to be set.
	 * @param var
	 *            variable to set.
	 */
	public void setValue(final Term var, final Term binding);

	/**
	 * Checks whether given variable is bound.
	 * 
	 * @param var
	 *            variable to determine.
	 * @return true if the given variable is bound.
	 */
	public boolean isBound(final Term var);

	/**
	 * Returns all variables in this binding.
	 * 
	 * @return set of all variables.
	 */
	public Set<Term> getAllVariables();

	/**
	 * Checks for emptiness of the binding.
	 * 
	 * @return true if the binding doesn't contain a variable.
	 */
	public boolean isEmpty();

	/**
	 * Clones the binding.
	 * 
	 * @return new copy of the binding.
	 */
	public ResultBinding clone();
}
