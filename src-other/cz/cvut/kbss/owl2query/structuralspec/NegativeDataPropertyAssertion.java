package cz.cvut.kbss.owl2query.structuralspec;


public interface NegativeDataPropertyAssertion extends Assertion {

	DataPropertyExpression getDataPropertyExpression();
	
	Individual getSourceIndividual();
	
	Literal getTargetValue();
}
