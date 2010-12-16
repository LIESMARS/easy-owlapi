package cz.cvut.owl2query.model;

import java.util.Set;


/**
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
