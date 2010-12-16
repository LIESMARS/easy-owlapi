package cz.cvut.kbss.owl2query.complexversion.model;

import java.util.Set;

public interface Query {

	Set<QueryAtom> getQueryAtoms();
	
}
