package org.mindswap.pellet.utils;

import java.util.Collection;

public interface CandidateSet<T> {

	Collection getKnowns();

	Collection getUnknowns();

}
