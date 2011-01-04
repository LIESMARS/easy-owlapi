package cz.cvut.kbss.owl2query.model;

import java.util.Set;

/**
 * An abstract access to all hierarchic information: - TBOX - told TBOX - RBOX
 * 
 * Each implementation of this class operates over two types of elements: -
 * named elements (classes, properties) - general elements (class expressions,
 * property expressions).
 * 
 * @author Petr Kremen
 */
public interface Hierarchy<G, T extends G> {

	/**
	 * Returns a set of all top elements (for hierarchies where no common root
	 * is defined). E.g. OWL property hierarchy can have two roots -
	 * owl:topObjectProperty and owl:topDataProperty.
	 * 
	 * @return a set of named elements comprising the top (uncomparable) layer
	 *         of this hierarchy
	 */
	public Set<T> getTops();

	/**
	 * Returns a set of direct/all subs, NOT including getEquivs.
	 * 
	 * The set might include elements from getBottoms() if applicable.
	 * 
	 * @return a set of named elements that are (direct) subelements of the
	 *         general element superG
	 */
	public Set<T> getSubs(final G superG, boolean direct);

	/**
	 * Returns a set of direct/all supers, NOT including getEquivs
	 * 
	 * The set might include elements from getTops() if applicable.
	 * 
	 * @return a set of named elements that are (direct) superelements of the
	 *         general element superG
	 */
	public Set<T> getSupers(final G subG, boolean direct);

	/**
	 * Returns a set of all equivalents subs, NOT including equivG.
	 * 
	 * @return a set of named elements that are equivalent to the general
	 *         element superG
	 */
	public Set<T> getEquivs(final G equivG);

	/**
	 * Returns a set of all bottom elements (in case no common sink is defined).
	 * E.g. OWL property hierarchy can have two sinks - owl:bottomObjectProperty
	 * and owl:bottomDataProperty.
	 * 
	 * @return a set of named elements comprising the bottom (uncomparable)
	 *         layer of this hierarchy
	 */
	public Set<T> getBottoms();
}
