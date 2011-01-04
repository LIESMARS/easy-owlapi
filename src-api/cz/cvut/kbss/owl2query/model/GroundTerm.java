package cz.cvut.kbss.owl2query.model;

public interface GroundTerm<T> extends Term<T> {

//	String getID();

	T getWrappedObject();
}
