package cz.cvut.kbss.owl2query.simpleversion.engine;

import cz.cvut.kbss.owl2query.simpleversion.model.ResultBinding;

/**
 * @author Petr Kremen
 */
abstract class QueryPlan<G> {

	protected InternalQuery<G> query;

	public QueryPlan(final InternalQuery<G> query) {
		this.query = query;
	}

	public InternalQuery<G> getQuery() {
		return query;
	}

	/**
	 * Returns next atom to be executed w.r. to the current binding.
	 * 
	 * @param binding
	 * @return
	 */
	public abstract QueryAtom<G> next(final ResultBinding<G> binding);

	/**
	 * Goes one level back to the last atom.
	 */
	public abstract void back();

	/**
	 * Checks whether there is another atom to execute.
	 * 
	 * @return true if there is another atom to execute.
	 */
	public abstract boolean hasNext();

	/**
	 * Resets the query planner.
	 */
	public abstract void reset();
}
