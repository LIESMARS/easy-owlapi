package cz.cvut.kbss.owl2query.model;

import java.util.List;

public interface QueryResult<G> extends Iterable<ResultBinding<G>> {

	/**
	 * Adds a new binding to the query result.
	 * 
	 * @param binding
	 *            to be added
	 */
	public void add(final ResultBinding<G> binding);

	/**
	 * Returns result variables.
	 * 
	 * @return variables that appear in the result
	 */
	public List<Variable<G>> getResultVars();

	public boolean isDistinct();

	/**
	 * Tests whether the result is empty or not.
	 * 
	 * @return true if the result contains not bindings
	 */
	public boolean isEmpty();

	/**
	 * Returns number of bindings in the result.
	 * 
	 * @return number of bindings
	 */
	public int size();
}
