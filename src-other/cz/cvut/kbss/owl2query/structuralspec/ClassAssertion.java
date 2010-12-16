package cz.cvut.kbss.owl2query.structuralspec;


public interface ClassAssertion extends Assertion {

	ClassExpression getClassExpression();
	
	Individual getIndividual();
	
}
