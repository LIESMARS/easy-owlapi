package cz.cvut.kbss.owl2query.structuralspec;

import java.util.Set;

public interface EquivalentDataProperties extends DataPropertyAxiom {		

	/**
	 * 2+ 
	 */
	Set<DataPropertyExpression> getDataPropertyExpressions();
}
