package cz.cvut.kbss.owl2query.complexversion.modelold;

import cz.cvut.kbss.owl2query.complexversion.model.Term;
import cz.cvut.kbss.owl2query.complexversion.util.CandidateSet;

public interface ABox {

	long satisfiabilityCount = 0;
	long consistencyCount = 0;

	void setInitialized(boolean b);

	boolean isConsistent();

	ABox copy();

	CandidateSet<Term> getObviousInstances(Term c);

}
