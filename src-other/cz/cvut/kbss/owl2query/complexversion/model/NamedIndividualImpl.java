package cz.cvut.kbss.owl2query.complexversion.model;

class NamedIndividualImpl<NI> extends AbstractGroundTerm<NI> implements
		NamedIndividual<NI> {

	NamedIndividualImpl(NI o) {
		super(TermType.NamedIndividual, o);
	}
}
