package cz.cvut.kbss.owl2query.complexversion.model;

public interface DataPropertyAssertionAtom<DPE, NI, L, DT, DR> extends
		QueryAtom {

	public abstract DataPropertyTerm<DPE> getPropertyTerm();

	public abstract NamedIndividualTerm<NI> getSubjectTerm();

	public abstract LiteralTerm<L, DT, DR> getObjectTerm();

}