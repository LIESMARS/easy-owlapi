package cz.cvut.kbss.owl2query.complexversion.model;

import java.util.Set;

public interface Variable<CE, OPE, DPE, AP, DT, DR, NI, L> extends Term,
		ClassTerm<CE>, ObjectPropertyTerm<OPE>, DataPropertyTerm<DPE>,
		NamedIndividualTerm<NI>, LiteralTerm<L, DT, DR>, DataRangeTerm<DR> {

	public abstract Set<TermType> getAllowedTermTypes();

	public abstract String getName();

}