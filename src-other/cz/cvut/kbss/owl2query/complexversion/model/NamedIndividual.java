package cz.cvut.kbss.owl2query.complexversion.model;

public interface NamedIndividual<NI> extends Individual,
		NamedIndividualTerm<NI> {
	/**
	 * @throws IllegalArgumentException
	 *             if this term is a variable and not a ground term
	 */
	public NI getWrappedObject();

}
