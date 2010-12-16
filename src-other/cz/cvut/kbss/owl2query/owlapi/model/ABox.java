package cz.cvut.kbss.owl2query.owlapi.model;


import cz.cvut.kbss.owl2query.owlapi.util.CandidateSet;


public interface ABox {

	long satisfiabilityCount = 0;
	long consistencyCount = 0;

	void setInitialized(boolean b);

	boolean isConsistent();

	ABox copy();

	CandidateSet<ATermAppl> getObviousInstances(ATermAppl c);

}
