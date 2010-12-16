package cz.cvut.kbss.owl2query.structuralspec;


public interface DataPropertyAssertion extends Assertion {

	DataPropertyExpression getDataPropertyExpression();
	
	Individual getSourceIndividual();
	
	Literal getTargetValue();
}
