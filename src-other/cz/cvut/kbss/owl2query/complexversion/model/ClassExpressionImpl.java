package cz.cvut.kbss.owl2query.complexversion.model;

class ClassExpressionImpl<CE> extends AbstractGroundTerm<CE> implements
		ClassExpression<CE> {

	ClassExpressionImpl(CE o) {
		super(TermType.ClassExpression, o);
	}
}
