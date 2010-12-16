package cz.cvut.kbss.owl2query.structuralspec;

import java.util.Set;

public interface ObjectUnionOf extends ClassExpression {

	/**
	 * 2+
	 */
	Set<ClassExpression> getClassExpressions();


}
