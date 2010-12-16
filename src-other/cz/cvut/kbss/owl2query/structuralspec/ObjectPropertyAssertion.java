package cz.cvut.kbss.owl2query.structuralspec;


public interface ObjectPropertyAssertion extends Assertion {

	ObjectPropertyExpression getObjectPropertyExpression();
	
	Individual getSourceIndividual();
	
	Individual getTargetIndividual();
}
