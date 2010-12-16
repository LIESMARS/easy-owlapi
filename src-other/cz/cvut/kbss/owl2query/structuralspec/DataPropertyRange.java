package cz.cvut.kbss.owl2query.structuralspec;


public interface DataPropertyRange extends DataPropertyAxiom {

	DataPropertyExpression getDataPropertyExpression();

	/**
	 * arity=1 
	 */
	DataRange getRange();
}
