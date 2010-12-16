package cz.cvut.kbss.owl2query.structuralspec;

public interface DataHasValue extends ClassExpression {

	Literal getLiteral();
	
	DataPropertyExpression getDataPropertyExpression();
}
