package cz.cvut.kbss.owl2query.structuralspec;

import java.util.Set;

public interface DisjointUnion extends ClassAxiom {
	
	/**
	 * 2+ 
	 */
	Set<ClassExpression> getDisjointClassExpressions();
	
	Class getDefinedClass();

}
