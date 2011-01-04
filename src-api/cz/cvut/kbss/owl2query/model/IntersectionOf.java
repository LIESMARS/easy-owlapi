package cz.cvut.kbss.owl2query.model;

import java.util.Set;

public abstract class IntersectionOf<T> extends QueryExpression<T> {
	public IntersectionOf(final Set<? extends Term<T>> ces) {
		super((Term<T>[]) ces.toArray(new Term[] {}));
	}
	
	public String toString() {
		return "IntersectionOf("+ terms+")";
	}

}