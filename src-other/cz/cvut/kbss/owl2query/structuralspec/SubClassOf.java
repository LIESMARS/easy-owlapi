package cz.cvut.kbss.owl2query.structuralspec;


public interface SubClassOf extends ClassAxiom {
	
	ClassExpression getSubClassExpression();

	ClassExpression getSuperClassExpression();
}
