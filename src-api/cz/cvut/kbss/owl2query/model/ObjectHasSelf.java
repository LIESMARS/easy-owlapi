package cz.cvut.kbss.owl2query.model;


public abstract class ObjectHasSelf<T> extends QueryExpression<T> {

	public ObjectHasSelf(final Term<T> ope) {
		super(ope);
	}
	
	public String toString() {
		return "ObjectHasSelf(" + terms+")";
	}

}
