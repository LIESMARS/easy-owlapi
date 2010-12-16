package cz.cvut.kbss.owl2query.complexversion.model;

public interface SameIndividualAtom<NI> extends QueryAtom {

	public abstract NamedIndividualTerm<NI> getNamedIndividualTerm1();

	public abstract NamedIndividualTerm<NI> getNamedIndividualTerm2();

}