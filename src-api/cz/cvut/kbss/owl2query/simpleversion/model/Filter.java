package cz.cvut.kbss.owl2query.simpleversion.model;

/**
 * @author Petr Kremen
 */
public interface Filter<G> {

	public boolean accept(final ResultBinding<G> binding);

}
