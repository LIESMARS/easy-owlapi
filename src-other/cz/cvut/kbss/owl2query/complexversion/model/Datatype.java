package cz.cvut.kbss.owl2query.complexversion.model;

/**
 * Arity of Datatype must be one.
 */
public interface Datatype<DT,DR> extends Entity, DataRange<DR> {

	public DT getWrappedDatatypeObject();
}
