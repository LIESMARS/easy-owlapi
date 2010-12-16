package cz.cvut.kbss.owl2query.structuralspec;


public interface ObjectHasValue extends ClassExpression {

	Individual getIndividual();
	
	ObjectPropertyExpression getObjectPropertyExpression();
}
