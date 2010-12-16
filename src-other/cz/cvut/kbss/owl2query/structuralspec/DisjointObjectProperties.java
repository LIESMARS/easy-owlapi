package cz.cvut.kbss.owl2query.structuralspec;

import java.util.Set;

public interface DisjointObjectProperties extends ObjectPropertyAxiom {		
	
	/**
	 * 2+ 
	 */
	Set<ObjectPropertyExpression> getObjectPropertyExpressions();
	
}
