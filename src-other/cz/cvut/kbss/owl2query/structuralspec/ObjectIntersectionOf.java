package cz.cvut.kbss.owl2query.structuralspec;

import java.util.Set;

public interface ObjectIntersectionOf extends ClassExpression {

	/**
	 * 2+
	 */
	Set<ClassExpression> getClassExpressions();

}
