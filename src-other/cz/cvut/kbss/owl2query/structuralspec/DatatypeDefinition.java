package cz.cvut.kbss.owl2query.structuralspec;

public interface DatatypeDefinition extends Axiom {

	/**
	 * arity=1 
	 */
	DataRange getDataRange();
	
	DataRange getDatatype();
	
}
