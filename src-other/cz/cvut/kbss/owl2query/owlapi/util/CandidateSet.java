package cz.cvut.kbss.owl2query.owlapi.util;

import java.util.Collection;

public interface CandidateSet<T> {

	Collection getKnowns();

	Collection getUnknowns();

}
