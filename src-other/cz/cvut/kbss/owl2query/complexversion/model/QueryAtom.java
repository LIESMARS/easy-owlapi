package cz.cvut.kbss.owl2query.complexversion.model;

import java.util.List;

public interface QueryAtom {

	QueryPredicate getPredicate();

	List<Term> getTerms();
}
