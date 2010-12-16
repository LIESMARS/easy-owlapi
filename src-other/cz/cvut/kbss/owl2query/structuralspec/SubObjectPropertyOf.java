package cz.cvut.kbss.owl2query.structuralspec;

import java.util.List;

public interface SubObjectPropertyOf extends ObjectPropertyAxiom {		
	
	ObjectPropertyExpression getSuperObjectPropertyExpression();

	/**
	 * Role chains 
	 * TODO add one more object 'ObjectPropertyChain' ?
	 */
	List<ObjectPropertyExpression> getSubObjectPropertyExpressions();
}
