package cz.cvut.kbss.owl2query.structuralspec;


public interface NegativeObjectPropertyAssertion extends Assertion {

	ObjectPropertyExpression getObjectPropertyExpression();
	
	Individual getSourceIndividual();
	
	Individual getTargetIndividual();
}
