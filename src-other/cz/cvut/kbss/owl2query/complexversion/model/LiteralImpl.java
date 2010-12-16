package cz.cvut.kbss.owl2query.complexversion.model;

class LiteralImpl<L, DT, DR> extends AbstractGroundTerm<L> implements
		Literal<L, DT, DR> {

	LiteralImpl(L o) {
		super(TermType.NamedIndividual, o);
	}
}
