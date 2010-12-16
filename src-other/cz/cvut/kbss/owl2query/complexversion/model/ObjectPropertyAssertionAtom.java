package cz.cvut.kbss.owl2query.complexversion.model;

public interface ObjectPropertyAssertionAtom<OPE, NI> extends QueryAtom {

	public abstract ObjectPropertyTerm<OPE> getPropertyTerm();

	public abstract NamedIndividualTerm<NI> getSubjectTerm();

	public abstract NamedIndividualTerm<NI> getObjectTerm();

}