package cz.cvut.kbss.owl2query.structuralspec;

import java.util.Set;

public interface ObjectOneOf extends ClassExpression {

	/**
	 * 1+
	 */
	Set<Individual> getIndividuals();

}
