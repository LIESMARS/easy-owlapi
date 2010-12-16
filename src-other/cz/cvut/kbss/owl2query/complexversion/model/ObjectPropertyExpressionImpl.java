package cz.cvut.kbss.owl2query.complexversion.model;

class ObjectPropertyExpressionImpl<OPE> extends AbstractGroundTerm<OPE>
		implements ObjectPropertyExpression<OPE> {

	ObjectPropertyExpressionImpl(OPE o) {
		super(TermType.ObjectPropertyExpression, o);
	}
}
