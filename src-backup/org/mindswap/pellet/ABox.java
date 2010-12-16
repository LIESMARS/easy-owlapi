package org.mindswap.pellet;

import org.mindswap.pellet.utils.CandidateSet;

import cz.cvut.kbss.owl2query.owlapi.model.ATermAppl;

public interface ABox {

	long satisfiabilityCount = 0;
	long consistencyCount = 0;
	void setInitialized(boolean b);
	boolean isConsistent();
	ABox copy();
	CandidateSet<ATermAppl> getObviousInstances(ATermAppl c);

}
