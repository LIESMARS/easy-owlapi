package cz.cvut.kbss.owl2query.structuralspec;

import java.util.Set;

public interface HasKey extends Axiom {

	Set<ObjectPropertyExpression> getObjectPropertyExpressions();

	Set<DataPropertyExpression> getDataPropertyExpressions();

	ClassExpression getClassExpression();

}
