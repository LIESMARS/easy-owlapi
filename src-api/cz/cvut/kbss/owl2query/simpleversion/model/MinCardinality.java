package cz.cvut.kbss.owl2query.simpleversion.model;

public abstract class MinCardinality<T> extends QueryExpression<T> {
	int card;

	public MinCardinality(final int card, final Term<T> ope) {
		super(ope);

		this.card = card;
	}

	public MinCardinality(final int card, final Term<T> ope, final Term<T> ce) {
		super(ope, ce);

		this.card = card;
	}
	
	public String toString() {
		return "MinCardinality("+card+", " + terms+")";
	}

}
