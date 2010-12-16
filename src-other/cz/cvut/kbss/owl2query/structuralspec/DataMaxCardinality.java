package cz.cvut.kbss.owl2query.structuralspec;


public interface DataMaxCardinality extends ClassExpression {

	/**
	 * 0..1 
	 * 
 	 * arity=1
	 */
	DataRange getDataRange();

	DataPropertyExpression getDataPropertyExpression();
	
	/**
	 * >= 0 
	 */
	int getCardinality();
}
