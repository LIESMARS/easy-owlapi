package cz.cvut.kbss.owl2query.simpleversion.model;


public abstract class AllValuesFrom<T> extends QueryExpression<T> {

	public AllValuesFrom(final Term<T> ope, final Term<T> ce) {
		super(ope, ce);
	}
	
	public String toString() {
		return "AllValuesFrom(" + terms+")";
	}

}
