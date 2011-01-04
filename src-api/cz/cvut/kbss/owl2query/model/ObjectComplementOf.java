package cz.cvut.kbss.owl2query.model;

public abstract class ObjectComplementOf<T> extends QueryExpression<T> {

	public ObjectComplementOf(final Term<T> ce) {
		super(ce);
	}
	
	public String toString() {
		return "ObjectComplementOf(" + terms+")";
	}

}
