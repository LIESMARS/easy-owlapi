package cz.cvut.kbss.owl2query.structuralspec;


public interface ObjectPropertyDomain extends ObjectPropertyAxiom {

	ClassExpression getDomain();

	ObjectPropertyExpression getObjectPropertyExpression();
}
