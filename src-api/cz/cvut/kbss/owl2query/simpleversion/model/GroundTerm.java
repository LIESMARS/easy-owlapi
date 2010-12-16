package cz.cvut.kbss.owl2query.simpleversion.model;

public interface GroundTerm<T> extends Term<T> {

//	String getID();

	T getWrappedObject();
}
