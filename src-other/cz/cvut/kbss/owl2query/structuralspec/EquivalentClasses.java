package cz.cvut.kbss.owl2query.structuralspec;

import java.util.Set;


public interface EquivalentClasses extends ClassAxiom {
	
	/**
	 * 2+ 
	 */
	Set<ClassExpression> getClassExpressions();

}
