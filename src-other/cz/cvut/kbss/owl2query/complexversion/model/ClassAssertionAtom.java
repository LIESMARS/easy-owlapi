package cz.cvut.kbss.owl2query.complexversion.model;

interface ClassAssertionAtom<CE, NI> extends QueryAtom {

	public abstract ClassTerm<CE> getClassTerm();

	public abstract NamedIndividualTerm<NI> getIndividualTerm();

}