package cz.cvut.kbss.owl2query.structuralspec;


public interface DataPropertyDomain extends DataPropertyAxiom {

	DataPropertyExpression getDataPropertyExpression();

	ClassExpression getDomain();
}
