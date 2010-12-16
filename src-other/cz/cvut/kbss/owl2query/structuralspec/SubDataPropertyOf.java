package cz.cvut.kbss.owl2query.structuralspec;

public interface SubDataPropertyOf extends DataPropertyAxiom {		
	
	DataPropertyExpression getSuperDataPropertyExpression();

	DataPropertyExpression getSubDataPropertyExpression();
}
