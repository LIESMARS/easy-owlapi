package cz.cvut.kbss.owl2query.structuralspec;


public interface ObjectSomeValuesFrom extends ClassExpression {

	ClassExpression getClassExpression();

	ObjectPropertyExpression getObjectPropertyExpression();
}
