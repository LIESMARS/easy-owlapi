package cz.cvut.kbss.owl2query.model;

public abstract class MaxCardinality<T> extends QueryExpression<T> {
	int card;

	public MaxCardinality(final int card, final Term<T> ope) {
		super(ope);

		this.card = card;
	}

	public MaxCardinality(final int card, final Term<T> ope, final Term<T> ce) {
		super(ope, ce);

		this.card = card;
	}
	
	public String toString() {
		return "MaxCardinality("+card+", " + terms+")";
	}

}
