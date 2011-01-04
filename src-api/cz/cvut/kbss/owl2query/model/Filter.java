package cz.cvut.kbss.owl2query.model;

/**
 * @author Petr Kremen
 */
public interface Filter<G> {

	public boolean accept(final ResultBinding<G> binding);

}
