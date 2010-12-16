package cz.cvut.kbss.owl2query.complexversion.util;

import java.util.Collection;

public interface CandidateSet<T> {

	Collection getKnowns();

	Collection getUnknowns();

}
