package cz.cvut.kbss.owl2query.model;

public abstract class SomeValuesFrom<T> extends QueryExpression<T> {

	public SomeValuesFrom(final Term<T> ope, final Term<T> ce) {
		super(ope, ce);
	}
	
	public String toString() {
		return "SomeValuesFrom(" + terms+")";
	}

}
