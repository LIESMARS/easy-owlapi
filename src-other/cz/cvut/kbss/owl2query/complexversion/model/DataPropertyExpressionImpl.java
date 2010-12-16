package cz.cvut.kbss.owl2query.complexversion.model;

class DataPropertyExpressionImpl<DPE> extends AbstractGroundTerm<DPE>
		implements DataPropertyExpression<DPE> {

	DataPropertyExpressionImpl(DPE o) {
		super(TermType.DataPropertyExpression, o);
	}
}
