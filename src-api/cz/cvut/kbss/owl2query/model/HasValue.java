package cz.cvut.kbss.owl2query.model;

public abstract class HasValue<T> extends QueryExpression<T> {

	public HasValue(final Term<T> ope, final Term<T> ce) {
		super(ope, ce);
	}
	
	public String toString() {
		return "HasValue("+ terms+")";
	}
}
