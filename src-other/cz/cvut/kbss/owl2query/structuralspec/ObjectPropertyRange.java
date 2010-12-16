package cz.cvut.kbss.owl2query.structuralspec;


public interface ObjectPropertyRange extends ObjectPropertyAxiom {

	ClassExpression getRange();

	ObjectPropertyExpression getObjectPropertyExpression();
}
