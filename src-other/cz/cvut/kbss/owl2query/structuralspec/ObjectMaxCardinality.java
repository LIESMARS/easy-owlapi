package cz.cvut.kbss.owl2query.structuralspec;

public interface ObjectMaxCardinality extends ClassExpression {

	/**
	 * 0..1
	 */
	ClassExpression getClassExpression();

	ObjectPropertyExpression getObjectPropertyExpression();

	/**
	 * >= 0
	 */
	int getCardinality();
}
