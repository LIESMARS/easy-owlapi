package cz.cvut.kbss.owl2query.structuralspec;

import java.util.Set;

public interface DisjointDataProperties extends DataPropertyAxiom {		

	/**
	 * 2+ 
	 */
	Set<DataPropertyExpression> getDataPropertyExpressions();
}
